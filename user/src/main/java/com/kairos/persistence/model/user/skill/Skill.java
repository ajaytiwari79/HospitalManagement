package com.kairos.persistence.model.user.skill;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.dto.user.country.skill.SkillDTO;
import com.kairos.persistence.model.auth.User;
import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.country.tag.Tag;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;


/**
 * Skill Domain extends UserBaseEntity
 * SKill has relationship with com.kairos.enums.SkillLevel Domain
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@NodeEntity
@Getter
@Setter
@NoArgsConstructor
public class Skill extends UserBaseEntity {
    @NotBlank(message = "error.Skill.name.notEmpty")
    private String name;
    private String description;
    private boolean isEnabled = true;
    private String shortName;
    private SkillStatus skillStatus;
    @Relationship(type = HAS_CATEGORY)
    private SkillCategory skillCategory;
    @Relationship(type = REQUESTED_BY)
    private User requestedBy;
    @Relationship(type = APPROVED_BY)
    private User approvedBy;
    @Relationship(type = HAS_TAG)
    private List<Tag> tags = new ArrayList<>();
    //time care id
    private String externalId;

    public Skill(String name, SkillCategory skillCategory) {
        this.name = name;
        this.skillCategory = skillCategory;
    }

    public Skill(SkillDTO skillDTO){
        this.name = skillDTO.getName();
        this.description = skillDTO.getDescription();
        this.shortName = skillDTO.getShortName();
    }

    public Map<String, Object> retrieveDetails() {
        Map<String,Object> data = new HashMap<>();
        data.put("id",this.id);
        data.put("name",this.name);
        data.put("description",this.description);
        data.put("shortName",this.shortName);
        return data;
    }

    public enum SkillStatus {
        PENDING, APPROVED, REJECTED;
    }
}
