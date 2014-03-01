/*
 * Copyright (C) 2014 Naveen Malik
 *
 * Needless Compass is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Needless Compass is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Needless Compass.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.namal.needless.compass.rest;

import com.google.gson.Gson;
import java.io.IOException;
import java.net.MalformedURLException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
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

        House house = new House();
        house.setAddress(streetAddress);
        TestApp.enrichSite(house);
        app.process(house);

        return TestApp.prettyJson(house);
    }

    @GET
    @Path("/houses")
    @Produces(MediaType.APPLICATION_JSON)
    public String getHouses() {
        Houses houses = MongoManager.loadHouses();
        return TestApp.prettyJson(houses);
    }

    @POST
    @Path("/house")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String addHouse(String houseJson) throws MalformedURLException, IOException {
        System.out.println(houseJson);
        Gson g = new Gson();
        House house = g.fromJson(houseJson, House.class);
        TestApp app = new TestApp();
        app.initialize();
        TestApp.enrichSite(house);
        MongoManager.createHouse(house);
        return TestApp.prettyJson(house);
    }

    @DELETE
    @Path("/house")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public void deleteHouse(String houseJson) throws MalformedURLException, IOException {
        System.out.println(houseJson);
        Gson g = new Gson();
        House house = g.fromJson(houseJson, House.class);
        MongoManager.deleteHouse(house);
    }

    @GET
    @Path("/sites")
    @Produces(MediaType.APPLICATION_JSON)
    public String getSites() {
        Sites sites = MongoManager.loadSites();
        return TestApp.prettyJson(sites);
    }

    @POST
    @Path("/site")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String addSite(String siteJson) throws MalformedURLException, IOException {
        System.out.println(siteJson);
        Gson g = new Gson();
        Site site = g.fromJson(siteJson, Site.class);
        TestApp app = new TestApp();
        app.initialize();
        TestApp.enrichSite(site);
        MongoManager.createSite(site);
        return TestApp.prettyJson(site);
    }
}
