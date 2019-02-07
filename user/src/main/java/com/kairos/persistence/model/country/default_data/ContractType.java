package com.kairos.persistence.model.country.default_data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.country.Country;
import org.apache.commons.lang.StringUtils;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.Range;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

import static com.kairos.persistence.model.constants.RelationshipConstants.BELONGS_TO;

/**
 * Created by oodles on 9/1/17.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@NodeEntity
public class ContractType extends UserBaseEntity {

    @NotBlank(message = "error.ContractType.name.notEmpty")
    private String name;
    @NotNull(message = "error.ContractType.code.notEmpty")
    @Range(min = 1, message = "error.ContractType.code.greaterThenOne")
    private int code;
    private String description;
    @Relationship(type = BELONGS_TO)
    private Country country;
    private boolean isEnabled = true;

    public ContractType() {
    }

    public ContractType(@NotBlank(message = "error.ContractType.name.notEmpty") String name, @NotNull(message = "error.ContractType.code.notEmpty") @Range(min = 1, message = "error.ContractType.code.greaterThenOne") int code, String description) {
        this.name = name;
        this.code = code;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = StringUtils.trim(name);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = StringUtils.trim(description);
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

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }


}
