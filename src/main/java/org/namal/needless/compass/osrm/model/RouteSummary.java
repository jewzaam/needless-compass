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
package org.namal.needless.compass.osrm.model;

/**
 * https://github.com/DennisOSRM/Project-OSRM/wiki/Output-json
 *
 * @author nmalik
 */
public class RouteSummary {
    /**
     * Distance in meters.
     */
    private long total_distance;

    /**
     * Estimated time in seconds.
     */
    private long total_time;

    /**
     * Name of the start point of the route.
     */
    private String start_point;

    /**
     * Name of the end point of the route.
     */
    private String end_point;

    /**
     * Distance in meters.
     *
     * @return the total_distance
     */
    public long getTotalDistance() {
        return total_distance;
    }

    /**
     * @param total_distance the total_distance to set
     */
    public void setTotalDistance(long total_distance) {
        this.total_distance = total_distance;
    }

    /**
     * Estimated time in seconds.
     *
     * @return the total_time
     */
    public long getTotalTime() {
        return total_time;
    }

    /**
     * @param total_time the total_time to set
     */
    public void setTotalTime(long total_time) {
        this.total_time = total_time;
    }

    /**
     * @return the start_point
     */
    public String getStartPoint() {
        return start_point;
    }

    /**
     * @param start_point the start_point to set
     */
    public void setStartPoint(String start_point) {
        this.start_point = start_point;
    }

    /**
     * @return the end_point
     */
    public String getEndPoint() {
        return end_point;
    }

    /**
     * @param end_point the end_point to set
     */
    public void setEndPoint(String end_point) {
        this.end_point = end_point;
    }
}
