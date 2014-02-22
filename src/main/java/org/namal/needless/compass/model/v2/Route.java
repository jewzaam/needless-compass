/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.namal.needless.compass.model.v2;

import org.namal.mongo.model.MongoObject;

/**
 * Information about the route between two points with optional waypoints. Note this is not owned by anybody. If two
 * users happen to request the same route we can reuse it.
 *
 * @author nmalik
 */
public class Route extends MongoObject {

    /**
     * Raw API request made to collect this information.
     */
    private String api_request;

    /**
     * The source (start) location.
     */
    private double[] source;

    /**
     * Any waypoints to stop at between source and destination.
     */
    private double[][] waypoints;

    /**
     * The destination (end) location.
     */
    private double[] destination;

    /**
     * Distance in miles.
     */
    private long distance_miles;

    /**
     * Estimated time in seconds.
     */
    private long time_seconds;

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
     * @return the source
     */
    public double[] getSource() {
        return source;
    }

    /**
     * @param source the source to set
     */
    public void setSource(double[] source) {
        this.source = source;
    }

    /**
     * @return the waypoints
     */
    public double[][] getWaypoints() {
        return waypoints;
    }

    /**
     * @param waypoints the waypoints to set
     */
    public void setWaypoints(double[][] waypoints) {
        this.waypoints = waypoints;
    }

    /**
     * @return the destination
     */
    public double[] getDestination() {
        return destination;
    }

    /**
     * @param destination the destination to set
     */
    public void setDestination(double[] destination) {
        this.destination = destination;
    }

    /**
     * @return the distance_miles
     */
    public long getDistance_miles() {
        return distance_miles;
    }

    /**
     * @param distance_miles the distance_miles to set
     */
    public void setDistanceMiles(long distance_miles) {
        this.distance_miles = distance_miles;
    }

    /**
     * @return the time_seconds
     */
    public long getTimeSeconds() {
        return time_seconds;
    }

    /**
     * @param time_seconds the time_seconds to set
     */
    public void setTimeSeconds(long time_seconds) {
        this.time_seconds = time_seconds;
    }
}
