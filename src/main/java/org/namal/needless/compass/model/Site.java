/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.namal.needless.compass.model;

import java.math.BigDecimal;

/**
 *
 * @author nmalik
 */
public class Site extends Coordinate {
    private String objectOwner = "default";
    private String name;
    private String[] categories;
    private String address;
    private BigDecimal rating;

    /**
     * @return the address
     */
    public String getAddress() {
        return address;
    }

    /**
     * @param address the address to set
     */
    public void setAddress(String address) {
        this.address = address;
    }

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
     * @return the categories
     */
    public String[] getCategories() {
        return categories;
    }

    /**
     * @param categories the categories to set
     */
    public void setCategories(String[] categories) {
        this.categories = categories;
    }

    /**
     * @return the objectOwner
     */
    public String getObjectOwner() {
        return objectOwner;
    }

    /**
     * @param objectOwner the objectOwner to set
     */
    public void setObjectOwner(String objectOwner) {
        this.objectOwner = objectOwner;
    }

    /**
     * @return the rating
     */
    public BigDecimal getRating() {
        return rating;
    }

    /**
     * @param rating the rating to set
     */
    public void setRating(BigDecimal rating) {
        this.rating = rating;
    }
}
