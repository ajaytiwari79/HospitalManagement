package com.kairos.activity.enums.counter;

/*
 * @author: mohit.shakya@oodlestechnologies.com
 * @dated: Jun/26/2018
 */

public enum CounterLevel {
    INDIVIDUAL("Individual"), ORGANIZATION("Organization"), UNIT("Unit");

    private String name;

    private CounterLevel(String name){
        this.name = name;
    }
}
