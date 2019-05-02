package com.kairos.persistence.model.auth;

import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.staff.personal_details.Staff;
import com.kairos.persistence.model.user.skill.Skill;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

import static com.kairos.persistence.model.constants.RelationshipConstants.STAFF_HAS_SKILLS;


/**
 * Created by oodles on 21/10/16.
 */
@RelationshipEntity(type = STAFF_HAS_SKILLS)
public class StaffSkillLevelRelationship extends UserBaseEntity {

    @StartNode
    private Staff staff;

    @EndNode
    private Skill skill;

    // Enum
    private Skill.SkillLevel skillLevel = Skill.SkillLevel.ADVANCE;
    private long startDate;
    private long endDate;
    private boolean isEnabled=true;

    public long getStartDate() {
        return startDate;
    }

    public long getEndDate() {
        return endDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }

    public Staff getStaff() {
        return staff;
    }

    public void setStaff(Staff staff) {
        this.staff = staff;
    }

    public Skill getSkill() {
        return skill;
    }

    public void setSkill(Skill skill) {
        this.skill = skill;
    }

    public Skill.SkillLevel getSkillLevel() {
        return skillLevel;
    }

    public void setSkillLevel(Skill.SkillLevel skillLevel) {
        this.skillLevel = skillLevel;
    }

    public StaffSkillLevelRelationship() {
    }

    public StaffSkillLevelRelationship(Staff staff, Skill skill, Skill.SkillLevel skillLevel) {
        this.staff = staff;
        this.skill = skill;
        this.skillLevel = skillLevel;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }
}
