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
package org.namal.needless.compass.google;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import org.namal.needless.compass.google.model.Result;

/**
 *
 * @author jewzaam
 */
public class GoogleGeocode {
    public static Result execute(Double latitude, Double longitude) throws MalformedURLException, IOException {
        if (latitude == null || longitude == null) {
            throw new MalformedURLException("Unable to construct URL, must supply latitude and longitude");
        }

        String geocodeApiUrlString = "http://maps.googleapis.com/maps/api/geocode/json?sensor=false&latlng=" + latitude + "," + longitude;

        return _execute(geocodeApiUrlString);
    }

    public static Result execute(String streetAddress) throws MalformedURLException, IOException {
        if (null == streetAddress || streetAddress.isEmpty()) {
            throw new MalformedURLException("Unable to construct URL, must supply street address");
        }

        String geocodeApiUrlString = "http://maps.googleapis.com/maps/api/geocode/json?sensor=false&address=" + URLEncoder.encode(streetAddress, "UTF-8");

        return _execute(geocodeApiUrlString);
    }

    private static Result _execute(String geocodeApiUrlString) throws MalformedURLException, IOException {
        URL url = new URL(geocodeApiUrlString);
        URLConnection con = url.openConnection();

        StringBuilder buff = new StringBuilder();

        try (InputStream is = con.getInputStream();
                InputStreamReader isr = new InputStreamReader(is, Charset.defaultCharset());
                BufferedReader reader = new BufferedReader(isr)) {
            String line;
            while ((line = reader.readLine()) != null) {
                buff.append(line).append("\n");
            }
        }

        String jsonString = buff.toString();
        Gson g = new Gson();
        org.namal.needless.compass.google.model.Geocode geocode = g.fromJson(jsonString, org.namal.needless.compass.google.model.Geocode.class);

        if (!"OK".equals(geocode.getStatus())) {
            throw new RuntimeException("Status from geocode API not OK: " + jsonString);
        }

        // use only first result
        return geocode.getResults()[0];
    }

}
