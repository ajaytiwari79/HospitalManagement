package com.kairos.shiftplanning.integration;

import com.amazonaws.HttpMethod;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.planner.shift_planning.ShiftPlanningProblemSubmitDTO;
import com.kairos.dto.response.ResponseDTO;

import java.util.HashMap;

import static com.kairos.shiftplanning.integration.RestClientUtil.publishRequest;

public class ActivityIntegration {

    public void updateDataOfShiftForPlanning(ShiftPlanningProblemSubmitDTO shiftPlanningProblemSubmitDTO){
        try {
            ResponseDTO responseDTO = publishRequest(shiftPlanningProblemSubmitDTO,"/kairos/activity/api/v1/unit/2403/get_details_for_auto_planning", HttpMethod.POST,new HashMap<>());
            ShiftPlanningProblemSubmitDTO submitDTO = ObjectMapperUtils.copyPropertiesByMapper(responseDTO.getData(), ShiftPlanningProblemSubmitDTO.class);
            shiftPlanningProblemSubmitDTO.setShifts(submitDTO.getShifts());
            shiftPlanningProblemSubmitDTO.setStaffingLevels(submitDTO.getStaffingLevels());
            shiftPlanningProblemSubmitDTO.setActivityConfiguration(submitDTO.getActivityConfiguration());
            shiftPlanningProblemSubmitDTO.setActivities(submitDTO.getActivities());
            shiftPlanningProblemSubmitDTO.setEmploymentIdAndWTAResponseMap(submitDTO.getEmploymentIdAndWTAResponseMap());
            shiftPlanningProblemSubmitDTO.setEmploymentIdAndCTAResponseMap(submitDTO.getEmploymentIdAndCTAResponseMap());
            shiftPlanningProblemSubmitDTO.setPlanningPeriod(submitDTO.getPlanningPeriod());
            shiftPlanningProblemSubmitDTO.setTimeTypeMap(submitDTO.getTimeTypeMap());
        } catch (Exception e) {
            throw new RuntimeException("There is some problem in fetching staff from Activity Service");
        }
    }

}
