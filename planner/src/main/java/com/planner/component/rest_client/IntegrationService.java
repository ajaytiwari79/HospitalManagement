package com.planner.component.rest_client;

import com.kairos.commons.client.RestTemplateResponseEnvelope;
import com.kairos.dto.planner.shift_planning.ShiftPlanningProblemSubmitDTO;
import com.kairos.dto.planner.solverconfig.DefaultDataDTO;
import com.kairos.dto.user.organization.OrganizationServiceDTO;
import com.kairos.enums.rest_client.RestClientUrlType;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;

@Service
@Transactional
public class IntegrationService {
    @Inject
    GenericRestClient genericRestClient;

    public DefaultDataDTO getDefaultDataForSolverConfig(Long unitId) {
        return genericRestClient.publishRequest(null, unitId, RestClientUrlType.UNIT, HttpMethod.GET, "/get_default_data_for_solver_cofig", null,true, new ParameterizedTypeReference<RestTemplateResponseEnvelope<DefaultDataDTO>>(){});
    }

    public List<OrganizationServiceDTO> getOrganisationServiceByunitId(Long unitId) {
        return genericRestClient.publishRequest(null, unitId, RestClientUrlType.UNIT, HttpMethod.GET, "/get_organisation_services_by_unit", null,false, new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<OrganizationServiceDTO>>>(){});
    }

    public void updateDataOfShiftForPlanningFromActivityService(ShiftPlanningProblemSubmitDTO shiftPlanningProblemSubmitDTO){
        try {
            //ResponseDTO responseDTO = publishRequest(shiftPlanningProblemSubmitDTO,"/kairos/activity/api/v1/unit/2403/get_details_for_auto_planning", com.amazonaws.HttpMethod.POST,new HashMap<>());
            ShiftPlanningProblemSubmitDTO submitDTO = genericRestClient.publishRequest(shiftPlanningProblemSubmitDTO, shiftPlanningProblemSubmitDTO.getUnitId(), RestClientUrlType.UNIT, HttpMethod.POST, "/get_details_for_auto_planning", null,true, new ParameterizedTypeReference<RestTemplateResponseEnvelope<ShiftPlanningProblemSubmitDTO>>(){});
            //ShiftPlanningProblemSubmitDTO submitDTO = ObjectMapperUtils.copyPropertiesByMapper(responseDTO.getData(), ShiftPlanningProblemSubmitDTO.class);
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

    public void updateDataOfShiftForPlanningFromUserService(ShiftPlanningProblemSubmitDTO shiftPlanningProblemSubmitDTO) {
        try {
            ShiftPlanningProblemSubmitDTO submitDTO = genericRestClient.publishRequest(shiftPlanningProblemSubmitDTO.getStaffIds(), shiftPlanningProblemSubmitDTO.getUnitId(), RestClientUrlType.UNIT, HttpMethod.POST, "/staff/get_all_staff_for_planning", null,false, new ParameterizedTypeReference<RestTemplateResponseEnvelope<ShiftPlanningProblemSubmitDTO>>(){});
            /*ResponseDTO responseDTO = publishRequest(shiftPlanningProblemSubmitDTO.getStaffIds(),"/kairos/user/api/v1/unit/2403/staff/get_all_staff_for_planning", com.amazonaws.HttpMethod.POST,new HashMap<>());
            ShiftPlanningProblemSubmitDTO submitDTO = ObjectMapperUtils.copyPropertiesByMapper(responseDTO.getData(), ShiftPlanningProblemSubmitDTO.class);
            */shiftPlanningProblemSubmitDTO.setStaffs(submitDTO.getStaffs());
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

