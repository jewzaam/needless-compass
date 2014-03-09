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

import java.util.List;
import org.jewzaam.mongo.model.MongoObject;

/**
 * Information about the route between two points with optional waypoints. Note
 * this is not owned by anybody. If two users happen to request the same route
 * we can reuse it.
 *
 * @author nmalik
 */
public class Route extends MongoObject {
    public static final String COLLECTION = "route";

    /**
     * Raw API request made to collect this information.
     */
    private String api_request;

    /**
     * The coordinates for the route in order.
     */
    private List<double[]> route;

    /**
     * Distance in meters.
     */
    private Long distance_meters;

    /**
     * Estimated time in seconds.
     */
    private Long time_seconds;

    /**
     * @return the api_request
     */
    public String getApiRequest() {
        return api_request;
    }

    /**
     * @param api_request the api_request to set
     */
    public void setApiRequest(String api_request) {
        this.api_request = api_request;
    }

    /**
     * @return the route
     */
    public List<double[]> getRoute() {
        return route;
    }

    /**
     * @param route the route to set
     */
    public void setRoute(List<double[]> route) {
        this.route = route;
    }

    /**
     * @return the distance_meters
     */
    public Long getDistanceMeters() {
        return distance_meters;
    }

    /**
     * @param distance_meters the distance_miles to set
     */
    public void setDistanceMeters(Long distance_meters) {
        this.distance_meters = distance_meters;
    }

    /**
     * @return the time_seconds
     */
    public Long getTimeSeconds() {
        return time_seconds;
    }

    /**
     * @param time_seconds the time_seconds to set
     */
    public void setTimeSeconds(Long time_seconds) {
        this.time_seconds = time_seconds;
    }
}
