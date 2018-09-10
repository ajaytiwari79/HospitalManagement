package com.planner.service.shift_planning;

import com.kairos.activity.cta.CTAResponseDTO;
import com.kairos.shiftplanning.domain.Activity;
import com.kairos.shiftplanning.domain.ShiftRequestPhase;
import com.kairos.shiftplanning.domain.Employee;
import com.planner.domain.query_results.StaffQueryResult;
import org.joda.time.LocalDate;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * This service will interact with
 * {@link ActivityMongoService}
 * and
 * {@link UserNeo4jService}
 * to prepare {ShiftPlanningInitialization Data}
 *
 * @author mohit
 */
@Service
public class ShiftPlanningInitializationService {

    @Inject
    private ActivityMongoService activityMongoService;

    @Inject
    private UserNeo4jService userNeo4jService;

    @Inject
    private CTAService ctaService;


    /**
     *
     */
    public void initializeShiftPlanning(Long unitId, Date fromPlanningDate, Date toPlanningDate, Long[] staffIds) {
        //1.) Initialize Employees
        List<Employee> employeeList = getEmployees(unitId, staffIds, fromPlanningDate, toPlanningDate);
        //2.)Initialize shifts
        List<ShiftRequestPhase> shiftRequestPhase = getShiftRequestPhase();
        //3.)Initialize activities
        List<Activity> activityList = getActivities();
        //4.)Initialize activitiesPerDay
        Map<LocalDate, List<Activity>> activitiesPerDay = getActivitiesPerDay();
    }


    /*******************************************************************************************************************/
    /**
     * @param unitId
     * @param staffIds
     * @param fromPlanningDate
     * @param toPlanningDate
     * @return
     */
    private List<Employee> getEmployees(Long unitId, Long[] staffIds, Date fromPlanningDate, Date toPlanningDate) {
        List<Employee> employeeList = new ArrayList<>();
        List<StaffQueryResult> staffWithSkillsAndUnitPostionIds = userNeo4jService.getStaffWithSkillsAndUnitPostionIds(unitId, staffIds);
        List<Long> unitPositionIds = staffWithSkillsAndUnitPostionIds.stream().map(s -> s.getStaffUnitPositions()).collect(Collectors.toList());
        Map<Long, Map<java.time.LocalDate, CTAResponseDTO>> longCTAResponseDTOMap = ctaService.getunitPositionIdWithLocalDateCTAMap(unitPositionIds, fromPlanningDate, toPlanningDate);
        for (StaffQueryResult staffQueryResult : staffWithSkillsAndUnitPostionIds) {
            Employee femployee = new Employee();
            //employee.setCollectiveTimeAgreement(activityMongoService.getCTARuleTemplateByUnitPositionIds());
        }
        return employeeList;
    }

    private Long[] getAllStaffUnitPositionIds(List<StaffQueryResult> staffWithSkillsAndUnitPostionIds) {
        Long[] unitPositionIds = new Long[staffWithSkillsAndUnitPostionIds.size()];
        int i = -1;
        for (StaffQueryResult staffQueryResult : staffWithSkillsAndUnitPostionIds) {
            unitPositionIds[i + 1] = staffQueryResult.getStaffUnitPositions();
        }
        return unitPositionIds;
    }

    private List<ShiftRequestPhase> getShiftRequestPhase() {
        return null;
    }

    private List<Activity> getActivities() {
        return null;
    }

    private Map<LocalDate, List<Activity>> getActivitiesPerDay() {
        return null;
    }
}
