package com.kairos.scheduler.service.scheduler_panel;

import com.kairos.dto.scheduler.IntegrationSettingsDTO;
import com.kairos.scheduler.persistence.model.scheduler_panel.IntegrationSettings;
import com.kairos.scheduler.persistence.repository.scheduler_panel.IntegrationConfigurationRepository;
import com.kairos.scheduler.service.MongoBaseService;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.scheduler.service.exception.ExceptionService;
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
    private IntegrationConfigurationRepository integrationConfigurationRepository;
    @Inject
    private ExceptionService exceptionService;


    public IntegrationSettingsDTO addIntegrationConfiguration(IntegrationSettings integrationSettings){
        save(integrationSettings);
        return ObjectMapperUtils.copyPropertiesByMapper(integrationSettings,IntegrationSettingsDTO.class);
    }

    public List<IntegrationSettingsDTO> getAllIntegrationServices(){

        return ObjectMapperUtils.copyPropertiesOfListByMapper(integrationConfigurationRepository.findAllAndIsEnabledTrue(),IntegrationSettingsDTO.class);
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

    public IntegrationSettingsDTO updateIntegrationService(BigInteger integrationServiceId,IntegrationSettings integrationSettings){
        Optional<IntegrationSettings> objectToUpdateOptional = integrationConfigurationRepository.findById(integrationServiceId);
        if(!objectToUpdateOptional.isPresent()){
            exceptionService.dataNotFoundByIdException("message.integrationsettings.notfound",integrationServiceId);
        }
        IntegrationSettings objectToUpdate = objectToUpdateOptional.get();
        objectToUpdate.setDescription(integrationSettings.getDescription());
        objectToUpdate.setName(integrationSettings.getName());
        objectToUpdate.setUniqueKey(integrationSettings.getUniqueKey());
        save(objectToUpdate);

        return ObjectMapperUtils.copyPropertiesByMapper(objectToUpdate,IntegrationSettingsDTO.class);
    }



}
