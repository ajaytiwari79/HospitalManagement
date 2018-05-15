package com.kairos.service.integration;

import com.kairos.client.WorkingTimeAgreementRestClient;
import com.kairos.client.planner.PlannerRestClient;
import com.kairos.activity.enums.IntegrationOperation;
import com.kairos.persistence.model.user.agreement.wta.WorkingTimeAgreement;
import com.kairos.persistence.model.user.country.EmploymentType;
import com.kairos.persistence.model.user.staff.Staff;
import com.kairos.persistence.model.user.staff.StaffBasicDetailsDTO;
import com.kairos.persistence.model.user.staff.StaffDTO;
import com.kairos.persistence.model.user.unit_position.UnitPosition;
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
    public void  publishStaff(Long unitId, Staff staff, IntegrationOperation integrationOperation) {
        plannerRestClient.publish(createStaffDTO(staff),unitId,integrationOperation);
    }
    @Async
    public void publishStaff(Long unitId, List<Staff> staff, IntegrationOperation integrationOperation) {
        plannerRestClient.publish(createStaffs(staff),unitId,integrationOperation);
    }
    @Async
    public void publishWTA(Long unitId,Long unitPositionId, WTAResponseDTO wtaResponseDTO, IntegrationOperation integrationOperation){
        plannerRestClient.publish(wtaResponseDTO,unitId,integrationOperation,unitPositionId);
    }
    @Async
    public void  publishUnitPosition(Long unitId, UnitPosition unitPosition, EmploymentType employmentType, IntegrationOperation integrationOperation) {
        if(integrationOperation.equals(IntegrationOperation.CREATE)){
            plannerRestClient.publish(createUnitPositionDTO(unitPosition,employmentType,unitId),unitId,integrationOperation,unitPosition.getStaff().getId());
        }else if(integrationOperation.equals(IntegrationOperation.UPDATE)){
            plannerRestClient.publish(createUnitPositionDTO(unitPosition,employmentType,unitId),unitId,integrationOperation,unitPosition.getStaff().getId(),unitPosition.getId());
        }
        else if(integrationOperation.equals(IntegrationOperation.DELETE)){
            plannerRestClient.publish(null,unitId,integrationOperation,unitPosition.getStaff().getId(),unitPosition.getId());
        }
    }

    private UnitPositionWtaDTO createUnitPositionDTO(UnitPosition unitPosition, EmploymentType employmentType,Long unitId) {
        WTAResponseDTO wtaResponseDTO=workingTimeAgreementRestClient.getWTAById(unitPosition.getWorkingTimeAgreementId());
        UnitPositionWtaDTO unitPositionWtaDTO=new UnitPositionWtaDTO(unitPosition.getId(),unitPosition.getExpertise().getId(),unitPosition.getPositionCode().getId(),unitPosition.getStartDateMillis(),unitPosition.getEndDateMillis(),
                unitPosition.getTotalWeeklyMinutes(),unitPosition.getTotalWeeklyMinutes()/60,unitPosition.getAvgDailyWorkingHours(),unitPosition.getWorkingDaysInWeek(),
                unitPosition.getHourlyWages(),unitPosition.getSalary(),employmentType.getId(),unitId,unitPosition.getSeniorityLevel().getId(),employmentType.getPaymentFrequency(),wtaResponseDTO, unitPosition.getStaff().getId());
        return unitPositionWtaDTO;
    }

    private List<StaffBasicDetailsDTO> createStaffs(List<Staff> staff) {
        List<StaffBasicDetailsDTO> dtos= new ArrayList<>();
        for(Staff s:staff){
            dtos.add(createStaffDTO(s));
        }
        return dtos;
    }

    private StaffBasicDetailsDTO createStaffDTO(Staff staff){

        return new StaffBasicDetailsDTO(staff.getId(),staff.getFirstName(),staff.getLastName(),null,staff.getCurrentStatus());

    }
}
