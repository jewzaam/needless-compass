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
package org.namal.needless.compass.rest;

import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.namal.mongo.MongoCRUD;
import org.namal.mongo.Result;
import org.namal.needless.compass.calculator.Calculator;
import org.namal.needless.compass.calculator.RouteCalculator;
import org.namal.needless.compass.google.GoogleGeocodeCommand;
import org.namal.needless.compass.hystrix.ScoreHousesCommand;
import org.namal.needless.compass.model.PointOfInterest;
import org.namal.needless.compass.model.Trip;

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

        // set _id to latitude|longitude
        if (poi.getLocation().getCoordinates() != null) {
            poi.setId(String.format("%s|%f|%f",
                    poi.getOwner(),
                    poi.getLocation().getCoordinates()[0],
                    poi.getLocation().getCoordinates()[1])
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
        // TODO consider not converting to java object and blinding persist.. could be risky? (api not exposed anyway)
        Result result = crud.upsert(Trip.COLLECTION, trip);
        return result.isError() ? "false" : "true";
    }

    @GET
    @Path("/scores")
    @Produces(MediaType.APPLICATION_JSON)
    public String getScores() {
        List<Calculator> calculators = new ArrayList<>();
        calculators.add(new RouteCalculator(crud));
        return new ScoreHousesCommand("default", crud, calculators).execute();
    }
}
