package com.kairos.persistence.model.organization.team;

import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.user.skill.Skill;
import org.neo4j.ogm.annotation.*;

import static com.kairos.persistence.model.constants.RelationshipConstants.TEAM_HAS_SKILLS;


/**
 * Created by prabjot on 21/2/17.
 */
@RelationshipEntity(type = TEAM_HAS_SKILLS)
public class TeamSkillRelationship extends UserBaseEntity {

    @StartNode private Team team;
    @EndNode private Skill skill;
    @Property boolean isEnabled = true;

    public Team getTeam() {
        return team;
    }

    public Skill getSkill() {
        return skill;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public void setSkill(Skill skill) {
        this.skill = skill;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }
}
