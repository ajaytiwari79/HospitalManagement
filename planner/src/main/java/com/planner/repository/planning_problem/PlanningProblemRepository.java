package com.planner.repository.planning_problem;

import com.kairos.dto.planner.planninginfo.PlanningProblemDTO;
import com.kairos.enums.planning_problem.PlanningProblemType;
import com.planner.domain.planning_problem.PlanningProblem;
import com.planner.repository.common.MongoBaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlanningProblemRepository extends MongoBaseRepository<PlanningProblem,String>{

    PlanningProblemDTO findPlanningProblemByType(PlanningProblemType type);
}
