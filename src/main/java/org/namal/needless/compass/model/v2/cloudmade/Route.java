/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.namal.needless.compass.model.v2.cloudmade;

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
