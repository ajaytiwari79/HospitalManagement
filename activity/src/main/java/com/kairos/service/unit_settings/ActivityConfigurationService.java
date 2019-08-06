package com.kairos.service.unit_settings;

import com.kairos.dto.activity.presence_type.PresenceTypeDTO;
import com.kairos.dto.activity.time_type.TimeTypeDTO;
import com.kairos.dto.activity.unit_settings.activity_configuration.*;
import com.kairos.dto.user.country.agreement.cta.cta_response.PhaseResponseDTO;
import com.kairos.dto.user.country.agreement.cta.cta_response.TimeTypeResponseDTO;
import com.kairos.persistence.model.phase.Phase;
import com.kairos.persistence.model.unit_settings.ActivityConfiguration;
import com.kairos.persistence.repository.activity.PlannedTimeTypeRepository;
import com.kairos.persistence.repository.phase.PhaseMongoRepository;
import com.kairos.persistence.repository.time_type.TimeTypeMongoRepository;
import com.kairos.persistence.repository.unit_settings.ActivityConfigurationRepository;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.service.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.ObjectUtils.isCollectionEmpty;
import static com.kairos.constants.ActivityMessagesConstants.*;
import static com.kairos.constants.AppConstants.*;

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
    private UserIntegrationService userIntegrationService;
    @Inject
    private PlannedTimeTypeRepository plannedTimeTypeRepository;

    public void createDefaultSettings(Long unitId, Long countryId, List<Phase> phases) {
        if(activityConfigurationRepository.existsByUnitIdAndDeletedFalse(unitId)){
            exceptionService.actionNotPermittedException(MESSAGE_ALREADY_EXISTS);
        }
        List<ActivityConfiguration> activityConfigurations = new ArrayList<>();
        if (isCollectionEmpty(phases)) {
            phases = phaseMongoRepository.findByOrganizationIdAndDeletedFalse(unitId);
        }
        List<PresenceTypeDTO> plannedTimeTypes = plannedTimeTypeRepository.getAllPresenceTypeByCountryId(countryId, false);
        Optional<PresenceTypeDTO> normalPlannedType = plannedTimeTypes.stream().filter(presenceTypeDTO -> presenceTypeDTO.getName().equalsIgnoreCase(NORMAL_TIME)).findAny();
        Optional<PresenceTypeDTO> extraTimePlannedType = plannedTimeTypes.stream().filter(presenceTypeDTO -> presenceTypeDTO.getName().equalsIgnoreCase(EXTRA_TIME)).findAny();
        BigInteger normalPlannedTypeId = normalPlannedType.map(PresenceTypeDTO::getId).orElse(null);
        BigInteger extraTimePlannedTypeId = extraTimePlannedType.isPresent() ? normalPlannedType.get().getId() : normalPlannedTypeId;
        for (Phase phase : phases) {
            if (DRAFT_PHASE_NAME.equals(phase.getName())) {
                createDefaultPresentSettings(phase.getId(), normalPlannedTypeId, activityConfigurations, unitId);
                createDefaultAbsenceSettings(phase.getId(), extraTimePlannedTypeId, activityConfigurations, unitId);
            } else {
                createDefaultAbsenceSettings(phase.getId(), normalPlannedTypeId, activityConfigurations, unitId);
                createDefaultPresentSettings(phase.getId(), normalPlannedTypeId, activityConfigurations, unitId);
            }

        }
        activityConfigurationRepository.saveEntities(activityConfigurations);
    }

    private void createDefaultPresentSettings(BigInteger phaseId, BigInteger applicablePlannedTimeId, List<ActivityConfiguration> activityConfigurations, Long unitId) {
        activityConfigurations.add(new ActivityConfiguration(unitId, new PresencePlannedTime(phaseId,applicablePlannedTimeId, applicablePlannedTimeId)));

    }

    private void createDefaultAbsenceSettings(BigInteger phaseId, BigInteger applicablePlannedTimeId, List<ActivityConfiguration> activityConfigurations, Long unitId) {
        activityConfigurations.add(new ActivityConfiguration(unitId, new AbsencePlannedTime(phaseId, applicablePlannedTimeId, false)));
    }

    public PresencePlannedTime updatePresenceActivityConfiguration(Long unitId, PresencePlannedTime presencePlannedTime) {
        ActivityConfiguration activityConfiguration = activityConfigurationRepository.findPresenceConfigurationByUnitIdAndPhaseId(unitId, presencePlannedTime.getPhaseId());
        if (!Optional.of(activityConfiguration).isPresent() || !Optional.ofNullable(activityConfiguration.getPresencePlannedTime()).isPresent()) {
            exceptionService.dataNotFoundByIdException(ERROR_PRESENCEACTIVITYCONFIGURATION_NOTFOUND);
        }
        activityConfiguration.getPresencePlannedTime().setManagementPlannedTimeId(presencePlannedTime.getManagementPlannedTimeId());
        activityConfiguration.getPresencePlannedTime().setStaffPlannedTimeId(presencePlannedTime.getStaffPlannedTimeId());

        activityConfigurationRepository.save(activityConfiguration);
        return presencePlannedTime;
    }

    public AbsencePlannedTime updateAbsenceActivityConfiguration(BigInteger activityConfigurationId, AbsencePlannedTime absencePlannedTime) {
        Optional<ActivityConfiguration> activityConfiguration = activityConfigurationRepository.findById(activityConfigurationId);
        if (!Optional.of(activityConfiguration).isPresent()) {
            exceptionService.dataNotFoundByIdException(ERROR_ABSENCEACTIVITYCONFIGURATION_NOTFOUND);
        }
        if (Optional.ofNullable(absencePlannedTime.getTimeTypeId()).isPresent()) {
            activityConfiguration.get().getAbsencePlannedTime().setTimeTypeId(absencePlannedTime.getTimeTypeId());
            activityConfiguration.get().getAbsencePlannedTime().setException(true);
        }
        activityConfiguration.get().getAbsencePlannedTime().setPlannedTimeId(absencePlannedTime.getPlannedTimeId());
        activityConfigurationRepository.save(activityConfiguration.get());
        return absencePlannedTime;

    }

    public BigInteger createAbsenceExceptionActivityConfiguration(Long unitId, AbsencePlannedTime absencePlannedTime) {
        if (!Optional.ofNullable(absencePlannedTime.getTimeTypeId()).isPresent()) {
            exceptionService.dataNotFoundByIdException(ERROR_TIMETYPE_UNSELECTED);
        }
        ActivityConfiguration activityConfiguration = new ActivityConfiguration(unitId, new AbsencePlannedTime(absencePlannedTime.getPhaseId(), absencePlannedTime.getTimeTypeId(), absencePlannedTime.getPlannedTimeId(), true));
        activityConfigurationRepository.save(activityConfiguration);
        return activityConfiguration.getId();

    }

    public List<ActivityConfigurationDTO> getAbsenceActivityConfiguration(Long unitId) {
        List<ActivityConfigurationDTO> activityConfigurationDTOS = activityConfigurationRepository.findAbsenceConfigurationByUnitId(unitId);
        List<ActivityConfigurationDTO> modifiableList = new ArrayList<>(activityConfigurationDTOS);
        modifiableList.sort((a1, a2) -> Integer.compare(a1.getPhase().getSequence(), (a2.getPhase().getSequence())));
        return modifiableList;
    }

    public List<ActivityConfigurationDTO> getPresenceActivityConfiguration(Long unitId) {
        List<ActivityConfigurationDTO> activityConfigurationDTOS = activityConfigurationRepository.findPresenceConfigurationByUnitId(unitId);
        List<ActivityConfigurationDTO> modifiableList = new ArrayList<>(activityConfigurationDTOS);
        modifiableList.sort((a1, a2) -> Integer.compare(a1.getPhase().getSequence(), (a2.getPhase().getSequence())));
        return modifiableList;
    }

    public ActivityConfigurationWrapper getDefaultData(Long unitId) {
        Long countryId = userIntegrationService.getCountryIdOfOrganization(unitId);
        if (!Optional.ofNullable(countryId).isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_COUNTRY_ID);
        }
        List<PhaseResponseDTO> phases = phaseMongoRepository.getAllPlanningPhasesByUnit(unitId);
        return getDefaultDataForCountry(phases, countryId);
    }


    public void createDefaultSettingsForCountry(Long countryId, List<Phase> phases) {
        List<ActivityConfiguration> activityConfigurations = new ArrayList<>();
        if (phases == null || phases.isEmpty()) {
            phases = phaseMongoRepository.getPlanningPhasesByCountry(countryId);
        }
        List<PresenceTypeDTO> plannedTimeTypes = plannedTimeTypeRepository.getAllPresenceTypeByCountryId(countryId, false);
        Optional<PresenceTypeDTO> normalPlannedType = plannedTimeTypes.stream().filter(presenceTypeDTO -> presenceTypeDTO.getName().equalsIgnoreCase(NORMAL_TIME)).findAny();
        Optional<PresenceTypeDTO> extraTimePlannedType = plannedTimeTypes.stream().filter(presenceTypeDTO -> presenceTypeDTO.getName().equalsIgnoreCase(EXTRA_TIME)).findAny();
        BigInteger normalPlannedTypeId = normalPlannedType.map(PresenceTypeDTO::getId).orElse(null);
        BigInteger extraTimePlannedTypeId = extraTimePlannedType.isPresent() ? normalPlannedType.get().getId() : normalPlannedTypeId;
        for (Phase phase : phases) {
            if (DRAFT_PHASE_NAME.equals(phase.getName())) {
                createDefaultPresentSettingsAtCountry(phase.getId(), normalPlannedTypeId, activityConfigurations, countryId);
                createDefaultAbsenceSettingsAtCountry(phase.getId(), extraTimePlannedTypeId, activityConfigurations, countryId);
            } else {
                createDefaultAbsenceSettingsAtCountry(phase.getId(), normalPlannedTypeId, activityConfigurations, countryId);
                createDefaultPresentSettingsAtCountry(phase.getId(), normalPlannedTypeId, activityConfigurations, countryId);
            }
        }
        activityConfigurationRepository.saveEntities(activityConfigurations);
    }

    private void createDefaultPresentSettingsAtCountry(BigInteger phaseId, BigInteger applicablePlannedTimeId, List<ActivityConfiguration> activityConfigurations, Long countryId) {
        activityConfigurations.add(new ActivityConfiguration(new PresencePlannedTime(phaseId, applicablePlannedTimeId, applicablePlannedTimeId), countryId));

    }

    private void createDefaultAbsenceSettingsAtCountry(BigInteger phaseId, BigInteger applicablePlannedTimeId, List<ActivityConfiguration> activityConfigurations, Long countryId) {
        activityConfigurations.add(new ActivityConfiguration(new AbsencePlannedTime(phaseId, applicablePlannedTimeId, false), countryId));
    }

    public PresencePlannedTime updatePresenceActivityConfigurationForCountry(Long countryId, PresencePlannedTime presencePlannedTime) {
        ActivityConfiguration activityConfiguration = activityConfigurationRepository.findPresenceConfigurationByCountryIdAndPhaseId(countryId, presencePlannedTime.getPhaseId());
        if (!Optional.of(activityConfiguration).isPresent() || !Optional.ofNullable(activityConfiguration.getPresencePlannedTime()).isPresent()) {
            exceptionService.dataNotFoundByIdException(ERROR_PRESENCEACTIVITYCONFIGURATION_NOTFOUND);
        }
        activityConfiguration.getPresencePlannedTime().setManagementPlannedTimeId(presencePlannedTime.getManagementPlannedTimeId());
        activityConfiguration.getPresencePlannedTime().setStaffPlannedTimeId(presencePlannedTime.getStaffPlannedTimeId());

        activityConfigurationRepository.save(activityConfiguration);
        return presencePlannedTime;
    }

    public AbsencePlannedTime updateAbsenceActivityConfigurationForCountry(BigInteger activityConfigurationId, AbsencePlannedTime absencePlannedTime) {
        Optional<ActivityConfiguration> activityConfiguration = activityConfigurationRepository.findById(activityConfigurationId);
        if (!Optional.of(activityConfiguration).isPresent()) {
            exceptionService.dataNotFoundByIdException(ERROR_ABSENCEACTIVITYCONFIGURATION_NOTFOUND);
        }
        if (Optional.ofNullable(absencePlannedTime.getTimeTypeId()).isPresent()) {
            activityConfiguration.get().getAbsencePlannedTime().setTimeTypeId(absencePlannedTime.getTimeTypeId());
            activityConfiguration.get().getAbsencePlannedTime().setException(true);
        }
        activityConfiguration.get().getAbsencePlannedTime().setPlannedTimeId(absencePlannedTime.getPlannedTimeId());
        activityConfigurationRepository.save(activityConfiguration.get());
        return absencePlannedTime;

    }

    public BigInteger createAbsenceExceptionActivityConfigurationForCountry(Long countryId, AbsencePlannedTime absencePlannedTime) {
        if (!Optional.ofNullable(absencePlannedTime.getTimeTypeId()).isPresent()) {
            exceptionService.dataNotFoundByIdException(ERROR_TIMETYPE_UNSELECTED);
        }
        ActivityConfiguration activityConfiguration = new ActivityConfiguration(new AbsencePlannedTime(absencePlannedTime.getPhaseId(), absencePlannedTime.getTimeTypeId(), absencePlannedTime.getPlannedTimeId(), true), countryId);
        activityConfigurationRepository.save(activityConfiguration);
        return activityConfiguration.getId();

    }

    public List<ActivityConfigurationDTO> getAbsenceActivityConfigurationForCountry(Long countryId) {
        return activityConfigurationRepository.findAbsenceConfigurationByCountryId(countryId);
    }

    public List<ActivityConfigurationDTO> getPresenceActivityConfigurationForCountry(Long countryId) {
        return activityConfigurationRepository.findPresenceConfigurationByCountryId(countryId);
    }


    public ActivityConfigurationWrapper getDefaultDataForCountry(Long countryId) {
        List<PhaseResponseDTO> phases = phaseMongoRepository.findPlanningPhasesByCountry(countryId);
        return getDefaultDataForCountry(phases, countryId);
    }

    private ActivityConfigurationWrapper getDefaultDataForCountry(List<PhaseResponseDTO> phases, Long countryId) {
        List<TimeTypeDTO> topLevelTimeType = timeTypeMongoRepository.getTopLevelTimeTypeIds(countryId);
        List<BigInteger> topLevelTimeTypeIds = topLevelTimeType.stream().map(TimeTypeDTO::getId).collect(Collectors.toList());
        List<TimeTypeResponseDTO> secondLevelTimeTypes = timeTypeMongoRepository.findAllChildByParentId(topLevelTimeTypeIds);
        List<PresenceTypeDTO> plannedTimeTypes = plannedTimeTypeRepository.getAllPresenceTypeByCountryId(countryId, false);
        return new ActivityConfigurationWrapper(phases, secondLevelTimeTypes, plannedTimeTypes);
    }
}
