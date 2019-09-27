package com.kairos.persistence.model.user.expertise;

import com.kairos.persistence.model.common.UserBaseEntity;
import lombok.Getter;
import lombok.Setter;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.StartNode;

@NodeEntity
@Getter
@Setter
public class ExpertiseLineSeniorityLevelRelationship extends UserBaseEntity {
    @StartNode
    private ExpertiseLine expertiseLine;
    @EndNode
    private SeniorityLevel seniorityLevel;
    private Long payGradeLevel;
}
