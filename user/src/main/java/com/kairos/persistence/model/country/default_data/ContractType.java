package com.kairos.persistence.model.country.default_data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.country.Country;
import org.apache.commons.lang.StringUtils;
import org.hibernate.validator.constraints.NotEmpty;
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

    private int code;

    //@NotEmpty(message = "error.ContractType.description.notEmpty") @NotNull(message = "error.ContractType.description.notnull")
    private String description;

    @Relationship(type = BELONGS_TO)
    private Country country;

    private boolean isEnabled = true;

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

    public ContractType() {
    }

    public Map<String, Object> retrieveDetails() {
        Map<String, Object> map = new HashMap();
        map.put("id",this.id);
        map.put("name",this.name);
        map.put("code",this.code);
        map.put("description",this.description);
        map.put("lastModificationDate",this.getLastModificationDate());
        map.put("creationDate",this.getCreationDate());
        return map;
    }

}
