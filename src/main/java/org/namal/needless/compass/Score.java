/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.namal.needless.compass;

import java.math.BigDecimal;

/**
 * Just to make it obvious in other code instead of peppering source with BigDecimal.
 *
 * @author nmalik
 */
public class Score extends BigDecimal {
    public Score(String val) {
        super(val);
    }

    public Score(double val) {
        super(val);
    }
}
