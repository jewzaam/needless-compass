/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.namal.needless.compass.model.v2;

import java.util.ArrayList;
import java.util.List;

/**
 * A tree of routes.
 *
 * @author nmalik
 */
public class RouteTree {
    public PointOfInterest parent;
    public final List<RouteTree> children = new ArrayList<>();

    public RouteTree(PointOfInterest poi) {
        parent = poi;
    }
}
