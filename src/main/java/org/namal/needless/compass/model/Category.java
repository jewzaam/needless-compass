/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.namal.needless.compass.model;

/**
 *
 * @author jewzaam
 */
public class Category {
    private String name;
    private boolean rateable;

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the rateable
     */
    public boolean isRateable() {
        return rateable;
    }

    /**
     * @param rateable the rateable to set
     */
    public void setRateable(boolean rateable) {
        this.rateable = rateable;
    }
}
