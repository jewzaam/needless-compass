/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.namal.needless.compass;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * An instance of a site that came from some location and therefore has a score. Can have child sites that are potential
 * targets for the next leg of the journey.
 *
 * @author nmalik
 */
public class Waypoint {
    private final Site current;
    private final Waypoint previous;
    private final Set<Waypoint> next = new HashSet<>();
    private Score score;

    public Waypoint(Site current, Waypoint previous) {
        this.current = current;
        this.previous = previous;
    }

    public Waypoint(Site current, Waypoint previous, Set<Waypoint> next) {
        this(current, previous);
        if (next != null && !next.isEmpty()) {
            this.next.addAll(next);
        }
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
     * Get the score from the previous site to the current site. Computes the score if it hasn't been computed yet.
     *
     * @return the score
     */
    public Score getScore() {
        if (null == score
                && null != previous
                && null != current) {
            double lat1 = Math.toRadians(previous.getCurrent().getLatitude().doubleValue());
            double lon1 = Math.toRadians(previous.getCurrent().getLongitude().doubleValue());
            double lat2 = Math.toRadians(current.getLatitude().doubleValue());
            double lon2 = Math.toRadians(current.getLongitude().doubleValue());
            // http://www.movable-type.co.uk/scripts/latlong.html (Equirectangular approximation)
            this.score = new Score(Math.sqrt(Math.pow((lon2 - lon1) * Math.cos((lat1 + lat2) / 2), 2) + Math.pow(lat2 - lat1, 2)) * 6371);
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
    public Iterator<Waypoint> getNext() {
        return next.iterator();
    }

    /**
     * @param n the next to set
     */
    public void addNext(Waypoint n) {
        this.next.add(n);
    }
}
