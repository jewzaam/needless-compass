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

    private final MongoCRUD crud = new MongoCRUD("needlesscompass");
    private final List<Calculator> calculators;

    public ScoreApplication(List<Calculator> scores) {
        this.calculators = scores;
    }

    public void initialize() throws IOException {
        crud.createIndex2dsphere(PointOfInterest.COLLECTION, Shape.ATTRIBUTE_LOCATION);
        crud.createIndex(PointOfInterest.COLLECTION, PointOfInterest.ATTRIBUTE_CATEGORIES);
    }

    public String process(String owner) {
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
        
        // collect scores for each house.  
        // for each calculator need to know:
        // * range of scores for all houses
        // * if low score is better

        while (houseItr.hasNext()) {
            House house = houseItr.next();
            // process each house
            long score = 0;
            for (Calculator calculator : calculators) {
                calculator.calculate(owner, house);
            }
            process(owner, house, trips);
            // and add it to the sorted set
            sortedHouses.add(house);
        }

        // return json with the best scored house first
        return new Gson().toJson(sortedHouses);
    }

    public static void main(String[] args) {
        ScoreApplication app = new ScoreApplication();
        try {
//            app.initialize();
//            System.out.println(app.process());
            House house = new House();
            house.setAddress("Raleigh NC");
            enrichSite(house);
            System.out.println(new Gson().toJson(house));
        } catch (IOException e) {
            System.err.println("Failed to start application:");
            e.printStackTrace();
        }
    }
}
