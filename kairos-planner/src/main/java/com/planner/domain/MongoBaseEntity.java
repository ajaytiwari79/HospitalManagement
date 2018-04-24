package com.planner.domain;

import org.springframework.data.annotation.Id;

import java.math.BigInteger;

public class MongoBaseEntity {
    @Id
    //this is same as karios PK id of any collection/table
    protected BigInteger id;


    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }
}
