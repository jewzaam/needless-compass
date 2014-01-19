package org.namal.needless.compass.rest;

import java.io.IOException;
import java.net.MalformedURLException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.namal.needless.compass.app.TestApp;
import org.namal.needless.compass.model.House;

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
        TestApp app = new TestApp();
        app.initialize();
        House house = TestApp.createHouseFromAddress(streetAddress);
        app.process(house);

        return TestApp.prettyJson(house);
    }
}
