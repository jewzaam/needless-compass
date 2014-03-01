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
package org.namal.needless.compass.model.cloudmade;

import java.util.List;

/**
 * http://cloudmade.com/documentation/routing#JS-response-JSON
 *
 * @author nmalik
 */
public class Route {
    /**
     * The API version.
     */
    double version;
    /**
     * 0 - OK, 1 - Error
     */
    int status;
    /**
     * Error message string.
     */
    String status_message;

    RouteSummary route_summary;

    /**
     * Array of nodes from start to end in 4326 projection.
     */
    double[][] route_geometry;

    List<RouteInstruction> route_instructions;
}
