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
public class Result {
    private Geometry geometry;
    private String types[];
    private String formatted_address;

    /**
     * @return the geometry
     */
    public Geometry getGeometry() {
        return geometry;
    }

    /**
     * @param geometry the geometry to set
     */
    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    /**
     * @return the types
     */
    public String[] getTypes() {
        return types;
    }

    /**
     * @param types the types to set
     */
    public void setTypes(String[] types) {
        this.types = types;
    }

    /**
     * @return the formatted_address
     */
    public String getFormatted_address() {
        return formatted_address;
    }

    /**
     * @param formatted_address the formatted_address to set
     */
    public void setFormatted_address(String formatted_address) {
        this.formatted_address = formatted_address;
    }
}
