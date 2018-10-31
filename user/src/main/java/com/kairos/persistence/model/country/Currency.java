package com.kairos.persistence.model.country;
import com.kairos.persistence.model.common.UserBaseEntity;
import org.apache.commons.lang.StringUtils;
import org.hibernate.validator.constraints.NotEmpty;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import static com.kairos.persistence.model.constants.RelationshipConstants.RELATED_TO;


/**
 * Created by prabjot on 9/1/17.
 */
@NodeEntity
public class Currency extends UserBaseEntity {

    @NotBlank(message = "error.Currency.name.notEmpty")
    private String name;

    private String description;

    @Relationship(type=RELATED_TO)
    private Country country;

    @NotBlank(message = "error.Currency.currencyCode.notEmpty")
    private String currencyCode;

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
