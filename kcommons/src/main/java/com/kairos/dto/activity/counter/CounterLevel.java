package com.kairos.dto.activity.counter;

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
