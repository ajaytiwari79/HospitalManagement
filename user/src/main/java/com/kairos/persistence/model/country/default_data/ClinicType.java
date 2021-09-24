package com.kairos.persistence.model.country.default_data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.country.Country;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import javax.validation.constraints.NotBlank;

import static com.kairos.persistence.model.constants.RelationshipConstants.BELONGS_TO;

/**
 * Created by oodles on 9/1/17.
 */
@NodeEntity
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClinicType extends UserBaseEntity {

    @NotBlank(message = "error.ClinicType.name.notEmpty")
    private String name;
    private String description;
    @Relationship(type = BELONGS_TO)
    Country country;
    private boolean isEnabled = true;

    public ClinicType() {
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
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

    public ClinicType(@NotBlank(message = "error.ClinicType.name.notEmpty") String name, String description) {
        this.name = name;
        this.description = description;
    }
}
