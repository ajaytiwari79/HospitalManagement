package com.kairos.activity.service.integration;

import com.kairos.activity.enums.IntegrationOperation;
import com.kairos.activity.response.dto.ActivityDTO;
import com.kairos.activity.response.dto.staffing_level.StaffingLevelDto;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PlannerSyncService {
    @Async
    public void publishStaffingLevel( Long unitId, StaffingLevelDto staffingLevelDto, IntegrationOperation integrationOperation){


    }
    @Async
    public void publishActivity(Long unitId, ActivityDTO staffingLevelDto, IntegrationOperation integrationOperation){


    }
}
