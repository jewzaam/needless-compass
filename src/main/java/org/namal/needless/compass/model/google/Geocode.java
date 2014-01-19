/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.namal.needless.compass.model.google;

/**
 *
 * @author jewzaam
 */
public class Geocode {
    private Result results[];
    private String status;

    /**
     * @return the results
     */
    public Result[] getResults() {
        return results;
    }

    /**
     * @param results the results to set
     */
    public void setResults(Result[] results) {
        this.results = results;
    }

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }
}
