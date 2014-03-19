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
package org.jewzaam.needless.compass.google;

import com.google.gson.Gson;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import org.jewzaam.needless.compass.google.model.Result;
import org.jewzaam.needless.compass.model.PointOfInterest;

/**
 *
 * @author jewzaam
 */
public class GoogleGeocodeCommand extends HystrixCommand<PointOfInterest> {
    private final PointOfInterest poi;

    public GoogleGeocodeCommand(PointOfInterest poi) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("GoogleGeocode"))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter().withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.SEMAPHORE))
        );
        this.poi = poi;
    }

    @Override
    protected PointOfInterest run() throws MalformedURLException, IOException {
        String geocodeApiUrlString = null;
        if (poi.getAddress() != null && !poi.getAddress().isEmpty()) {
            geocodeApiUrlString = "http://maps.googleapis.com/maps/api/geocode/json?sensor=false&address=" + URLEncoder.encode(poi.getAddress(), "UTF-8");
//        } else if (poi.getLocation().getCoordinates() != null && poi.getLocation().getCoordinates().length == 1) {
//            geocodeApiUrlString = "http://maps.googleapis.com/maps/api/geocode/json?sensor=false&latlng=" + poi.getLocation().getCoordinates()[0] + "," + poi.getLocation().getCoordinates()[1];
        } else {
            throw new MalformedURLException("Unable to construct URL, must supply latitude/longitude or street address");
        }

        // short cut.. if already have address, location, and name, just bail.
        if (poi.getName() != null && !poi.getName().isEmpty()
                && poi.getAddress() != null && !poi.getAddress().isEmpty()
                && poi.getLocation().getCoordinate() != null && poi.getLocation().getCoordinate().length == 2) {
            return poi;
        }

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
        org.jewzaam.needless.compass.google.model.Geocode geocode = g.fromJson(jsonString, org.jewzaam.needless.compass.google.model.Geocode.class);

        if (!"OK".equals(geocode.getStatus())) {
            throw new RuntimeException("Status from geocode API not OK: " + jsonString);
        }

        // use only first result
        Result result = geocode.getResults()[0];

        // update poi
        if (poi.getName() == null || poi.getName().isEmpty()) {
            poi.setName(result.getFormattedAddress());
        }
        poi.setAddress(result.getFormattedAddress());
        poi.getLocation().setCoordinate(new double[]{
            Double.parseDouble(result.getGeometry().getLocation().getLat()),
            Double.parseDouble(result.getGeometry().getLocation().getLng())
        });

        return poi;
    }

    @Override
    protected PointOfInterest getFallback() {
        throw new RuntimeException(getFailedExecutionException());
    }

}
