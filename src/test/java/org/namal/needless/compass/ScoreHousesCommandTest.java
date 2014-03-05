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
package org.namal.needless.compass;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.namal.needless.compass.calculator.Calculator;
import org.namal.needless.compass.calculator.RouteCalculator;
import org.namal.needless.compass.hystrix.ScoreHousesCommand;
import org.namal.needless.compass.model.House;
import org.namal.needless.compass.model.PointOfInterest;
import org.namal.needless.compass.model.Trip;

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
        House house = new House();
        house.setOwner(OWNER);
        house.setAddress("8004 Lake Shore Drive, Garner, NC 27529, USA");
        house.getLocation().setCoordinate(new double[]{35.662709, -78.63383600000002});
        crud.upsert(PointOfInterest.COLLECTION, house);

        PointOfInterest poi = new PointOfInterest();
        poi.setOwner(OWNER);
        poi.addCategory("coffee");
        poi.setName("Aversboro Coffee");
        poi.setAddress("1401 Aversboro Road, Garner, NC 27529, USA");
        house.getLocation().setCoordinate(new double[]{35.694968, -78.616082});
        crud.upsert(PointOfInterest.COLLECTION, poi);

        Trip trip = new Trip();
        trip.setOwner(OWNER);
        trip.setCategoryNames(new String[]{"coffee"});
        trip.setFrequency(new BigDecimal("1"));
        trip.setName("Coffee Shop");
        crud.upsert(Trip.COLLECTION, trip);
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
