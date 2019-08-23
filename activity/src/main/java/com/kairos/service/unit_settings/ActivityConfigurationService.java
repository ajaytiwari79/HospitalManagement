package com.kairos.service.unit_settings;

import com.kairos.commons.custom_exception.DataNotFoundException;
import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.dto.activity.counter.enums.ConfLevel;
import com.kairos.dto.activity.phase.PhaseDTO;
import com.kairos.dto.activity.presence_type.PresenceTypeDTO;
import com.kairos.dto.activity.shift.PlannedTime;
import com.kairos.dto.activity.shift.StaffEmploymentDetails;
import com.kairos.dto.activity.time_type.TimeTypeDTO;
import com.kairos.dto.activity.unit_settings.activity_configuration.*;
import com.kairos.dto.user.country.agreement.cta.cta_response.EmploymentTypeDTO;
import com.kairos.dto.user.country.agreement.cta.cta_response.PhaseResponseDTO;
import com.kairos.dto.user.country.agreement.cta.cta_response.TimeTypeResponseDTO;
import com.kairos.dto.user.user.staff.StaffAdditionalInfoDTO;
import com.kairos.enums.TimeTypeEnum;
import com.kairos.enums.phase.PhaseDefaultName;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.activity.ActivityWrapper;
import com.kairos.persistence.model.phase.Phase;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.model.shift.ShiftActivity;
import com.kairos.persistence.model.unit_settings.ActivityConfiguration;
import com.kairos.persistence.repository.activity.PlannedTimeTypeRepository;
import com.kairos.persistence.repository.phase.PhaseMongoRepository;
import com.kairos.persistence.repository.time_type.TimeTypeMongoRepository;
import com.kairos.persistence.repository.unit_settings.ActivityConfigurationRepository;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.service.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.phase.PhaseService;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.ObjectUtils.*;
import static com.kairos.constants.ActivityMessagesConstants.*;
import static com.kairos.constants.AppConstants.*;
import static com.kairos.constants.CommonMessageConstants.PLANNED_TIME_CANNOT_EMPTY;
import static com.kairos.constants.CommonMessageConstants.PLANNED_TIME_NOT_CONFIGURE;
import static com.kairos.service.shift.ShiftValidatorService.convertMessage;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toMap;

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
    @Inject private PhaseService phaseService;

    public void createDefaultSettings(Long unitId, Long countryId, List<Phase> phases,List<Long> employmentTypeIds) {
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
            createDefaultPresentSettings(phase.getId(), normalPlannedTypeId, activityConfigurations, unitId,employmentTypeIds,ConfLevel.UNIT);
            createDefaultAbsenceSettings(phase.getId(), DRAFT_PHASE_NAME.equals(phase.getName()) ? extraTimePlannedTypeId : normalPlannedTypeId, activityConfigurations, unitId, ConfLevel.UNIT);

        }
        activityConfigurationRepository.saveEntities(activityConfigurations);
    }

    private void createDefaultPresentSettings(BigInteger phaseId, BigInteger applicablePlannedTimeId, List<ActivityConfiguration> activityConfigurations, Long referenceId,List<Long> employmentTypeIds,ConfLevel confLevel) {
        List<EmploymentWisePlannedTimeConfiguration> employmentWisePlannedTimeConfigurations = new ArrayList<>();
        employmentTypeIds.forEach(employmentTypeId -> employmentWisePlannedTimeConfigurations.add(new EmploymentWisePlannedTimeConfiguration(employmentTypeId,newArrayList(applicablePlannedTimeId))));
        PresencePlannedTime presencePlannedTime = new PresencePlannedTime(phaseId,employmentWisePlannedTimeConfigurations, newArrayList(applicablePlannedTimeId));
        activityConfigurations.add(ConfLevel.UNIT.equals(confLevel) ? new ActivityConfiguration(referenceId, presencePlannedTime) : new ActivityConfiguration(presencePlannedTime,referenceId));
    }

    private void createDefaultAbsenceSettings(BigInteger phaseId, BigInteger applicablePlannedTimeId, List<ActivityConfiguration> activityConfigurations, Long referenceId,ConfLevel confLevel) {
        AbsencePlannedTime absencePlannedTime = new AbsencePlannedTime(phaseId, newArrayList(applicablePlannedTimeId), false);
        activityConfigurations.add(ConfLevel.UNIT.equals(confLevel) ? new ActivityConfiguration(referenceId, absencePlannedTime) : new ActivityConfiguration(absencePlannedTime,referenceId));
    }

    public PresencePlannedTime updatePresenceActivityConfiguration(Long unitId, PresencePlannedTime presencePlannedTime) {
        ActivityConfiguration activityConfiguration = activityConfigurationRepository.findPresenceConfigurationByUnitIdAndPhaseId(unitId, presencePlannedTime.getPhaseId());
        if (!Optional.ofNullable(activityConfiguration).isPresent() || !Optional.ofNullable(activityConfiguration.getPresencePlannedTime()).isPresent()) {
            exceptionService.dataNotFoundByIdException(ERROR_PRESENCEACTIVITYCONFIGURATION_NOTFOUND);
        }
        activityConfiguration.getPresencePlannedTime().setEmploymentWisePlannedTimeConfigurations(presencePlannedTime.getEmploymentWisePlannedTimeConfigurations());
        activityConfiguration.getPresencePlannedTime().setManagementPlannedTimeIds(presencePlannedTime.getManagementPlannedTimeIds());
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
        activityConfiguration.get().getAbsencePlannedTime().setPlannedTimeIds(absencePlannedTime.getPlannedTimeIds());
        activityConfigurationRepository.save(activityConfiguration.get());
        return absencePlannedTime;

    }

    public BigInteger createAbsenceExceptionActivityConfiguration(Long unitId, AbsencePlannedTime absencePlannedTime) {
        if (!Optional.ofNullable(absencePlannedTime.getTimeTypeId()).isPresent()) {
            exceptionService.dataNotFoundByIdException(ERROR_TIMETYPE_UNSELECTED);
        }
        ActivityConfiguration activityConfiguration = new ActivityConfiguration(unitId, new AbsencePlannedTime(absencePlannedTime.getPhaseId(), absencePlannedTime.getTimeTypeId(), absencePlannedTime.getPlannedTimeIds(), true));
        activityConfigurationRepository.save(activityConfiguration);
        return activityConfiguration.getId();

    }

    public List<ActivityConfigurationDTO> getAbsenceActivityConfiguration(Long unitId) {
        List<ActivityConfigurationDTO> activityConfigurationDTOS = activityConfigurationRepository.findAbsenceConfigurationByUnitId(unitId);
        Map<BigInteger,Integer> phaseMap = phaseService.getPhasesByUnit(unitId).stream().collect(Collectors.toMap(k->k.getId(),v->v.getSequence()));
        List<ActivityConfigurationDTO> modifiableList = new ArrayList<>(activityConfigurationDTOS);
        modifiableList.sort((a1, a2) -> Integer.compare(phaseMap.get(a1.getAbsencePlannedTime().getPhaseId()), phaseMap.get(a2.getAbsencePlannedTime().getPhaseId())));
        return modifiableList;
    }

    public List<ActivityConfigurationDTO> getPresenceActivityConfiguration(Long unitId) {
        List<ActivityConfigurationDTO> activityConfigurationDTOS = activityConfigurationRepository.findPresenceConfigurationByUnitId(unitId);
        Map<BigInteger,Integer> phaseMap = phaseService.getPhasesByUnit(unitId).stream().collect(Collectors.toMap(k->k.getId(),v->v.getSequence()));
        List<ActivityConfigurationDTO> modifiableList = new ArrayList<>(activityConfigurationDTOS);
        modifiableList.sort((a1, a2) -> Integer.compare(phaseMap.get(a1.getPresencePlannedTime().getPhaseId()), phaseMap.get(a2.getPresencePlannedTime().getPhaseId())));
        return modifiableList;
    }

    public ActivityConfigurationWrapper getDefaultData(Long unitId) {
        Long countryId = userIntegrationService.getCountryIdOfOrganization(unitId);
        if (!Optional.ofNullable(countryId).isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_COUNTRY_ID);
        }
        List<PhaseDTO> phases = phaseMongoRepository.getPhasesByUnit(unitId, Sort.Direction.ASC);
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
        List<EmploymentTypeDTO> employmentTypeDTOS = userIntegrationService.getEmploymentTypeByCountry(countryId);
        List<Long> employmentTypeIds = employmentTypeDTOS.stream().map(employmentTypeDTO -> employmentTypeDTO.getId()).collect(Collectors.toList());
        for (Phase phase : phases) {
            createDefaultPresentSettings(phase.getId(), normalPlannedTypeId, activityConfigurations, countryId,employmentTypeIds,ConfLevel.UNIT);
            createDefaultAbsenceSettings(phase.getId(), DRAFT_PHASE_NAME.equals(phase.getName()) ? extraTimePlannedTypeId : normalPlannedTypeId, activityConfigurations, countryId, ConfLevel.UNIT);
        }
        activityConfigurationRepository.saveEntities(activityConfigurations);
    }

    public PresencePlannedTime updatePresenceActivityConfigurationForCountry(Long countryId, PresencePlannedTime presencePlannedTime) {
        ActivityConfiguration activityConfiguration = activityConfigurationRepository.findPresenceConfigurationByCountryIdAndPhaseId(countryId, presencePlannedTime.getPhaseId());
        if (!Optional.of(activityConfiguration).isPresent() || !Optional.ofNullable(activityConfiguration.getPresencePlannedTime()).isPresent()) {
            exceptionService.dataNotFoundByIdException(ERROR_PRESENCEACTIVITYCONFIGURATION_NOTFOUND);
        }
        activityConfiguration.getPresencePlannedTime().setEmploymentWisePlannedTimeConfigurations(presencePlannedTime.getEmploymentWisePlannedTimeConfigurations());
        activityConfiguration.getPresencePlannedTime().setManagementPlannedTimeIds(presencePlannedTime.getManagementPlannedTimeIds());

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
        activityConfiguration.get().getAbsencePlannedTime().setPlannedTimeIds(absencePlannedTime.getPlannedTimeIds());
        activityConfigurationRepository.save(activityConfiguration.get());
        return absencePlannedTime;

    }

    public BigInteger createAbsenceExceptionActivityConfigurationForCountry(Long countryId, AbsencePlannedTime absencePlannedTime) {
        if (!Optional.ofNullable(absencePlannedTime.getTimeTypeId()).isPresent()) {
            exceptionService.dataNotFoundByIdException(ERROR_TIMETYPE_UNSELECTED);
        }
        ActivityConfiguration activityConfiguration = new ActivityConfiguration(new AbsencePlannedTime(absencePlannedTime.getPhaseId(), absencePlannedTime.getTimeTypeId(), absencePlannedTime.getPlannedTimeIds(), true), countryId);
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
        List<PhaseDTO> phases = phaseMongoRepository.getPhasesByCountryId(countryId, Sort.Direction.ASC);
        return getDefaultDataForCountry(phases, countryId);
    }

    private ActivityConfigurationWrapper getDefaultDataForCountry(List<PhaseDTO> phases, Long countryId) {
        List<EmploymentTypeDTO> employmentTypeDTOS = userIntegrationService.getEmploymentTypeByCountry(countryId);
        List<TimeTypeDTO> topLevelTimeType = timeTypeMongoRepository.getTopLevelTimeTypeIds(countryId);
        List<BigInteger> topLevelTimeTypeIds = topLevelTimeType.stream().map(TimeTypeDTO::getId).collect(Collectors.toList());
        List<TimeTypeResponseDTO> secondLevelTimeTypes = timeTypeMongoRepository.findAllChildByParentId(topLevelTimeTypeIds);
        List<PresenceTypeDTO> plannedTimeTypes = plannedTimeTypeRepository.getAllPresenceTypeByCountryId(countryId, false);
        return new ActivityConfigurationWrapper(phases, secondLevelTimeTypes, plannedTimeTypes,employmentTypeDTOS);
    }

    public void addPlannedTimeInShift(Shift shift, Map<BigInteger, ActivityWrapper> activityWrappers, StaffAdditionalInfoDTO staffAdditionalInfoDTO) {
        Phase phase = phaseService.getCurrentPhaseByUnitIdAndDate(shift.getUnitId(), shift.getActivities().get(0).getStartDate(), shift.getActivities().get(shift.getActivities().size() - 1).getEndDate());
        List<PlannedTime> plannedTimeList = shift.getActivities().stream().flatMap(k -> k.getPlannedTimes().stream()).collect(Collectors.toList());
        Map<DateTimeInterval, PlannedTime> plannedTimeMap = plannedTimeList.stream().filter(distinctByKey(plannedTime -> new DateTimeInterval(plannedTime.getStartDate(), plannedTime.getEndDate()))).collect(toMap(k -> new DateTimeInterval(k.getStartDate(), k.getEndDate()), Function.identity()));
        for (ShiftActivity shiftActivity : shift.getActivities()) {
            List<BigInteger> plannedTimeIds = addPlannedTimeInShift(shift.getUnitId(), phase.getId(), activityWrappers.get(shiftActivity.getActivityId()).getActivity(), staffAdditionalInfoDTO);
            BigInteger plannedTimeId = null;
            if(PhaseDefaultName.TIME_ATTENDANCE.equals(phase.getPhaseEnum()) && plannedTimeIds.contains(shiftActivity.getPlannedTimeId())){
                plannedTimeId = shiftActivity.getPlannedTimeId();
            }else {
                plannedTimeId = plannedTimeIds.get(0);
            }
            if(isNull(plannedTimeId)){
                exceptionService.dataNotFoundByIdException(PLANNED_TIME_CANNOT_EMPTY);
            }
            List<PlannedTime> plannedTimes = isNull(shift.getId()) ? newArrayList(new PlannedTime(plannedTimeId, shiftActivity.getStartDate(), shiftActivity.getEndDate())) : filterPlannedTimes(shiftActivity.getStartDate(), shiftActivity.getEndDate(), plannedTimeMap, plannedTimeId);
            shiftActivity.setPlannedTimes(plannedTimes);
        }
    }

    public List<BigInteger> addPlannedTimeInShift(Long unitId, BigInteger phaseId, Activity activity, StaffAdditionalInfoDTO staffAdditionalInfoDTO) {
        Boolean managementPerson = Optional.ofNullable(staffAdditionalInfoDTO.getUserAccessRoleDTO()).isPresent() && staffAdditionalInfoDTO.getUserAccessRoleDTO().getManagement();
        return (TimeTypeEnum.ABSENCE.equals(activity.getBalanceSettingsActivityTab().getTimeType())) ? getAbsencePlannedTime(unitId, phaseId, activity)
                : getPresencePlannedTime(unitId, phaseId, managementPerson, staffAdditionalInfoDTO);
    }

    private List<BigInteger> getAbsencePlannedTime(Long unitId, BigInteger phaseId, Activity activity) {
        List<ActivityConfiguration> activityConfigurations = activityConfigurationRepository.findAllAbsenceConfigurationByUnitIdAndPhaseId(unitId, phaseId);
        List<BigInteger> plannedTimeIds = null;
        for (ActivityConfiguration activityConfiguration : activityConfigurations) {
            if (!Optional.ofNullable(activityConfiguration.getAbsencePlannedTime()).isPresent()) {
                exceptionService.dataNotFoundByIdException(ERROR_ACTIVITYCONFIGURATION_NOTFOUND);
            }
            if (activityConfiguration.getAbsencePlannedTime().isException() && activity.getBalanceSettingsActivityTab().getTimeTypeId().equals(activityConfiguration.getAbsencePlannedTime().getTimeTypeId())) {
                plannedTimeIds = activityConfiguration.getAbsencePlannedTime().getPlannedTimeIds();
                break;
            } else {
                plannedTimeIds = activityConfiguration.getAbsencePlannedTime().getPlannedTimeIds();
            }
        }
        if(isCollectionEmpty(plannedTimeIds)){
            exceptionService.dataNotFoundByIdException(PLANNED_TIME_NOT_CONFIGURE);
        }
        return plannedTimeIds;
    }

    public List<BigInteger> getPresencePlannedTime(Long unitId, BigInteger phaseId, Boolean managementPerson, StaffAdditionalInfoDTO staffAdditionalInfoDTO) {
        List<BigInteger> plannedTimeIds;
        ActivityConfiguration activityConfiguration = findPresenceConfigurationByUnitIdAndPhaseId(unitId, phaseId);
        if (!Optional.ofNullable(activityConfiguration).isPresent() || !Optional.ofNullable(activityConfiguration.getPresencePlannedTime()).isPresent()) {
            exceptionService.dataNotFoundByIdException(ERROR_ACTIVITYCONFIGURATION_NOTFOUND);
        }
        if(managementPerson){
            plannedTimeIds = activityConfiguration.getPresencePlannedTime().getManagementPlannedTimeIds();
        }else {
            plannedTimeIds = activityConfiguration.getPresencePlannedTime().getEmploymentWisePlannedTimeConfigurations().stream()
                    .filter(employmentWisePlannedTimeConfiguration -> employmentWisePlannedTimeConfiguration.getEmploymentTypeId().equals(staffAdditionalInfoDTO.getEmployment().getEmploymentType().getId()))
                    .findFirst().orElseThrow(()->new DataNotFoundException(convertMessage(PLANNED_TIME_NOT_CONFIGURE))).getStaffPlannedTimeIds();
        }
        if(isCollectionEmpty(plannedTimeIds)){
            exceptionService.dataNotFoundByIdException(PLANNED_TIME_NOT_CONFIGURE);
        }
        return plannedTimeIds;
    }

    private List<PlannedTime> filterPlannedTimes(Date startDate, Date endDate, Map<DateTimeInterval, PlannedTime> plannedTimeMap, BigInteger plannedTimeId) {
        DateTimeInterval activityInterval = new DateTimeInterval(startDate, endDate);
        plannedTimeMap = plannedTimeMap.entrySet().stream().filter(map -> map.getKey().overlaps(activityInterval)).collect(toMap(k -> k.getKey(), k -> k.getValue()));
        plannedTimeMap = plannedTimeMap.entrySet().stream().sorted(comparing(k -> k.getKey().getStartDate())).collect(toMap(e -> e.getKey(),v->v.getValue(), (e1, e2) -> e2, LinkedHashMap::new));
        List<PlannedTime> plannedTimes = new ArrayList<>();
        final boolean endDateInside = plannedTimeMap.entrySet().stream().anyMatch(k -> k.getKey().containsStartOrEnd(endDate));
        final boolean activityIntervalOverLapped = plannedTimeMap.entrySet().stream().anyMatch(k -> k.getKey().overlaps(activityInterval));

        if (!activityIntervalOverLapped) {
            plannedTimes.add(new PlannedTime(plannedTimeId, startDate, endDate));
        } else {

            if (plannedTimeMap.size() != 0) {
                DateTimeInterval lastInterval = plannedTimeMap.keySet().stream().skip(plannedTimeMap.keySet().size() - 1).findFirst().get();
                boolean addedAtLeading = false;
                for (Map.Entry<DateTimeInterval, PlannedTime> plannedTimeInterval : plannedTimeMap.entrySet()) {
                    DateTimeInterval shiftActivityInterVal = new DateTimeInterval(startDate, endDate);
                    if (plannedTimeInterval.getKey().containsInterval(shiftActivityInterVal)) {
                        plannedTimes.add(new PlannedTime(plannedTimeInterval.getValue().getPlannedTimeId(), startDate, endDate));
                        break;
                    } else if (startDate.before(plannedTimeInterval.getKey().getStartDate())) {
                        if (!addedAtLeading) {
                            plannedTimes.add(new PlannedTime(plannedTimeId, startDate, plannedTimeInterval.getKey().getStartDate()));
                            addedAtLeading = true;
                        }
                        plannedTimes.add(new PlannedTime(plannedTimeInterval.getValue().getPlannedTimeId(), plannedTimeInterval.getKey().getStartDate(), plannedTimeInterval.getKey().getEndDate()));
                        startDate = plannedTimeInterval.getKey().getEndDate();
                    } else if (startDate.equals(plannedTimeInterval.getKey().getStartDate()) || startDate.after(plannedTimeInterval.getKey().getStartDate())) {
                        plannedTimes.add(new PlannedTime(plannedTimeInterval.getValue().getPlannedTimeId(), startDate, plannedTimeInterval.getKey().getEndDate()));
                        startDate = plannedTimeInterval.getKey().getEndDate();
                    }  else if (!plannedTimeInterval.getKey().overlaps(shiftActivityInterVal)) {
                        plannedTimes.add(new PlannedTime(plannedTimeId, startDate, endDate));
                    }
                }
                if (!endDateInside) {
                    plannedTimes.add(new PlannedTime(plannedTimeId, lastInterval.getEndDate(), endDate));
                }

            }
        }
        return plannedTimes;
    }

    public boolean existsByUnitIdAndDeletedFalse(Long unitId){
        return activityConfigurationRepository.existsByUnitIdAndDeletedFalse(unitId);
    }

    public List<ActivityConfiguration> findAllByUnitIdAndDeletedFalse(Long unitId){
        return activityConfigurationRepository.findAllByUnitIdAndDeletedFalse(unitId);
    }

    public ActivityConfiguration findPresenceConfigurationByUnitIdAndPhaseId(Long unitId, BigInteger phaseId){
        return activityConfigurationRepository.findPresenceConfigurationByUnitIdAndPhaseId(unitId,phaseId);
    }

    public List<ActivityConfigurationDTO> findPresenceConfigurationByUnitId(Long unitId){
        return activityConfigurationRepository.findPresenceConfigurationByUnitId(unitId);
    }

    public List<ActivityConfigurationDTO> findAbsenceConfigurationByUnitId(Long unitId){
        return activityConfigurationRepository.findAbsenceConfigurationByUnitId(unitId);
    }

    public List<ActivityConfiguration> findAllAbsenceConfigurationByUnitIdAndPhaseId(Long unitId, BigInteger phaseId){
        return activityConfigurationRepository.findAllAbsenceConfigurationByUnitIdAndPhaseId(unitId,phaseId);
    }

    public ActivityConfiguration findPresenceConfigurationByCountryIdAndPhaseId(Long countryId,BigInteger phaseId){
        return activityConfigurationRepository.findPresenceConfigurationByCountryIdAndPhaseId(countryId,phaseId);
    }

    public List<ActivityConfigurationDTO> findAbsenceConfigurationByCountryIdAndPhaseId(Long unitId,BigInteger phaseId){
        return activityConfigurationRepository.findAbsenceConfigurationByCountryIdAndPhaseId(unitId,phaseId);
    }
}
