package com.kairos.activity.persistence.enums.counter;

/*
 * @author: mohit.shakya@oodlestechnologies.com
 * @dated: Jun/26/2018
 */

public enum CounterLevel {
    INDIVIDUAL(""), ORGANIZATION(""), UNIT(""), NONE("");

    private String name;

    private CounterLevel(String name){
        this.name = name;
    }
}
