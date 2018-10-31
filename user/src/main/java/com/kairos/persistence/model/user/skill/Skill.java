package com.kairos.persistence.model.user.skill;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.auth.User;
import com.kairos.persistence.model.country.tag.Tag;
import com.kairos.dto.user.country.skill.SkillDTO;
import org.hibernate.validator.constraints.NotEmpty;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;


/**
 * Skill Domain extends UserBaseEntity
 * SKill has relationship with SkillLevel Domain
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@NodeEntity
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
    User requestedBy;
    @Relationship(type = APPROVED_BY)
    User approvedBy;
    @Relationship(type = HAS_TAG)
    private List<Tag> tags = new ArrayList<>();
    //time care id
    private String externalId;

    public Skill(String name, SkillCategory skillCategory) {
        this.name = name;
        this.skillCategory = skillCategory;
    }

    public Skill() {
    }

    public Skill(SkillDTO skillDTO){
        this.name = skillDTO.getName();
        this.description = skillDTO.getDescription();
        this.shortName = skillDTO.getShortName();
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public SkillCategory getSkillCategory() {
        return skillCategory;
    }

    public void setSkillCategory(SkillCategory skillCategory) {
        this.skillCategory = skillCategory;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String, Object> retrieveDetails() {
        Map<String,Object> data = new HashMap<>();
        data.put("id",this.id);
        data.put("name",this.name);
        data.put("description",this.description);
        data.put("shortName",this.shortName);
        return data;
    }

    public enum SkillLevel {

        BASIC("Basic"), ADVANCE("Advance"), EXPERT("Expert");
        public String value;

        SkillLevel(String value) {
            this.value = value;
        }

        public static SkillLevel getByValue(String value) {
            for (SkillLevel skillLevel : SkillLevel.values()) {
                if (skillLevel.value.equals(value)) {
                    return skillLevel;
                }
            }
            return null;
        }
    }

    public SkillStatus getSkillStatus() {
        return skillStatus;
    }

    public void setSkillStatus(SkillStatus skillStatus) {
        this.skillStatus = skillStatus;
    }

    public User getRequestedBy() {
        return requestedBy;
    }

    public User getApprovedBy() {
        return approvedBy;
    }

    public void setRequestedBy(User requestedBy) {
        this.requestedBy = requestedBy;
    }

    public void setApprovedBy(User approvedBy) {
        this.approvedBy = approvedBy;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public enum SkillStatus {
        PENDING, APPROVED, REJECTED;
    }
}
