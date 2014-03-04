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
public class Route {
    public static final int STATUS_SUCCESS = 0;

    /**
     * 0 - SUCCESSFUL
     */
    public int status;

    public String status_message;

    public RouteSummary route_summary;

    public org.namal.needless.compass.model.Route convert() {
        org.namal.needless.compass.model.Route output = new org.namal.needless.compass.model.Route();

        output.setDistanceMeters(route_summary.getTotalDistance());
        output.setTimeSeconds(route_summary.getTotalTime());

        return output;
    }
}
