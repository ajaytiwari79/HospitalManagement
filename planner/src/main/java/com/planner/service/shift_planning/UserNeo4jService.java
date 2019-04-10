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
     * Filter staff have no employmentId in this unit
     *
     * @param staffIds
     * @return
     */
    public List<StaffQueryResult> getStaffWithSkillsAndEmploymentIds(Long unitId, List<Long> staffIds) {
        List<StaffQueryResult> staffQueryResults = new ArrayList<>();
        for (StaffQueryResult staffQueryResult : userNeo4jRepo.getStaffWithSkillsAndEmploymentIds(unitId, staffIds)) {
            if (staffQueryResult.getEmploymentId() != null) staffQueryResults.add(staffQueryResult);
        }
        return staffQueryResults;

    }


}
