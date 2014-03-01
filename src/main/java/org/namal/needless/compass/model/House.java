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
package org.namal.needless.compass.model;

/**
 *
 * @author nmalik
 */
public class House extends PointOfInterest implements Comparable<House> {
    public static final String CATEGORY_HOUSE = "house";

    public Integer score;

    @Override
    public int compareTo(House t) {
        if (null == t) {
            return 1;
        }
        if (score > t.score) {
            return 1;
        } else if (score == t.score) {
            return 0;
        } else {
            return -1;
        }
    }
}
