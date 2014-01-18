/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.namal.needless.compass.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * An instance of a site that came from some location and therefore has a score.
 * Can have child sites that are potential targets for the next leg of the
 * journey.
 *
 * @author nmalik
 */
public class Waypoint implements Comparable<Waypoint> {
    private final Site current;
    private final Waypoint previous;
    private final List<Waypoint> next = new ArrayList<>();
    private Score score;

    public Waypoint(Site current, Waypoint previous) {
        this.current = current;
        this.previous = previous;

        if (this.previous != null) {
            this.previous.addNext(this);
        }
    }

    /**
     * Create a waypoint without a previous location. Use this constructor to
     * create a starting point for waypoint chains.
     *
     * @param current
     */
    public Waypoint(Site current) {
        this(current, null);
    }

    /**
     * Get the current site.
     *
     * @return the current site
     */
    public Site getCurrent() {
        return current;
    }

    /**
     * Get the score from the previous site to the current site. Computes the
     * score if it hasn't been computed yet.
     *
     * @return the score
     */
    public Score getScore() {
        if (null == score) {
            if (null != previous && null != current) {
                double lat1 = Math.toRadians(previous.getCurrent().getLatitude().doubleValue());
                double lon1 = Math.toRadians(previous.getCurrent().getLongitude().doubleValue());
                double lat2 = Math.toRadians(current.getLatitude().doubleValue());
                double lon2 = Math.toRadians(current.getLongitude().doubleValue());
                // http://www.movable-type.co.uk/scripts/latlong.html (Equirectangular approximation)
                score = new Score(getPrevious().getScore().doubleValue() + Math.sqrt(Math.pow((lon2 - lon1) * Math.cos((lat1 + lat2) / 2), 2) + Math.pow(lat2 - lat1, 2)) * 6371);
            } else {
                score = new Score("0");
            }
        }
        return score;
    }

    /**
     * Get the previous site.
     *
     * @return the previous
     */
    public Waypoint getPrevious() {
        return previous;
    }

    /**
     * @return the next
     */
    public Iterator<Waypoint> iterator() {
        return next.iterator();
    }

    public List<Waypoint> getNext() {
        return next;
    }

    /**
     * @param n the next to set
     */
    private void addNext(Waypoint n) {
        this.next.add(n);
    }

    @Override
    public int compareTo(Waypoint o) {
        if (null == o) {
            return 1;
        }
        if (getScore().doubleValue() > o.getScore().doubleValue()) {
            return 1;
        } else if (getScore().doubleValue() == o.getScore().doubleValue()) {
            return 0;
        } else {
            return -1;
        }
    }
}
