package com.kairos.persistence.model.organization_type;

import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.List;
import java.util.Map;

/**
 * Created by prabjot on 12/4/17.
 */
@QueryResult
public class OrgTypeSkillQueryResult {

    private List<Map<String,Object>> skill;

    public void setSkill(List<Map<String, Object>> skill) {
        this.skill = skill;
    }

    public List<Map<String, Object>> getSkill() {
        return skill;
    }
}
