package org.namal.needless.compass.rest;

import com.google.gson.Gson;
import java.io.IOException;
import java.net.MalformedURLException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.namal.needless.compass.app.TestApp;
import org.namal.needless.compass.model.House;
import org.namal.needless.compass.model.Site;
import org.namal.needless.compass.model.Sites;
import org.namal.needless.compass.mongo.MongoManager;

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

    @GET
    @Path("/sites")
    @Produces(MediaType.APPLICATION_JSON)
    public String getSites() {
        Sites sites = MongoManager.loadSites();
        return TestApp.prettyJson(sites);
    }

    @PUT
    @Path("/site")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String addSite(String siteJson) throws MalformedURLException, IOException {
        Gson g = new Gson();
        Site s = g.fromJson(siteJson, Site.class);
        TestApp app = new TestApp();
        app.initialize();
        Site site = TestApp.createSiteFromAddress(s.getAddress());
        site.setCategories(s.getCategories());
        MongoManager.createSite(site);
        return TestApp.prettyJson(site);
    }
}
