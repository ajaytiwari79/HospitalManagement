package com.kairos.persistence.model.user.expertise;

import com.kairos.persistence.model.common.UserBaseEntity;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.io.Serializable;

import static com.kairos.persistence.model.constants.RelationshipConstants.FOR_SENIORITY_LEVEL;
@NodeEntity
@QueryResult
public class SeniorityLevelFunction extends UserBaseEntity  implements Serializable {
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
