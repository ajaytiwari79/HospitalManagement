package com.kairos.persistence.model.user.tpa_services;
import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.country.Country;
import org.hibernate.validator.constraints.NotEmpty;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import static com.kairos.persistence.model.constants.RelationshipConstants.BELONGS_TO;


/**
 * Created by prabjot on 17/1/17.
 */
@NodeEntity
public class IntegrationConfiguration extends UserBaseEntity {

    @NotBlank(message = "name can not be null")
    private String name;
    private String description;
    @NotBlank(message = "unique key can not be null")
    private String uniqueKey;
    private boolean isEnabled = true;
    @Relationship(type = BELONGS_TO)
    private Country country;

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Country getCountry() {
        return country;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public String getUniqueKey() {
        return uniqueKey;
    }

    public void setUniqueKey(String uniqueKey) {
        this.uniqueKey = uniqueKey;
    }
}
