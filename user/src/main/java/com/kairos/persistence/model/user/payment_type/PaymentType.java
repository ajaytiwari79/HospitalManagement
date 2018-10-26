package com.kairos.persistence.model.user.payment_type;

import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.country.Country;
import org.apache.commons.lang.StringUtils;
import org.hibernate.validator.constraints.NotEmpty;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import static com.kairos.persistence.model.constants.RelationshipConstants.BELONGS_TO;


/**
 * Created by prabjot on 9/1/17.
 */
@NodeEntity
public class PaymentType extends UserBaseEntity {


    @NotBlank(message = "error.PaymentType.name.notEmpty")
    private String name;
    private String description;
    private boolean isEnabled = true;
    @Relationship(type = BELONGS_TO)
    Country country;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = StringUtils.trim(description);
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

        this.name = StringUtils.trim(name);
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }
}
