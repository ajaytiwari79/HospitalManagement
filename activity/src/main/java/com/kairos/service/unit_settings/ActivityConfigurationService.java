package com.kairos.service.unit_settings;

import com.kairos.commons.custom_exception.DataNotFoundByIdException;
import com.kairos.commons.custom_exception.DataNotFoundException;
import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.activity.counter.enums.ConfLevel;
import com.kairos.dto.activity.phase.PhaseDTO;
import com.kairos.dto.activity.presence_type.PresenceTypeDTO;
import com.kairos.dto.activity.shift.PlannedTime;
import com.kairos.dto.activity.time_type.TimeTypeDTO;
import com.kairos.dto.activity.unit_settings.activity_configuration.*;
import com.kairos.dto.user.country.agreement.cta.cta_response.EmploymentTypeDTO;
import com.kairos.dto.user.country.agreement.cta.cta_response.TimeTypeResponseDTO;
import com.kairos.dto.user.user.staff.StaffAdditionalInfoDTO;
import com.kairos.dto.user_context.UserContext;
import com.kairos.enums.TimeTypeEnum;
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
import com.kairos.service.activity.PlannedTimeTypeService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.phase.PhaseService;
import com.kairos.wrapper.phase.PhaseActivityDTO;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.asLocalDate;
import static com.kairos.commons.utils.ObjectUtils.*;
import static com.kairos.constants.ActivityMessagesConstants.*;
import static com.kairos.constants.AppConstants.*;
import static com.kairos.constants.CommonMessageConstants.PLANNED_TIME_CANNOT_EMPTY;
import static com.kairos.constants.CommonMessageConstants.PLANNED_TIME_NOT_CONFIGURE;
import static com.kairos.service.shift.ShiftValidatorService.convertMessage;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toMap;

@Service
public class ActivityConfigurationService {

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
    @Inject private PlannedTimeTypeService plannedTimeTypeService;

    @CacheEvict(value = {"getPresenceActivityConfiguration","getAbsenceActivityConfiguration","getNonWorkingActivityConfiguration"},allEntries = true)
    public void createDefaultSettings(Long unitId, Long countryId, List<Phase> phases,List<Long> employmentTypeIds) {
        if(!activityConfigurationRepository.existsByUnitIdAndDeletedFalse(unitId)) {
            List<ActivityConfiguration> activityConfigurations = new ArrayList<>();
            if (isCollectionEmpty(phases)) {
                phases = phaseMongoRepository.findByOrganizationIdAndDeletedFalse(unitId);
            }
            List<PresenceTypeDTO> plannedTimeTypes = plannedTimeTypeRepository.getAllPresenceTypeByCountryId(countryId, false);
            PresenceTypeDTO normalPlannedType = plannedTimeTypes.stream().filter(presenceTypeDTO -> presenceTypeDTO.getName().equalsIgnoreCase(NORMAL_TIME)).findAny().orElse(null);
            PresenceTypeDTO extraTimePlannedType = plannedTimeTypes.stream().filter(presenceTypeDTO -> presenceTypeDTO.getName().equalsIgnoreCase(EXTRA_TIME)).findAny().orElse(null);
            BigInteger normalPlannedTypeId = isNull(normalPlannedType) ? null : normalPlannedType.getId();
            BigInteger extraTimePlannedTypeId = isNotNull(extraTimePlannedType) ? normalPlannedType.getId() : normalPlannedTypeId;
            for (Phase phase : phases) {
                createDefaultPresentSettings(phase.getId(), normalPlannedTypeId, activityConfigurations, unitId, employmentTypeIds, ConfLevel.UNIT);
                createDefaultAbsenceSettings(phase.getId(), DRAFT_PHASE_NAME.equals(phase.getName()) ? extraTimePlannedTypeId : normalPlannedTypeId, activityConfigurations, unitId, ConfLevel.UNIT);
                createDefaultNonWorkingSettings(phase.getId(), DRAFT_PHASE_NAME.equals(phase.getName()) ? extraTimePlannedTypeId : normalPlannedTypeId, activityConfigurations, unitId, ConfLevel.UNIT);
            }
            activityConfigurationRepository.saveEntities(activityConfigurations);
        }
    }

    @CacheEvict(value = {"getPresenceActivityConfiguration","getAbsenceActivityConfiguration","getNonWorkingActivityConfiguration"},allEntries = true)
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

    private void createDefaultNonWorkingSettings(BigInteger phaseId, BigInteger applicablePlannedTimeId, List<ActivityConfiguration> activityConfigurations, Long referenceId,ConfLevel confLevel) {
        NonWorkingPlannedTime nonWorkingPlannedTime = new NonWorkingPlannedTime(phaseId, newArrayList(applicablePlannedTimeId), false);
        activityConfigurations.add(ConfLevel.UNIT.equals(confLevel) ? new ActivityConfiguration(referenceId, nonWorkingPlannedTime) : new ActivityConfiguration(nonWorkingPlannedTime,referenceId));
    }

    @CacheEvict(value = "getPresenceActivityConfiguration", key = "#unitId")
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

    @CacheEvict(value = "getAbsenceActivityConfiguration", key = "#unitId")
    public AbsencePlannedTime updateAbsenceActivityConfiguration(Long unitId, BigInteger activityConfigurationId, AbsencePlannedTime absencePlannedTime) {
        return updateAbsenceActivityConfiguration(activityConfigurationId,absencePlannedTime);
    }

    public AbsencePlannedTime updateAbsenceActivityConfiguration(BigInteger activityConfigurationId, AbsencePlannedTime absencePlannedTime) {
        ActivityConfiguration activityConfiguration = activityConfigurationRepository.findById(activityConfigurationId).orElseThrow(()->new DataNotFoundByIdException(convertMessage(ERROR_ABSENCEACTIVITYCONFIGURATION_NOTFOUND)));
        if (Optional.ofNullable(absencePlannedTime.getTimeTypeId()).isPresent()) {
            activityConfiguration.getAbsencePlannedTime().setTimeTypeId(absencePlannedTime.getTimeTypeId());
            activityConfiguration.getAbsencePlannedTime().setException(true);
        }
        activityConfiguration.getAbsencePlannedTime().setPlannedTimeIds(absencePlannedTime.getPlannedTimeIds());
        activityConfigurationRepository.save(activityConfiguration);
        return absencePlannedTime;
    }

    @CacheEvict(value = {"getPresenceActivityConfiguration","getAbsenceActivityConfiguration","getNonWorkingActivityConfiguration"},allEntries = true)
    public BigInteger createAbsenceExceptionActivityConfiguration(Long unitOrCountryId, AbsencePlannedTime absencePlannedTime, boolean forCountry) {
        if (!Optional.ofNullable(absencePlannedTime.getTimeTypeId()).isPresent()) {
            exceptionService.dataNotFoundByIdException(ERROR_TIMETYPE_UNSELECTED);
        }
        ActivityConfiguration activityConfiguration = forCountry ? new ActivityConfiguration(new AbsencePlannedTime(absencePlannedTime.getPhaseId(), absencePlannedTime.getTimeTypeId(), absencePlannedTime.getPlannedTimeIds(), true), unitOrCountryId)
                :new ActivityConfiguration(unitOrCountryId, new AbsencePlannedTime(absencePlannedTime.getPhaseId(), absencePlannedTime.getTimeTypeId(), absencePlannedTime.getPlannedTimeIds(), true));
        activityConfigurationRepository.save(activityConfiguration);
        return activityConfiguration.getId();
    }

    @Cacheable(value = "getAbsenceActivityConfiguration", key = "#unitId", cacheManager = "cacheManager")
    public List<ActivityConfigurationDTO> getAbsenceActivityConfiguration(Long unitId) {
        List<ActivityConfigurationDTO> activityConfigurationDTOS = activityConfigurationRepository.findAbsenceConfigurationByUnitId(unitId);
        Map<BigInteger,Integer> phaseMap = phaseService.getPhasesByUnit(unitId).stream().collect(Collectors.toMap(k->k.getId(),v->v.getSequence()));
        List<ActivityConfigurationDTO> modifiableList = new ArrayList<>(activityConfigurationDTOS);
        modifiableList.sort(Comparator.comparingInt(a -> phaseMap.get(a.getAbsencePlannedTime().getPhaseId())));
        return modifiableList;
    }

    @Cacheable(value = "getPresenceActivityConfiguration", key = "#unitId", cacheManager = "cacheManager")
    public List<ActivityConfigurationDTO> getPresenceActivityConfiguration(Long unitId) {
        List<ActivityConfigurationDTO> activityConfigurationDTOS = activityConfigurationRepository.findPresenceConfigurationByUnitId(unitId);
        Map<BigInteger,Integer> phaseMap = phaseService.getPhasesByUnit(unitId).stream().collect(Collectors.toMap(k->k.getId(),v->v.getSequence()));
        List<ActivityConfigurationDTO> modifiableList = new ArrayList<>(activityConfigurationDTOS);
        modifiableList.sort(Comparator.comparingInt(a -> phaseMap.get(a.getPresencePlannedTime().getPhaseId())));
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
        if(!activityConfigurationRepository.existsByCountryIdAndDeletedFalse(countryId)) {
            List<ActivityConfiguration> activityConfigurations = new ArrayList<>();
            if (phases == null || phases.isEmpty()) {
                phases = ObjectMapperUtils.copyCollectionPropertiesByMapper(phaseMongoRepository.getPhasesByCountryId(countryId, Sort.Direction.ASC), Phase.class);
            }
            List<PresenceTypeDTO> plannedTimeTypes = plannedTimeTypeRepository.getAllPresenceTypeByCountryId(countryId, false);
            Optional<PresenceTypeDTO> normalPlannedType = plannedTimeTypes.stream().filter(presenceTypeDTO -> presenceTypeDTO.getName().equalsIgnoreCase(NORMAL_TIME)).findAny();
            Optional<PresenceTypeDTO> extraTimePlannedType = plannedTimeTypes.stream().filter(presenceTypeDTO -> presenceTypeDTO.getName().equalsIgnoreCase(EXTRA_TIME)).findAny();
            BigInteger normalPlannedTypeId = normalPlannedType.map(PresenceTypeDTO::getId).orElse(null);
            BigInteger extraTimePlannedTypeId = extraTimePlannedType.isPresent() ? normalPlannedType.get().getId() : normalPlannedTypeId;
            List<EmploymentTypeDTO> employmentTypeDTOS = userIntegrationService.getEmploymentTypeByCountry(countryId);
            List<Long> employmentTypeIds = employmentTypeDTOS.stream().map(employmentTypeDTO -> employmentTypeDTO.getId()).collect(Collectors.toList());
            for (Phase phase : phases) {
                createDefaultPresentSettings(phase.getId(), normalPlannedTypeId, activityConfigurations, countryId, employmentTypeIds, ConfLevel.COUNTRY);
                createDefaultAbsenceSettings(phase.getId(), DRAFT_PHASE_NAME.equals(phase.getName()) ? extraTimePlannedTypeId : normalPlannedTypeId, activityConfigurations, countryId, ConfLevel.COUNTRY);
                createDefaultNonWorkingSettings(phase.getId(), DRAFT_PHASE_NAME.equals(phase.getName()) ? extraTimePlannedTypeId : normalPlannedTypeId, activityConfigurations, countryId, ConfLevel.COUNTRY);
            }
            activityConfigurationRepository.saveEntities(activityConfigurations);
        }
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

    public void addPlannedTimeInShift(Shift shift, Map<BigInteger, ActivityWrapper> activityWrapperMap, StaffAdditionalInfoDTO staffAdditionalInfoDTO,boolean shiftTypeChanged,Phase phase) {
        //Phase phase = phaseService.getCurrentPhaseByUnitIdAndDate(shift.getUnitId(), shift.getActivities().get(0).getStartDate(), shift.getActivities().get(shift.getActivities().size() - 1).getEndDate());
        List<PlannedTime> plannedTimeList = shift.getActivities().stream().flatMap(k -> k.getPlannedTimes().stream()).collect(Collectors.toList());
        Map<DateTimeInterval, PlannedTime> plannedTimeMap = plannedTimeList.stream().filter(distinctByKey(plannedTime -> new DateTimeInterval(plannedTime.getStartDate(), plannedTime.getEndDate()))).collect(toMap(k -> new DateTimeInterval(k.getStartDate(), k.getEndDate()), Function.identity()));
        Set<TimeTypeEnum> timeTypes = shift.getActivities().stream().map(shiftActivity -> activityWrapperMap.get(shiftActivity.getActivityId()).getActivity().getActivityBalanceSettings().getTimeType()).collect(Collectors.toSet());
        List<ActivityConfiguration> absenceActivityConfigurations = null;
        ActivityConfiguration presenceActivityConfiguration = null;
        ActivityConfiguration nonWorkingActivityConfiguration = null;
        if(timeTypes.contains(TimeTypeEnum.ABSENCE)){
            absenceActivityConfigurations = activityConfigurationRepository.findAllAbsenceConfigurationByUnitIdAndPhaseId(shift.getUnitId(), phase.getId());
        }
        if(timeTypes.contains(TimeTypeEnum.PRESENCE)){
            presenceActivityConfiguration = findPresenceConfigurationByUnitIdAndPhaseId(shift.getUnitId(), phase.getId());
        }
        if(!CollectionUtils.removeAll(timeTypes,newHashSet(TimeTypeEnum.ABSENCE,TimeTypeEnum.PRESENCE)).isEmpty()){
            nonWorkingActivityConfiguration = activityConfigurationRepository.findAllNonWorkingConfigurationByUnitIdAndPhaseId(shift.getUnitId(), phase.getId());
        }
        for (ShiftActivity shiftActivity : shift.getActivities()) {
            List<BigInteger> plannedTimeIds = addPlannedTimeInShift(activityWrapperMap.get(shiftActivity.getActivityId()).getActivity(), staffAdditionalInfoDTO,absenceActivityConfigurations,presenceActivityConfiguration,nonWorkingActivityConfiguration);
            BigInteger plannedTimeId;
            if(!asLocalDate(shiftActivity.getStartDate()).isAfter(LocalDate.now()) && plannedTimeIds.contains(shiftActivity.getPlannedTimeId())){
                plannedTimeId = shiftActivity.getPlannedTimeId();
            }else {
                plannedTimeId = plannedTimeIds.get(0);
            }
            if(isNull(plannedTimeId)){
                exceptionService.dataNotFoundByIdException(PLANNED_TIME_CANNOT_EMPTY);
            }
            List<PlannedTime> plannedTimes = isNull(shift.getId()) || shiftTypeChanged ? newArrayList(new PlannedTime(plannedTimeId, shiftActivity.getStartDate(), shiftActivity.getEndDate())) : filterPlannedTimes(shiftActivity.getStartDate(), shiftActivity.getEndDate(), plannedTimeMap, plannedTimeId);
            shiftActivity.setPlannedTimes(plannedTimes);
        }
    }


    public List<BigInteger> addPlannedTimeInShift(Activity activity, StaffAdditionalInfoDTO staffAdditionalInfoDTO,List<ActivityConfiguration> absenceActivityConfigurations,ActivityConfiguration presenceActivityConfiguration,ActivityConfiguration nonWorkingActivityConfiguration) {
        boolean managementPerson = UserContext.getUserDetails().isManagement();
        List<BigInteger> plannedTimes;
        switch (activity.getActivityBalanceSettings().getTimeType()){
            case ABSENCE :
                plannedTimes = getAbsencePlannedTime(activity,absenceActivityConfigurations);
                break;
            case PRESENCE:
                plannedTimes = getPresencePlannedTime(managementPerson, staffAdditionalInfoDTO,presenceActivityConfiguration);
                break;
            default:
                plannedTimes = getNonWorkingPlannedTime(nonWorkingActivityConfiguration);

        }
        return plannedTimes;
    }

    private List<BigInteger> getAbsencePlannedTime(Activity activity,List<ActivityConfiguration> activityConfigurations) {
        List<BigInteger> plannedTimeIds = null;
        for (ActivityConfiguration activityConfiguration : activityConfigurations) {
            if (!Optional.ofNullable(activityConfiguration.getAbsencePlannedTime()).isPresent()) {
                exceptionService.dataNotFoundByIdException(ERROR_ACTIVITYCONFIGURATION_NOTFOUND);
            }
            if (activityConfiguration.getAbsencePlannedTime().isException() && activity.getActivityBalanceSettings().getTimeTypeId().equals(activityConfiguration.getAbsencePlannedTime().getTimeTypeId())) {
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

    private List<BigInteger> getNonWorkingPlannedTime(ActivityConfiguration nonWorkingActivityConfiguration) {
        if (!Optional.ofNullable(nonWorkingActivityConfiguration).isPresent()) {
            exceptionService.dataNotFoundByIdException(ERROR_ACTIVITYCONFIGURATION_NOTFOUND);
        }
        List<BigInteger> plannedTimeIds = nonWorkingActivityConfiguration.getNonWorkingPlannedTime().getPlannedTimeIds();
        if(isCollectionEmpty(plannedTimeIds)){
            exceptionService.dataNotFoundByIdException(PLANNED_TIME_NOT_CONFIGURE);
        }
        return plannedTimeIds;
    }

    public List<BigInteger> getPresencePlannedTime(Boolean managementPerson, StaffAdditionalInfoDTO staffAdditionalInfoDTO,ActivityConfiguration activityConfiguration) {
        List<BigInteger> plannedTimeIds;
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
        plannedTimeMap = plannedTimeMap.entrySet().stream().filter(map -> map.getKey().overlaps(activityInterval)).collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
        plannedTimeMap = plannedTimeMap.entrySet().stream().sorted(comparing(k -> k.getKey().getStartDate())).collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));
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

    public List<ActivityConfigurationDTO> findAllByUnitIdAndDeletedFalse(Long unitId){
        return activityConfigurationRepository.findAllByUnitIdAndDeletedFalse(unitId);
    }

    public ActivityConfiguration findPresenceConfigurationByUnitIdAndPhaseId(Long unitId, BigInteger phaseId){
        return activityConfigurationRepository.findPresenceConfigurationByUnitIdAndPhaseId(unitId,phaseId);
    }

    public BigInteger createNonWorkingExceptionActivityConfiguration(Long unitOrCountryId, NonWorkingPlannedTime nonWorkingPlannedTime, boolean forCountry) {
        if (isNull(nonWorkingPlannedTime.getTimeTypeId()) && !nonWorkingPlannedTime.isException()) {
            exceptionService.dataNotFoundByIdException(ERROR_TIMETYPE_UNSELECTED);
        }
        ActivityConfiguration activityConfiguration = forCountry ? new ActivityConfiguration(new NonWorkingPlannedTime(nonWorkingPlannedTime.getPhaseId(), nonWorkingPlannedTime.getTimeTypeId(), nonWorkingPlannedTime.getPlannedTimeIds(), true), unitOrCountryId)
                :new ActivityConfiguration(unitOrCountryId, new NonWorkingPlannedTime(nonWorkingPlannedTime.getPhaseId(), nonWorkingPlannedTime.getTimeTypeId(), nonWorkingPlannedTime.getPlannedTimeIds(), true));
        activityConfigurationRepository.save(activityConfiguration);
        return activityConfiguration.getId();
    }


    public NonWorkingPlannedTime updateNonWorkingActivityConfiguration(BigInteger activityConfigurationId, NonWorkingPlannedTime nonWorkingPlannedTime) {
        Optional<ActivityConfiguration> activityConfiguration = activityConfigurationRepository.findById(activityConfigurationId);
        if (!Optional.of(activityConfiguration).isPresent()) {
            exceptionService.dataNotFoundByIdException(ERROR_NONWORKINGACTIVITYCONFIGURATION_NOTFOUND);
        }
        if (Optional.ofNullable(nonWorkingPlannedTime.getTimeTypeId()).isPresent()) {
            activityConfiguration.get().getNonWorkingPlannedTime().setTimeTypeId(nonWorkingPlannedTime.getTimeTypeId());
            activityConfiguration.get().getNonWorkingPlannedTime().setException(true);
        }
        activityConfiguration.get().getNonWorkingPlannedTime().setPlannedTimeIds(nonWorkingPlannedTime.getPlannedTimeIds());
        activityConfigurationRepository.save(activityConfiguration.get());
        return nonWorkingPlannedTime;
    }

    @CacheEvict(value = "getNonWorkingActivityConfiguration",key = "#unitId")
    public NonWorkingPlannedTime updateNonWorkingActivityConfiguration(Long unitId,BigInteger activityConfigurationId, NonWorkingPlannedTime nonWorkingPlannedTime) {
        return updateNonWorkingActivityConfiguration(activityConfigurationId,nonWorkingPlannedTime);
    }

    @Cacheable(value = "getNonWorkingActivityConfiguration", key = "#unitId", cacheManager = "cacheManager")
    public List<ActivityConfigurationDTO> getNonWorkingActivityConfiguration(Long unitId) {
        List<ActivityConfigurationDTO> activityConfigurationDTOS = activityConfigurationRepository.findNonWorkingConfigurationByUnitId(unitId);
        Map<BigInteger,Integer> phaseMap = phaseService.getPhasesByUnit(unitId).stream().collect(Collectors.toMap(k->k.getId(),v->v.getSequence()));
        List<ActivityConfigurationDTO> modifiableList = new ArrayList<>(activityConfigurationDTOS);
        modifiableList.sort(Comparator.comparingInt(a -> phaseMap.get(a.getNonWorkingPlannedTime().getPhaseId())));
        return modifiableList;
    }

    public List<ActivityConfigurationDTO> getNonWorkingActivityConfigurationForCountry(Long countryId) {
        return activityConfigurationRepository.findNonWorkingConfigurationByCountryId(countryId);
    }

    //todo this method to copy default NonWorkingActivityConfiguration from AbsenceActivityConfiguration
    public boolean copyNonWorkingActivityConfigurationFromAbsence() {
        List<ActivityConfiguration> activityConfigurations = activityConfigurationRepository.findAllAbsenceConfiguration();
        if(isCollectionNotEmpty(activityConfigurations)){
            activityConfigurations.forEach(activityConfiguration -> {
                NonWorkingPlannedTime nonWorkingPlannedTime = new NonWorkingPlannedTime(activityConfiguration.getAbsencePlannedTime().getPhaseId(),activityConfiguration.getAbsencePlannedTime().getTimeTypeId(),activityConfiguration.getAbsencePlannedTime().getPlannedTimeIds(),isNull(activityConfiguration.getAbsencePlannedTime().getTimeTypeId()));
                if(isNotNull(activityConfiguration.getCountryId())){
                    createNonWorkingExceptionActivityConfiguration(activityConfiguration.getCountryId(), nonWorkingPlannedTime,true);
                }else{
                    createNonWorkingExceptionActivityConfiguration(activityConfiguration.getUnitId(), nonWorkingPlannedTime,false);
                }
            });
        }
        return true;
    }

    public PhaseActivityDTO getPlannedTimeTypeConfiguration(Long unitId){
        List<PresenceTypeDTO> plannedTimes = plannedTimeTypeService.getAllPresenceTypeByCountry(UserContext.getUserDetails().getCountryId());
        List<ActivityConfigurationDTO> activityConfigurations = findAllByUnitIdAndDeletedFalse(unitId);
        return PhaseActivityDTO.builder().plannedTimes(plannedTimes).activityConfigurations(activityConfigurations).build();
    }
}
