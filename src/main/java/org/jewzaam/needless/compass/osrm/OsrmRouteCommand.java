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
package org.jewzaam.needless.compass.osrm;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.List;
import org.jewzaam.needless.compass.osrm.model.Route;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Route geometry encoding:
 * <ul>
 * <li>https://developers.google.com/maps/documentation/utilities/polylinealgorithm?csw=1</li>
 * <li>https://github.com/DennisOSRM/Project-OSRM/wiki/Server-api</li>
 * <li>https://github.com/DennisSchiefer/Project-OSRM-Web/blob/develop/WebContent/routing/OSRM.RoutingGeometry.js</li>
 * </ul>
 *
 * @author jewzaam
 */
public class OsrmRouteCommand extends AbstractRouteCommand {
    private static final Logger LOGGER = LoggerFactory.getLogger(OsrmRouteCommand.class);

    private static final String OSRM_BASE_URI = "http://router.project-osrm.org/viaroute?z=0";
    private static final String OSRM_WAYPOINT_URI = "&loc=%f,%f";
    private static final String HTTP_REFERER;

    static {
        String hostname = System.getenv("OPENSHIFT_APP_DNS");
        if (null == hostname || hostname.isEmpty()) {
            hostname = "needlesscompass-nmalik.rhcloud.com";
        }
        HTTP_REFERER = String.format("http://%s/", hostname);
    }

    /**
     * For testing I don't want this enabled, so doing this as a quick and dirty hack.
     */
    public static void disable() {
        disabled = true;
    }

    private static boolean disabled = false;

    public OsrmRouteCommand(List<double[]> coordinates) {
        super("OsrmRoute", coordinates);
    }

    @Override
    public org.jewzaam.needless.compass.model.Route run() throws MalformedURLException, IOException {
        if (disabled) {
            throw new IllegalStateException("is disabled, shouldn't see any executions");
        }

        if (null == getCoordinates() || getCoordinates().size() < 2) {
            throw new MalformedURLException("Unable to construct URL, must at least two coordinates");
        }

        StringBuilder buffUrl = new StringBuilder(OSRM_BASE_URI);

        for (double[] coordinate : getCoordinates()) {
            if (null == coordinate || coordinate.length < 2) {
                throw new MalformedURLException("Unable to construct URL, each coordiante must have two values");
            }
            buffUrl.append(String.format(OSRM_WAYPOINT_URI, coordinate[0], coordinate[1]));
        }

        LOGGER.info("Requesting route: {}", buffUrl.toString());

        URL url = new URL(buffUrl.toString());
        URLConnection con = url.openConnection();

        // set Referer per: https://github.com/DennisOSRM/Project-OSRM/wiki/API%20Usage%20Policy
        con.setRequestProperty("Referer", HTTP_REFERER);

        StringBuilder buffResponse = new StringBuilder();

        try (InputStream is = con.getInputStream();
                InputStreamReader isr = new InputStreamReader(is, Charset.defaultCharset());
                BufferedReader reader = new BufferedReader(isr)) {
            String line;
            while ((line = reader.readLine()) != null) {
                buffResponse.append(line).append("\n");
            }
        }

        String jsonString = buffResponse.toString();
        Gson g = new Gson();
        Route route = g.fromJson(jsonString, Route.class);

        if (Route.STATUS_SUCCESS != route.status) {
            throw new RuntimeException("Status from OSRM API not successful: " + jsonString);
        }

        org.jewzaam.needless.compass.model.Route output = route.convert();

        // set the things that are not available on the osrm result set
        output.setRoute(getCoordinates());
        output.setApiRequest(buffUrl.toString());

        return output;
    }
}
