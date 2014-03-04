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

import java.math.BigDecimal;

/**
 * For a trip we only care about all legs of the journey. It may be that one
 * possible path seems good but a longer trip to subsequent waypoints might make
 * it worse. ASSUMPTION: won't use same category in a way point more than once
 * in a trip.
 *
 * @author nmalik
 */
public class Trip {
    public static final String COLLECTION = "trip";

    private String name;
    private String[] categoryNames;
    private BigDecimal frequency;

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the categoryNames
     */
    public String[] getCategoryNames() {
        return categoryNames;
    }

    /**
     * @param categoryNames the categoryNames to set
     */
    public void setCategoryNames(String[] categoryNames) {
        this.categoryNames = categoryNames;
    }

    /**
     * @return the frequency
     */
    public BigDecimal getFrequency() {
        return frequency;
    }

    /**
     * @param frequency the frequency to set
     */
    public void setFrequency(BigDecimal frequency) {
        this.frequency = frequency;
    }
}
