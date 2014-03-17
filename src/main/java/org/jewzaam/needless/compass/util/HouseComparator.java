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
package org.jewzaam.needless.compass.util;

import java.util.Comparator;
import org.jewzaam.needless.compass.model.House;

/**
 *
 * @author jewzaam
 */
public class HouseComparator implements Comparator<House> {
    private final String sort;
    private final int dir;

    public HouseComparator(String sort, String dir) {
        this.sort = sort;
        if (dir == null || "asc".equalsIgnoreCase(dir)) {
            this.dir = 1;
        } else if ("desc".equalsIgnoreCase(dir)) {
            this.dir = -1;
        } else {
            this.dir = 1;
        }
    }

    @Override
    public int compare(House t, House t1) {
        if (null == t || null == t.getScores().get(sort)) {
            return dir * -1;
        }
        if (null == t1 || null == t1.getScores().get(sort)) {
            return dir * 1;
        }
        if (t.getScores().get(sort) < t1.getScores().get(sort)) {
            return dir * -1;
        } else if (t.getScores().get(sort) == t1.getScores().get(sort)) {
            return 0;
        } else {
            return dir * 1;
        }
    }
}
