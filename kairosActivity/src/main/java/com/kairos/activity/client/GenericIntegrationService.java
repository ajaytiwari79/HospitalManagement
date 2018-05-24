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
}
