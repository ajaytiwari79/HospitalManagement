package com.kairos.activity.service.integration;

import com.kairos.activity.client.dto.RestTemplateResponseEnvelope;
import com.kairos.activity.client.planner.PlannerRestClient;
import com.kairos.activity.enums.IntegrationOperation;
import com.kairos.activity.persistence.model.activity.Activity;
import com.kairos.activity.response.dto.staffing_level.PresenceStaffingLevelDto;
import com.kairos.activity.response.dto.staffing_level.StaffingLevelDTO;
import com.kairos.activity.service.organization.OrganizationActivityService;
import com.kairos.client.dto.activity.ActivityNoTabsDTO;
import com.kairos.response.dto.web.wta.WTAResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

@Service
@Transactional
public class PlannerSyncService {
    private final Logger logger = LoggerFactory.getLogger(OrganizationActivityService.class);
    @Autowired
    private PlannerRestClient plannerRestClient;
    @Async
    public Future<RestTemplateResponseEnvelope<Map>> publishStaffingLevel(Long unitId, StaffingLevelDTO staffingLevelDto, IntegrationOperation integrationOperation){
        return new AsyncResult(plannerRestClient.publish(staffingLevelDto,unitId,integrationOperation));
    }
    @Async
    public void publishStaffingLevels(Long unitId, List<StaffingLevelDTO> staffingLevelDtos, IntegrationOperation integrationOperation){
        plannerRestClient.publish(staffingLevelDtos,unitId,integrationOperation);
    }
    @Async
    public void publishActivity(Long unitId, Activity activity, IntegrationOperation integrationOperation){
        plannerRestClient.publish(createActivityDTO(activity),unitId,integrationOperation);
    }
    @Async
    public void publishActivities(Long unitId, List<Activity> activities, IntegrationOperation integrationOperation){
        plannerRestClient.publish(createActivityDTOs(activities),unitId,integrationOperation);
    }



    public List<ActivityNoTabsDTO> createActivityDTOs(List<Activity> activities) {
        List<ActivityNoTabsDTO> dtos= new ArrayList<>();
        for(Activity ac: activities){
            dtos.add(createActivityDTO(ac));
        }
        return dtos;
    }

    public ActivityNoTabsDTO createActivityDTO(Activity activity) {
        return new ActivityNoTabsDTO(activity.getId(),activity.getName(),activity.getExpertises(),activity.getDescription(),activity.getGeneralActivityTab().getCategoryId(),activity.getSkillActivityTab().getActivitySkillIds(),activity.getEmploymentTypes(),0l,0l,0l);
    }
}
