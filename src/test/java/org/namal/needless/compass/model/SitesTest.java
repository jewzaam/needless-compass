/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.namal.needless.compass.model;

import org.namal.needless.compass.model.Sites;
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
public class SitesTest {

    @Test
    public void load() throws IOException {
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
