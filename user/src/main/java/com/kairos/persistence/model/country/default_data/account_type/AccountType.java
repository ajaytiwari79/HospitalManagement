package com.kairos.persistence.model.country.default_data.account_type;

import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.country.Country;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import static com.kairos.persistence.model.constants.RelationshipConstants.IN_COUNTRY;

@NodeEntity
public class AccountType extends UserBaseEntity {
    private String name;
    @Relationship(type = IN_COUNTRY)
    private Country country;

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

    public AccountType() {
    }

    public AccountType(String name, Country country) {
        this.name = name;
        this.country = country;
    }
}
