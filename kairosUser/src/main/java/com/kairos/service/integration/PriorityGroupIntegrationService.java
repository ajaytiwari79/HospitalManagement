package com.kairos.service.integration;

import com.kairos.activity.enums.IntegrationOperation;
import com.kairos.activity.util.ObjectMapperUtils;
import com.kairos.client.priority_group.PriorityGroupRestClient;
import com.kairos.persistence.model.user.expertise.OrderAndActivityDTO;
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

    public void createDefaultPriorityGroupsFromCountry(long countryId,long unitId){
        Map<String,Object> countryDetail =new HashMap<>();
        countryDetail.put("countryId",countryId);
        priorityGroupRestClient.publish(null,unitId,true, IntegrationOperation.CREATE,"/priority_groups",countryDetail);
    }
    public OrderAndActivityDTO getAllOrderAndActivitiesByUnit(long unitId){
        return ObjectMapperUtils.copyPropertiesByMapper(priorityGroupRestClient.publish(null ,unitId, true, IntegrationOperation.GET,"/orders_and_activities",null),OrderAndActivityDTO.class);
        }
}
