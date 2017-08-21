package com.kairos.persistence.model.user.country;

import org.neo4j.ogm.annotation.NodeEntity;

import com.kairos.persistence.model.common.UserBaseEntity;

/**
 * Created by oodles on 23/11/16.
 */
@NodeEntity
public class CountryTimeType extends UserBaseEntity {

    String name;
    String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
