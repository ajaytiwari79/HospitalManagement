package com.kairos.persistence.model.country.default_data;

import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.country.Country;
import org.apache.commons.lang.StringUtils;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import javax.validation.constraints.NotBlank;

import static com.kairos.constants.UserMessagesConstants.ERROR_CURRENCY_CURRENCYCODE_NOTEMPTY;
import static com.kairos.constants.UserMessagesConstants.ERROR_CURRENCY_NAME_NOTEMPTY;
import static com.kairos.persistence.model.constants.RelationshipConstants.BELONGS_TO;

/**
 * Created by prabjot on 9/1/17.
 */
@NodeEntity
public class Currency extends UserBaseEntity {

    @NotBlank(message = ERROR_CURRENCY_NAME_NOTEMPTY)
    private String name;
    private String description;
    @Relationship(type=BELONGS_TO)
    private Country country;
    @NotBlank(message = ERROR_CURRENCY_CURRENCYCODE_NOTEMPTY)
    private String currencyCode;

    public Currency() {
    }

    public Currency(@NotBlank(message = ERROR_CURRENCY_NAME_NOTEMPTY) String name, String description, @NotBlank(message = ERROR_CURRENCY_CURRENCYCODE_NOTEMPTY) String currencyCode) {
        this.name = name;
        this.description = description;
        this.currencyCode = currencyCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = StringUtils.trim(description);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = StringUtils.trim(name);
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = StringUtils.trim(currencyCode);
    }

}
