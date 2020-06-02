package com.kairos.shiftplanning.integration;

import com.amazonaws.HttpMethod;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.planner.shift_planning.ShiftPlanningProblemSubmitDTO;
import com.kairos.dto.response.ResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

import static com.kairos.shiftplanning.integration.RestClientUtil.publishRequest;

public class UserIntegration {

    public static final Logger LOGGER = LoggerFactory.getLogger(UserIntegration.class);

    public void updateDataOfShiftForPlanning(ShiftPlanningProblemSubmitDTO shiftPlanningProblemSubmitDTO) {
        try {
            ResponseDTO responseDTO = publishRequest(shiftPlanningProblemSubmitDTO.getStaffIds(),"/kairos/user/api/v1/unit/2403/staff/get_all_staff_for_planning",HttpMethod.POST,new HashMap<>());
            ShiftPlanningProblemSubmitDTO submitDTO = ObjectMapperUtils.copyPropertiesByMapper(responseDTO.getData(), ShiftPlanningProblemSubmitDTO.class);
            shiftPlanningProblemSubmitDTO.setStaffs(submitDTO.getStaffs());
            shiftPlanningProblemSubmitDTO.setExpertiseNightWorkerSettingMap(submitDTO.getExpertiseNightWorkerSettingMap());
            shiftPlanningProblemSubmitDTO.setDayTypeMap(submitDTO.getDayTypeMap());
            shiftPlanningProblemSubmitDTO.setTimeSlotMap(submitDTO.getTimeSlotMap());
            shiftPlanningProblemSubmitDTO.setExpertiseNightWorkerSettingMap(submitDTO.getExpertiseNightWorkerSettingMap());
            shiftPlanningProblemSubmitDTO.setBreakSettingMap(submitDTO.getBreakSettingMap());
        } catch (Exception e) {
            throw new RuntimeException("There is some problem in fetching staff from userservice");
        }
    }

}
