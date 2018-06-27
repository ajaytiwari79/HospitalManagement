package com.kairos.persistence.model.user.payment_type;

import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.user.country.Country;
import org.hibernate.validator.constraints.NotEmpty;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import javax.validation.constraints.NotNull;

import static com.kairos.persistence.model.constants.RelationshipConstants.BELONGS_TO;


/**
 * Created by prabjot on 9/1/17.
 */
@NodeEntity
public class PaymentType extends UserBaseEntity {


    @NotEmpty(message = "error.PaymentType.name.notEmpty") @NotNull(message = "error.PaymentType.name.notnull")
    private String name;

    @NotEmpty(message = "error.PaymentType.description.notEmpty") @NotNull(message = "error.PaymentType.description.notnull")
    private String description;

    private boolean isEnabled = true;

    @Relationship(type = BELONGS_TO)
    Country country;

    public String getDescription() {
        return description;
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

    public void setName(String name) {

        this.name = name;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }
}
