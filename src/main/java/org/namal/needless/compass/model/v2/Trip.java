/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.namal.needless.compass.model.v2;

import java.math.BigDecimal;

/**
 * For a trip we only care about all legs of the journey. It may be that one possible path seems good but a longer trip
 * to subsequent waypoints might make it worse. ASSUMPTION: won't use same category in a way point more than once in a
 * trip.
 *
 * @author nmalik
 */
public class Trip {
    private String name;
    private String[] categoryNames;
    private BigDecimal frequency;

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
     * @return the categoryNames
     */
    public String[] getCategoryNames() {
        return categoryNames;
    }

    /**
     * @param categoryNames the categoryNames to set
     */
    public void setCategoryNames(String[] categoryNames) {
        this.categoryNames = categoryNames;
    }

    /**
     * @return the frequency
     */
    public BigDecimal getFrequency() {
        return frequency;
    }

    /**
     * @param frequency the frequency to set
     */
    public void setFrequency(BigDecimal frequency) {
        this.frequency = frequency;
    }
}
