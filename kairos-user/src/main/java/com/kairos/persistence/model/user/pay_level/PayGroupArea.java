package com.kairos.persistence.model.user.pay_level;

import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.user.country.Country;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import static com.kairos.persistence.model.constants.RelationshipConstants.LOCATED_IN;

/**
 * Created by prabjot on 20/12/17.
 */
@NodeEntity
public class PayGroupArea extends UserBaseEntity {

    private String name;
    private String description;
    private boolean deleted;
    @Relationship(type = LOCATED_IN)
    Country country;



    public PayGroupArea() {
        //default constructor
    }

    public PayGroupArea(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }
}
