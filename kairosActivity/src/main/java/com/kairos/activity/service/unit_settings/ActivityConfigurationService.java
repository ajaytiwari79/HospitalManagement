package com.kairos.activity.service.unit_settings;

import com.kairos.activity.client.OrganizationRestClient;
import com.kairos.activity.persistence.model.activity.TimeType;
import com.kairos.activity.persistence.model.unit_settings.ActivityConfiguration;
import com.kairos.activity.persistence.repository.activity.PlannedTimeTypeRepository;
import com.kairos.activity.persistence.repository.activity.TimeTypeMongoRepository;
import com.kairos.activity.persistence.repository.phase.PhaseMongoRepository;
import com.kairos.activity.persistence.repository.unit_settings.ActivityConfigurationRepository;
import com.kairos.activity.response.dto.activity.TimeTypeDTO;
import com.kairos.activity.service.MongoBaseService;
import com.kairos.activity.service.exception.ExceptionService;
import com.kairos.enums.unit_settings.TimeTypeEnum;
import com.kairos.response.dto.web.phase.PhaseDTO;
import com.kairos.response.dto.web.presence_type.PresenceTypeDTO;
import com.kairos.response.dto.web.unit_settings.activity_configuration.ActivityConfigurationDTO;
import com.kairos.response.dto.web.unit_settings.activity_configuration.ActivityConfigurationWrapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ActivityConfigurationService extends MongoBaseService {

    @Inject
    private ActivityConfigurationRepository activityConfigurationRepository;
    @Inject
    private TimeTypeMongoRepository timeTypeMongoRepository;
    @Inject
    private ExceptionService exceptionService;
    @Inject
    private PhaseMongoRepository phaseMongoRepository;
    @Inject
    private OrganizationRestClient organizationRestClient;
    @Inject
    private PlannedTimeTypeRepository plannedTimeTypeRepository;

    public ActivityConfigurationDTO updateActivityConfiguration(Long unitId, ActivityConfigurationDTO activityConfigurationDTO) {

        /**
         * If this is Absence then user might select time type id based.
         **/
        if (activityConfigurationDTO.getTimeType().equals(TimeTypeEnum.ABSENCE) && Optional.ofNullable(activityConfigurationDTO.getTimeTypeId()).isPresent()) {
            TimeType timeType = timeTypeMongoRepository.findOneById(activityConfigurationDTO.getTimeTypeId());
            if (!Optional.ofNullable(timeType).isPresent()) {
                exceptionService.dataNotFoundByIdException("message.timetype.notfound");
            }

        }
        ActivityConfiguration activityConfiguration = new ActivityConfiguration();
        BeanUtils.copyProperties(activityConfigurationDTO,activityConfiguration);

        activityConfiguration.setUnitId(unitId);
        save(activityConfiguration);
        activityConfigurationDTO.setId(activityConfiguration.getId());
        return activityConfigurationDTO;
    }

    public List<ActivityConfigurationDTO> getActivityConfiguration(Long unitId, TimeTypeEnum timeTypeEnum) {
        List<ActivityConfigurationDTO> activityConfigurations = activityConfigurationRepository.findByUnitIdAndDeletedFalseAndTimeTypeEqualsIgnoreCase(unitId, timeTypeEnum);
        return activityConfigurations;
    }

    public ActivityConfigurationWrapper getDefaultData(Long unitId) {
        Long countryId = organizationRestClient.getCountryIdOfOrganization(unitId);
        if (!Optional.ofNullable(countryId).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.country.id");
        }
        List<PhaseDTO> phases = phaseMongoRepository.getApplicablePlanningPhasesByUnit(unitId);
        List<TimeTypeDTO> topLevelTimeType = timeTypeMongoRepository.getTopLevelTimeTypeIds(countryId);
        List<BigInteger> topLevelTimeTypeIds= topLevelTimeType.stream().map(TimeTypeDTO::getId).collect(Collectors.toList());
        List<TimeTypeDTO> topLevelTimeTypes = timeTypeMongoRepository.findAllChildByParentId(topLevelTimeTypeIds);
        List<PresenceTypeDTO> plannedTimeTypes = plannedTimeTypeRepository.getAllPresenceTypeByCountryId(countryId, false);
        ActivityConfigurationWrapper activityConfigurationWrapper = new ActivityConfigurationWrapper(phases, topLevelTimeTypes, plannedTimeTypes);

        return activityConfigurationWrapper;
    }
}
