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
package org.jewzaam.needless.compass.hystrix;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import org.jewzaam.mongo.model.geo.Point;
import org.jewzaam.needless.compass.AbstractMongoTest;
import org.junit.Assert;
import org.junit.Test;
import org.jewzaam.needless.compass.calculator.Calculator;
import org.jewzaam.needless.compass.calculator.RouteCalculator;
import org.jewzaam.needless.compass.model.House;
import org.jewzaam.needless.compass.model.PointOfInterest;
import org.jewzaam.needless.compass.model.Route;
import org.jewzaam.needless.compass.model.Trip;
import org.jewzaam.needless.compass.osrm.OsrmRouteCommand;
import org.junit.Before;
import org.junit.Ignore;

/**
 *
 * @author nmalik
 */
public class ScoreHousesCommandTest extends AbstractMongoTest {
    private static final String OWNER = "default";
    private final static Gson GSON = new Gson();

    public static final String loadResource(String resourceName) throws IOException {
        StringBuilder buff = new StringBuilder();

        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceName);
                InputStreamReader isr = new InputStreamReader(is, Charset.defaultCharset());
                BufferedReader reader = new BufferedReader(isr)) {
            String line;
            while ((line = reader.readLine()) != null) {
                buff.append(line).append("\n");
            }
        }

        return buff.toString();
    }

    @Before
    @Override
    public void setup() {
        super.setup();
        OsrmRouteCommand.disable();
    }

    private void loadDataForClass(String name, String collectionName, Class clazz) throws IOException {
        String resourceName = String.format("%s/%s/%s.json",
                getClass().getSimpleName(),
                name,
                clazz.getSimpleName());
        String json = loadResource(resourceName);

        // just get a generic list, don't care what it looks like for upsert later, so can be generic map
        List data = GSON.fromJson(json, List.class);

        for (Object obj : data) {
            crud.upsert(collectionName, obj);
        }
    }

    /**
     * setup data needed
     */
    public void loadData(String name) throws IOException {
        loadDataForClass(name, PointOfInterest.COLLECTION, PointOfInterest.class);
        loadDataForClass(name, Trip.COLLECTION, Trip.class);
        loadDataForClass(name, Route.COLLECTION, Route.class);

        // create 2dsphere index needed for using geospatial query
        crud.createIndex2dsphere(PointOfInterest.COLLECTION, Point.ATTRIBUTE_LOCATION);
    }

    @Test
    public void simple() throws IOException {
        loadData("simple");
        List<Calculator> calculators = new ArrayList<>();
        calculators.add(new RouteCalculator(crud));

        ScoreHousesCommand cmd = new ScoreHousesCommand(OWNER, crud, calculators);
        List<House> houses = cmd.execute();
        Assert.assertNotNull(houses);
        Assert.assertEquals(1, houses.size());
        Assert.assertEquals(100, houses.get(0).getScores().get(RouteCalculator.CALCULATOR_NAME).intValue());
    }

    @Test
    public void twoHouses() throws IOException {
        loadData("twoHouses");
        List<Calculator> calculators = new ArrayList<>();
        calculators.add(new RouteCalculator(crud));

        ScoreHousesCommand cmd = new ScoreHousesCommand(OWNER, crud, calculators);
        List<House> houses = cmd.execute();
        Assert.assertNotNull(houses);
        Assert.assertEquals(2, houses.size());
        Assert.assertEquals(0, houses.get(0).getScores().get(RouteCalculator.CALCULATOR_NAME).intValue());
        Assert.assertEquals(100, houses.get(1).getScores().get(RouteCalculator.CALCULATOR_NAME).intValue());
    }

    @Test
    @Ignore
    public void fourHouses() throws IOException {
        loadData("fourHouses");
        List<Calculator> calculators = new ArrayList<>();
        calculators.add(new RouteCalculator(crud));

        ScoreHousesCommand cmd = new ScoreHousesCommand(OWNER, crud, calculators);
        List<House> houses = cmd.execute();
        Assert.assertNotNull(houses);
        Assert.assertEquals(4, houses.size());
        Assert.assertEquals(0, houses.get(0).getScores().get(RouteCalculator.CALCULATOR_NAME).intValue());
        Assert.assertEquals(20, houses.get(1).getScores().get(RouteCalculator.CALCULATOR_NAME).intValue());
        Assert.assertEquals(70, houses.get(2).getScores().get(RouteCalculator.CALCULATOR_NAME).intValue());
        Assert.assertEquals(100, houses.get(3).getScores().get(RouteCalculator.CALCULATOR_NAME).intValue());
    }
}
