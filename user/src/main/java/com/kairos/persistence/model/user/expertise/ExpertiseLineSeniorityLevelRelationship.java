package com.kairos.persistence.model.user.expertise;

import com.kairos.persistence.model.common.UserBaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

import static com.kairos.persistence.model.constants.RelationshipConstants.FOR_SENIORITY_LEVEL;

@NodeEntity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@RelationshipEntity(type = FOR_SENIORITY_LEVEL)
public class ExpertiseLineSeniorityLevelRelationship extends UserBaseEntity {
    @StartNode
    private ExpertiseLine expertiseLine;
    @EndNode
    private SeniorityLevel seniorityLevel;
    private Long payGradeId;
    private Long payGradeLevel;
}
