package org.namal.needless.compass;

import java.io.IOException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

/**
 * Simple service to test out NewRelic custom metrics.
 *
 * @author nmalik
 */
@Path("/needless-compass")
public class NeedlessCompassResource {

    /**
     * Increment the given number
     *
     * @param number the number to increment
     * @return the new value
     */
    @GET
    @Path("/")
    public String data() throws IOException {
        NeedlessCompassApp app = new NeedlessCompassApp();
        app.initialize();
        return app.process();
    }
}
