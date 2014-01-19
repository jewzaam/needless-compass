/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.namal.needless.compass.model;

/**
 * Just to make it obvious in other code instead of peppering source with
 * BigDecimal.
 *
 * @author nmalik
 */
public class Score implements Comparable<Score> {

    double value;

    public Score(String val) {
        value = Double.valueOf(val);
    }

    public Score(double val) {
        value = val;
    }

    public double doubleValue() {
        return value;
    }

    @Override
    public String toString() {
        return Double.toString(value);
    }

    @Override
    public int compareTo(Score t) {
        if (null == t) {
            return 1;
        }
        if (doubleValue() > t.doubleValue()) {
            return 1;
        } else if (doubleValue() == t.doubleValue()) {
            return 0;
        } else {
            return -1;
        }
    }
}
