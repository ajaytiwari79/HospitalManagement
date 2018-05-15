package com.kairos.service.integration;

import com.kairos.activity.enums.IntegrationOperation;
import com.kairos.client.priority_group.PriorityGroupRestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
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
        priorityGroupRestClient.publish(null,unitId,IntegrationOperation.CREATE,"/copy_priority_group",countryDetail);
    }
}
