package com.kairos.activity.client;

import com.kairos.activity.enums.IntegrationOperation;
import com.kairos.response.dto.web.cta.EmploymentTypeDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class GenericIntegrationService {
    @Autowired GenericRestClient genericRestClient;

    public List<EmploymentTypeDTO> getAllEmploymentType(Long countryId){
        return genericRestClient.publish(null, countryId,false, IntegrationOperation.GET, "/employment_type", null);
    }
    public Long getUnitPositionId(Long unitId, Long staffId,Long expertiseId){
      Integer value=  genericRestClient.publish(null, unitId,true, IntegrationOperation.GET, "/staff/{staffId}/expertise/{expertiseId}/unitPositionId", null,staffId,expertiseId);
      return  value.longValue();
    }


}
