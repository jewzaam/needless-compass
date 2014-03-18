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
package org.jewzaam.needless.compass.calculator;

import org.jewzaam.needless.compass.model.House;

/**
 * Generic interface for any scoring.
 *
 * @author jewzaam
 */
public abstract class Calculator {
    private long min = Long.MAX_VALUE;
    private long max = Long.MIN_VALUE;
    private long count = 0l;

    /**
     * Runs the calculation. If a calculation isn't run but you want to account for a cached value use the cache() api.
     *
     * @param owner
     * @param house
     * @return
     * @throws java.lang.Exception
     */
    public long calculate(String owner, House house) throws Exception {
        long score = score(owner, house);
        cache(score);
        return score;
    }

    public void cache(long score) {
        count++;

        if (score < min) {
            min = score;
        }
        if (score > max) {
            max = score;
        }
    }

    /**
     * The minimum value cached.
     *
     * @return
     */
    public long min() {
        return min;
    }

    /**
     * The maximum value cached.
     *
     * @return
     */
    public long max() {
        return max;
    }

    /**
     * The total number of values cached.
     *
     * @return
     */
    public long count() {
        return count;
    }

    /**
     * The name of the calculator.
     *
     * @return
     */
    public abstract String name();

    /**
     * Do the actual calculation.
     *
     * @param owner
     * @param house
     * @return
     * @throws Exception
     */
    protected abstract long score(String owner, House house) throws Exception;

    /**
     * If a lower score is better return true, else return false.
     *
     * @return
     */
    public abstract boolean isLowerBetter();
}
