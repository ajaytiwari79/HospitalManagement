package com.planner.domain;

import org.bson.codecs.pojo.annotations.BsonId;
import org.springframework.data.annotation.Id;

import java.math.BigInteger;
import java.util.Date;

public class MongoBaseEntity {
    @Id
    protected BigInteger id;
    protected Boolean deleted;
    protected Date createdAt;
    protected Date updatedAt;


    //Setters and Getters
    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}
