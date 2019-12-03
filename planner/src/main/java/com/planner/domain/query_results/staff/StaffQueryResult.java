package com.planner.domain.query_results.staff;

import com.planner.domain.query_results.expertise.ExpertiseQueryResult;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

@QueryResult
@Getter
@Setter
public class StaffQueryResult {

    private Long staffId ;
     private String staffName;
     private Long employmentId;
     private HashSet<Map> staffSkills;
     private List<ExpertiseQueryResult> employmentExpertise;

}
