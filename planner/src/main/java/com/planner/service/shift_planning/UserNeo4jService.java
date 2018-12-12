package com.planner.service.shift_planning;

import com.planner.domain.query_results.staff.StaffQueryResult;
import com.planner.repository.shift_planning.UserNeo4jRepo;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;


@Service
public class UserNeo4jService {
    @Inject
    private UserNeo4jRepo userNeo4jRepo;


    /**
     * Filter staff have no unitPositionId in this unit
     *
     * @param staffIds
     * @return
     */
    public List<StaffQueryResult> getStaffWithSkillsAndUnitPostionIds(Long unitId, List<Long> staffIds) {
        List<StaffQueryResult> staffQueryResults = new ArrayList<>();
        for (StaffQueryResult staffQueryResult : userNeo4jRepo.getStaffWithSkillsAndUnitPostionIds(unitId, staffIds)) {
            if (staffQueryResult.getStaffUnitPosition() != null) staffQueryResults.add(staffQueryResult);
        }
        return staffQueryResults;

    }


}
