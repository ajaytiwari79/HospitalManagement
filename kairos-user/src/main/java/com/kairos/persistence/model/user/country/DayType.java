package com.kairos.persistence.model.user.country;

import java.util.HashMap;
import java.util.Map;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.persistence.model.common.UserBaseEntity;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;

/**
 * Created by oodles on 9/1/17.
 */
@NodeEntity
@JsonIgnoreProperties(ignoreUnknown = true)
public class DayType  extends UserBaseEntity{
    @NotEmpty(message = "error.DayType.name.notEmpty") @NotNull(message = "error.DayType.name.notnull")
    private String name;

    @NotNull
    int code;

    @NotEmpty(message = "error.DayType.description.notEmpty") @NotNull(message = "error.DayType.description.notnull")
    private String description;

    @NotEmpty(message = "error.DayType.colorCode.notEmpty") @NotNull(message = "error.DayType.colorCode.notnull")
    private String colorCode;

    @Relationship(type = BELONGS_TO)
    private Country country;

    private boolean isEnabled = true;

    public String getColorCode() {
        return colorCode;
    }

    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
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
        this.description = description;
    }

    // Constructor
    public DayType() {
    }

    public Map<String, Object> retrieveDetails() {
        Map<String, Object> map = new HashMap();
        map.put("id",this.id);
        map.put("name",this.name);
        map.put("description",this.description);
        map.put("country",this.country.getName());
        map.put("code",this.code);
        map.put("colorCode",this.colorCode);
        map.put("lastModificationDate",this.getLastModificationDate());
        map.put("creationDate",this.getCreationDate());
        return map;
    }
}
