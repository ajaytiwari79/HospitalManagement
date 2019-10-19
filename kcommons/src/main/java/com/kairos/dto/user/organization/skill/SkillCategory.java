package com.kairos.dto.user.organization.skill;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;


/**
 * SkillCategory Domain
 * SkillCategory has relationship with Skill
 */
@Getter
@Setter
@NoArgsConstructor
public class SkillCategory {
   // Country country;

    @NotEmpty(message = "error.SkillCategory.name.notEmpty") @NotNull(message = "error.SkillCategory.name.notnull")
    private String name;


    private String description;


    private boolean isEnabled = true;


    public SkillCategory(String name) {
        this.name = name;
    }



    public Map<String,Object> retieveDetails() {
        Map<String,Object> objectMap = new HashMap<>();
        objectMap.put("name",this.getName());
        objectMap.put("description",this.description);
        return objectMap;
    }
}
