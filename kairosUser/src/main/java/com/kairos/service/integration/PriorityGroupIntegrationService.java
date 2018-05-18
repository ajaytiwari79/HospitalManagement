package com.kairos.service.integration;

import com.kairos.activity.enums.IntegrationOperation;
import com.kairos.activity.response.dto.ActivityDTO;
import com.kairos.activity.util.ObjectMapperUtils;
import com.kairos.client.priority_group.PriorityGroupRestClient;
import com.kairos.persistence.model.user.expertise.OrderAndActivityDTO;
import com.kairos.response.dto.web.ActivityWithTimeTypeDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class PriorityGroupIntegrationService {
    @Autowired
    PriorityGroupRestClient priorityGroupRestClient;


    public void createDefaultPriorityGroupsFromCountry(long countryId, long unitId) { Map<String, Object> countryDetail = new HashMap<>();
        countryDetail.put("countryId", countryId);
        priorityGroupRestClient.publish(null, unitId,true, IntegrationOperation.CREATE, "/copy_priority_group", countryDetail);
    }

    public OrderAndActivityDTO getAllOrderAndActivitiesByUnit(long unitId) {
        return ObjectMapperUtils.copyPropertiesByMapper(priorityGroupRestClient.publish(null, unitId,true, IntegrationOperation.GET, "/orders_and_activities", null), OrderAndActivityDTO.class);
    }

    public ActivityWithTimeTypeDTO getAllActivitiesAndTimeTypes(long countryId){
        return priorityGroupRestClient.publish(null,countryId,false,IntegrationOperation.GET,"/activities_with_time_types",null);
    }

}
