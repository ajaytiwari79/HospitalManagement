package com.kairos.persistence.model.user.expertise.response;

import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.List;
import java.util.Map;

/**
 * Created by prabjot on 4/4/17.
 */
@QueryResult
public class ExpertiseSkillQueryResult {

    private List<Map<String,Object>> skills;

    public List<Map<String, Object>> getSkills() {
        return skills;
    }

    public void setSkills(List<Map<String, Object>> skills) {
        this.skills = skills;
    }
}
