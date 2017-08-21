package com.kairos.persistence.model.common;

import org.neo4j.ogm.annotation.GraphId;

/**
 * Contains common fields of an entitiy
 *
 */
public class UserBaseEntity {

    @GraphId protected Long id;

    private Long creationDate;
    private Long lastModificationDate;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }


    public Long getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Long creationDate) {
        this.creationDate = creationDate;
    }

    public Long getLastModificationDate() {
        return lastModificationDate;
    }

    public void setLastModificationDate(Long lastModificationDate) {
        this.lastModificationDate = lastModificationDate;
    }





}
