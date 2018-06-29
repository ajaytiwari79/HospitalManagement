package com.kairos.persistence.enums.counter;

/*
 * @author: mohit.shakya@oodlestechnologies.com
 * @dated: Jun/26/2018
 */

public enum ChartType {
    PIE("Pie-Chart"), BAR("Bar-Chart");

    private String type;

    private ChartType(String type){
        this.type = type;
    }
}
