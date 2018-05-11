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

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    protected Boolean deleted;
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
}
