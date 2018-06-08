package com.kairos.service.integration;

import com.kairos.activity.enums.IntegrationOperation;
import com.kairos.activity.util.ObjectMapperUtils;
import com.kairos.client.priority_group.PriorityGroupRestClient;
import com.kairos.persistence.model.organization.OrgTypeAndSubTypeDTO;
import com.kairos.persistence.model.organization.OrganizationTypeAndSubTypeDTO;
import com.kairos.persistence.model.user.expertise.Response.OrderAndActivityDTO;
import com.kairos.response.dto.web.ActivityWithTimeTypeDTO;
import com.kairos.response.dto.web.unit_settings.TAndAGracePeriodSettingDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
public class PriorityGroupIntegrationService {
    @Autowired
    PriorityGroupRestClient priorityGroupRestClient;

    public void createDefaultPriorityGroupsFromCountry(long countryId, long unitId) {
        Map<String, Object> countryDetail = new HashMap<>();
        countryDetail.put("countryId", countryId);
        priorityGroupRestClient.publish(null, unitId,true, IntegrationOperation.CREATE, "/priority_groups", countryDetail);
    }

    public OrderAndActivityDTO getAllOrderAndActivitiesByUnit(long unitId){
        return ObjectMapperUtils.copyPropertiesByMapper(priorityGroupRestClient.publish(null ,unitId, true, IntegrationOperation.GET,"/orders_and_activities",null),OrderAndActivityDTO.class);
        }

        public void crateDefaultDataForOrganization(Long unitId,Long countryId){
            Map<String,Object> countryDetail =new HashMap<>();
            countryDetail.put("countryId",countryId);
            priorityGroupRestClient.publish(null,unitId,true, IntegrationOperation.CREATE,"/organization_default_data",countryDetail);
        }



    public ActivityWithTimeTypeDTO getAllActivitiesAndTimeTypes(long countryId){
        return ObjectMapperUtils.copyPropertiesByMapper(priorityGroupRestClient.publish(null,countryId,false,IntegrationOperation.GET,"/activities_with_time_types",null),ActivityWithTimeTypeDTO.class);
    }

    public ActivityWithTimeTypeDTO getAllActivitiesAndTimeTypesByUnit(Long unitId,Long countryId){
        Map<String, Object> countryDetail = new HashMap<>();
        countryDetail.put("countryId", countryId);
        return ObjectMapperUtils.copyPropertiesByMapper(priorityGroupRestClient.publish(null,unitId,true,IntegrationOperation.GET,"/activities_with_time_types",countryDetail),ActivityWithTimeTypeDTO.class);
    }

    public void createDefaultOpenShiftRuleTemplate(OrgTypeAndSubTypeDTO orgTypeAndSubTypeDTO, long unitId) {
        priorityGroupRestClient.publish(orgTypeAndSubTypeDTO, unitId,true, IntegrationOperation.CREATE, "/open_shift/copy_rule_template", null);
    }


    public void createDefaultGracePeriodSetting(TAndAGracePeriodSettingDTO tAndAGracePeriodSettingDTO,Long unitId) {
        priorityGroupRestClient.publish(tAndAGracePeriodSettingDTO,unitId,true,IntegrationOperation.CREATE,"/grace_period_setting",null);
    }
}

