package com.kairos.persistence.model.country.default_data;

import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.country.Country;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import static com.kairos.persistence.model.constants.RelationshipConstants.IN_COUNTRY;

//  Created By vipul   On 9/8/18
@NodeEntity
public class UnitType extends UserBaseEntity {
    private String name;
    private String description;
    @Relationship(type = IN_COUNTRY)
    private Country country;

    public UnitType() {
        // dc
    }

    public String getDescription() {
        return description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public UnitType(String name, String description, Country country) {
        this.name = name;
        this.description = description;
        this.country = country;
    }
}
