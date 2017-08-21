package com.kairos.persistence.model.user.country;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import com.kairos.persistence.model.common.UserBaseEntity;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;

/**
 * Created by prabjot on 9/1/17.
 */
@NodeEntity
public class Currency extends UserBaseEntity {

    @NotEmpty(message = "error.Currency.name.notEmpty") @NotNull(message = "error.Currency.name.notnull")
    private String name;

    private String description;

    @Relationship(type=RELATED_TO)
    private Country country;

    @NotEmpty(message = "error.Currency.currencyCode.notEmpty") @NotNull(message = "error.Currency.currencyCode.notnull")
    private String currencyCode;

    private boolean isEnabled = true;

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

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public boolean isEnabled() {
        return isEnabled;
    }


    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }
}
