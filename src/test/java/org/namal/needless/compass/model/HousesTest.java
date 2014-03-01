/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.namal.needless.compass.model;

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
public class HousesTest {
    @Test
    public void load() throws IOException {
        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("houses.json");
                InputStreamReader isr = new InputStreamReader(is, Charset.defaultCharset())) {
            Gson g = new Gson();
            Houses houses = g.fromJson(isr, Houses.class);
            Assert.assertNotNull(houses);
            Assert.assertNotNull(houses.getHouses());
            Assert.assertTrue(houses.getHouses().length > 0);
        }
    }
}
