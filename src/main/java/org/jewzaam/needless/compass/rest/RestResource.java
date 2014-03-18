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
package org.jewzaam.needless.compass.rest;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.jewzaam.mongo.MongoCRUD;
import org.jewzaam.mongo.Result;
import org.jewzaam.mongo.model.geo.Point;
import org.jewzaam.needless.compass.calculator.Calculator;
import org.jewzaam.needless.compass.calculator.RouteCalculator;
import org.jewzaam.needless.compass.google.GoogleGeocodeCommand;
import org.jewzaam.needless.compass.hystrix.ScoreHousesCommand;
import org.jewzaam.needless.compass.model.House;
import org.jewzaam.needless.compass.model.PointOfInterest;
import org.jewzaam.needless.compass.model.Route;
import org.jewzaam.needless.compass.model.Trip;
import org.jewzaam.needless.compass.util.HouseComparator;

/**
 * Simple service to test out NewRelic custom metrics.
 *
 * @author nmalik
 */
@Path("/nc")
public class RestResource {
    private static final MongoCRUD crud;

    static {
        crud = new MongoCRUD("needlesscompass");
        crud.createIndex2dsphere(PointOfInterest.COLLECTION, Point.ATTRIBUTE_LOCATION);
    }

    private String get(String owner, String collectionName) {
        if (owner == null || owner.isEmpty()) {
            owner = "default";
        }

        Iterator<Map> itr = crud.find(
                // collection
                collectionName,
                // query
                String.format("{owner:%s}", owner),
                // projection
                null,
                Map.class
        );

        StringBuilder buff = new StringBuilder("[");
        while (itr.hasNext()) {
            Map m = itr.next();
            buff.append(crud.getConverter().toJson(m));
            if (itr.hasNext()) {
                buff.append(",");
            }
        }
        buff.append("]");

        return buff.toString();
    }

    @GET
    @Path("/poi")
    @Produces(MediaType.APPLICATION_JSON)
    public String getPointsOfInterest(@QueryParam("owner") String owner) {
        return get(owner, PointOfInterest.COLLECTION);
    }

    @GET
    @Path("/trip")
    @Produces(MediaType.APPLICATION_JSON)
    public String getTrips(@QueryParam("owner") String owner) {
        return get(owner, Trip.COLLECTION);
    }

    @GET
    @Path("/route")
    @Produces(MediaType.APPLICATION_JSON)
    public String getRoutes(@QueryParam("owner") String owner) {
        return get(owner, Route.COLLECTION);
    }

    @POST
    @Path("/pois")
    @Produces(MediaType.APPLICATION_JSON)
    public String addPointsOfInterest(String jsonString) {
        Type type = new TypeToken<List<PointOfInterest>>() {
        }.getType();
        List<PointOfInterest> pois = new Gson().fromJson(jsonString, type);

        boolean error = false;
        for (PointOfInterest poi : pois) {
            error &= !addPointOfInterest(poi);
        }
        return error ? "false" : "true";
    }

    @POST
    @Path("/poi")
    @Produces(MediaType.APPLICATION_JSON)
    public String addPointOfInterest(String jsonString) {
        PointOfInterest poi = new Gson().fromJson(jsonString, PointOfInterest.class);
        return String.valueOf(addPointOfInterest(poi));
    }

    private boolean addPointOfInterest(PointOfInterest poi) {
        // use geocode servie to enrich point of interest
        poi = new GoogleGeocodeCommand(poi).execute();
        poi.initialize(); // set when values

        if (poi.getOwner() == null || poi.getOwner().isEmpty()) {
            poi.setOwner("default");
        }

        // set _id to latitude|longitude
        if (poi.getLocation().getCoordinate() != null) {
            poi.setId(String.format("%s|%f|%f",
                    poi.getOwner(),
                    poi.getLocation().getCoordinate()[0],
                    poi.getLocation().getCoordinate()[1])
            );
        }

        Result result = crud.upsert(PointOfInterest.COLLECTION, poi);
        return !result.isError();
    }

    @POST
    @Path("/trip")
    @Produces(MediaType.APPLICATION_JSON)
    public String addTrip(String jsonString) {
        Trip trip = new Gson().fromJson(jsonString, Trip.class);
        return String.valueOf(addTrip(trip));
    }

    @POST
    @Path("/trips")
    @Produces(MediaType.APPLICATION_JSON)
    public String addTrips(String jsonString) {
        Type type = new TypeToken<List<Trip>>() {
        }.getType();
        List<Trip> trips = new Gson().fromJson(jsonString, type);
        // TODO consider not converting to java object and blinding persist.. could be risky? (api not exposed anyway)
        boolean error = false;
        for (Trip trip : trips) {
            error &= !addTrip(trip);
        }
        return error ? "false" : "true";
    }

    private boolean addTrip(Trip trip) {
        trip.initialize(); // set when values
        if (trip.getOwner() == null || trip.getOwner().isEmpty()) {
            trip.setOwner("default");
        }
        trip.setId(String.format("%s|%s", trip.getOwner(), trip.getName()));
        Result result = crud.upsert(Trip.COLLECTION, trip);
        return !result.isError();
    }

    /**
     * /scores?sort=ROUTE&dir=asc
     *
     * @param sort - the score to sort on [optional]
     * @param dir - the direction to sort: asc [default] or desc
     * @return
     */
    @GET
    @Path("/scores")
    @Produces(MediaType.APPLICATION_JSON)
    public String getScores(@QueryParam("sort") String sort, @QueryParam("dir") String dir) {
        List<Calculator> calculators = new ArrayList<>();
        calculators.add(new RouteCalculator(crud));
        List<House> houses = new ScoreHousesCommand("default", crud, calculators).execute();

        if (sort != null) {
            Collections.sort(houses, new HouseComparator(sort, dir));
        }

        return new Gson().toJson(houses);
    }
}
