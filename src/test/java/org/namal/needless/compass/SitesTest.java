/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.namal.needless.compass;

import com.google.gson.Gson;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author nmalik
 */
public class SitesTest {
    @Test
    public void gsonTest() {
        Gson g = new Gson();
        Sites sites = g.fromJson("{\"sites\":[{\"name\":\"Foo\",\"category\":\"grocery\",\"coordinates\":[{\"latitude\":1,\"longitude\":2.58958,\"address\":\"asdf\"}]}]}", Sites.class);

        Assert.assertNotNull(sites);
        Assert.assertNotNull(sites.getSites());
        Assert.assertEquals(1, sites.getSites().length);

        Site s = sites.getSites()[0];

        Assert.assertEquals("Foo", s.getName());
        Assert.assertEquals("grocery", s.getCategory());
        Assert.assertNotNull(s.getCoordinates());
        Assert.assertEquals(1, s.getCoordinates().length);
        Assert.assertEquals(new BigDecimal("1"), s.getCoordinates()[0].getLatitude());
        Assert.assertEquals(new BigDecimal("2.58958"), s.getCoordinates()[0].getLongitude());
        Assert.assertEquals("asdf", s.getCoordinates()[0].getAddress());
    }

    @Test
    public void loadSites() throws IOException {
        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("sites.json");
                InputStreamReader isr = new InputStreamReader(is, Charset.defaultCharset())) {
            Gson g = new Gson();
            Sites sites = g.fromJson(isr, Sites.class);
            Assert.assertNotNull(sites);
            Assert.assertNotNull(sites.getSites());
            Assert.assertTrue(sites.getSites().length > 0);
        }
    }
}
