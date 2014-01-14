/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.namal.needless.compass;

import com.google.gson.Gson;
import java.math.BigDecimal;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author nmalik
 */
public class SiteTest {

    @Test
    public void gsonTest() {
        Gson g = new Gson();
        Site s = g.fromJson("{\"name\":\"Foo\",\"category\":\"grocery\",\"coordinates\":[{\"latitude\":1,\"longitude\":2.58958,\"address\":\"asdf\"}]}", Site.class);

        Assert.assertNotNull(s);

        Assert.assertEquals("Foo", s.getName());
        Assert.assertEquals("grocery", s.getCategory());
        Assert.assertNotNull(s.getCoordinates());
        Assert.assertEquals(1, s.getCoordinates().length);
        Assert.assertEquals(new BigDecimal("1"), s.getCoordinates()[0].getLatitude());
        Assert.assertEquals(new BigDecimal("2.58958"), s.getCoordinates()[0].getLongitude());
        Assert.assertEquals("asdf", s.getCoordinates()[0].getAddress());
    }
}
