package com.kairos.persistence.model.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.dto.activity.common.UserInfo;
import org.neo4j.ogm.annotation.GraphId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Contains common fields of an entity
 *
 */
@JsonInclude(JsonInclude.Include.NON_NULL)

public abstract class UserBaseEntity implements Serializable {

    //@GeneratedValue
    @GraphId protected Long id;
    @JsonIgnore
    protected boolean deleted;
    @JsonIgnore

    @CreatedDate
    private LocalDateTime creationDate;
    @JsonIgnore

    @LastModifiedDate
    private LocalDateTime lastModificationDate;

    @JsonIgnore
    protected UserInfo createdBy;
    @JsonIgnore
    protected UserInfo lastModifiedBy;


    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDateTime getLastModificationDate() {
        return lastModificationDate;
    }

    public void setLastModificationDate(LocalDateTime lastModificationDate) {
        this.lastModificationDate = lastModificationDate;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

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
}
