package com.kairos.scheduler.service.scheduler_panel;

import com.kairos.scheduler.persistence.model.scheduler_panel.IntegrationSettings;
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

    public HashMap<String, Object> addIntegrationConfiguration(IntegrationSettings integrationSettings){
        save(integrationSettings);
        return filterIntegrationServiceData(integrationSettings);
    }

    public List<IntegrationSettings> getAllIntegrationServices(){
        List<Map<String,Object>> integrationService = new ArrayList<>();

        return integrationConfigurationRepository.findAllAndIsEnabledTrue();
    }

    public boolean deleteIntegrationService(BigInteger integrationServiceId){
        Optional<IntegrationSettings> objectToDelete =integrationConfigurationRepository.findById(integrationServiceId);

        if(!objectToDelete.isPresent()){
            return false;
        }
        objectToDelete.get().setEnabled(false);
        save(objectToDelete.get());
        return true;
    }

    public HashMap<String, Object> updateIntegrationService(BigInteger integrationServiceId,IntegrationSettings integrationSettings){
        Optional<IntegrationSettings> objectToUpdateOptional = integrationConfigurationRepository.findById(integrationServiceId);
        if(!objectToUpdateOptional.isPresent()){
            return null;
        }
        IntegrationSettings objectToUpdate = objectToUpdateOptional.get();
        objectToUpdate.setDescription(integrationSettings.getDescription());
        objectToUpdate.setName(integrationSettings.getName());
        objectToUpdate.setUniqueKey(integrationSettings.getUniqueKey());
        save(objectToUpdate);
        return filterIntegrationServiceData(objectToUpdate);
    }

    private HashMap<String,Object> filterIntegrationServiceData(IntegrationSettings integrationSettings){
        HashMap<String,Object> map = new HashMap<>(2);
        map.put("id", integrationSettings.getId());
        map.put("description", integrationSettings.getDescription());
        map.put("uniqueKey", integrationSettings.getUniqueKey());
        return map;
    }

}
