/*
 * Copyright (C) 2014 Naveen Malik
 *
 * Needless Compass is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Needless Compass is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Needless Compass.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.jewzaam.needless.compass.calculator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import org.jewzaam.mongo.MongoCRUD;
import org.jewzaam.mongo.model.geojson.Feature;
import org.jewzaam.needless.compass.model.House;
import org.jewzaam.needless.compass.model.PointOfInterest;
import org.jewzaam.needless.compass.model.Route;
import org.jewzaam.needless.compass.model.RouteTree;
import org.jewzaam.needless.compass.model.Trip;
import org.jewzaam.needless.compass.osrm.OsrmRouteCommand;
import org.jewzaam.needless.compass.util.RouteTimeComparator;

/**
 *
 * @author jewzaam
 */
public class RouteCalculator extends Calculator {
    public static final String CALCULATOR_NAME = "ROUTE";

    private static final int FIND_POI_LIMIT = 3;

    private final MongoCRUD crud;

    public RouteCalculator(MongoCRUD crud) {
        this.crud = crud;
    }

    @Override
    public String name() {
        return CALCULATOR_NAME;
    }

    @Override
    protected long score(String owner, House house) throws IOException {
        // process the house
        return processHouse(owner, house);
    }

    private long processHouse(String owner, House house) throws IOException {
        // get all trips
        Iterator<Trip> tripItr = crud.find(
                // collection
                Trip.COLLECTION,
                // query
                String.format("{owner:'%s'}", owner),
                // projection
                null,
                Trip.class
        );

        List<Trip> trips = new ArrayList<>();
        while (tripItr.hasNext()) {
            trips.add(tripItr.next());
        }
        // for each trip find closes sites that match using global limit and get a total score
        long score = 0;
        for (Trip trip : trips) {
            RouteTree tree = processTrip(owner, house, trip);
            score += findLowestCost(owner, tree);
        }

        return score;
    }

    private RouteTree processTrip(String owner, House house, Trip trip) {
        // for each trip prep a route and collect transient points
        RouteTree root = new RouteTree(null, house);
        List<RouteTree> processing = new ArrayList<>();

        // initialize processing list with root
        processing.add(root);

        for (String categoryName : trip.getCategoryNames()) {
            // add children to each node we need to process
            // for each tree processed collect its children as the next round to process
            List<RouteTree> newProcessing = new ArrayList<>();
            for (RouteTree tree : processing) {
                addChildren(owner, tree, categoryName);
                newProcessing.addAll(tree.children);
            }

            // simply replace the processing list to kick off the next round of processing
            processing = newProcessing;
        }

        // add the house to the end of each leaf
        for (RouteTree tree : processing) {
            tree.children.add(new RouteTree(tree, house));
        }

        return root;
    }

    /**
     * Add children to the parent by finding closest POI within limit.
     *
     * @param owner
     * @param parent
     * @param categoryName
     */
    private void addChildren(String owner, RouteTree parent, String categoryName) {
        // get POI near last coordinates
        double[] coordinates = parent.poi.getCoordinates();
        Iterator<PointOfInterest> poiItr = crud.find(
                // collection
                PointOfInterest.COLLECTION,
                // query
                String.format("{%s:{$near:{$geometry:{type:'Point',coordinates:[%f,%f]}}},owner:'%s',categories:{$in:['%s']}}",
                        Feature.ATTRIBUTE_GEOMETRY, // location attribute
                        coordinates[0], // lat
                        coordinates[1], // long
                        owner,
                        categoryName
                ),
                // projection
                null,
                // limit
                FIND_POI_LIMIT,
                PointOfInterest.class
        );

        while (poiItr.hasNext()) {
            parent.children.add(new RouteTree(parent, poiItr.next()));
        }
    }

    private long findLowestCost(String owner, RouteTree root) throws IOException {
        // ----------------------------------------------------------------------------
        // 1. find the leafs of the tree
        // 2. for each leaf find the route
        // 3.1. if route exists, load it from datastore
        // 3.2. if route does not exist, create route and save
        // 4. find lowest "cost" route (TODO incoproate user preferences eventually...)
        // ----------------------------------------------------------------------------

        List<RouteTree> leafs = new ArrayList<>();
        List<Route> routes = new ArrayList<>();

        // 1. find the leafs of the tree
        Stack<Iterator<RouteTree>> stack = new Stack<>();
        stack.push(root.children.iterator());

        while (!stack.empty() && stack.peek().hasNext()) {
            RouteTree child = stack.peek().next();
            if (child.children.isEmpty()) {
                leafs.add(child);
                stack.pop();
            } else {
                stack.push(child.children.iterator());
            }
        }

        // key=id
        Map<String, Route> searchRoutes = new HashMap<>();
        StringBuilder buff = new StringBuilder("{$or:[");

        Iterator<RouteTree> itr = leafs.iterator();
        while (itr.hasNext()) {
            RouteTree leaf = itr.next();

            // 2. for each leaf find the route
            LinkedList<double[]> coordinates = new LinkedList<>();

            // for each leaf node collect the locations as coordinates
            coordinates.addFirst((double[]) leaf.poi.getCoordinates());

            // grab the coordinates of each ancestor
            RouteTree current = leaf;
            while (current.parent != null) {
                coordinates.addFirst((double[]) current.parent.poi.getCoordinates());
                current = current.parent;
            }

            // 3.1. if route exists, load it from datastore
            Route search = new Route();
            search.setOwner(owner);
            search.setRoute(coordinates);

            // force id to be generated
            search.prepare();

            searchRoutes.put(search.getId(), search);

            buff.append("{_id:'").append(search.getId()).append("'}");

            if (itr.hasNext()) {
                buff.append(",");
            }
        }
        buff.append("]}");

        Iterator<Route> itrRoute = crud.find(Route.COLLECTION, buff.toString(), null, Route.class);

        while (itrRoute.hasNext()) {
            // for each route found add it to the routes list and remove it from the search list
            Route route = itrRoute.next();
            routes.add(route);
            searchRoutes.remove(route.getId());
        }

        // for the routes still in the search set we need to look things up
        for (Route search : searchRoutes.values()) {
            // use coordinates to get a route
            Route route = new OsrmRouteCommand(search.getRoute()).execute();

            // initialize
            route.initialize();
            route.setOwner(owner);
            route.setCreatedBy(owner);
            route.setLastUpdatedBy(owner);

            // save route
            crud.upsert(Route.COLLECTION, route);

            routes.add(route);
        }

        // 4. find lowest "cost" route (TODO incoproate user preferences eventually...)
        Collections.sort(routes, new RouteTimeComparator());

        // first element is lowest "cost", return it
        return routes.isEmpty() ? -1l : routes.get(0).getTimeSeconds();
    }

    @Override
    public boolean isLowerBetter() {
        return true;
    }
}
