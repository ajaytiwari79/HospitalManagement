package com.kairos.service.integration;

import com.kairos.client.planner.PlannerRestClient;
import com.kairos.activity.enums.IntegrationOperation;
import com.kairos.persistence.model.user.staff.Staff;
import com.kairos.persistence.model.user.staff.StaffDTO;
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
    @Async
    public <T> void  publishStaff(Long unitId, Staff staff, IntegrationOperation integrationOperation) {
        plannerRestClient.publish(createStaff(staff),unitId,integrationOperation);
    }
    @Async
    public <T> void  publishStaffs(Long unitId, List<Staff> staff, IntegrationOperation integrationOperation) {
        plannerRestClient.publish(createStaffs(staff),unitId,integrationOperation);
    }

    private List<StaffDTO> createStaffs(List<Staff> staff) {
        List<StaffDTO> dtos= new ArrayList<>();
        for(Staff s:staff){
            dtos.add(createStaff(s));
        }
        return dtos;
    }

    private StaffDTO createStaff(Staff staff){
        return new StaffDTO(staff.getId(),staff.getFirstName(),staff.getLastName(),new BigInteger(staff.getCprNumber()),null,null,null,null,null,null,
                null,null,null,staff.getOrganizationId(),null,staff.getCurrentStatus());
    }
}
