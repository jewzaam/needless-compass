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

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.jewzaam.mongo.MongoCRUD;
import org.jewzaam.needless.compass.calculator.Calculator;
import org.jewzaam.needless.compass.model.House;
import org.jewzaam.needless.compass.model.PointOfInterest;
import org.jewzaam.needless.compass.model.Score;

/**
 *
 * @author nmalik
 */
public class ScoreHousesCommand extends HystrixCommand<List<House>> {
    private final String owner;
    private final MongoCRUD crud;
    private final List<Calculator> calculators;

    public ScoreHousesCommand(String owner, MongoCRUD crud, List<Calculator> calculators) {
        super(HystrixCommand.Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("ScoreHouses"))
                .andCommandKey(HystrixCommandKey.Factory.asKey("ScoreHouses"))
        );
        this.owner = owner;
        this.crud = crud;
        this.calculators = calculators;
    }

    @Override
    protected List<House> run() throws Exception {
        Iterator<House> houseItr = crud.find(
                // collection
                PointOfInterest.COLLECTION,
                // query
                String.format("{owner:'%s',categories:{$in: ['%s']}}", owner, House.CATEGORY_HOUSE),
                // projection
                null,
                House.class
        );

        List<House> houses = new ArrayList<>();
        Set<House> toSave = new HashSet<>();

        // Run calculator for each house where that house doesn't already have a score.
        // Each calculator will collect min, max, and total scores.
        while (houseItr.hasNext()) {
            House house = houseItr.next();
            // process each house
            for (Calculator calculator : calculators) {
                if (house.getScores().get(calculator.name()) == null) {
                    long score = calculator.calculate(owner, house);
                    house.putScore(calculator.name(), new Score(score, null));
                    toSave.add(house);
                } else {
                    calculator.cache(house.getScores().get(calculator.name()).getRaw());
                }
            }
            houses.add(house);
        }

        // Scores are all collected but need to adjusted as percentages.
        for (Calculator calculator : calculators) {
            // score = 55, min = 5, max = 105, % is 50
            // score - min / (max - min)
            double range = calculator.max() - calculator.min();
            for (House house : houses) {
                // if range is 0 then the min and max are the same.. score is 100% then.
                Score score = house.getScores().get(calculator.name());
                long percentage = (long) (100 * (range == 0 ? 1 : (score.getRaw() - calculator.min()) / range));
                if (calculator.isLowerBetter() && range > 0) {
                    percentage = 100 - percentage;
                }
                if (score.getPercentage() == null || score.getPercentage() != percentage) {
                    score.setPercentage(percentage);
                    toSave.add(house);
                }
            }
        }

        // Save houses to save the scores.
        for (House house : toSave) {
            crud.upsert(PointOfInterest.COLLECTION, house);
        }

        // return json with the best scored house first
        return houses;
    }
}
