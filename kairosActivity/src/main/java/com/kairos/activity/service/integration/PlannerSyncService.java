package com.kairos.activity.service.integration;

import com.kairos.activity.client.planner.PlannerRestClient;
import com.kairos.activity.enums.IntegrationOperation;
import com.kairos.activity.response.dto.staffing_level.PresenceStaffingLevelDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PlannerSyncService {
    @Autowired
    private PlannerRestClient plannerRestClient;
    @Async
    public void publishStaffingLevel(Long unitId, PresenceStaffingLevelDto presenceStaffingLevelDto, IntegrationOperation integrationOperation){
        plannerRestClient.publish(presenceStaffingLevelDto,unitId,integrationOperation);

    }
}
