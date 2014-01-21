/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.namal.needless.compass.app;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;
import org.namal.needless.compass.model.House;
import org.namal.needless.compass.model.Houses;
import org.namal.needless.compass.model.Score;
import org.namal.needless.compass.model.Site;
import org.namal.needless.compass.model.Sites;
import org.namal.needless.compass.model.Trip;
import org.namal.needless.compass.model.Trips;
import org.namal.needless.compass.model.Waypoint;
import org.namal.needless.compass.model.google.Geocode;
import org.namal.needless.compass.model.google.Result;
import org.namal.needless.compass.mongo.MongoManager;

/**
 *
 * @author nmalik
 */
public class TestApp {

    private Sites sites;
    private Houses houses;
    private Trips trips;
    private Map<String, Set<Site>> categorySiteMap;

    public TestApp() {
    }

    public void initialize() throws IOException {
        /*
         try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("sites.json");
         InputStreamReader isr = new InputStreamReader(is, Charset.defaultCharset())) {
         Gson g = new Gson();
         sites = g.fromJson(isr, Sites.class);
         if (null == sites || sites.getSites().length <= 0) {
         throw new IllegalStateException("No sites found!");
         }
         }
         */
        sites = MongoManager.loadSites();

        /*
         try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("houses.json");
         InputStreamReader isr = new InputStreamReader(is, Charset.defaultCharset())) {
         Gson g = new Gson();
         houses = g.fromJson(isr, Houses.class);
         if (null == houses || houses.getHouses().length <= 0) {
         throw new IllegalStateException("No houses found!");
         }
         }
         */
        houses = MongoManager.loadHouses();

        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("trips.json");
                InputStreamReader isr = new InputStreamReader(is, Charset.defaultCharset())) {
            Gson g = new Gson();
            trips = g.fromJson(isr, Trips.class);
            if (null == trips || trips.getTrips().length <= 0) {
                throw new IllegalStateException("No trips found!");
            }
        }

        categorySiteMap = new HashMap<>();

        for (Site site : sites.getSites()) {
            for (String category : site.getCategories()) {
                if (!categorySiteMap.containsKey(category)) {
                    categorySiteMap.put(category, new HashSet<Site>());
                }
                categorySiteMap.get(category).add(site);
            }
        }
    }

    public String process() {
        // for each house + trip find all categories that satisfy the waypoint needs and then compute final best (lowest) score
        for (House house : houses.getHouses()) {
            process(house);
        }

        // sorted output
        TreeSet<House> sortedHouses = new TreeSet<>();
        sortedHouses.addAll(Arrays.asList(houses.getHouses()));

        return prettyJson(sortedHouses);
    }

    private static final Gson prettyGson = new GsonBuilder().setPrettyPrinting().create();

    public static String prettyJson(Object o) {
        return prettyGson.toJson(o);
    }

    public void process(House house) {
        for (Trip trip : trips.getTrips()) {
            // reset current site to house and create starting waypoint (as 'previous')
            Waypoint currentWaypoint = new Waypoint(house);

            house.addPath(trip, currentWaypoint);

            addWaypoints(house, currentWaypoint, trip.getCategoryNames(), 0);
        }
        // got a bunch 'o data, now figure out a score for this house!
        // get the lowest score from each of the first children of the path's waypoint

        Map<Trip, Waypoint> paths = house.getPaths();
        double score = 0;
        for (Trip trip : paths.keySet()) {
            Waypoint root = paths.get(trip);
            score += (computeMinScore(root) * trip.getFrequency().doubleValue());
        }
        house.setScore(new Score(score));
    }

    /**
     * Recursively add next waypoints to current waypoint starting at given cateogry name index and ending with the
     * given house.
     *
     * @param current
     * @param categoryNames
     * @param categoryNameIndex
     */
    public void addWaypoints(House house, Waypoint current, String[] categoryNames, int categoryNameIndex) {
        if (categoryNameIndex >= categoryNames.length) {
            Waypoint childWaypoint = new Waypoint(house, current);
            return;
        }

        if (!categorySiteMap.containsKey(categoryNames[categoryNameIndex])) {
            throw new IllegalStateException("Unable to find category data: " + categoryNames[categoryNameIndex]);
        }

        for (Site site : categorySiteMap.get(categoryNames[categoryNameIndex])) {
            Waypoint childWaypoint = new Waypoint(site, current);
            addWaypoints(house, childWaypoint, categoryNames, categoryNameIndex + 1);
        }
    }

    public double computeMinScore(Waypoint wp) {
        // collect sorted set of leaf waypoints. first will be smallest score
        Set<Waypoint> leafs = new TreeSet<>();

        Stack<Iterator<Waypoint>> stack = new Stack<>();
        stack.push(wp.iterator());

        while (!stack.empty() && stack.peek().hasNext()) {
            Waypoint child = stack.peek().next();
            child.getScore(); // for now just to prime the score
            if (child.getNext().isEmpty()) {
                leafs.add(child);
                stack.pop();
            } else {
                stack.push(child.iterator());
            }
        }

        return leafs.isEmpty() ? 0.0 : leafs.iterator().next().getScore().doubleValue();
    }

    public static void enrichSite(Site site) throws MalformedURLException, IOException {
        String geocodeApiUrlString;
        if (site.getAddress() != null && !site.getAddress().isEmpty()) {
            geocodeApiUrlString = "http://maps.googleapis.com/maps/api/geocode/json?sensor=false&address=" + URLEncoder.encode(site.getAddress(), "UTF-8");
        } else if (site.getLatitude() != null && site.getLongitude() != null) {
            geocodeApiUrlString = "http://maps.googleapis.com/maps/api/geocode/json?sensor=false&latlng=" + site.getLatitude().toString() + "," + site.getLongitude().toString();
        } else {
            throw new MalformedURLException("Unable to construct URL, must supply either address or latitude/longitude on Site");
        }
        URL url = new URL(geocodeApiUrlString);
        URLConnection con = url.openConnection();

        StringBuilder buff = new StringBuilder();

        try (InputStream is = con.getInputStream();
                InputStreamReader isr = new InputStreamReader(is, Charset.defaultCharset());
                BufferedReader reader = new BufferedReader(isr)) {
            String line;
            while ((line = reader.readLine()) != null) {
                buff.append(line).append("\n");
            }
        }

        String jsonString = buff.toString();
        Gson g = new Gson();
        Geocode geocode = g.fromJson(jsonString, Geocode.class);

        if (!"OK".equals(geocode.getStatus())) {
            throw new RuntimeException("Status from geocode API not OK: " + jsonString);
        }

        // use only first result
        Result result = geocode.getResults()[0];
        site.setAddress(result.getFormatted_address());
        site.setLatitude(new BigDecimal(result.getGeometry().getLocation().getLat()));
        site.setLongitude(new BigDecimal(result.getGeometry().getLocation().getLng()));
        site.setName(result.getFormatted_address());
    }

    public static void main(String[] args) {
        TestApp app = new TestApp();
        try {
//            app.initialize();
//            System.out.println(app.process());
            House house = new House();
            house.setAddress("Raleigh NC");
            enrichSite(house);
            System.out.println(new Gson().toJson(house));
        } catch (IOException e) {
            System.err.println("Failed to start application:");
            e.printStackTrace();
        }
    }
}
