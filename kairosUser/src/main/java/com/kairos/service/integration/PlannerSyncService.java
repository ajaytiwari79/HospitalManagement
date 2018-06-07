package com.kairos.service.integration;

import com.kairos.client.WorkingTimeAgreementRestClient;
import com.kairos.client.planner.PlannerRestClient;
import com.kairos.activity.enums.IntegrationOperation;
import com.kairos.persistence.model.user.country.EmploymentType;
import com.kairos.response.dto.web.staff.Staff;
import com.kairos.persistence.model.user.unit_position.UnitPosition;
import com.kairos.persistence.model.user.unit_position.UnitPositionEmploymentTypeRelationShip;
import com.kairos.response.dto.web.UnitPositionWtaDTO;
import com.kairos.response.dto.web.wta.WTAResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class PlannerSyncService {
    private final Logger logger = LoggerFactory.getLogger(PlannerSyncService.class);
    @Autowired
    @Qualifier("optaplannerServiceRestClient")
    private PlannerRestClient plannerRestClient;
    @Autowired
    private WorkingTimeAgreementRestClient workingTimeAgreementRestClient;
    @Async
    public void  publishStaff(Long unitId, com.kairos.persistence.model.user.staff.Staff staff, IntegrationOperation integrationOperation) {
        plannerRestClient.publish(createStaffDTO(staff),unitId,integrationOperation);
    }
    @Async
    public void publishAllStaff(Long unitId, List<com.kairos.persistence.model.user.staff.Staff> staff, IntegrationOperation integrationOperation) {
        plannerRestClient.publish(createStaffList(staff),unitId,integrationOperation);
    }
    @Async
    public void publishWTA(Long unitId,Long unitPositionId, WTAResponseDTO wtaResponseDTO, IntegrationOperation integrationOperation){
        plannerRestClient.publish(wtaResponseDTO,unitId,integrationOperation,unitPositionId);
    }
    @Async
    public void  publishUnitPosition(Long unitId, UnitPosition unitPosition, EmploymentType employmentType, IntegrationOperation integrationOperation) {
        if(integrationOperation.equals(IntegrationOperation.CREATE)){
            plannerRestClient.publish(createUnitPositionDTO(unitPosition,employmentType,unitId,null),unitId,integrationOperation,unitPosition.getStaff().getId());
        }else if(integrationOperation.equals(IntegrationOperation.UPDATE)){
            plannerRestClient.publish(createUnitPositionDTO(unitPosition,employmentType,unitId,null),unitId,integrationOperation,unitPosition.getStaff().getId(),unitPosition.getId());
        }
        else if(integrationOperation.equals(IntegrationOperation.DELETE)){
            plannerRestClient.publish(null,unitId,integrationOperation,unitPosition.getStaff().getId(),unitPosition.getId());
        }
    }

    /*@Async
    public void  publishAllUnitPositions(Long unitId, List<UnitPosition> unitPosition, EmploymentType employmentType, IntegrationOperation integrationOperation) {
        if(integrationOperation.equals(IntegrationOperation.CREATE)){
            plannerRestClient.publish(createUnitPositionDTO(unitPosition,employmentType,unitId),unitId,integrationOperation,unitPosition.getStaff().getId());
        }else if(integrationOperation.equals(IntegrationOperation.UPDATE)){
            plannerRestClient.publish(createUnitPositionDTO(unitPosition,employmentType,unitId),unitId,integrationOperation,unitPosition.getStaff().getId(),unitPosition.getId());
        }
        else if(integrationOperation.equals(IntegrationOperation.DELETE)){
            plannerRestClient.publish(null,unitId,integrationOperation,unitPosition.getStaff().getId(),unitPosition.getId());
        }
    }*/


    private UnitPositionWtaDTO createUnitPositionDTO(UnitPosition unitPosition, EmploymentType employmentType,Long unitId,WTAResponseDTO wtaResponseDTO) {
        if(wtaResponseDTO==null){
            wtaResponseDTO=workingTimeAgreementRestClient.getWTAById(unitPosition.getWorkingTimeAgreementId());
        }
        UnitPositionWtaDTO unitPositionWtaDTO=new UnitPositionWtaDTO(unitPosition.getId(),unitPosition.getExpertise().getId(),unitPosition.getPositionCode().getId(),unitPosition.getStartDateMillis(),unitPosition.getEndDateMillis(),
                unitPosition.getTotalWeeklyMinutes(),unitPosition.getTotalWeeklyMinutes()/60,unitPosition.getAvgDailyWorkingHours(),unitPosition.getWorkingDaysInWeek(),
                unitPosition.getHourlyWages(),unitPosition.getSalary(),employmentType.getId(),unitId,unitPosition.getSeniorityLevel().getId(),employmentType.getPaymentFrequency(),wtaResponseDTO, unitPosition.getStaff().getId());
        return unitPositionWtaDTO;
    }

    private List<UnitPositionWtaDTO> createUnitPositionDTOs(Long unitId,List<UnitPositionEmploymentTypeRelationShip> unitPositionEmploymentTypeRelationShips) {
        List<UnitPositionWtaDTO> unitPositionWtaDTOS=new ArrayList<>();
        List<BigInteger> upIds=unitPositionEmploymentTypeRelationShips.stream().map(upr->upr.getUnitPosition().getWorkingTimeAgreementId()).collect(Collectors.toList());
        List<WTAResponseDTO> wtaResponseDTOS=workingTimeAgreementRestClient.getWTAByIds(upIds);
        Map<BigInteger,WTAResponseDTO> wtaResponseDTOMap = wtaResponseDTOS.stream().collect(Collectors.toMap(w->w.getId(), w->w));
        unitPositionEmploymentTypeRelationShips.forEach(upr->{
            unitPositionWtaDTOS.add(createUnitPositionDTO(upr.getUnitPosition(),upr.getEmploymentType(),unitId,wtaResponseDTOMap.get(upr.getUnitPosition().getWorkingTimeAgreementId())));
        });
        return unitPositionWtaDTOS;
    }

    private List<Staff> createStaffList(List<com.kairos.persistence.model.user.staff.Staff> staff) {
        List<Staff> dtos= new ArrayList<>();
        for(com.kairos.persistence.model.user.staff.Staff s:staff){
            dtos.add(createStaffDTO(s));
        }
        return dtos;
    }

    private Staff createStaffDTO(com.kairos.persistence.model.user.staff.Staff staff){

        return new Staff(staff.getId(),staff.getFirstName(),staff.getLastName(),null,staff.getCurrentStatus());

    }

    public void publishAllUnitPositions(Long organisationId, List<UnitPositionEmploymentTypeRelationShip> unitPositionEmploymentTypeRelationShips, IntegrationOperation create) {

    }
}
