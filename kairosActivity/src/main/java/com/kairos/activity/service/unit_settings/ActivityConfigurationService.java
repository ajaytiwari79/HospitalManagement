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
import com.kairos.activity.unit_settings.activity_configuration.AbsencePlannedTime;
import com.kairos.activity.unit_settings.activity_configuration.ActivityConfigurationDTO;
import com.kairos.activity.unit_settings.activity_configuration.ActivityConfigurationWrapper;
import com.kairos.activity.unit_settings.activity_configuration.PresencePlannedTime;
import com.kairos.response.dto.web.cta.PhaseResponseDTO;
import com.kairos.response.dto.web.cta.TimeTypeResponseDTO;
import com.kairos.response.dto.web.presence_type.PresenceTypeDTO;
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

    public void createDefaultSettings(Long unitId, Long countryId, List<Phase> phases) {
        // TODO REMOVE
        List<ActivityConfiguration> activityConfigurations = new ArrayList<>();
        if (phases == null || phases.isEmpty())
            phases = phaseMongoRepository.getPlanningPhasesByUnit(unitId);
        // TODO FIXME unable to find regex on $IN
        List<PresenceTypeDTO> plannedTimeTypes = plannedTimeTypeRepository.getAllPresenceTypeByCountryId(countryId, false);
        Optional<PresenceTypeDTO> normalPlannedType = plannedTimeTypes.stream().filter(presenceTypeDTO -> presenceTypeDTO.getName().equalsIgnoreCase(NORMAL_TIME)).findAny();
        Optional<PresenceTypeDTO> extraTimePlannedType = plannedTimeTypes.stream().filter(presenceTypeDTO -> presenceTypeDTO.getName().equalsIgnoreCase(EXTRA_TIME)).findAny();
        BigInteger normalPlannedTypeId = normalPlannedType.isPresent() ? normalPlannedType.get().getId() : null;
        BigInteger extraTimePlannedTypeId = extraTimePlannedType.isPresent() ? normalPlannedType.get().getId() : normalPlannedTypeId;
        for (Phase phase : phases) {
            switch (phase.getName()) {
                case DRAFT_PHASE_NAME:
                    createDefaultPresentSettings(phase.getId(), normalPlannedTypeId, activityConfigurations, unitId);
                    createDefaultAbsenceSettings(phase.getId(), extraTimePlannedTypeId, activityConfigurations, unitId);
                    break;
                case REQUEST_PHASE_NAME:
                    createDefaultAbsenceSettings(phase.getId(), normalPlannedTypeId, activityConfigurations, unitId);
                    createDefaultPresentSettings(phase.getId(), normalPlannedTypeId, activityConfigurations, unitId);
                    break;
                case CONSTRUCTION_PHASE_NAME:
                    createDefaultAbsenceSettings(phase.getId(), normalPlannedTypeId, activityConfigurations, unitId);
                    createDefaultPresentSettings(phase.getId(), normalPlannedTypeId, activityConfigurations, unitId);
                    break;
                case PUZZLE_PHASE_NAME:
                    createDefaultAbsenceSettings(phase.getId(), normalPlannedTypeId, activityConfigurations, unitId);
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
        List<ActivityConfigurationDTO> activityConfigurations = activityConfigurationRepository.findAbsenceConfigurationByUnitId(unitId);
        return activityConfigurations;
    }

    public List<ActivityConfigurationDTO> getPresenceActivityConfiguration(Long unitId) {
        return activityConfigurationRepository.findPresenceConfigurationByUnitId(unitId);
    }

    public ActivityConfigurationWrapper getDefaultData(Long unitId) {
        Long countryId = organizationRestClient.getCountryIdOfOrganization(unitId);
        if (!Optional.ofNullable(countryId).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.country.id");
        }
        List<PhaseResponseDTO> phases = phaseMongoRepository.getAllPlanningPhasesByUnit(unitId);
        List<TimeTypeDTO> topLevelTimeType = timeTypeMongoRepository.getTopLevelTimeTypeIds(countryId);
        List<BigInteger> topLevelTimeTypeIds = topLevelTimeType.stream().map(TimeTypeDTO::getId).collect(Collectors.toList());
        List<TimeTypeResponseDTO> secondLevelTimeTypes = timeTypeMongoRepository.findAllChildByParentId(topLevelTimeTypeIds);
        List<PresenceTypeDTO> plannedTimeTypes = plannedTimeTypeRepository.getAllPresenceTypeByCountryId(countryId, false);
        ActivityConfigurationWrapper activityConfigurationWrapper = new ActivityConfigurationWrapper(phases, secondLevelTimeTypes, plannedTimeTypes);

        return activityConfigurationWrapper;
    }
}
