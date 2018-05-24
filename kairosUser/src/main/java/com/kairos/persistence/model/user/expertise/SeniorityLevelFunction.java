package com.kairos.persistence.model.user.expertise;

import com.kairos.persistence.model.common.UserBaseEntity;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import static com.kairos.persistence.model.constants.RelationshipConstants.FOR_SENIORITY_LEVEL;
@NodeEntity
public class SeniorityLevelFunction extends UserBaseEntity {
    @Relationship(type = FOR_SENIORITY_LEVEL)
    private SeniorityLevel seniorityLevel;
    public SeniorityLevelFunction() {
        // DC
    }

    public SeniorityLevel getSeniorityLevel() {
        return seniorityLevel;
    }

    public void setSeniorityLevel(SeniorityLevel seniorityLevel) {
        this.seniorityLevel = seniorityLevel;
    }
}
