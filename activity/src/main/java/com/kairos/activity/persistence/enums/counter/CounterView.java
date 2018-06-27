package com.kairos.activity.persistence.enums.counter;

/*
 * @author: mohit.shakya@oodlestechnologies.com
 * @dated: Jun/26/2018
 */

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
