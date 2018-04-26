package com.kairos.activity.persistence.enums.counter;

public enum ChartType {
    PIE("Pie-Chart"), BAR("Bar-Chart");

    private String type;

    private ChartType(String type){
        this.type = type;
    }
}
