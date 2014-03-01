/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
