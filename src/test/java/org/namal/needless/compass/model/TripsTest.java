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
package org.namal.needless.compass.model;

import org.namal.needless.compass.model.Trips;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author nmalik
 */
public class TripsTest {
    @Test
    public void load() throws IOException {
        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("trips.json");
                InputStreamReader isr = new InputStreamReader(is, Charset.defaultCharset())) {
            Gson g = new Gson();
            Trips trips = g.fromJson(isr, Trips.class);
            Assert.assertNotNull(trips);
            Assert.assertNotNull(trips.getTrips());
            Assert.assertTrue(trips.getTrips().length > 0);
        }
    }
}
