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
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.jewzaam.mongo.MongoCRUD;
import org.jewzaam.mongo.Result;
import org.jewzaam.needless.compass.calculator.Calculator;
import org.jewzaam.needless.compass.calculator.RouteCalculator;
import org.jewzaam.needless.compass.google.GoogleGeocodeCommand;
import org.jewzaam.needless.compass.hystrix.ScoreHousesCommand;
import org.jewzaam.needless.compass.model.House;
import org.jewzaam.needless.compass.model.PointOfInterest;
import org.jewzaam.needless.compass.model.Trip;

/**
 * Simple service to test out NewRelic custom metrics.
 *
 * @author nmalik
 */
@Path("/nc")
public class RestResource {
    private static final MongoCRUD crud = new MongoCRUD("needlesscompass");

    @POST
    @Path("/poi")
    @Produces(MediaType.APPLICATION_JSON)
    public String addPoitnOfInterest(String jsonString) {
        // use geocode servie to enrich point of interest
        PointOfInterest poi = new Gson().fromJson(jsonString, PointOfInterest.class);
        poi = new GoogleGeocodeCommand(poi).execute();
        poi.initialize(); // set when values

        // set _id to latitude|longitude
        if (poi.getLocation().getCoordinate() != null) {
            poi.setId(String.format("%s|%f|%f",
                    poi.getOwner(),
                    poi.getLocation().getCoordinate()[0],
                    poi.getLocation().getCoordinate()[1])
            );
        }

        Result result = crud.upsert(PointOfInterest.COLLECTION, poi);
        return result.isError() ? "false" : "true";
    }

    @POST
    @Path("/trip")
    @Produces(MediaType.APPLICATION_JSON)
    public String addTrip(String jsonString) {
        Trip trip = new Gson().fromJson(jsonString, Trip.class);
        trip.initialize(); // set when values
        // TODO consider not converting to java object and blinding persist.. could be risky? (api not exposed anyway)
        Result result = crud.upsert(Trip.COLLECTION, trip);
        return result.isError() ? "false" : "true";
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
            trip.initialize(); // set when values
            Result result = crud.upsert(Trip.COLLECTION, trip);
            error &= result.isError();
        }
        return error ? "false" : "true";
    }
    
    @GET
    @Path("/scores")
    @Produces(MediaType.APPLICATION_JSON)
    public String getScores() {
        List<Calculator> calculators = new ArrayList<>();
        calculators.add(new RouteCalculator(crud));
        List<House> houses = new ScoreHousesCommand("default", crud, calculators).execute();
        return new Gson().toJson(houses);
    }
}
