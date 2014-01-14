/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.namal.needless.compass;

import java.util.Map;
import java.util.Set;

/**
 *
 * @author nmalik
 */
public class House {
    private Site site;
    private Score score;

    /**
     * For each trip, create a set of waypoints that realize all the waypoint combos possible.
     */
    private Map<Trip, Set<Waypoint>> paths;

    /**
     * @return the site
     */
    public Site getSite() {
        return site;
    }

    /**
     * @param site the site to set
     */
    public void setSite(Site site) {
        this.site = site;
    }

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
    public Map<Trip, Set<Waypoint>> getPaths() {
        return paths;
    }

    /**
     * @param paths the paths to set
     */
    public void setPaths(Map<Trip, Set<Waypoint>> paths) {
        this.paths = paths;
    }
}
