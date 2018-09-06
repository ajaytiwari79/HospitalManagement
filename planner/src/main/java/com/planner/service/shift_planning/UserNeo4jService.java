package com.planner.service.shift_planning;

import com.planner.domain.query_results.StaffQueryResult;
import com.planner.repository.shift_planning.UserNeo4jRepo;
import com.planner.repository.shift_planning.UserNeo4jRepository;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;


@Service
public class UserNeo4jService {
    @Inject
    private UserNeo4jRepo userNeo4jRepo;


    /**
     *
     * @param staffIds
     * @return
     */
    public List<StaffQueryResult> getStaffWithSkillsAndUnitPostionIds(Long unitId,Long[] staffIds)
    {   return  userNeo4jRepo.getStaffWithSkillsAndUnitPostionIds(unitId,staffIds);

    }


}
