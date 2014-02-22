/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.namal.needless.compass.model.v2;

import java.util.HashSet;
import java.util.Set;
import org.namal.mongo.model.geo.Shape;

/**
 *
 * @author nmalik
 */
public class PointOfInterest extends Shape {
    private final Set<String> categories = new HashSet<>();
    private String name;
    private String address;

    public void addCategory(String category) {
        categories.add(category);
    }

    public void removeCategory(String category) {
        categories.remove(category);
    }

    public Set<String> getCategories() {
        return categories;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }
}
