package com.kairos.client.dto;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;


/**
 * SkillCategory Domain
 * SkillCategory has relationship with Skill
 */
public class SkillCategory {
   // Country basic_details;

    @NotEmpty(message = "error.SkillCategory.name.notEmpty") @NotNull(message = "error.SkillCategory.name.notnull")
    private String name;

   // @NotEmpty(message = "error.SkillCategory.description.notEmpty") @NotNull(message = "error.SkillCategory.description.notnull")
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

  //  public void setCountry(Country basic_details) {
     //   this.basic_details = basic_details;
   // }

    public void setDescription(String description) {
        this.description = description;
    }

   // public Country getCountry() {
      //  return basic_details;
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
