package com.kairos.service.integration;

import com.kairos.activity.activity.activity_tabs.ActivityNoTabsDTO;
import com.kairos.activity.staffing_level.StaffingLevelPlanningDTO;
import com.kairos.rest_client.RestTemplateResponseEnvelope;
import com.kairos.rest_client.planner.PlannerRestClient;
import com.kairos.enums.IntegrationOperation;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.service.organization.OrganizationActivityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
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
    public Future<RestTemplateResponseEnvelope<Map>> publishStaffingLevel(Long unitId, StaffingLevelPlanningDTO staffingLevelPlanningDto, IntegrationOperation integrationOperation) {
        return null;//new AsyncResult(plannerRestClient.publish(staffingLevelDto, unitId, integrationOperation));
    }

    @Async
    public void publishStaffingLevels(Long unitId, List<StaffingLevelPlanningDTO> staffingLevelPlanningDtos, IntegrationOperation integrationOperation) {
        //plannerRestClient.publish(staffingLevelDtos, unitId, integrationOperation);
    }

    @Async
    public void publishActivity(Long unitId, Activity activity, IntegrationOperation integrationOperation) {
       // plannerRestClient.publish(createActivityDTO(activity), unitId, integrationOperation);
    }

    @Async
    public void publishActivities(Long unitId, List<Activity> activities, IntegrationOperation integrationOperation) {
        //plannerRestClient.publish(createActivityDTOs(activities), unitId, integrationOperation);
    }


    public List<ActivityNoTabsDTO> createActivityDTOs(List<Activity> activities) {
        List<ActivityNoTabsDTO> dtos = new ArrayList<>();
        for (Activity ac : activities) {
            dtos.add(createActivityDTO(ac));
        }
        return dtos;
    }

    public ActivityNoTabsDTO createActivityDTO(Activity activity) {
        return new ActivityNoTabsDTO(activity.getId(), activity.getName(), activity.getExpertises(), activity.getDescription(), activity.getSkillActivityTab() == null ? new ArrayList<>() : activity.getSkillActivityTab().getActivitySkillIds(), activity.getEmploymentTypes(), 0l, 0l, 0l);
    }
}
