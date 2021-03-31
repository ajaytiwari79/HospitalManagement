package com.kairos.persistence.model.user.expertise;

import com.kairos.persistence.model.common.UserBaseEntity;
import lombok.Getter;
import lombok.Setter;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import static com.kairos.persistence.model.constants.RelationshipConstants.FOR_SENIORITY_LEVEL;
@NodeEntity
@Getter
@Setter
public class SeniorityLevelFunction extends UserBaseEntity   {
    private static final long serialVersionUID = 3924648720065690045L;
    @Relationship(type = FOR_SENIORITY_LEVEL)
    private SeniorityLevel seniorityLevel;
}
