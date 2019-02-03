package com.kairos.persistence.model.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.dto.activity.common.UserInfo;
import org.springframework.data.annotation.*;

import java.math.BigInteger;
import java.util.Date;

/**
 * Created by oodles on 4/1/17.
 */
@JsonIgnoreProperties({"createdAt","createdBy","lastModifiedBy","updatedAt"}) //This annotation is used because at the time of deserialization this will become null
public abstract class MongoBaseEntity {

    @Id
    protected BigInteger id;
    @CreatedDate
    protected Date createdAt;
    @LastModifiedDate
    protected Date updatedAt;
    @JsonIgnore
    protected boolean deleted;
    @CreatedBy
    protected UserInfo createdBy;
    @LastModifiedBy
    protected UserInfo lastModifiedBy;


    public UserInfo getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UserInfo createdBy) {
        this.createdBy = createdBy;
    }

    public UserInfo getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(UserInfo lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
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

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}
