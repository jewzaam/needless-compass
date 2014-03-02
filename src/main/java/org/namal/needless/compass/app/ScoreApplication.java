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

import com.google.gson.Gson;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import org.namal.mongo.MongoCRUD;
import org.namal.mongo.model.geo.Shape;
import org.namal.needless.compass.model.House;
import org.namal.needless.compass.model.PointOfInterest;

/**
 *
 * @author nmalik
 */
public class ScoreApplication {
    private static final MongoCRUD crud = new MongoCRUD("needlesscompass");
    private final List<Calculator> calculators;

    public ScoreApplication() {
        this.calculators = new ArrayList<>();
        calculators.add(new RouteCalculator(crud));
    }

    public ScoreApplication(List<Calculator> calculators) {
        this.calculators = calculators;
    }

    public void initialize() throws IOException {
        crud.createIndex2dsphere(PointOfInterest.COLLECTION, Shape.ATTRIBUTE_LOCATION);
        crud.createIndex(PointOfInterest.COLLECTION, PointOfInterest.ATTRIBUTE_CATEGORIES);
    }

    public String process(String owner) throws Exception {
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

        List<House> houses = new ArrayList<>();

        // run calculator for each house.
        // each calculator will collect min, max, and total scores.
        while (houseItr.hasNext()) {
            House house = houseItr.next();
            // process each house
            for (Calculator calculator : calculators) {
                house.setScore(calculator.name(), calculator.calculate(owner, house));
            }
            houses.add(house);
        }

        // scores are all collected but need to adjusted as percentages
        for (Calculator calculator : calculators) {
            // score = 55, min = 5, max = 105, % is 50
            // score - min / (max - min)
            for (House house : houses) {
                long score = 100 * ((house.getScores().get(calculator.name()) - calculator.min())
                        / (calculator.max() - calculator.min()));
                house.setScore(calculator.name(), score);
            }
        }

        // return json with the best scored house first
        return new Gson().toJson(sortedHouses);
    }

    public static void main(String[] args) throws Exception {
        ScoreApplication app = new ScoreApplication();
        try {
            app.initialize();
            System.out.println(app.process("test"));
        } catch (IOException e) {
            System.err.println("Failed to start application:");
            e.printStackTrace();
        }
    }
}
