package com.kairos.activity.persistence.enums.counter;

public enum CounterView {
    CELL("Cell"), CHART("Chart"), BOTH("Both");
    private String name;
    private CounterView(String name){
        this.name = name;
    }

    public String getName(){
        return this.name;
    }
}
