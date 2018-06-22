package com.kairos.activity.service.unit_settings;

import com.kairos.activity.client.OrganizationRestClient;
import com.kairos.activity.persistence.model.phase.Phase;
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
import com.kairos.response.dto.web.unit_settings.activity_configuration.AbsencePlannedTime;
import com.kairos.response.dto.web.unit_settings.activity_configuration.ActivityConfigurationDTO;
import com.kairos.response.dto.web.unit_settings.activity_configuration.ActivityConfigurationWrapper;
import com.kairos.response.dto.web.unit_settings.activity_configuration.PresencePlannedTime;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.kairos.activity.constants.AppConstants.*;

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

    public void createDefaultPhaseSettings(Long unitId, Long countryId, List<Phase> phases) {
        // TODO REMOVE
        List<ActivityConfiguration> activityConfigurations = new ArrayList<>();
        if (phases == null || phases.isEmpty())
            phases = phaseMongoRepository.getPlanningPhasesByUnit(unitId);
        // TODO FIXME unable to find regex on $IN
        List<PresenceTypeDTO> plannedTimeTypes = plannedTimeTypeRepository.getAllPresenceTypeByCountryId(countryId, false);
        Optional<PresenceTypeDTO> normalPlannedType = plannedTimeTypes.stream().filter(presenceTypeDTO -> presenceTypeDTO.getName().equalsIgnoreCase(NORMAL_TIME)).findAny();
        Optional<PresenceTypeDTO> extraTimePlannedType = plannedTimeTypes.stream().filter(presenceTypeDTO -> presenceTypeDTO.getName().equalsIgnoreCase(EXTRA_TIME)).findAny();
        BigInteger normalPlannedTypeId = normalPlannedType.isPresent() ? normalPlannedType.get().getId() : null;
        BigInteger extraTimePlannedTypeId = normalPlannedType.isPresent() ? normalPlannedType.get().getId() : normalPlannedTypeId;
        for (Phase phase : phases) {
            switch (phase.getName()) {
                case DRAFT_PHASE_NAME:
                    createDefaultPresentSettings(phase.getId(), normalPlannedTypeId, activityConfigurations, unitId);
                    getDefaultAbsenceSettings(phase.getId(), extraTimePlannedTypeId, activityConfigurations, unitId);
                    break;
                case REQUEST_PHASE_NAME:
                    getDefaultAbsenceSettings(phase.getId(), normalPlannedTypeId, activityConfigurations, unitId);
                    createDefaultPresentSettings(phase.getId(), normalPlannedTypeId, activityConfigurations, unitId);
                    break;
                case CONSTRUCTION_PHASE_NAME:
                    getDefaultAbsenceSettings(phase.getId(), normalPlannedTypeId, activityConfigurations, unitId);
                    createDefaultPresentSettings(phase.getId(), normalPlannedTypeId, activityConfigurations, unitId);
                    break;
                case PUZZLE_PHASE_NAME:
                    getDefaultAbsenceSettings(phase.getId(), normalPlannedTypeId, activityConfigurations, unitId);
                    createDefaultPresentSettings(phase.getId(), normalPlannedTypeId, activityConfigurations, unitId);
                    break;
                default:
                    // no operation
            }
        }
        save(activityConfigurations);

    }

    private void createDefaultPresentSettings(BigInteger phaseId, BigInteger applicablePlannedTimeId, List<ActivityConfiguration> activityConfigurations, Long unitId) {
        activityConfigurations.add(new ActivityConfiguration(unitId, new PresencePlannedTime(phaseId, applicablePlannedTimeId, applicablePlannedTimeId)));

    }

    private void getDefaultAbsenceSettings(BigInteger phaseId, BigInteger applicablePlannedTimeId, List<ActivityConfiguration> activityConfigurations, Long unitId) {
        activityConfigurations.add(new ActivityConfiguration(unitId, new AbsencePlannedTime(phaseId, applicablePlannedTimeId, false)));
    }

    public ActivityConfigurationDTO updateActivityConfiguration(Long unitId, ActivityConfigurationDTO activityConfigurationDTO) {
        /**
         * If this is Absence then user might select time type id based.
         **/
        ActivityConfiguration activityConfiguration = null;
        if (activityConfigurationDTO.getTimeType().equals(TimeTypeEnum.PRESENCE)) {
            activityConfiguration = activityConfigurationRepository.findPresenceConfigurationByUnitIdAndPhaseId(unitId, activityConfigurationDTO.getPresencePlannedTime().getPhaseId());
        } else if (activityConfigurationDTO.getTimeType().equals(TimeTypeEnum.ABSENCE)) {
            activityConfiguration = activityConfigurationRepository.findAbsenceConfigurationByUnitIdAndPhaseId(unitId, activityConfigurationDTO.getAbsencePlannedTime().getPhaseId());

        }
        BeanUtils.copyProperties(activityConfigurationDTO, activityConfiguration);
        activityConfiguration.setUnitId(unitId);
        save(activityConfiguration);
        activityConfigurationDTO.setId(activityConfiguration.getId());
        return activityConfigurationDTO;
    }

    public List<ActivityConfigurationDTO> getActivityConfiguration(Long unitId, TimeTypeEnum timeTypeEnum) {
        List<ActivityConfigurationDTO> activityConfigurations = new ArrayList<>();
        if (timeTypeEnum.equals(TimeTypeEnum.ABSENCE)) {
            activityConfigurations = activityConfigurationRepository.findAbsenceConfigurationByUnitIdAndPhaseId(unitId);
        } else {
            activityConfigurations = activityConfigurationRepository.findPresenceConfigurationByUnitIdAndPhaseId(unitId);
        }
        return activityConfigurations;
    }

    public ActivityConfigurationWrapper getDefaultData(Long unitId) {
        Long countryId = organizationRestClient.getCountryIdOfOrganization(unitId);
        if (!Optional.ofNullable(countryId).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.country.id");
        }
        List<PhaseDTO> phases = phaseMongoRepository.getApplicablePlanningPhasesByUnit(unitId);
        List<TimeTypeDTO> topLevelTimeType = timeTypeMongoRepository.getTopLevelTimeTypeIds(countryId);
        List<BigInteger> topLevelTimeTypeIds = topLevelTimeType.stream().map(TimeTypeDTO::getId).collect(Collectors.toList());
        List<TimeTypeDTO> topLevelTimeTypes = timeTypeMongoRepository.findAllChildByParentId(topLevelTimeTypeIds);
        List<PresenceTypeDTO> plannedTimeTypes = plannedTimeTypeRepository.getAllPresenceTypeByCountryId(countryId, false);
        ActivityConfigurationWrapper activityConfigurationWrapper = new ActivityConfigurationWrapper(phases, topLevelTimeTypes, plannedTimeTypes);

        return activityConfigurationWrapper;
    }
}
