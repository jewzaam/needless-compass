package org.namal.needless.compass.rest;

import java.io.IOException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import org.namal.needless.compass.app.TestApp;

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
    @Produces("application/json")
    public String data() throws IOException {
        TestApp app = new TestApp();
        app.initialize();
        return app.process();
    }
}
