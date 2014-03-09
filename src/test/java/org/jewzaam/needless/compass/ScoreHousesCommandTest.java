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
package org.jewzaam.needless.compass;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.jewzaam.mongo.model.geo.Point;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.jewzaam.needless.compass.calculator.Calculator;
import org.jewzaam.needless.compass.calculator.RouteCalculator;
import org.jewzaam.needless.compass.hystrix.ScoreHousesCommand;
import org.jewzaam.needless.compass.model.House;
import org.jewzaam.needless.compass.model.PointOfInterest;
import org.jewzaam.needless.compass.model.Route;
import org.jewzaam.needless.compass.model.Trip;

/**
 *
 * @author nmalik
 */
public class ScoreHousesCommandTest extends AbstractMongoTest {
    private static final String OWNER = "default";

    /**
     * setup data needed
     */
    @Before
    public void setup() {
        Route route = new Route();
        route.setOwner(OWNER);
        route.setRoute(new ArrayList<double[]>());
        route.initialize();
        route.setDistanceMeters(10000l);
        route.setTimeSeconds(6000l);

        {
            House house = new House();
            house.initialize();
            house.setOwner(OWNER);
            house.setAddress("8004 Lake Shore Drive, Garner, NC 27529, USA");
            house.getLocation().setCoordinate(new double[]{35.662709, -78.63383600000002});
            route.getRoute().add(house.getLocation().getCoordinate());
            crud.upsert(PointOfInterest.COLLECTION, house);
        }

        {
            PointOfInterest poi = new PointOfInterest();
            poi.initialize();
            poi.setOwner(OWNER);
            poi.addCategory("coffee");
            poi.setName("Aversboro Coffee");
            poi.setAddress("1401 Aversboro Road, Garner, NC 27529, USA");
            poi.getLocation().setCoordinate(new double[]{35.694968, -78.616082});
            route.getRoute().add(poi.getLocation().getCoordinate());
            crud.upsert(PointOfInterest.COLLECTION, poi);
        }

        // add house again to route by copying first element to end
        route.getRoute().add(route.getRoute().get(0));

        {
            Trip trip = new Trip();
            trip.initialize();
            trip.setOwner(OWNER);
            trip.setCategoryNames(new String[]{"coffee"});
            trip.setFrequency(new BigDecimal("1"));
            trip.setName("Coffee Shop");
            crud.upsert(Trip.COLLECTION, trip);
        }

        // save the route (acts as a cached route)
        crud.upsert(Route.COLLECTION, route);

        crud.createIndex2dsphere(PointOfInterest.COLLECTION, Point.ATTRIBUTE_LOCATION);
    }

    @Test
    public void score() {
        List<Calculator> calculators = new ArrayList<>();
        calculators.add(new RouteCalculator(crud));

        ScoreHousesCommand cmd = new ScoreHousesCommand(OWNER, crud, calculators);
        String output = cmd.execute();
        Assert.assertNotNull(output);
        System.out.println("*********** " + output);
    }
}
