package com.kairos.scheduler.service.scheduler_panel;

import com.kairos.scheduler.persistence.model.scheduler_panel.IntegrationConfiguration;
import com.kairos.scheduler.persistence.repository.IntegrationConfigurationRepository;
import com.kairos.scheduler.service.MongoBaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;

/**
 * Created by prabjot on 17/1/17.
 */
@Service
@Transactional
public class IntegrationConfigurationService extends MongoBaseService {

    @Inject
    IntegrationConfigurationRepository integrationConfigurationRepository;

    public HashMap<String, Object> addIntegrationConfiguration(IntegrationConfiguration integrationConfiguration){
        save(integrationConfiguration);
        return filterIntegrationServiceData(integrationConfiguration);
    }

    public List<IntegrationConfiguration> getAllIntegrationServices(){
        List<Map<String,Object>> integrationService = new ArrayList<>();

        return integrationConfigurationRepository.findAllAndIsEnabledTrue();
    }

    public boolean deleteIntegrationService(BigInteger integrationServiceId){
        Optional<IntegrationConfiguration> objectToDelete =integrationConfigurationRepository.findById(integrationServiceId);

        if(!objectToDelete.isPresent()){
            return false;
        }
        objectToDelete.get().setEnabled(false);
        save(objectToDelete.get());
        return true;
    }

    public HashMap<String, Object> updateIntegrationService(BigInteger integrationServiceId,IntegrationConfiguration integrationConfiguration){
        Optional<IntegrationConfiguration> objectToUpdateOptional = integrationConfigurationRepository.findById(integrationServiceId);
        if(!objectToUpdateOptional.isPresent()){
            return null;
        }
        IntegrationConfiguration objectToUpdate = objectToUpdateOptional.get();
        objectToUpdate.setDescription(integrationConfiguration.getDescription());
        objectToUpdate.setName(integrationConfiguration.getName());
        objectToUpdate.setUniqueKey(integrationConfiguration.getUniqueKey());
        save(objectToUpdate);
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
