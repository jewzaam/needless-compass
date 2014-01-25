/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.namal.needless.compass.model;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author nmalik
 */
public class House extends Site implements Comparable<House> {
    private Coordinate[] anchors;
    private Score score;
    /**
     * For each trip, the starting dummy "house" waypoint. Each waypoint has
     * child (next) waypoints.
     */
    private transient final Map<Trip, Waypoint> paths = new HashMap<>();

    /**
     * @return the score
     */
    public Score getScore() {
        return score;
    }

    /**
     * @param score the score to set
     */
    public void setScore(Score score) {
        this.score = score;
    }

    /**
     * @return the paths
     */
    public Map<Trip, Waypoint> getPaths() {
        return paths;
    }

    /**
     * @param trip
     * @param waypoint
     */
    public void addPath(Trip trip, Waypoint waypoint) {
        paths.put(trip, waypoint);
    }

    @Override
    public int compareTo(House t) {
        if (null == t) {
            return 1;
        }
        return getScore().compareTo(t.getScore());
    }

    /**
     * @return the anchors
     */
    public Coordinate[] getAnchors() {
        return anchors;
    }

    /**
     * @param anchors the anchors to set
     */
    public void setAnchors(Coordinate[] anchors) {
        this.anchors = anchors;
    }
}
