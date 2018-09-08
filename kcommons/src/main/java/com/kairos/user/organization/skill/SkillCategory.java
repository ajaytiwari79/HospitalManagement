package com.kairos.user.organization.skill;
import java.util.HashMap;
import java.util.Map;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;


/**
 * SkillCategory Domain
 * SkillCategory has relationship with Skill
 */
public class SkillCategory {
   // Country country;

    @NotEmpty(message = "error.SkillCategory.name.notEmpty") @NotNull(message = "error.SkillCategory.name.notnull")
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

  //  public void setCountryId(Country country) {
     //   this.country = country;
   // }

    public void setDescription(String description) {
        this.description = description;
    }

   // public Country getCountryId() {
      //  return country;
   // }

    public String getDescription() {
        return description;
    }

    public Map<String,Object> retieveDetails() {
        Map<String,Object> objectMap = new HashMap<>();
       // objectMap.put("id",this.id);
        objectMap.put("name",this.getName());
        objectMap.put("description",this.description);
        return objectMap;
    }
}
