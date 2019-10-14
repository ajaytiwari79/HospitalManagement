package com.kairos.persistence.model.user.skill;

import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.country.Country;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import javax.validation.constraints.NotBlank;
import java.util.HashMap;
import java.util.Map;

import static com.kairos.persistence.model.constants.RelationshipConstants.BELONGS_TO;


/**
 * SkillCategory Domain
 * SkillCategory has relationship with Skill
 */
@NodeEntity
@Getter
@Setter
public class SkillCategory extends UserBaseEntity {

    @Relationship(type = BELONGS_TO)
    Country country;

    @NotBlank(message = "error.SkillCategory.name.notEmpty")
    private String name;

    private String description;


    private boolean isEnabled = true;

    public Map<String,Object> retieveDetails() {
        Map<String,Object> objectMap = new HashMap<>();
        objectMap.put("id",this.id);
        objectMap.put("name",this.getName());
        objectMap.put("description",this.description);
        return objectMap;
    }
}
