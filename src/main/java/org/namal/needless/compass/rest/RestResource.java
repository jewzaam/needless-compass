package org.namal.needless.compass.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.apache.taglibs.standard.tag.common.core.Util;
import org.namal.needless.compass.app.TestApp;
import sun.net.www.http.HttpClient;

/**
 * Simple service to test out NewRelic custom metrics.
 *
 * @author nmalik
 */
@Path("/nc")
public class RestResource {

    /**
     * Increment the given number
     *
     * @return all houses with calculated scores
     * @throws java.io.IOException
     */
    @GET
    @Path("/data")
    @Produces(MediaType.APPLICATION_JSON)
    public String data() throws IOException {
        TestApp app = new TestApp();
        app.initialize();
        return app.process();
    }

    @GET
    @Path("/score")
    @Produces(MediaType.APPLICATION_JSON)
    public String score(@QueryParam("address") String streetAddress) throws MalformedURLException, IOException {
        String urlString = "http://maps.googleapis.com/maps/api/geocode/json?sensor=false&address=" + Util.URLEncode(streetAddress, "UTF-8");
        HttpClient hc = HttpClient.New(new URL(urlString));

        StringBuilder buff = new StringBuilder();

        try (InputStream is = hc.getInputStream();
                InputStreamReader isr = new InputStreamReader(is, Charset.defaultCharset());
                BufferedReader reader = new BufferedReader(isr)) {
            String line;
            while ((line = reader.readLine()) != null) {
                buff.append(line).append("\n");
            }
        }

        return buff.toString();
    }
}
