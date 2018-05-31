package com.kairos.activity.client;

import com.kairos.activity.enums.IntegrationOperation;
import com.kairos.activity.util.ObjectMapperUtils;
import com.kairos.response.dto.web.open_shift.PriorityGroupDefaultData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class GenericIntegrationService {
    @Autowired GenericRestClient genericRestClient;

    public Long getUnitPositionId(Long unitId, Long staffId,Long expertiseId){
      Integer value=  genericRestClient.publish(null, unitId,true, IntegrationOperation.GET, "/staff/{staffId}/expertise/{expertiseId}/unitPositionId", null,staffId,expertiseId);
      return  value.longValue();
    }

    public PriorityGroupDefaultData getExpertiseAndEmployment(Long countryId){
        return ObjectMapperUtils.copyPropertiesByMapper(genericRestClient.publish(null, countryId,false, IntegrationOperation.GET, "/country/"+countryId+"/employment_type_and_expertise", null), PriorityGroupDefaultData.class);
    }

    public PriorityGroupDefaultData getExpertiseAndEmploymentForUnit(Long unitId){
        return ObjectMapperUtils.copyPropertiesByMapper(genericRestClient.publish(null, unitId,true, IntegrationOperation.GET, "/employment_type_and_expertise", null), PriorityGroupDefaultData.class);
    }
}