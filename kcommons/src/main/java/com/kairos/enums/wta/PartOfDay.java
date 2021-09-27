package com.kairos.enums.wta;

import java.io.Serializable;

/**
 * @author pradeep
 * @date - 11/4/18
 */

public enum PartOfDay implements Serializable {
    NIGHT("Night"),DAY("Day"),EVENING("Evening");

    private String value;
    PartOfDay(String value) {
        this.value = value;
    }

    public String getValue(){
        return this.value;
    }


}
