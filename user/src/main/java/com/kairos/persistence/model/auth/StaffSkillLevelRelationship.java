package com.kairos.persistence.model.auth;

import com.kairos.annotations.KPermissionRelatedModel;
import com.kairos.annotations.KPermissionRelationshipFrom;
import com.kairos.annotations.KPermissionRelationshipTo;
import com.kairos.enums.SkillLevel;
import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.staff.personal_details.Staff;
import com.kairos.persistence.model.user.skill.Skill;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

import java.time.LocalDate;

import static com.kairos.persistence.model.constants.RelationshipConstants.STAFF_HAS_SKILLS;


/**
 * Created by oodles on 21/10/16.
 */
@KPermissionRelatedModel
@RelationshipEntity(type = STAFF_HAS_SKILLS)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StaffSkillLevelRelationship extends UserBaseEntity {
    @KPermissionRelationshipFrom
    @StartNode
    private Staff staff;

    @KPermissionRelationshipTo
    @EndNode
    private Skill skill;
    private SkillLevel skillLevel = SkillLevel.ADVANCE;
    private long startDate;
    private long endDate;
    private boolean isEnabled=true;
}
