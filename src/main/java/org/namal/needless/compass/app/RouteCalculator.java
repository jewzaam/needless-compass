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
package org.namal.needless.compass.app;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;
import org.namal.mongo.MongoCRUD;
import org.namal.mongo.model.geo.Shape;
import org.namal.needless.compass.model.House;
import org.namal.needless.compass.model.PointOfInterest;
import org.namal.needless.compass.model.Route;
import org.namal.needless.compass.model.RouteTree;
import org.namal.needless.compass.model.Trip;
import org.namal.needless.compass.osrm.OsrmRouteCommand;
import org.namal.needless.compass.util.RouteTimeComparator;

/**
 *
 * @author jewzaam
 */
public class RouteCalculator extends Calculator {
    private static final int FIND_POI_LIMIT = 3;

    private final MongoCRUD crud;

    public RouteCalculator(MongoCRUD crud) {
        this.crud = crud;
    }

    public String name() {
        return "ROUTE";
    }

    @Override
    protected long score(String owner, House house) throws IOException {
        // get all trips
        Iterator<Trip> tripItr = crud.find(
                // collection
                Trip.COLLECTION,
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
                PointOfInterest.COLLECTION,
                // query
                String.format("{owner:%s,categories:%s}", owner, House.CATEGORY_HOUSE),
                // projection
                null
        );

        // process the house
        return process(owner, house, trips);
    }

    private long process(String owner, House house, List<Trip> trips) throws IOException {
        // for each trip find closes sites that match using global limit and get a total score
        long score = 0;
        for (Trip trip : trips) {
            RouteTree tree = process(owner, house, trip);
            score += findLowestCost(owner, tree);
        }

        return score;
    }

    private RouteTree process(String owner, House house, Trip trip) {
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
        Iterator<PointOfInterest> poiItr = crud.find(
                // collection
                PointOfInterest.COLLECTION,
                // query
                String.format("{%s: { $near: { $geometry: { type: \"Point\", coordinates: [%f,%f] } } }, owner:%s, category:%s }",
                        Shape.ATTRIBUTE_LOCATION, // location attribute
                        parent.poi.getLocation().getCoordinates()[0][0], // lat
                        parent.poi.getLocation().getCoordinates()[0][1], // long
                        owner,
                        categoryName
                ),
                // projection
                null,
                // limit
                FIND_POI_LIMIT
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

        Set<RouteTree> leafs = new TreeSet<>();
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

        for (RouteTree leaf : leafs) {
            // 2. for each leaf find the route
            LinkedList<double[]> coordinates = new LinkedList<>();

            // for each leaf node collect the locations as coordinates
            coordinates.addFirst(leaf.poi.getLocation().getCoordinates()[0]);

            // grab the coordinates of each ancestor
            RouteTree current = leaf;
            while (current.parent != null) {
                coordinates.addFirst(current.parent.poi.getLocation().getCoordinates()[0]);
                current = current.parent;
            }

            // 3.1. if route exists, load it from datastore
            Route search = new Route();
            search.setOwner(owner);
            search.setRoute(coordinates);

            // see if route is saved already
            Route route = crud.findOne(Route.COLLECTION, search);

            // 3.2. if route does not exist, create route and save
            if (null == route) {
                // use coordinates to get a route
                route = new OsrmRouteCommand(coordinates).execute();

                // set who/when
                route.setOwner(owner);
                route.setCreatedBy(owner);
                route.setLastUpdatedBy(owner);
                Date sysdate = new Date();
                route.setCreationDate(sysdate);
                route.setLastUpdateDate(sysdate);

                // save route
                crud.upsert(Route.COLLECTION, route);
            }

            routes.add(route);
        }

        // 4. find lowest "cost" route (TODO incoproate user preferences eventually...)
        Collections.sort(routes, new RouteTimeComparator());

        // first element is lowest "cost", return it
        return routes.isEmpty() ? null : routes.get(0).getTimeSeconds();
    }
}
