/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.namal.needless.compass.model.cloudmade;

/**
 * http://cloudmade.com/documentation/routing#JS-response-JSON
 *
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
     * Transit points if they are present in request.
     */
    private double[][] transit_points;

    /**
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

    /**
     * @return the transit_points
     */
    public double[][] getTransitPoints() {
        return transit_points;
    }

    /**
     * @param transit_points the transit_points to set
     */
    public void setTransitPoints(double[][] transit_points) {
        this.transit_points = transit_points;
    }
}
