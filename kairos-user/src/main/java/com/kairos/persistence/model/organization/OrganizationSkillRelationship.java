package com.kairos.persistence.model.organization;

import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.user.skill.Skill;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

import static com.kairos.persistence.model.constants.RelationshipConstants.ORGANISATION_HAS_SKILL;


/**
 * Created by prabjot on 16/1/17.
 */
@RelationshipEntity(type = ORGANISATION_HAS_SKILL)
public class OrganizationSkillRelationship extends UserBaseEntity {

    @StartNode private Organization organization;
    @EndNode
    private Skill skill;
    private boolean isEnabled=true;
    private String iconName;
    private String visitourId;

    public Organization getOrganization() {
        return organization;
    }

    public Skill getSkill() {
        return skill;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public void setSkill(Skill skill) {
        this.skill = skill;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public String getIconName() {
        return iconName;
    }

    public void setIconName(String iconName) {
        this.iconName = iconName;
    }

    public void setVisitourId(String visitourId) {
        this.visitourId = visitourId;
    }

    public String getVisitourId() {

        return visitourId;
    }
}
