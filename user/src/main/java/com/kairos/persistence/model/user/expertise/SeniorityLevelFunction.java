package com.kairos.persistence.model.user.expertise;

import com.kairos.persistence.model.common.UserBaseEntity;
import lombok.Getter;
import lombok.Setter;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.io.Serializable;

import static com.kairos.persistence.model.constants.RelationshipConstants.FOR_SENIORITY_LEVEL;
@NodeEntity
@Getter
@Setter
public class SeniorityLevelFunction extends UserBaseEntity  implements Serializable {
    @Relationship(type = FOR_SENIORITY_LEVEL)
    private SeniorityLevel seniorityLevel;
}
