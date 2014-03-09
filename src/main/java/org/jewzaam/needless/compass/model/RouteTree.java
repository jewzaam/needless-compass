/*
 * Copyright (C) 2014 Naveen Malik
 *
 * Needless Compass is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Needless Compass is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Needless Compass.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.jewzaam.needless.compass.model;

import java.util.ArrayList;
import java.util.List;

/**
 * A tree of routes.
 *
 * @author nmalik
 */
public class RouteTree {
    public final RouteTree parent;
    public final PointOfInterest poi;
    public final List<RouteTree> children = new ArrayList<>();

    public RouteTree(RouteTree parent, PointOfInterest poi) {
        this.parent = parent;
        this.poi = poi;
    }
}
