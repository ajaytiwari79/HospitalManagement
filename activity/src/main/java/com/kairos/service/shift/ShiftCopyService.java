package com.kairos.service.shift;

import com.kairos.commons.service.locale.LocaleService;
import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.activity.activity.ActivityDTO;
import com.kairos.dto.activity.cta.CTAResponseDTO;
import com.kairos.dto.activity.cta.CTARuleTemplateDTO;
import com.kairos.dto.activity.period.PlanningPeriodDTO;
import com.kairos.dto.activity.shift.*;
import com.kairos.dto.user.country.agreement.cta.cta_response.DayTypeDTO;
import com.kairos.dto.user.staff.unit_position.StaffUnitPositionUnitDataWrapper;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.activity.ActivityWrapper;
import com.kairos.persistence.model.break_settings.BreakSettings;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.model.unit_settings.ActivityConfiguration;
import com.kairos.persistence.model.wta.WTAQueryResultDTO;
import com.kairos.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.persistence.repository.break_settings.BreakSettingMongoRepository;
import com.kairos.persistence.repository.common.MongoSequenceRepository;
import com.kairos.persistence.repository.cta.CostTimeAgreementRepository;
import com.kairos.persistence.repository.period.PlanningPeriodMongoRepository;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.persistence.repository.unit_settings.ActivityConfigurationRepository;
import com.kairos.persistence.repository.wta.WorkingTimeAgreementMongoRepository;
import com.kairos.rest_client.GenericIntegrationService;
import com.kairos.service.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.utils.ShiftValidatorService;
import com.kairos.utils.time_bank.TimeBankCalculationService;
import com.kairos.wrapper.ShiftResponseDTO;
import com.kairos.wrapper.shift.ShiftWithActivityDTO;
import org.apache.commons.collections.CollectionUtils;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.constants.AppConstants.FULL_DAY_CALCULATION;
import static com.kairos.constants.AppConstants.FULL_WEEK;
import static com.kairos.constants.AppConstants.NO_CONFLICTS;
import static com.kairos.utils.ShiftValidatorService.getValidDays;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.reducing;

/**
 * CreatedBy vipulpandey on 28/11/18
 **/
@Service
public class ShiftCopyService extends MongoBaseService {

    @Inject
    private ShiftMongoRepository shiftMongoRepository;
    @Inject
    private ExceptionService exceptionService;
    @Inject
    private ActivityMongoRepository activityRepository;
    @Inject
    private GenericIntegrationService genericIntegrationService;
    @Inject
    private BreakSettingMongoRepository breakSettingMongoRepository;
    @Inject
    private ShiftBreakService shiftBreakService;
    @Inject
    private ShiftValidatorService shiftValidatorService;
    @Inject
    private LocaleService localeService;
    @Inject
    private CostTimeAgreementRepository costTimeAgreementRepository;
    @Inject
    private ShiftService shiftService;
    @Inject
    private PlanningPeriodMongoRepository planningPeriodMongoRepository;
    @Inject
    private ActivityConfigurationRepository activityConfigurationRepository;
    @Inject
    private WorkingTimeAgreementMongoRepository workingTimeAgreementMongoRepository;
    @Inject
    private MongoSequenceRepository mongoSequenceRepository;
    @Inject
    private TimeBankCalculationService timeBankCalculationService;
    private static final Logger logger = LoggerFactory.getLogger(ShiftCopyService.class);

    public CopyShiftResponse copyShifts(Long unitId, CopyShiftDTO copyShiftDTO) {
        List<ShiftResponseDTO> shifts = shiftMongoRepository.findAllByIdGroupByDate(copyShiftDTO.getShiftIds());
        if (!Optional.ofNullable(shifts).isPresent() || shifts.isEmpty()) {
            exceptionService.invalidOperationException("message.shift.notBlank");
        }
        Set<BigInteger> activityIds = shifts.stream().flatMap(s -> s.getShifts().stream().flatMap(ss -> ss.getActivities().stream().map(a -> a.getActivityId()))).collect(Collectors.toSet());
        List<ActivityWrapper> activities = activityRepository.findActivitiesAndTimeTypeByActivityId(new ArrayList<>(activityIds));
        Map<BigInteger, ActivityWrapper> activityMap = activities.stream().collect(Collectors.toMap(k -> k.getActivity().getId(), v -> v));
        StaffUnitPositionUnitDataWrapper dataWrapper = genericIntegrationService.getStaffsUnitPosition(unitId, copyShiftDTO.getStaffIds(), copyShiftDTO.getExpertiseId());
        List<StaffUnitPositionDetails> staffDataList = dataWrapper.getStaffUnitPositionDetails();
        List<Long> unitPositionIds = staffDataList.stream().map(StaffUnitPositionDetails::getId).collect(Collectors.toList());
        findAndAddCTAInUnitPositions(staffDataList, copyShiftDTO, dataWrapper, unitPositionIds);
        Map<Long, List<WTAQueryResultDTO>> WTAMapByUnitPositionId = findAllWTAGroupByUnitPositionId(unitPositionIds, copyShiftDTO);

        List<Long> expertiseIds = staffDataList.stream().map(staffUnitPositionDetails -> staffUnitPositionDetails.getExpertise().getId()).collect(Collectors.toList());
        List<BreakSettings> breakSettings = breakSettingMongoRepository.findAllByDeletedFalseAndExpertiseIdInOrderByCreatedAtAsc(expertiseIds);
        Map<BigInteger, ActivityWrapper> breakActivitiesMap = shiftBreakService.getBreakActivities(breakSettings, unitId);
        activityMap.putAll(breakActivitiesMap);

        List<ActivityConfiguration> activityConfigurations = activityConfigurationRepository.findAllByUnitIdAndDeletedFalse(unitId); // might we add more optimization later
        if (activityConfigurations.isEmpty()) {
            exceptionService.dataNotFoundException("error.activityConfiguration.notFound");
        }
        Integer unCopiedShiftCount = 0;

        List<PlanningPeriodDTO> planningPeriods = planningPeriodMongoRepository.findAllPlanningPeriodBetweenDatesAndUnitId(unitId, DateUtils.asDate(copyShiftDTO.getStartDate()), DateUtils.asDate(copyShiftDTO.getEndDate()));
        if (planningPeriods.isEmpty()) {
            exceptionService.actionNotPermittedException("message.shift.planning.period.exits", copyShiftDTO.getStartDate());
        }

        Map<DateTimeInterval, PlanningPeriodDTO> planningPeriodMap = planningPeriods.stream().collect(Collectors.toMap(k -> new DateTimeInterval(k.getStartDate(), k.getEndDate()), v -> v));
        CopyShiftResponse copyShiftResponse = new CopyShiftResponse();
        List<ShiftResponseDTO> previousShiftBetweenDatesByUnitPosition = shiftMongoRepository.findShiftsBetweenDurationByUnitPositions(unitPositionIds, DateUtils.asDate(copyShiftDTO.getStartDate().atTime(LocalTime.MIN)), DateUtils.asDate(copyShiftDTO.getEndDate().atTime(LocalTime.MAX)));
        Map<Long, List<Shift>> unitPositionWiseShifts = previousShiftBetweenDatesByUnitPosition.stream().collect(Collectors.toMap(key -> key.getUnitPositionId(), value -> value.getShifts()));
        for (Long currentStaffId : copyShiftDTO.getStaffIds()) {
            StaffUnitPositionDetails staffUnitPosition = staffDataList.parallelStream().filter(unitPosition -> unitPosition.getStaff().getId().equals(currentStaffId)).findFirst().get();

            List<WTAQueryResultDTO> wtaQueryResultDTOS = WTAMapByUnitPositionId.get(staffUnitPosition.getId());
            List<Shift> currentStaffPreviousShifts = unitPositionWiseShifts.get(staffUnitPosition.getId());

            Map<String, List<ShiftResponse>> response = copyForThisStaff(shifts, staffUnitPosition, activityMap, copyShiftDTO, breakActivitiesMap, dataWrapper, breakSettings, wtaQueryResultDTOS, planningPeriodMap, activityConfigurations, currentStaffPreviousShifts);
            StaffWiseShiftResponse successfullyCopied = new StaffWiseShiftResponse(staffUnitPosition.getStaff(), response.get("success"));
            StaffWiseShiftResponse errorInCopy = new StaffWiseShiftResponse(staffUnitPosition.getStaff(), response.get("error"));
            unCopiedShiftCount += response.get("error").size();
            copyShiftResponse.getSuccessFul().add(successfullyCopied);
            copyShiftResponse.getFailure().add(errorInCopy);
        }
        copyShiftResponse.setUnCopiedShiftCount(unCopiedShiftCount);
        return copyShiftResponse;
    }

    private Map<String, List<ShiftResponse>> copyForThisStaff(List<ShiftResponseDTO> shifts, StaffUnitPositionDetails staffUnitPosition, Map<BigInteger, ActivityWrapper> activityMap, CopyShiftDTO copyShiftDTO, Map<BigInteger, ActivityWrapper> breakActivitiesMap, StaffUnitPositionUnitDataWrapper dataWrapper, List<BreakSettings> breakSettings, List<WTAQueryResultDTO> wtaQueryResultDTOS, Map<DateTimeInterval, PlanningPeriodDTO> planningPeriodMap, List<ActivityConfiguration> activityConfigurations, List<Shift> currentStaffPreviousShifts) {
        List<Shift> newShifts = new ArrayList<>(shifts.size());
        Map<String, List<ShiftResponse>> statusMap = new HashMap<>();
        List<ShiftWithActivityDTO> newCreatedShiftWithActivityDTOs= new ArrayList<>();
        List<ShiftResponse> successfullyCopiedShifts = new ArrayList<>();
        List<ShiftResponse> errorInCopyingShifts = new ArrayList<>();
        int counter = 0;
        LocalDate shiftCreationStartDate = copyShiftDTO.getStartDate();
        LocalDate shiftCreationLastDate = copyShiftDTO.getEndDate();

        ShiftResponse shiftResponse;
        while (shiftCreationLastDate.isAfter(shiftCreationStartDate) || shiftCreationLastDate.equals(shiftCreationStartDate)) {
            ShiftResponseDTO shiftResponseDTO = shifts.get(counter);
            List<String> validationMessages = new ArrayList<>();
            for (Shift sourceShift : shiftResponseDTO.getShifts()) {
                ShiftWithActivityDTO shiftWithActivityDTO = convertIntoShiftWithActivity(sourceShift,activityMap);

                PlanningPeriodDTO planningPeriod = getCurrentPlanningPeriod(planningPeriodMap, shiftCreationStartDate);
                Date startDate = DateUtils.getDateByLocalDateAndLocalTime(shiftCreationStartDate, DateUtils.asLocalTime(sourceShift.getStartDate()));
                Date endDate = DateUtils.getDateByLocalDateAndLocalTime(shiftCreationStartDate, DateUtils.asLocalTime(sourceShift.getEndDate()));
                shiftWithActivityDTO.setEndDate(endDate);
                shiftWithActivityDTO.setStartDate(startDate);
                String shiftExistsMessage = validateShiftExistanceBetweenDuration(shiftCreationStartDate, sourceShift, currentStaffPreviousShifts);
                if (shiftExistsMessage != null) {
                    validationMessages.add(shiftExistsMessage);
                }

                validationMessages.addAll(shiftValidatorService.validateShiftWhileCopy(dataWrapper, shiftWithActivityDTO, staffUnitPosition, wtaQueryResultDTOS, planningPeriod, activityMap,newCreatedShiftWithActivityDTOs));
                shiftResponse = addShift(validationMessages, sourceShift, staffUnitPosition, startDate, endDate, newShifts, breakActivitiesMap, activityMap, dataWrapper, breakSettings, activityConfigurations, planningPeriod);
                if (shiftResponse.isSuccess()) {
                    successfullyCopiedShifts.add(shiftResponse);
                    newCreatedShiftWithActivityDTOs.add(convertIntoShiftWithActivity(newShifts.get(counter),activityMap));
                } else {
                    errorInCopyingShifts.add(shiftResponse);
                }
            }
            shiftCreationStartDate = shiftCreationStartDate.plusDays(1);
            if (counter++ == shifts.size() - 1) {
                counter = 0;
            }

        }
        statusMap.put("success", successfullyCopiedShifts);
        statusMap.put("error", errorInCopyingShifts);
        if (!newShifts.isEmpty()) {
            save(newShifts);
        }
        return statusMap;
    }
    private ShiftWithActivityDTO convertIntoShiftWithActivity(Shift sourceShift, Map<BigInteger, ActivityWrapper> activityMap){
        ShiftWithActivityDTO shiftWithActivityDTO = ObjectMapperUtils.copyPropertiesByMapper(sourceShift, ShiftWithActivityDTO.class);
        shiftWithActivityDTO.getActivities().forEach(s -> {
            ActivityDTO activityDTO = ObjectMapperUtils.copyPropertiesByMapper(activityMap.get(s.getActivityId()).getActivity(), ActivityDTO.class);
            s.setActivity(activityDTO);
        });
        return shiftWithActivityDTO;
    }

    private ShiftResponse addShift(List<String> responseMessages, Shift sourceShift, StaffUnitPositionDetails staffUnitPosition, Date startDate, Date endDate, List<Shift> newShifts, Map<BigInteger, ActivityWrapper> breakActivitiesMap, Map<BigInteger, ActivityWrapper> activityMap, StaffUnitPositionUnitDataWrapper dataWrapper, List<BreakSettings> breakSettings, List<ActivityConfiguration> activityConfigurations, PlanningPeriodDTO planningPeriod) {
        if (responseMessages.isEmpty()) {
         Shift  copiedShift = new Shift(startDate, endDate,
                    sourceShift.getRemarks(), sourceShift.getActivities(), staffUnitPosition.getStaff().getId(), sourceShift.getUnitId(),
                    sourceShift.getScheduledMinutes(), sourceShift.getDurationMinutes(), sourceShift.getExternalId(), staffUnitPosition.getId(), sourceShift.getParentOpenShiftId(),  sourceShift.getId()
                    , planningPeriod.getCurrentPhaseId(), planningPeriod.getId(),staffUnitPosition.getUserId());

            List<ShiftActivity> shiftActivities = shiftBreakService.addBreakInShiftsWhileCopy(activityMap, copiedShift, null, dataWrapper.getTimeSlotWrappers(), breakSettings);
            copiedShift.setActivities(shiftActivities);
            setScheduleMinuteAndHours(copiedShift, activityMap, dataWrapper, staffUnitPosition, planningPeriod, activityConfigurations);
            newShifts.add(copiedShift);

            return new ShiftResponse(sourceShift.getId(), sourceShift.getActivities().get(0).getActivityName(), Collections.singletonList(NO_CONFLICTS), true, DateUtils.asLocalDate(startDate));

        } else {
            List<String> errors = new ArrayList<>();
            responseMessages.forEach(responseMessage -> {
                try {
                    errors.add(localeService.getMessage(responseMessage));
                } catch (Exception e) {
                    // if its not appropriate to convert then we will add this simply
                    errors.add(responseMessage);
                }
            });
            return new ShiftResponse(sourceShift.getId(), sourceShift.getActivities().get(0).getActivityName(), errors, false, DateUtils.asLocalDate(startDate));
        }
    }

    private Map<Long, List<WTAQueryResultDTO>> findAllWTAGroupByUnitPositionId(List<Long> unitPositionIds, CopyShiftDTO copyShiftDTO) {
        List<WTAQueryResultDTO> wtaQueryResults = workingTimeAgreementMongoRepository.getWTAByUnitPositionIdsAndDates(unitPositionIds, DateUtils.asDate(copyShiftDTO.getStartDate()), DateUtils.asDate(copyShiftDTO.getEndDate()));
        return wtaQueryResults.stream().collect(groupingBy(WTAQueryResultDTO::getUnitPositionId));

    }

    private void findAndAddCTAInUnitPositions(List<StaffUnitPositionDetails> staffDataList, CopyShiftDTO copyShiftDTO, StaffUnitPositionUnitDataWrapper dataWrapper, List<Long> unitPositionIds) {
        List<CTAResponseDTO> ctaResponseDTOS = costTimeAgreementRepository.getCTAByUnitPositionIdsAndDate(unitPositionIds, DateUtils.asDate(copyShiftDTO.getStartDate()), DateUtils.asDate(copyShiftDTO.getEndDate()));
        Map<Long, List<CTAResponseDTO>> CTAResponseMapByUnitPositionIds = ctaResponseDTOS.stream().collect(groupingBy(CTAResponseDTO::getUnitPositionId));
        staffDataList.stream().forEach(staffAdditionalInfoDTO -> {
            if (CTAResponseMapByUnitPositionIds.get(staffAdditionalInfoDTO.getId()) != null) {
                List<CTAResponseDTO> ctaResponseDTOSList = CTAResponseMapByUnitPositionIds.get(staffAdditionalInfoDTO.getId());
                List<CTARuleTemplateDTO> ctaRuleTemplateDTOS = ctaResponseDTOSList.stream().flatMap(ctaResponseDTO -> ctaResponseDTO.getRuleTemplates().stream()).collect(Collectors.toList());
                staffAdditionalInfoDTO.setCtaRuleTemplates(ctaRuleTemplateDTOS);
                ShiftCalculationService.setDayTypeToCTARuleTemplate(staffAdditionalInfoDTO, dataWrapper.getDayTypes(), dataWrapper.getPublicHoliday());
            }
        });
    }

    private void setScheduleMinuteAndHours(Shift shift, Map<BigInteger, ActivityWrapper> activityWrapperMap, StaffUnitPositionUnitDataWrapper dataWrapper, StaffUnitPositionDetails staffUnitPosition,
                                           PlanningPeriodDTO planningPeriod, List<ActivityConfiguration> activityConfigurations) {
        int scheduledMinutes = 0, durationMinutes = 0;
        for (ShiftActivity shiftActivity : shift.getActivities()) {
            if (shiftActivity.getId() == null) {
                shiftActivity.setId(mongoSequenceRepository.nextSequence(ShiftActivity.class.getSimpleName()));
            }
            ActivityWrapper currentActivityWrapper = activityWrapperMap.get(shiftActivity.getActivityId());
            if (CollectionUtils.isNotEmpty(dataWrapper.getDayTypes())) {
                Map<Long, DayTypeDTO> dayTypeDTOMap = dataWrapper.getDayTypes().stream().collect(Collectors.toMap(k -> k.getId(), v -> v));
                Set<DayOfWeek> activityDayTypes = getValidDays(dayTypeDTOMap, currentActivityWrapper.getActivity().getTimeCalculationActivityTab().getDayTypes());
                if (activityDayTypes.contains(DateUtils.asLocalDate(shiftActivity.getStartDate()).getDayOfWeek())) {
                    timeBankCalculationService.calculateScheduleAndDurationHour(shiftActivity, currentActivityWrapper.getActivity(), staffUnitPosition);
                    scheduledMinutes += shiftActivity.getScheduledMinutes();
                    durationMinutes += shiftActivity.getDurationMinutes();
                }
            }
            shiftActivity.setBackgroundColor(currentActivityWrapper.getActivity().getGeneralActivityTab().getBackgroundColor());
            shiftActivity.setActivityName(currentActivityWrapper.getActivity().getName());
            shiftActivity.setTimeType(currentActivityWrapper.getTimeType());
            shiftActivity.setPlannedTimeId(addPlannedTimeInShift(planningPeriod.getCurrentPhaseId(), currentActivityWrapper.getActivity(), staffUnitPosition, dataWrapper, activityConfigurations));
        }
        shift.setScheduledMinutes(scheduledMinutes);
        shift.setDurationMinutes(durationMinutes);
    }

    private BigInteger addPlannedTimeInShift(BigInteger phaseId, Activity activity, StaffUnitPositionDetails staffAdditionalInfoDTO, StaffUnitPositionUnitDataWrapper dataWrapper, List<ActivityConfiguration> activityConfigurations) {
        /**
         * This is used for checking the activity is for presence type
         **/
        Boolean managementPerson = Optional.ofNullable(dataWrapper.getUser()).isPresent() && dataWrapper.getUser().getManagement();

        return (activity.getTimeCalculationActivityTab().getMethodForCalculatingTime().equals(FULL_DAY_CALCULATION)
                || activity.getTimeCalculationActivityTab().getMethodForCalculatingTime().equals(FULL_WEEK))
                ? getAbsencePlannedTime(phaseId, staffAdditionalInfoDTO, activityConfigurations)
                : getPresencePlannedTime(phaseId, managementPerson, staffAdditionalInfoDTO, activityConfigurations);
    }

    private BigInteger getAbsencePlannedTime(BigInteger phaseId, StaffUnitPositionDetails staffAdditionalInfoDTO, List<ActivityConfiguration> activityConfigurations) {
        BigInteger plannedTimeId = null;
        for (ActivityConfiguration activityConfiguration : activityConfigurations) {
            if (activityConfiguration.getAbsencePlannedTime() != null && activityConfiguration.getAbsencePlannedTime().getPhaseId().equals(phaseId)) {
                plannedTimeId = activityConfiguration.getAbsencePlannedTime().getPlannedTimeId();
                if (activityConfiguration.getAbsencePlannedTime().isException()) {
                    break; // if anyone is of exception type then we need to break and don't need to search in next entries
                }
            }
        }
        // checking weather this is allowed to staff or not
        if (Optional.ofNullable(staffAdditionalInfoDTO.getIncludedPlannedTime()).isPresent() && plannedTimeId.equals(staffAdditionalInfoDTO.getExcludedPlannedTime())) {
            plannedTimeId = staffAdditionalInfoDTO.getIncludedPlannedTime();
        }
        return plannedTimeId;
    }

    private BigInteger getPresencePlannedTime(BigInteger phaseId, Boolean managementPerson, StaffUnitPositionDetails staffAdditionalInfoDTO, List<ActivityConfiguration> activityConfigurations) {
        ActivityConfiguration appliedActivityConfiguration = null;
        for (ActivityConfiguration activityConfiguration : activityConfigurations) {
            if (activityConfiguration.getPresencePlannedTime() != null && activityConfiguration.getPresencePlannedTime().getPhaseId().equals(phaseId)) {
                appliedActivityConfiguration = activityConfiguration;
            }

        }
        return (managementPerson) ? getApplicablePlannedType(staffAdditionalInfoDTO, appliedActivityConfiguration.getPresencePlannedTime().getManagementPlannedTimeId())
                : getApplicablePlannedType(staffAdditionalInfoDTO, appliedActivityConfiguration.getPresencePlannedTime().getStaffPlannedTimeId());
    }

    private BigInteger getApplicablePlannedType(StaffUnitPositionDetails staffUnitPositionDetails, BigInteger plannedTypeId) {
        if (Optional.ofNullable(staffUnitPositionDetails.getIncludedPlannedTime()).isPresent()) {
            plannedTypeId = plannedTypeId.equals(staffUnitPositionDetails.getExcludedPlannedTime()) ? staffUnitPositionDetails.getIncludedPlannedTime() : plannedTypeId;
        }
        return plannedTypeId;

    }

    private String validateShiftExistanceBetweenDuration(LocalDate shiftCreationStartDate, Shift sourceShift, List<Shift> currentStaffPreviousShifts) {
        String response=null;
        if (currentStaffPreviousShifts != null) {
            Date startDateTime = DateUtils.getDateByLocalDateAndLocalTime(shiftCreationStartDate, DateUtils.asLocalTime(sourceShift.getStartDate()));
            Date endDateTime = DateUtils.getDateByLocalDateAndLocalTime(shiftCreationStartDate, DateUtils.asLocalTime(sourceShift.getEndDate()));
            DateTimeInterval shiftInterval = new DateTimeInterval(startDateTime, endDateTime);
            Optional<Shift> shift = currentStaffPreviousShifts.stream().filter(s -> shiftInterval.overlaps(new DateTimeInterval(s.getStartDate().getTime(), s.getEndDate().getTime()))).findFirst();
            if (shift.isPresent()) {
                response= ("message.shift.date.startandend");
            }
        }
        return response;
    }

    private PlanningPeriodDTO getCurrentPlanningPeriod(Map<DateTimeInterval, PlanningPeriodDTO> planningPeriodMap, LocalDate shiftCreationStartDate) {
        PlanningPeriodDTO planningPeriod = null;
        for (DateTimeInterval currentInterval : planningPeriodMap.keySet()) {
            if (currentInterval.contains(DateUtils.getLongFromLocalDate(shiftCreationStartDate))) {
                planningPeriod = planningPeriodMap.get(currentInterval);
                break;
            }
        }
        return planningPeriod;
    }

}
