/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.namal.needless.compass;

import com.google.gson.Gson;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author nmalik
 */
public class NeedlessCompassApp {
    private Categories categories;
    private Houses houses;
    private Trips trips;

    private Map<String, Set<Category>> categoryMap;

    public NeedlessCompassApp() {

    }

    public void initialize() throws IOException {
        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("categories.json");
                InputStreamReader isr = new InputStreamReader(is, Charset.defaultCharset())) {
            Gson g = new Gson();
            categories = g.fromJson(isr, Categories.class);
            if (null == categories || categories.getCategories().length <= 0) {
                throw new IllegalStateException("No categories found!");
            }
        }
        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("houses.json");
                InputStreamReader isr = new InputStreamReader(is, Charset.defaultCharset())) {
            Gson g = new Gson();
            houses = g.fromJson(isr, Houses.class);
            if (null == houses || houses.getHouses().length <= 0) {
                throw new IllegalStateException("No houses found!");
            }
        }
        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("trips.json");
                InputStreamReader isr = new InputStreamReader(is, Charset.defaultCharset())) {
            Gson g = new Gson();
            trips = g.fromJson(isr, Trips.class);
            if (null == trips || trips.getTrips().length <= 0) {
                throw new IllegalStateException("No trips found!");
            }
        }

        categoryMap = new HashMap<>();

        for (Category category : categories.getCategories()) {
            if (!categoryMap.containsKey(category.getName())) {
                categoryMap.put(category.getName(), new HashSet<Category>());
            }
            categoryMap.get(category.getName()).add(category);
        }
    }

    public void process() {
        // for each house + trip find all categories that satisfy the waypoint needs and then compute final best (lowest) score
        for (House house : houses.getHouses()) {
            for (Trip trip : trips.getTrips()) {
                // reset current site to house and create starting waypoint (as 'previous')
                Waypoint currentWaypoint = new Waypoint(house.getSite());

                house.addPath(trip, currentWaypoint);

                addWaypoints(house, currentWaypoint, trip.getCategoryNames(), 0);
            }
            // got a bunch 'o data, now figure out a score for this house!
            // get the lowest score from each of the first children of the path's waypoint

            Map<Trip, Waypoint> paths = house.getPaths();
            double score = 0;
            for (Trip trip : paths.keySet()) {
                Waypoint root = paths.get(trip);
                double s = computeMinScore(root) * trip.getFrequency().doubleValue();
                score += s;
                // System.out.println(house.getSite().getName() + " | " + trip.getName() + " | " + s);
            }
            house.setScore(new Score(score));
        }

        Gson g = new Gson();
        System.out.println(g.toJson(houses));
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
            Waypoint childWaypoint = new Waypoint(house.getSite(), current);
            return;
        }

        if (!categoryMap.containsKey(categoryNames[categoryNameIndex])) {
            throw new IllegalStateException("Unable to find category data: " + categoryNames[categoryNameIndex]);
        }

        for (Category category : categoryMap.get(categoryNames[categoryNameIndex])) {
            for (Site categorySite : category.getSites()) {
                Waypoint childWaypoint = new Waypoint(categorySite, current);
                addWaypoints(house, childWaypoint, categoryNames, categoryNameIndex + 1);
            }
        }
    }

    public double computeMinScore(Waypoint wp) {
        Iterator<Waypoint> children = wp.getNext();
        double minScore = 0.0;
        while (children.hasNext()) {
            double s = computeMinScore(children.next());
            if (0 == minScore) {
                minScore = s;
            } else {
                minScore = Math.min(minScore, s);
            }
        }

        return minScore + wp.getScore().doubleValue();
    }

    public static void main(String[] args) {
        NeedlessCompassApp app = new NeedlessCompassApp();
        try {
            app.initialize();
            app.process();
        } catch (IOException e) {
            System.err.println("Failed to start application:");
            e.printStackTrace();;
        }
    }
}
