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
package org.jewzaam.needless.compass.model;

/**
 * A single score with raw value and a percentage value. Percentage value is for
 * convenience and should be recalculated as needed.
 *
 * @author nmalik
 */
public class Score {
    private Long raw;
    private Long percentage;

    public Score(Long raw, Long percentage) {
        this.raw = raw;
        this.percentage = percentage;
    }

    /**
     * @return the raw
     */
    public Long getRaw() {
        return raw;
    }

    /**
     * @param raw the raw to set
     */
    public void setRaw(Long raw) {
        this.raw = raw;
    }

    /**
     * @return the percentage
     */
    public Long getPercentage() {
        return percentage;
    }

    /**
     * @param percentage the percentage to set
     */
    public void setPercentage(Long percentage) {
        this.percentage = percentage;
    }
}
