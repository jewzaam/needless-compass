/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.namal.needless.compass;

/**
 *
 * @author nmalik
 */
public class Category {
    private String name;
    private Site[] sites;

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the sites
     */
    public Site[] getSites() {
        return sites;
    }

    /**
     * @param sites the sites to set
     */
    public void setSites(Site[] sites) {
        this.sites = sites;
    }
}
