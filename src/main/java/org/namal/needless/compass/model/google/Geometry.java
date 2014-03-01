/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.namal.needless.compass.model.google;

/**
 *
 * @author jewzaam
 */
public class Geometry {
    private Location location;
    private String location_type;

    /**
     * @return the location
     */
    public Location getLocation() {
        return location;
    }

    /**
     * @param location the location to set
     */
    public void setLocation(Location location) {
        this.location = location;
    }

    /**
     * @return the location_type
     */
    public String getLocation_type() {
        return location_type;
    }

    /**
     * @param location_type the location_type to set
     */
    public void setLocation_type(String location_type) {
        this.location_type = location_type;
    }
}
