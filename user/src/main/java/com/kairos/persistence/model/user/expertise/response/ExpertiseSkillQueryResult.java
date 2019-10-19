package com.kairos.persistence.model.user.expertise.response;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.List;
import java.util.Map;

/**
 * Created by prabjot on 4/4/17.
 */
@QueryResult
@Getter
@Setter
public class ExpertiseSkillQueryResult {
    private List<Map<String,Object>> skills;
}
