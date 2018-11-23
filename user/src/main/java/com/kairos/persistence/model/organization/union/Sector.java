package com.kairos.persistence.model.organization.union;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.country.Country;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import static com.kairos.persistence.model.constants.RelationshipConstants.HAS_SYSTEM_LANGUAGE;
import static com.kairos.persistence.model.constants.RelationshipConstants.IN_COUNTRY;
@NodeEntity
@JsonIgnoreProperties(ignoreUnknown = true)
public class Sector extends UserBaseEntity {
    private String name;
    @Relationship(type= IN_COUNTRY)
    private Country country;

    public Sector() {

    }
    public Sector(Long id, String name) {
        this.id = id;
        this.name=name;
    }
    public Sector(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Country getCountry() {
        return country;
    }
    public void setCountry(Country country) {
        this.country = country;
    }
}