package com.kairos.persistence.model.user.skill;

import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.country.Country;
import org.hibernate.validator.constraints.NotEmpty;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

import static com.kairos.persistence.model.constants.RelationshipConstants.BELONGS_TO;


/**
 * SkillCategory Domain
 * SkillCategory has relationship with Skill
 */
@NodeEntity
public class SkillCategory extends UserBaseEntity {

    @Relationship(type = BELONGS_TO)
    Country country;

    @NotBlank(message = "error.SkillCategory.name.notEmpty")
    private String name;

    private String description;


    private boolean isEnabled = true;



    public SkillCategory() {

    }

    public SkillCategory(String name) {
        this.name = name;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Country getCountry() {
        return country;
    }

    public String getDescription() {
        return description;
    }

    public Map<String,Object> retieveDetails() {
        Map<String,Object> objectMap = new HashMap<>();
        objectMap.put("id",this.id);
        objectMap.put("name",this.getName());
        objectMap.put("description",this.description);
        return objectMap;
    }
}
