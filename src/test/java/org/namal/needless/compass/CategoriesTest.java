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
import java.nio.charset.Charset;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author nmalik
 */
public class CategoriesTest {

    @Test
    public void loadSites() throws IOException {
        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("categories.json");
                InputStreamReader isr = new InputStreamReader(is, Charset.defaultCharset())) {
            Gson g = new Gson();
            Categories categories = g.fromJson(isr, Categories.class);
            Assert.assertNotNull(categories);
            Assert.assertNotNull(categories.getCategories());
            Assert.assertTrue(categories.getCategories().length > 0);
        }
    }
}
