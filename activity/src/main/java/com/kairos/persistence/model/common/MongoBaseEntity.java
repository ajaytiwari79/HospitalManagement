package com.kairos.persistence.model.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kairos.dto.activity.common.UserInfo;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;

import java.math.BigInteger;
import java.util.Date;

/**
 * Created by oodles on 4/1/17.
 */
public abstract class MongoBaseEntity {

    @Id
    protected BigInteger id;
    @CreatedDate
    protected Date createdAt;

    @LastModifiedDate
    protected Date updatedAt;
    protected boolean deleted;
    protected UserInfo createdBy;
    protected UserInfo lastModifiedBy;


    public UserInfo getCreatedBy() {
        return createdBy;
    }

    @JsonIgnore
    public void setCreatedBy(UserInfo createdBy) {
        this.createdBy = createdBy;
    }

    public UserInfo getLastModifiedBy() {
        return lastModifiedBy;
    }

    @JsonIgnore
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

    @JsonIgnore
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    @JsonIgnore
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
