package com.kairos.persistence.model.user.expertise;

import com.kairos.persistence.model.user.skill.Skill;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

import static com.kairos.persistence.model.constants.RelationshipConstants.EXPERTISE_HAS_SKILLS;

/**
 * Created by prabjot on 4/4/17.
 */
@RelationshipEntity(type = EXPERTISE_HAS_SKILLS)
public class ExpertiseSkillRelationship {

    @StartNode private Expertise expertise;
    @EndNode private Skill skill;
    @Property boolean isEnabled;

    public Expertise getExpertise() {
        return expertise;
    }

    public Skill getSkill() {
        return skill;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setExpertise(Expertise expertise) {
        this.expertise = expertise;
    }

    public void setSkill(Skill skill) {
        this.skill = skill;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }
}
