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
package org.jewzaam.needless.compass.util;

import org.jewzaam.needless.compass.util.RouteDistanceComparator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.jewzaam.needless.compass.model.Route;

/**
 *
 * @author jewzaam
 */
public class RouteDistanceComparatorTest {

    private Route newRoute(long distance) {
        Route r = new Route();
        r.setDistanceMeters(distance);
        return r;
    }

    @Test
    public void sort() {
        List<Route> routes = new ArrayList<>();

        routes.add(newRoute(0));
        routes.add(newRoute(-1));
        routes.add(newRoute(2));
        routes.add(newRoute(0));
        routes.add(newRoute(1));

        Collections.sort(routes, new RouteDistanceComparator());

        boolean success = true;

        long previous = -1000;
        for (Route route : routes) {
            success &= (previous <= route.getDistanceMeters());
            previous = route.getDistanceMeters();
        }

        Assert.assertTrue(success);
    }
}
