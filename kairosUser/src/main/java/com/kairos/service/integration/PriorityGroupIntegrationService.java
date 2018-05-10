package com.kairos.service.integration;

import com.kairos.activity.enums.IntegrationOperation;
import com.kairos.activity.response.dto.ActivityDTO;
import com.kairos.client.priority_group.PriorityGroupRestClient;
import com.kairos.response.dto.web.OrderResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
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

    public List<OrderResponseDTO> getAllOrderByUnit(long unitId){
        return priorityGroupRestClient.publish(null ,unitId,IntegrationOperation.GET,"/orders",null);

        }
    public List<ActivityDTO> getAllActivityByUnit(long unitId){
        return priorityGroupRestClient.publish(null ,unitId,IntegrationOperation.GET,"/orders",null);

    }


}
