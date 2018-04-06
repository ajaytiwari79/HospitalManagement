package com.kairos.activity.persistence.enums.counter;

public enum CounterLevel {
    INDIVIDUAL(""), ORGANIZATION(""), UNIT(""), NONE("");

    private String name;

    private CounterLevel(String name){
        this.name = name;
    }
}
