package com.kairos.persistence.model.user.language;

import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.country.Country;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.NotEmpty;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

import static com.kairos.persistence.model.constants.RelationshipConstants.BELONGS_TO;


/**
 * Created by prabjot on 19/10/16.
 */
@NodeEntity
public class LanguageLevel extends UserBaseEntity {

    @NotBlank(message = "error.LanguageLevel.name.notEmpty")
    private String name;
    private String description;
    @Relationship(type =  BELONGS_TO)
    private Country country;

    public LanguageLevel(String name) {
        this.name = StringUtils.trim(name);
    }

    private boolean isEnabled = true;

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

    public LanguageLevel(){}

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Map<String,Object> retrieveDetails(){
        Map<String,Object> data = new HashMap<>();
        data.put("id",this.id);
        data.put("name",this.name);
        data.put("description",this.description);
        data.put("lastModificationDate",this.getLastModificationDate());
        data.put("creationDate",this.getCreationDate());
        return data;


    }
}
