package com.kairos.service.unit_settings;

import com.kairos.dto.activity.presence_type.PresenceTypeDTO;
import com.kairos.dto.activity.time_type.TimeTypeDTO;
import com.kairos.dto.activity.unit_settings.activity_configuration.AbsencePlannedTime;
import com.kairos.dto.activity.unit_settings.activity_configuration.ActivityConfigurationDTO;
import com.kairos.dto.activity.unit_settings.activity_configuration.ActivityConfigurationWrapper;
import com.kairos.dto.activity.unit_settings.activity_configuration.PresencePlannedTime;
import com.kairos.persistence.model.phase.Phase;
import com.kairos.persistence.model.unit_settings.ActivityConfiguration;
import com.kairos.persistence.repository.activity.PlannedTimeTypeRepository;
import com.kairos.persistence.repository.activity.TimeTypeMongoRepository;
import com.kairos.persistence.repository.phase.PhaseMongoRepository;
import com.kairos.persistence.repository.unit_settings.ActivityConfigurationRepository;
import com.kairos.rest_client.OrganizationRestClient;
import com.kairos.service.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.dto.user.country.agreement.cta.cta_response.PhaseResponseDTO;
import com.kairos.dto.user.country.agreement.cta.cta_response.TimeTypeResponseDTO;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

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
    private OrganizationRestClient organizationRestClient;
    @Inject
    private PlannedTimeTypeRepository plannedTimeTypeRepository;

    public void createDefaultSettings(Long unitId, Long countryId, List<Phase> phases) {
        if(activityConfigurationRepository.existsByUnitIdAndDeletedFalse(unitId)){
            exceptionService.actionNotPermittedException("message.already.exists");
        }
        // TODO REMOVE
        List<ActivityConfiguration> activityConfigurations = new ArrayList<>();
        if (phases == null || phases.isEmpty())
            phases = phaseMongoRepository.findByOrganizationIdAndDeletedFalse(unitId);
        // TODO FIXME unable to find regex on $IN
        List<PresenceTypeDTO> plannedTimeTypes = plannedTimeTypeRepository.getAllPresenceTypeByCountryId(countryId, false);
        Optional<PresenceTypeDTO> normalPlannedType = plannedTimeTypes.stream().filter(presenceTypeDTO -> presenceTypeDTO.getName().equalsIgnoreCase(NORMAL_TIME)).findAny();
        Optional<PresenceTypeDTO> extraTimePlannedType = plannedTimeTypes.stream().filter(presenceTypeDTO -> presenceTypeDTO.getName().equalsIgnoreCase(EXTRA_TIME)).findAny();
        BigInteger normalPlannedTypeId = normalPlannedType.map(PresenceTypeDTO::getId).orElse(null);
        BigInteger extraTimePlannedTypeId = extraTimePlannedType.isPresent() ? normalPlannedType.get().getId() : normalPlannedTypeId;
        for (Phase phase : phases) {
            if(DRAFT_PHASE_NAME.equals(phase.getName())){
                createDefaultPresentSettings(phase.getId(), normalPlannedTypeId, activityConfigurations, unitId);
                createDefaultAbsenceSettings(phase.getId(), extraTimePlannedTypeId, activityConfigurations, unitId);
            }
            else {
                createDefaultAbsenceSettings(phase.getId(), normalPlannedTypeId, activityConfigurations, unitId);
                createDefaultPresentSettings(phase.getId(), normalPlannedTypeId, activityConfigurations, unitId);
            }

        }
        save(activityConfigurations);
    }

    private void createDefaultPresentSettings(BigInteger phaseId, BigInteger applicablePlannedTimeId, List<ActivityConfiguration> activityConfigurations, Long unitId) {
        activityConfigurations.add(new ActivityConfiguration(unitId, new PresencePlannedTime(phaseId, applicablePlannedTimeId, applicablePlannedTimeId)));

    }

    private void createDefaultAbsenceSettings(BigInteger phaseId, BigInteger applicablePlannedTimeId, List<ActivityConfiguration> activityConfigurations, Long unitId) {
        activityConfigurations.add(new ActivityConfiguration(unitId, new AbsencePlannedTime(phaseId, applicablePlannedTimeId, false)));
    }

    public PresencePlannedTime updatePresenceActivityConfiguration(Long unitId, PresencePlannedTime presencePlannedTime) {
        ActivityConfiguration activityConfiguration = activityConfigurationRepository.findPresenceConfigurationByUnitIdAndPhaseId(unitId, presencePlannedTime.getPhaseId());
        if (!Optional.of(activityConfiguration).isPresent() || !Optional.ofNullable(activityConfiguration.getPresencePlannedTime()).isPresent()) {
            exceptionService.dataNotFoundByIdException("error.presenceActivityConfiguration.notFound");
        }
        activityConfiguration.getPresencePlannedTime().setManagementPlannedTimeId(presencePlannedTime.getManagementPlannedTimeId());
        activityConfiguration.getPresencePlannedTime().setStaffPlannedTimeId(presencePlannedTime.getStaffPlannedTimeId());

        save(activityConfiguration);
        return presencePlannedTime;
    }

    public AbsencePlannedTime updateAbsenceActivityConfiguration(Long unitId, BigInteger activityConfigurationId, AbsencePlannedTime absencePlannedTime) {
        Optional<ActivityConfiguration> activityConfiguration = activityConfigurationRepository.findById(activityConfigurationId);
        if (!Optional.of(activityConfiguration).isPresent()) {
            exceptionService.dataNotFoundByIdException("error.absenceActivityConfiguration.notFound");
        }
        if (Optional.ofNullable(absencePlannedTime.getTimeTypeId()).isPresent()) {
            activityConfiguration.get().getAbsencePlannedTime().setTimeTypeId(absencePlannedTime.getTimeTypeId());
            activityConfiguration.get().getAbsencePlannedTime().setException(true);
        }
        activityConfiguration.get().getAbsencePlannedTime().setPlannedTimeId(absencePlannedTime.getPlannedTimeId());
        save(activityConfiguration.get());
        return absencePlannedTime;

    }

    public BigInteger createAbsenceExceptionActivityConfiguration(Long unitId, AbsencePlannedTime absencePlannedTime) {
        if (!Optional.ofNullable(absencePlannedTime.getTimeTypeId()).isPresent()) {
            exceptionService.dataNotFoundByIdException("error.timetype.unselected");
        }
        ActivityConfiguration activityConfiguration = new ActivityConfiguration(unitId, new AbsencePlannedTime(absencePlannedTime.getPhaseId(), absencePlannedTime.getTimeTypeId(), absencePlannedTime.getPlannedTimeId(), true));
        save(activityConfiguration);
        return activityConfiguration.getId();

    }

    public List<ActivityConfigurationDTO> getAbsenceActivityConfiguration(Long unitId) {
        List<ActivityConfigurationDTO> activityConfigurationDTOS =  activityConfigurationRepository.findAbsenceConfigurationByUnitId(unitId);
        List<ActivityConfigurationDTO> modifiableList = new ArrayList<>(activityConfigurationDTOS);
        modifiableList.sort((a1,a2)->Integer.compare(a1.getPhase().getSequence(),(a2.getPhase().getSequence())));
        return modifiableList;
    }

    public List<ActivityConfigurationDTO> getPresenceActivityConfiguration(Long unitId) {
        List<ActivityConfigurationDTO> activityConfigurationDTOS = activityConfigurationRepository.findPresenceConfigurationByUnitId(unitId);
        List<ActivityConfigurationDTO> modifiableList = new ArrayList<>(activityConfigurationDTOS);
        modifiableList.sort((a1,a2)->Integer.compare(a1.getPhase().getSequence(),(a2.getPhase().getSequence())));
        return modifiableList;
    }

    public ActivityConfigurationWrapper getDefaultData(Long unitId) {
        Long countryId = organizationRestClient.getCountryIdOfOrganization(unitId);
        if (!Optional.ofNullable(countryId).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.country.id");
        }
        List<PhaseResponseDTO> phases = phaseMongoRepository.getAllPlanningPhasesByUnit(unitId);
        return getDefaultDataForCountry(phases,countryId);
    }


    public void createDefaultSettingsForCountry(Long countryId, List<Phase> phases) {
        // TODO REMOVE
        List<ActivityConfiguration> activityConfigurations = new ArrayList<>();
        if (phases == null || phases.isEmpty()){
            phases = phaseMongoRepository.getPlanningPhasesByCountry(countryId);
        }
        // TODO FIXME unable to find regex on $IN
        List<PresenceTypeDTO> plannedTimeTypes = plannedTimeTypeRepository.getAllPresenceTypeByCountryId(countryId, false);
        Optional<PresenceTypeDTO> normalPlannedType = plannedTimeTypes.stream().filter(presenceTypeDTO -> presenceTypeDTO.getName().equalsIgnoreCase(NORMAL_TIME)).findAny();
        Optional<PresenceTypeDTO> extraTimePlannedType = plannedTimeTypes.stream().filter(presenceTypeDTO -> presenceTypeDTO.getName().equalsIgnoreCase(EXTRA_TIME)).findAny();
        BigInteger normalPlannedTypeId = normalPlannedType.map(PresenceTypeDTO::getId).orElse(null);
        BigInteger extraTimePlannedTypeId = extraTimePlannedType.isPresent() ? normalPlannedType.get().getId() : normalPlannedTypeId;
        for (Phase phase : phases) {
            switch (phase.getName()) {
                case DRAFT_PHASE_NAME:
                    createDefaultPresentSettingsAtCountry(phase.getId(), normalPlannedTypeId, activityConfigurations, countryId);
                    createDefaultAbsenceSettingsAtCountry(phase.getId(), extraTimePlannedTypeId, activityConfigurations, countryId);
                    break;
                case REQUEST_PHASE_NAME:
                    createDefaultAbsenceSettingsAtCountry(phase.getId(), normalPlannedTypeId, activityConfigurations, countryId);
                    createDefaultPresentSettingsAtCountry(phase.getId(), normalPlannedTypeId, activityConfigurations, countryId);
                    break;
                case CONSTRUCTION_PHASE_NAME:
                    createDefaultAbsenceSettingsAtCountry(phase.getId(), normalPlannedTypeId, activityConfigurations, countryId);
                    createDefaultPresentSettingsAtCountry(phase.getId(), normalPlannedTypeId, activityConfigurations, countryId);
                    break;
                case PUZZLE_PHASE_NAME:
                    createDefaultAbsenceSettingsAtCountry(phase.getId(), normalPlannedTypeId, activityConfigurations, countryId);
                    createDefaultPresentSettingsAtCountry(phase.getId(), normalPlannedTypeId, activityConfigurations, countryId);
                    break;
            }
        }
        save(activityConfigurations);
    }

    private void createDefaultPresentSettingsAtCountry(BigInteger phaseId, BigInteger applicablePlannedTimeId, List<ActivityConfiguration> activityConfigurations, Long countryId) {
        activityConfigurations.add(new ActivityConfiguration(new PresencePlannedTime(phaseId, applicablePlannedTimeId, applicablePlannedTimeId),countryId));

    }

    private void createDefaultAbsenceSettingsAtCountry(BigInteger phaseId, BigInteger applicablePlannedTimeId, List<ActivityConfiguration> activityConfigurations, Long countryId) {
        activityConfigurations.add(new ActivityConfiguration( new AbsencePlannedTime(phaseId, applicablePlannedTimeId, false),countryId));
    }

    public PresencePlannedTime updatePresenceActivityConfigurationForCountry(Long countryId, PresencePlannedTime presencePlannedTime) {
        ActivityConfiguration activityConfiguration = activityConfigurationRepository.findPresenceConfigurationByCountryIdAndPhaseId(countryId, presencePlannedTime.getPhaseId());
        if (!Optional.of(activityConfiguration).isPresent() || !Optional.ofNullable(activityConfiguration.getPresencePlannedTime()).isPresent()) {
            exceptionService.dataNotFoundByIdException("error.presenceActivityConfiguration.notFound");
        }
        activityConfiguration.getPresencePlannedTime().setManagementPlannedTimeId(presencePlannedTime.getManagementPlannedTimeId());
        activityConfiguration.getPresencePlannedTime().setStaffPlannedTimeId(presencePlannedTime.getStaffPlannedTimeId());

        save(activityConfiguration);
        return presencePlannedTime;
    }

    public AbsencePlannedTime updateAbsenceActivityConfigurationForCountry(Long countryId, BigInteger activityConfigurationId, AbsencePlannedTime absencePlannedTime) {
        Optional<ActivityConfiguration> activityConfiguration = activityConfigurationRepository.findById(activityConfigurationId);
        if (!Optional.of(activityConfiguration).isPresent()) {
            exceptionService.dataNotFoundByIdException("error.absenceActivityConfiguration.notFound");
        }
        if (Optional.ofNullable(absencePlannedTime.getTimeTypeId()).isPresent()) {
            activityConfiguration.get().getAbsencePlannedTime().setTimeTypeId(absencePlannedTime.getTimeTypeId());
            activityConfiguration.get().getAbsencePlannedTime().setException(true);
        }
        activityConfiguration.get().getAbsencePlannedTime().setPlannedTimeId(absencePlannedTime.getPlannedTimeId());
        save(activityConfiguration.get());
        return absencePlannedTime;

    }

    public BigInteger createAbsenceExceptionActivityConfigurationForCountry(Long countryId, AbsencePlannedTime absencePlannedTime) {
        if (!Optional.ofNullable(absencePlannedTime.getTimeTypeId()).isPresent()) {
            exceptionService.dataNotFoundByIdException("error.timetype.unselected");
        }
        ActivityConfiguration activityConfiguration = new ActivityConfiguration( new AbsencePlannedTime(absencePlannedTime.getPhaseId(), absencePlannedTime.getTimeTypeId(), absencePlannedTime.getPlannedTimeId(), true),countryId);
        save(activityConfiguration);
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
        return getDefaultDataForCountry(phases,countryId);
    }

    private ActivityConfigurationWrapper getDefaultDataForCountry(List<PhaseResponseDTO> phases,Long countryId) {
        List<TimeTypeDTO> topLevelTimeType = timeTypeMongoRepository.getTopLevelTimeTypeIds(countryId);
        List<BigInteger> topLevelTimeTypeIds = topLevelTimeType.stream().map(TimeTypeDTO::getId).collect(Collectors.toList());
        List<TimeTypeResponseDTO> secondLevelTimeTypes = timeTypeMongoRepository.findAllChildByParentId(topLevelTimeTypeIds);
        List<PresenceTypeDTO> plannedTimeTypes = plannedTimeTypeRepository.getAllPresenceTypeByCountryId(countryId, false);
        return new ActivityConfigurationWrapper(phases, secondLevelTimeTypes, plannedTimeTypes);
    }
}
