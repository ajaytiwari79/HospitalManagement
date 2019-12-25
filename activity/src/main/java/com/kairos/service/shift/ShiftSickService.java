package com.kairos.service.shift;

import com.kairos.commons.utils.DateUtils;
import com.kairos.constants.CommonConstants;
import com.kairos.dto.activity.cta.CTAResponseDTO;
import com.kairos.dto.activity.period.PeriodDTO;
import com.kairos.dto.activity.shift.StaffEmploymentDetails;
import com.kairos.dto.activity.staffing_level.Duration;
import com.kairos.dto.user.user.staff.StaffAdditionalInfoDTO;
import com.kairos.dto.user_context.UserContext;
import com.kairos.enums.EmploymentSubType;
import com.kairos.enums.TimeCalaculationType;
import com.kairos.enums.TimeTypeEnum;
import com.kairos.enums.shift.ShiftStatus;
import com.kairos.enums.shift.ShiftType;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.activity.ActivityWrapper;
import com.kairos.persistence.model.activity.tabs.TimeCalculationActivityTab;
import com.kairos.persistence.model.activity.tabs.rules_activity_tab.SicknessSetting;
import com.kairos.persistence.model.attendence_setting.SickSettings;
import com.kairos.persistence.model.period.PlanningPeriod;
import com.kairos.persistence.model.phase.Phase;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.model.shift.ShiftActivity;
import com.kairos.persistence.model.wta.WTAQueryResultDTO;
import com.kairos.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.persistence.repository.attendence_setting.SickSettingsRepository;
import com.kairos.persistence.repository.cta.CostTimeAgreementRepository;
import com.kairos.persistence.repository.period.PlanningPeriodMongoRepository;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.persistence.repository.time_bank.TimeBankRepository;
import com.kairos.persistence.repository.wta.WorkingTimeAgreementMongoRepository;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.service.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.phase.PhaseService;
import com.kairos.service.time_bank.TimeBankService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.ObjectUtils.*;
import static com.kairos.commons.utils.ObjectUtils.isCollectionEmpty;
import static com.kairos.constants.ActivityMessagesConstants.*;
import static com.kairos.constants.AppConstants.*;
import static com.kairos.utils.worktimeagreement.RuletemplateUtils.setDayTypeToCTARuleTemplate;
import static java.time.temporal.ChronoUnit.MINUTES;

/**
 * CreatedBy vipulpandey on 31/8/18
 **/
@Service
public class ShiftSickService extends MongoBaseService {
    private static final Logger logger = LoggerFactory.getLogger(ShiftSickService.class);

    @Inject
    private ActivityMongoRepository activityRepository;
    @Inject
    private ExceptionService exceptionService;
    @Inject
    private CostTimeAgreementRepository costTimeAgreementRepository;
    @Inject
    private ShiftMongoRepository shiftMongoRepository;
    @Inject
    private PlanningPeriodMongoRepository planningPeriodMongoRepository;
    @Inject
    private SickSettingsRepository sickSettingsRepository;
    @Inject
    private ShiftService shiftService;
    @Inject
    private PhaseService phaseService;
    @Inject
    private UserIntegrationService userIntegrationService;
    @Inject
    private WorkingTimeAgreementMongoRepository workingTimeAgreementMongoRepository;
    @Inject
    private TimeBankRepository timeBankRepository;
    @Inject
    private TimeBankService timeBankService;
    @Inject
    private ShiftBreakService shiftBreakService;


    public Map<String, Long> createSicknessShiftsOfStaff(Long unitId, BigInteger activityId, Long staffId, Duration duration) {
        ActivityWrapper activityWrapper = activityRepository.findActivityAndTimeTypeByActivityId(activityId);
        Activity activity = activityWrapper.getActivity();
        if (!Optional.ofNullable(activity).isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_ACTIVITY_ID, activityId);
        }
        if (activity.getTimeCalculationActivityTab().getMethodForCalculatingTime().equals(ENTERED_MANUALLY)) {
            if (duration == null || duration.getFrom() == null || duration.getTo() == null) {
                exceptionService.actionNotPermittedException(ERROR_STARTEND_NOTBLANK);
            }
        }
        if (!activity.getRulesActivityTab().isAllowedAutoAbsence()) {
            exceptionService.actionNotPermittedException(ACTIVITY_NOTELIGIBLE_FOR_ABSENCE, activity.getName());
        }
        StaffEmploymentDetails staffEmploymentDetails = userIntegrationService.verifyUnitEmploymentOfStaff(staffId, unitId);
        if (!Optional.ofNullable(staffEmploymentDetails).isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_STAFFEMPLOYMENT_NOTFOUND);
        }
        CTAResponseDTO ctaResponseDTO = costTimeAgreementRepository.getCTAByEmploymentIdAndDate(staffEmploymentDetails.getId(), DateUtils.getDateFromLocalDate(null));
        if (!Optional.ofNullable(ctaResponseDTO).isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_CTA_NOTFOUND);
        }
        staffEmploymentDetails.setCtaRuleTemplates(ctaResponseDTO.getRuleTemplates());
        PlanningPeriod planningPeriod = planningPeriodMongoRepository.findCurrentDatePlanningPeriod(unitId, DateUtils.getCurrentLocalDate(), DateUtils.getCurrentLocalDate());
        if (!Optional.ofNullable(planningPeriod).isPresent()) {
            exceptionService.actionNotPermittedException(MESSAGE_PERIODSETTING_NOTFOUND);
        }
        logger.info("The current planning period is {}", planningPeriod.getName());
        List<Shift> staffOriginalShiftsOfDates = shiftMongoRepository.findAllShiftsByStaffIds(Collections.singletonList(staffId), DateUtils.getDateFromLocalDate(null), DateUtils.addDays(DateUtils.getDateFromLocalDate(null), activity.getRulesActivityTab().getRecurrenceDays()));
        //This method is used to fetch the shift of the days specified and marked them as disabled as the user is sick.
        createSicknessShiftsOfStaff(staffId, unitId, activity, staffEmploymentDetails, staffOriginalShiftsOfDates, duration, planningPeriod);
        SickSettings sickSettings = new SickSettings(staffId, unitId, UserContext.getUserDetails().getId(), activityId, DateUtils.getCurrentLocalDate(), staffEmploymentDetails.getId());
        save(sickSettings);
        Map<String, Long> response = new HashMap<>();
        response.put("unitId", unitId);
        response.put("staffId", staffId);
        return response;

    }

    public void createSicknessShiftsOfStaff(Activity activity, List<Shift> staffOriginalShiftsOfDates, Shift previousDaySickShift, List<PeriodDTO> periodDTOList) {
        short shiftNeedsToAddForDays = activity.getRulesActivityTab().getRecurrenceDays();
        logger.info(staffOriginalShiftsOfDates.size() + "", " shifts found for days");
        staffOriginalShiftsOfDates.forEach(s -> s.setDisabled(true));
        List<Shift> shifts = new ArrayList<>();
        while (shiftNeedsToAddForDays != 0 && activity.getRulesActivityTab().getRecurrenceTimes() > 0) {

            ShiftActivity shiftActivity = new ShiftActivity(activity.getId(), activity.getName());
            shiftActivity.setStartDate(DateUtils.getDateAfterDaysWithTime(shiftNeedsToAddForDays, DateUtils.asLocalTime(previousDaySickShift.getStartDate())));
            shiftActivity.setEndDate(DateUtils.getDateAfterDaysWithTime(shiftNeedsToAddForDays, DateUtils.asLocalTime(previousDaySickShift.getEndDate())));


            LocalDate shiftAdditionDate = DateUtils.getLocalDateAfterDays(shiftNeedsToAddForDays);
            PeriodDTO planningPeriodForSameDate = periodDTOList.stream().filter(periodDTO -> (
                    (periodDTO.getStartDate().isAfter(shiftAdditionDate) || periodDTO.getStartDate().isEqual(shiftAdditionDate))
                            && (periodDTO.getEndDate().isBefore(shiftAdditionDate) || periodDTO.getEndDate().isEqual(shiftAdditionDate)))).findAny().orElse(null);
            if (planningPeriodForSameDate != null) {
                Shift shift = new Shift(null, null, previousDaySickShift.getStaffId(), Arrays.asList(shiftActivity), previousDaySickShift.getEmploymentId(), previousDaySickShift.getUnitId(), planningPeriodForSameDate.getPhaseId(), planningPeriodForSameDate.getId());
                shift.setDurationMinutes(previousDaySickShift.getDurationMinutes());
                shifts.add(shift);
            }
            shiftNeedsToAddForDays--;


        }
        addPreviousShiftAndSaveShift(staffOriginalShiftsOfDates, shifts, null);

    }

    private void addPreviousShiftAndSaveShift(List<Shift> staffOriginalShiftsOfDates, List<Shift> shifts, Set<LocalDate> dates) {
        Map<LocalDate, Long> dateLongMap = new HashMap<>();
        StaffAdditionalInfoDTO staffAdditionalInfoDTO = userIntegrationService.verifyUnitEmploymentOfStaff(null, shifts.get(0).getStaffId(), shifts.get(0).getEmploymentId(), Collections.emptySet());
        if (staffAdditionalInfoDTO.getEmployment().getAppliedFunctions() != null) {
            dateLongMap = userIntegrationService.removeFunctionFromEmploymentByDates(staffAdditionalInfoDTO.getUnitId(), staffAdditionalInfoDTO.getEmployment().getId(), dates);
        }
        for (Shift shift : staffOriginalShiftsOfDates) {
            shift.setFunctionId(dateLongMap.get(DateUtils.asLocalDate(shift.getStartDate())));
            shifts.add(shift);
        }
        //shifts.addAll(staffOriginalShiftsOfDates);
        if (!shifts.isEmpty()) {
            Set<LocalDateTime> dateTimes = shifts.stream().map(s -> DateUtils.asLocalDateTime(s.getActivities().get(0).getStartDate())).collect(Collectors.toSet());
            Map<Date, Phase> phaseListByDate = phaseService.getPhasesByDates(shifts.get(0).getUnitId(), dateTimes);
            //shiftMongoRepository.saveEntities(shifts);

            Map<BigInteger, ActivityWrapper> activityWrapperMap = shiftService.getActivityWrapperMap(shifts, null);
            for (Shift shift : shifts) {
                staffAdditionalInfoDTO = userIntegrationService.verifyUnitEmploymentOfStaff(DateUtils.asLocalDate(shift.getActivities().get(0).getStartDate()), shifts.get(0).getStaffId(), shifts.get(0).getEmploymentId(), Collections.emptySet());
                WTAQueryResultDTO wtaQueryResultDTO = workingTimeAgreementMongoRepository.getWTAByEmploymentIdAndDate(staffAdditionalInfoDTO.getEmployment().getId(), DateUtils.onlyDate(shift.getActivities().get(0).getStartDate()));
                CTAResponseDTO ctaResponseDTO = costTimeAgreementRepository.getCTAByEmploymentIdAndDate(staffAdditionalInfoDTO.getEmployment().getId(), shifts.get(0).getActivities().get(0).getStartDate());
                if (!Optional.ofNullable(ctaResponseDTO).isPresent()) {
                    exceptionService.invalidRequestException("error.cta.notFound", shift.getActivities().get(0).getStartDate());
                }
                staffAdditionalInfoDTO.getEmployment().setCtaRuleTemplates(ctaResponseDTO.getRuleTemplates());
                setDayTypeToCTARuleTemplate(staffAdditionalInfoDTO);
                if (!TIME_AND_ATTENDANCE.equals(phaseListByDate.get(shift.getStartDate()).getName()) || isCollectionNotEmpty(shift.getBreakActivities())) {
                    List<ShiftActivity> breakActivities = shiftBreakService.updateBreakInShift(false, shift, activityWrapperMap, staffAdditionalInfoDTO, wtaQueryResultDTO.getBreakRule(), staffAdditionalInfoDTO.getTimeSlotSets(), shift);
                    shift.setBreakActivities(breakActivities);
                }
                shiftService.saveShiftWithActivity(activityWrapperMap, shift, staffAdditionalInfoDTO, false, null, phaseListByDate.get(shift.getStartDate()), null);
            }

            shiftService.saveShiftWithActivity(phaseListByDate, shifts, staffAdditionalInfoDTO);

        }
    }

    private void calculateShiftStartAndEndTime(Shift currentShift, Shift previousDayShift, short shiftNeedsToAddForDays, List<PeriodDTO> periodDTOList) {
        currentShift.setStartDate(DateUtils.getDateAfterDaysWithTime(shiftNeedsToAddForDays, DateUtils.asLocalTime(previousDayShift.getStartDate())));
        currentShift.setEndDate(DateUtils.getDateAfterDaysWithTime(shiftNeedsToAddForDays, DateUtils.asLocalTime(previousDayShift.getEndDate())));
        currentShift.setScheduledMinutes(previousDayShift.getScheduledMinutes());
        currentShift.setDurationMinutes(previousDayShift.getDurationMinutes());
        LocalDate shiftAdditionDate = DateUtils.getLocalDateAfterDays(shiftNeedsToAddForDays);
        PeriodDTO planningPeriodForSameDate = periodDTOList.stream().filter(periodDTO -> (
                (periodDTO.getStartDate().isAfter(shiftAdditionDate) || periodDTO.getStartDate().isEqual(shiftAdditionDate))
                        && (periodDTO.getEndDate().isBefore(shiftAdditionDate) || periodDTO.getEndDate().isEqual(shiftAdditionDate)))).findAny().orElse(null);
        if (planningPeriodForSameDate != null) {
            currentShift.setPlanningPeriodId(planningPeriodForSameDate.getId());
            currentShift.setPhaseId(planningPeriodForSameDate.getPhaseId());
        }
    }

    //TODO Refactor db queries
    private void createSicknessShiftsOfStaff(Long staffId, Long unitId, Activity activity, StaffEmploymentDetails staffEmploymentDetails, List<Shift> staffOriginalShiftsOfDates, Duration duration, PlanningPeriod planningPeriod) {
        short shiftNeedsToAddForDays = activity.getRulesActivityTab().getRecurrenceDays();
        logger.info(staffOriginalShiftsOfDates.size() + "", " shifts found for days");
        Set<LocalDate> dates = new HashSet<>();
        staffOriginalShiftsOfDates.forEach(s -> {
                    s.setDisabled(true);
                    dates.add(DateUtils.asLocalDate(s.getStartDate()));
                }
        );

        List<Shift> shifts = new ArrayList<>();
        while (shiftNeedsToAddForDays != 0 && activity.getRulesActivityTab().getRecurrenceTimes() > 0) {
            shiftNeedsToAddForDays--;
            ShiftActivity shiftActivity = calculateShiftStartAndEndTime(shiftNeedsToAddForDays, activity.getTimeCalculationActivityTab(), staffEmploymentDetails, duration);
            shiftActivity.setActivityId(activity.getId());
            shiftActivity.setActivityName(activity.getName());
            ShiftType shiftType = TimeTypeEnum.ABSENCE.equals(activity.getBalanceSettingsActivityTab().getTimeType()) ? ShiftType.ABSENCE : ShiftType.PRESENCE;
            // ShiftType shiftType = ((FULL_WEEK.equals(activity.getTimeCalculationActivityTab().getMethodForCalculatingTime()) || FULL_DAY_CALCULATION.equals(activity.getTimeCalculationActivityTab().getMethodForCalculatingTime()))) ? ShiftType.ABSENCE : ShiftType.PRESENCE;
            Shift currentShift = new Shift(shiftActivity.getStartDate(), shiftActivity.getEndDate(), staffId, Arrays.asList(shiftActivity), staffEmploymentDetails.getId(), unitId, planningPeriod.getCurrentPhaseId(), planningPeriod.getId());
            currentShift.setShiftType(shiftType);
            shifts.add(currentShift);
        }
        addPreviousShiftAndSaveShift(staffOriginalShiftsOfDates, shifts, dates);
    }

    private ShiftActivity calculateShiftStartAndEndTime(short shiftNeedsToAddForDays, TimeCalculationActivityTab timeCalculationActivityTab, StaffEmploymentDetails employment, Duration duration) {
        int scheduledMinutes = 0;
        int weeklyMinutes;
        int shiftDurationInMinute = 0;
        ShiftActivity shiftActivity = new ShiftActivity();
        switch (timeCalculationActivityTab.getMethodForCalculatingTime()) {
            case ENTERED_MANUALLY:
                shiftDurationInMinute = (int) MINUTES.between(duration.getFrom(), duration.getTo());
                scheduledMinutes = new Double(shiftDurationInMinute * timeCalculationActivityTab.getMultiplyWithValue()).intValue();
                shiftActivity.setDurationMinutes(shiftDurationInMinute);
                shiftActivity.setScheduledMinutes(scheduledMinutes);
                break;
            case CommonConstants.FULL_DAY_CALCULATION:
            case CommonConstants.FULL_WEEK:
                weeklyMinutes = (TimeCalaculationType.FULL_TIME_WEEKLY_HOURS_TYPE.equals(timeCalculationActivityTab.getFullDayCalculationType()))
                        ? employment.getFullTimeWeeklyMinutes() : employment.getTotalWeeklyMinutes();
                int shiftEndHour = weeklyMinutes / employment.getWorkingDaysInWeek();
                duration.setFrom(timeCalculationActivityTab.getDefaultStartTime());
                duration.setTo(duration.getFrom().plusMinutes(shiftEndHour));
                shiftDurationInMinute = (int) MINUTES.between(duration.getFrom(), duration.getTo());
                scheduledMinutes = new Double(shiftDurationInMinute * timeCalculationActivityTab.getMultiplyWithValue()).intValue();
                break;
            default:
                exceptionService.illegalArgumentException(ERROR_ACTIVITY_TIMECALCULATION_INVALIDARGUMENT);
        }
        shiftActivity.setDurationMinutes(shiftDurationInMinute);
        shiftActivity.setScheduledMinutes(scheduledMinutes);
        shiftActivity.setStartDate(DateUtils.getDateAfterDaysWithTime(shiftNeedsToAddForDays, duration.getFrom()));
        shiftActivity.setEndDate(DateUtils.getDateAfterDaysWithTime(shiftNeedsToAddForDays, duration.getTo()));
        return shiftActivity;
    }

    //TODO Refactor db queries
    public void disableSicknessShiftsOfStaff(Long staffId, Long unitId) {
        List<Shift> oldShift = new ArrayList<>();
        List<Shift> newShift = new ArrayList<>();
        StaffEmploymentDetails staffEmploymentDetails = userIntegrationService.verifyUnitEmploymentOfStaff(staffId, unitId);
        if (!Optional.ofNullable(staffEmploymentDetails).isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_STAFFEMPLOYMENT_NOTFOUND);
        }
        List<Shift> shifts = shiftMongoRepository.findAllDisabledOrSickShiftsByEmploymentIdAndUnitId(staffEmploymentDetails.getId(), unitId, DateUtils.getCurrentLocalDate());
        Map<LocalDate, List<Shift>> dateAndShiftMap = shifts.stream().filter(shift -> !shift.isSickShift()).collect(Collectors.groupingBy(k -> DateUtils.asLocalDate(k.getStartDate()), Collectors.toList()));
        List<BigInteger> activityIds = shifts.stream().flatMap(s -> s.getActivities().stream().map(a -> a.getActivityId())).collect(Collectors.toList());
        List<ActivityWrapper> activities = activityRepository.findActivitiesAndTimeTypeByActivityId(activityIds);
        Map<BigInteger, ActivityWrapper> activityWrapperMap = activities.stream().collect(Collectors.toMap(k -> k.getActivity().getId(), v -> v));
        for (Shift shift : shifts) {
          updateShift(shift,activityWrapperMap,dateAndShiftMap,oldShift,newShift);
        }
        if(isCollectionNotEmpty(newShift)){

        }
        shiftMongoRepository.saveEntities(oldShift);
        if (CollectionUtils.isNotEmpty(shifts)) {
            StaffAdditionalInfoDTO staffAdditionalInfoDTO = userIntegrationService.verifyUnitEmploymentOfStaff(null, shifts.get(0).getStaffId(), shifts.get(0).getEmploymentId(), Collections.emptySet());
            timeBankService.updateDailyTimeBankEntries(shifts, staffEmploymentDetails, staffAdditionalInfoDTO.getDayTypes());
        }
    }


    private Shift updateShift(Shift shift, Map<BigInteger, ActivityWrapper> activityWrapperMap, Map<LocalDate, List<Shift>> dateAndShiftMap, List<Shift> oldShift, List<Shift> newShift) {
        if (shift.isSickShift()) {
            List<Shift> shifts = dateAndShiftMap.get(DateUtils.asLocalDate(shift.getStartDate()));
            for (Shift shift1 : shifts) {
                ActivityWrapper activityWrapper = activityWrapperMap.get(shift.getActivities().get(0).getActivityId());
                updateShift(activityWrapper, shift1,oldShift,newShift);
            }
        }
        return shift;
    }

    private void updateShift(ActivityWrapper activityWrapper, Shift shift, List<Shift> oldShift, List<Shift> newShifts) {
        Shift newShift=null;
        switch (activityWrapper.getActivity().getRulesActivityTab().getSicknessSetting().getReplaceSkillActivityEnum()) {
            case PROTECTED_DAYS_OFF:
                break;
            case FREE_DAY:
                shift.setDeleted(true);
                break;
            case PUBLISHED_ACTIVITY:
                if (shift.getActivities().stream().anyMatch(shiftActivity -> shiftActivity.getStatus().contains(ShiftStatus.PUBLISH))) {
                    shift.setDisabled(false);
                } else {
                    shift.setDeleted(true);
                }
                break;
            case UNPUBLISHED_ACTIVITY:
                if (shift.getActivities().stream().anyMatch(shiftActivity -> !shiftActivity.getStatus().contains(ShiftStatus.PUBLISH))) {
                    shift.setDisabled(false);
                } else {
                    shift.setDeleted(true);
                }
                break;
            default:
                break;
        }
        oldShift.add(shift);
    }
}









