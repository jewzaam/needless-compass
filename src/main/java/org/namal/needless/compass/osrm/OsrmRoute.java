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
package org.namal.needless.compass.osrm;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import org.namal.needless.compass.osrm.model.Route;

/**
 *
 * @author jewzaam
 */
public class OsrmRoute {
    private static final String OSRM_BASE_URI = "http://router.project-osrm.org/viaroute?z=0";
    private static final String OSRM_WAYPOINT_URI = "&loc=%f,%f";

    public static Route execute(double[][] coordinates) throws MalformedURLException, IOException {
        if (null == coordinates || coordinates.length < 2) {
            throw new MalformedURLException("Unable to construct URL, must at least two coordinates");
        }

        StringBuilder buffUrl = new StringBuilder(OSRM_BASE_URI);

        for (double[] coordinate : coordinates) {
            if (null == coordinate || coordinate.length < 2) {
                throw new MalformedURLException("Unable to construct URL, each coordiante must have two values");
            }
            buffUrl.append(String.format(OSRM_WAYPOINT_URI, coordinate[0], coordinate[1]));
        }

        URL url = new URL(buffUrl.toString());
        URLConnection con = url.openConnection();

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

        return route;
    }
}
