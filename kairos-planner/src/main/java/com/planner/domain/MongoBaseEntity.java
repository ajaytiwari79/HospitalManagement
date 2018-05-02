package com.planner.domain;

import org.springframework.data.annotation.Id;

import java.math.BigInteger;
public class MongoBaseEntity {
    @Id
    protected String id;

    public BigInteger getKairosId() {
        return kairosId;
    }

    public void setKairosId(BigInteger kairosId) {
        this.kairosId = kairosId;
    }

    protected BigInteger kairosId;
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
}
