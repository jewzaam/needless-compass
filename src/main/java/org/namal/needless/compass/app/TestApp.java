/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.namal.needless.compass.app;

import com.google.gson.Gson;
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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;
import org.namal.mongo.MongoCRUD;
import org.namal.mongo.model.geo.Shape;
import org.namal.needless.compass.model.Coordinate;
import org.namal.needless.compass.model.v2.House;
import org.namal.needless.compass.model.Score;
import org.namal.needless.compass.model.Site;
import org.namal.needless.compass.model.Waypoint;
import org.namal.needless.compass.model.google.Geocode;
import org.namal.needless.compass.model.google.Result;
import org.namal.needless.compass.model.v2.PointOfInterest;
import org.namal.needless.compass.model.v2.RouteTree;
import org.namal.needless.compass.model.v2.Trip;

/**
 *
 * @author nmalik
 */
public class TestApp {
    private static final int FIND_POC_LIMIT = 3;
    private static final String COLLECTION_POI = "poi";
    private static final String COLLECTION_ROUTE = "route";
    private static final String COLLECTION_TRIP = "trip";

    private final MongoCRUD crud = new MongoCRUD("needlesscompass");

    public TestApp() {
    }

    public void initialize() throws IOException {
        crud.createIndex2dsphere(COLLECTION_POI, Shape.ATTRIBUTE_LOCATION);
        crud.createIndex(COLLECTION_POI, PointOfInterest.ATTRIBUTE_CATEGORIES);
    }

    public String process(String owner) {

        // get all trips
        Iterator<Trip> tripItr = crud.find(
                // collection
                COLLECTION_TRIP,
                // query
                String.format("{owner:%s}", owner),
                // projection
                null
        );

        List<Trip> trips = new ArrayList<>();
        while (tripItr.hasNext()) {
            trips.add(tripItr.next());
        }

        // get all houses and initialize a sorted set.  scores will sort it eventually
        TreeSet<House> sortedHouses = new TreeSet<>();

        Iterator<House> houseItr = crud.find(
                // collection
                COLLECTION_POI,
                // query
                String.format("{owner:%s,categories:%s}", owner, House.CATEGORY_HOUSE),
                // projection
                null
        );

        while (houseItr.hasNext()) {
            House house = houseItr.next();
            process(owner, house, trips);
            sortedHouses.add(house);
        }

        return new Gson().toJson(sortedHouses);
    }

    public void process(String owner, House house, List<Trip> trips) {

        // for each trip find closes sites that match using global limit
        for (Trip trip : trips) {
            // for each trip prep a route and collect transient points
            RouteTree root = new RouteTree(house);
            RouteTree current = root;

            for (String categoryName : trip.getCategoryNames()) {
                // get POI near last coordinates
                Iterator<PointOfInterest> poiItr = crud.find(
                        // collection
                        COLLECTION_POI,
                        // query
                        String.format("{%s: { $near: { $geometry: { type: \"Point\", coordinates: [%f,%f] } } }, owner:%s, category:%s }",
                                Shape.ATTRIBUTE_LOCATION, // location attribute
                                current.parent.getLocation().getCoordinates()[0][0], // lat
                                current.parent.getLocation().getCoordinates()[0][1], // long
                                owner,
                                categoryName
                        ),
                        // projection
                        null,
                        // limit
                        FIND_POC_LIMIT
                );

                while (poiItr.hasNext()) {
                    current.children.add(new RouteTree(poiItr.next()));
                }
            }

            // reset current site to house and create starting waypoint (as 'previous')
            Waypoint rootWaypoint = new Waypoint(house);

            // add main waypoint as path
            house.addPath(trip, rootWaypoint);

            // for each anchor, process paths, else just process from house
            addWaypoints(house, rootWaypoint, trip.getCategoryNames(), 0);
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
     * @param start
     * @param current
     * @param categoryNames
     * @param categoryNameIndex
     */
    public void addWaypoints(Coordinate start, Waypoint current, String[] categoryNames, int categoryNameIndex) {
        if (categoryNameIndex >= categoryNames.length) {
            Waypoint childWaypoint = new Waypoint(start, current);
            return;
        }

        if (!categorySiteMap.containsKey(categoryNames[categoryNameIndex])) {
            throw new IllegalStateException("Unable to find category data: " + categoryNames[categoryNameIndex]);
        }

        for (Site site : categorySiteMap.get(categoryNames[categoryNameIndex])) {
            Waypoint childWaypoint = new Waypoint(site, current);
            addWaypoints(start, childWaypoint, categoryNames, categoryNameIndex + 1);
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
        if (site.getName() == null || site.getName().isEmpty()) {
            site.setName(result.getFormatted_address());
        }
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
