package com.kairos.activity.counter;

import java.math.BigInteger;

public class KPICategoryDTO {
    private BigInteger id;
    private String name;

    public KPICategoryDTO(){

    }

    public KPICategoryDTO(BigInteger id, String name){
        this.id = id;
        this.name = name;
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
