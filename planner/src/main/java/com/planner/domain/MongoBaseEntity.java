package com.planner.domain;

import org.bson.codecs.pojo.annotations.BsonId;
import org.springframework.data.annotation.Id;

import java.math.BigInteger;
public class MongoBaseEntity {
    @BsonId
    protected String id;
    protected BigInteger kairosId;

    public BigInteger getKairosId() {
        return kairosId;
    }

    public void setKairosId(BigInteger kairosId) {
        this.kairosId = kairosId;
    }
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
