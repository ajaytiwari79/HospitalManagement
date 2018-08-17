package com.kairos.service.tpa_services;
import com.kairos.persistence.model.user.tpa_services.IntegrationConfiguration;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.persistence.repository.user.tpa_services.IntegrationConfigurationGraphRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by prabjot on 17/1/17.
 */
@Service
@Transactional
public class IntegrationConfigurationService {

    @Inject
    CountryGraphRepository countryGraphRepository;

    @Inject
    IntegrationConfigurationGraphRepository integrationConfigurationGraphRepository;

    public HashMap<String, Object> addIntegrationConfiguration(IntegrationConfiguration integrationConfiguration){
        integrationConfigurationGraphRepository.save(integrationConfiguration);
        return filterIntegrationServiceData(integrationConfiguration);
    }

    public List<Map<String,Object>> getAllIntegrationServices(){
        List<Map<String,Object>> integrationService = new ArrayList<>();
        for(Map<String,Object> map : integrationConfigurationGraphRepository.getAllIntegrationServices()){
            integrationService.add((Map<String,Object>)map.get("integrationConfiguration"));
        }

        return integrationService;
    }

    public boolean deleteIntegrationService(long integrationServiceId){
        IntegrationConfiguration objectToDelete =integrationConfigurationGraphRepository.findOne(integrationServiceId);
        if(objectToDelete == null){
            return false;
        }
        objectToDelete.setEnabled(false);
        integrationConfigurationGraphRepository.save(objectToDelete);
        return true;
    }

    public HashMap<String, Object> updateIntegrationService(long integrationServiceId,IntegrationConfiguration integrationConfiguration){
        IntegrationConfiguration objectToUpdate = integrationConfigurationGraphRepository.findOne(integrationServiceId);
        if(objectToUpdate == null){
            return null;
        }
        objectToUpdate.setDescription(integrationConfiguration.getDescription());
        objectToUpdate.setName(integrationConfiguration.getName());
        objectToUpdate.setUniqueKey(integrationConfiguration.getUniqueKey());
        integrationConfigurationGraphRepository.save(objectToUpdate);
        return filterIntegrationServiceData(objectToUpdate);
    }

    private HashMap<String,Object> filterIntegrationServiceData(IntegrationConfiguration integrationConfiguration){
        HashMap<String,Object> map = new HashMap<>(2);
        map.put("id",integrationConfiguration.getId());
        map.put("description",integrationConfiguration.getDescription());
        map.put("uniqueKey",integrationConfiguration.getUniqueKey());
        return map;
    }

}
