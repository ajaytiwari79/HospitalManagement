package com.kairos.service.shift;

import com.kairos.commons.utils.DateUtils;
import com.kairos.dto.activity.cta.CTAResponseDTO;
import com.kairos.dto.activity.period.PeriodDTO;
import com.kairos.dto.activity.shift.ShiftActivity;
import com.kairos.dto.activity.shift.StaffUnitPositionDetails;
import com.kairos.dto.activity.staffing_level.Duration;
import com.kairos.dto.user.user.staff.StaffAdditionalInfoDTO;
import com.kairos.enums.TimeCalaculationType;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.activity.ActivityWrapper;
import com.kairos.persistence.model.activity.tabs.TimeCalculationActivityTab;
import com.kairos.persistence.model.attendence_setting.SickSettings;
import com.kairos.persistence.model.period.PlanningPeriod;
import com.kairos.persistence.model.phase.Phase;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.persistence.repository.attendence_setting.SickSettingsRepository;
import com.kairos.persistence.repository.cta.CostTimeAgreementRepository;
import com.kairos.persistence.repository.period.PlanningPeriodMongoRepository;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.rest_client.GenericIntegrationService;
import com.kairos.rest_client.StaffRestClient;
import com.kairos.service.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.phase.PhaseService;
import com.kairos.utils.user_context.UserContext;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.constants.AppConstants.*;
import static com.kairos.persistence.model.constants.RelationshipConstants.ORGANIZATION;
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
    private StaffRestClient staffRestClient;
    @Inject
    private CostTimeAgreementRepository costTimeAgreementRepository;
    @Inject
    private ShiftMongoRepository shiftMongoRepository;
    @Inject
    private
    PlanningPeriodMongoRepository planningPeriodMongoRepository;
    @Inject
    private SickSettingsRepository sickSettingsRepository;
    @Inject
    private ShiftService shiftService;
    @Inject
    private PhaseService phaseService;
    @Inject
    private GenericIntegrationService genericIntegrationService;


    public Map<String, Long> createSicknessShiftsOfStaff(Long unitId, BigInteger activityId, Long staffId, Duration duration) {
        ActivityWrapper activityWrapper = activityRepository.findActivityAndTimeTypeByActivityId(activityId);
        Activity activity = activityWrapper.getActivity();
        if (!Optional.ofNullable(activity).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.activity.id", activityId);
        }
        if (activity.getTimeCalculationActivityTab().getMethodForCalculatingTime().equals(ENTERED_MANUALLY)) {
            if (duration == null || duration.getFrom() == null || duration.getTo() == null) {
                exceptionService.actionNotPermittedException("error.startEnd.notBlank");
            }
        }
        if (!activity.getRulesActivityTab().isAllowedAutoAbsence()) {
            exceptionService.actionNotPermittedException("activity.notEligible.for.absence", activity.getName());
        }
        StaffUnitPositionDetails staffUnitPositionDetails = genericIntegrationService.verifyUnitEmploymentOfStaff(staffId, unitId, ORGANIZATION);
        if (!Optional.ofNullable(staffUnitPositionDetails).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.staffUnitPosition.notFound");
        }
        CTAResponseDTO ctaResponseDTO = costTimeAgreementRepository.getCTAByUnitPositionIdAndDate(staffUnitPositionDetails.getId(), DateUtils.getDateFromLocalDate(null));
        if (!Optional.ofNullable(ctaResponseDTO).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.cta.notFound");
        }
        staffUnitPositionDetails.setCtaRuleTemplates(ctaResponseDTO.getRuleTemplates());
        PlanningPeriod planningPeriod = planningPeriodMongoRepository.findCurrentDatePlanningPeriod(unitId, DateUtils.getCurrentLocalDate(), DateUtils.getCurrentLocalDate());
        if (!Optional.ofNullable(planningPeriod).isPresent()) {
            exceptionService.actionNotPermittedException("message.periodsetting.notFound");
        }
        logger.info("The current planning period is {}", planningPeriod.getName());
        List<Shift> staffOriginalShiftsOfDates = shiftMongoRepository.findAllShiftsByStaffIds(Collections.singletonList(staffId), DateUtils.getDateFromLocalDate(null), DateUtils.addDays(DateUtils.getDateFromLocalDate(null), activity.getRulesActivityTab().getRecurrenceDays()));
        //This method is used to fetch the shift of the days specified and marked them as disabled as the user is sick.
        createSicknessShiftsOfStaff(staffId, unitId, activity, staffUnitPositionDetails, staffOriginalShiftsOfDates, duration, planningPeriod);
        SickSettings sickSettings = new SickSettings(staffId, unitId, UserContext.getUserDetails().getId(), activityId, DateUtils.getCurrentLocalDate(), staffUnitPositionDetails.getId());
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
                Shift shift = new Shift(null, null, previousDaySickShift.getStaffId(), Arrays.asList(shiftActivity), previousDaySickShift.getUnitPositionId(), previousDaySickShift.getUnitId(), planningPeriodForSameDate.getPhaseId(), planningPeriodForSameDate.getId());
                shift.setDurationMinutes(previousDaySickShift.getDurationMinutes());
                shifts.add(shift);
            }
            shiftNeedsToAddForDays--;


        }
        addPreviousShiftAndSaveShift(staffOriginalShiftsOfDates, shifts, null);

    }

    private void addPreviousShiftAndSaveShift(List<Shift> staffOriginalShiftsOfDates, List<Shift> shifts, Set<LocalDate> dates) {
        Map<LocalDate, Long> dateLongMap = new HashMap<>();
        StaffAdditionalInfoDTO staffAdditionalInfoDTO = genericIntegrationService.verifyUnitEmploymentOfStaff(null, shifts.get(0).getStaffId(), ORGANIZATION, shifts.get(0).getUnitPositionId());
        if (staffAdditionalInfoDTO.getUnitPosition().getAppliedFunctions() != null) {
            dateLongMap = genericIntegrationService.removeFunctionFromUnitPositionByDates(staffAdditionalInfoDTO.getUnitId(), staffAdditionalInfoDTO.getUnitPosition().getId(), dates);
        }
        for (Shift shift : staffOriginalShiftsOfDates) {
            shift.setFunctionId(dateLongMap.get(DateUtils.asLocalDate(shift.getStartDate())));
            shifts.add(shift);
        }
        //shifts.addAll(staffOriginalShiftsOfDates);
        if (!shifts.isEmpty()) {
            Set<LocalDateTime> dateTimes = shifts.stream().map(s -> DateUtils.asLocalDateTime(s.getActivities().get(0).getStartDate())).collect(Collectors.toSet());
            Map<LocalDate, Phase> phaseListByDate = phaseService.getPhasesByDates(shifts.get(0).getUnitId(), dateTimes);
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

    private void createSicknessShiftsOfStaff(Long staffId, Long unitId, Activity activity, StaffUnitPositionDetails staffUnitPositionDetails, List<Shift> staffOriginalShiftsOfDates, Duration duration, PlanningPeriod planningPeriod) {
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
            ShiftActivity shiftActivity = calculateShiftStartAndEndTime(shiftNeedsToAddForDays, activity.getTimeCalculationActivityTab(), staffUnitPositionDetails, duration);
            shiftActivity.setActivityId(activity.getId());
            shiftActivity.setActivityName(activity.getName());
            Shift currentShift = new Shift(null, null, staffId, Arrays.asList(shiftActivity), staffUnitPositionDetails.getId(), unitId, planningPeriod.getCurrentPhaseId(), planningPeriod.getId());
            shifts.add(currentShift);
        }
        addPreviousShiftAndSaveShift(staffOriginalShiftsOfDates, shifts, dates);
    }

    private ShiftActivity calculateShiftStartAndEndTime(short shiftNeedsToAddForDays, TimeCalculationActivityTab timeCalculationActivityTab, StaffUnitPositionDetails unitPosition, Duration duration) {
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
            case FULL_DAY_CALCULATION:
            case FULL_WEEK:
                weeklyMinutes = (TimeCalaculationType.FULL_TIME_WEEKLY_HOURS_TYPE.equals(timeCalculationActivityTab.getFullDayCalculationType()))
                        ? unitPosition.getFullTimeWeeklyMinutes() : unitPosition.getTotalWeeklyMinutes();
                int shiftEndHour = weeklyMinutes / unitPosition.getWorkingDaysInWeek();
                duration.setFrom(timeCalculationActivityTab.getDefaultStartTime());
                duration.setTo(duration.getFrom().plusMinutes(shiftEndHour));
                shiftDurationInMinute = (int) MINUTES.between(duration.getFrom(), duration.getTo());
                scheduledMinutes = new Double(shiftDurationInMinute * timeCalculationActivityTab.getMultiplyWithValue()).intValue();
                break;
            default:
                exceptionService.illegalArgumentException("error.activity.timeCalculation.InvalidArgument");
        }
        shiftActivity.setDurationMinutes(shiftDurationInMinute);
        shiftActivity.setScheduledMinutes(scheduledMinutes);
        shiftActivity.setStartDate(DateUtils.getDateAfterDaysWithTime(shiftNeedsToAddForDays, duration.getFrom()));
        shiftActivity.setEndDate(DateUtils.getDateAfterDaysWithTime(shiftNeedsToAddForDays, duration.getTo()));
        return shiftActivity;
    }

    public void disableSicknessShiftsOfStaff(Long staffId, Long unitId) {

        StaffUnitPositionDetails staffUnitPositionDetails = genericIntegrationService.verifyUnitEmploymentOfStaff(staffId, unitId, ORGANIZATION);
        if (!Optional.ofNullable(staffUnitPositionDetails).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.staffUnitPosition.notFound");
        }
        List<Shift> shifts = shiftMongoRepository.findAllDisabledOrSickShiftsByUnitPositionIdAndUnitId(staffUnitPositionDetails.getId(), unitId, DateUtils.getCurrentLocalDate());
        Map<Long, Set<LocalDate>> dateAndFunctionIdMap = new HashMap<>();
        for (Shift shift : shifts) {
            Set<LocalDate> dates;
            if (shift.isSickShift()) {
                shift.setDeleted(true);// delete the sick shift.
            } else if (shift.isDisabled()) {
                shift.setDisabled(false);
                if (shift.getFunctionId() != null) {
                    if (dateAndFunctionIdMap.keySet().contains(shift.getFunctionId())) {
                        dates = dateAndFunctionIdMap.get(shift.getFunctionId()) != null ? dateAndFunctionIdMap.get(shift.getFunctionId()) : new HashSet<>();
                        dates.add(DateUtils.asLocalDate(shift.getStartDate()));
                        dateAndFunctionIdMap.put(shift.getFunctionId(), dates);
                    } else {
                        dateAndFunctionIdMap.put(shift.getFunctionId(), new HashSet<>(Arrays.asList(DateUtils.asLocalDate(shift.getStartDate()))));
                    }


                }
            }
        }
        Boolean functionRestored = genericIntegrationService.restoreFunctionFromUnitPositionByDate(unitId, staffUnitPositionDetails.getId(), dateAndFunctionIdMap);
        if (functionRestored != null && !functionRestored) {
            logger.error("error occurred in User service while restoring functions");
        }
        if (CollectionUtils.isNotEmpty(shifts)) {
            StaffAdditionalInfoDTO staffAdditionalInfoDTO = genericIntegrationService.verifyUnitEmploymentOfStaff(null, shifts.get(0).getStaffId(), ORGANIZATION, shifts.get(0).getUnitPositionId());
            Set<LocalDateTime> dates = shifts.stream().map(s -> DateUtils.asLocalDateTime(s.getActivities().get(0).getStartDate())).collect(Collectors.toSet());
            Map<LocalDate, Phase> phaseListByDate = phaseService.getPhasesByDates(shifts.get(0).getUnitId(), dates);
            shiftService.saveShiftWithActivity(phaseListByDate, shifts, staffAdditionalInfoDTO);
        }
    }

}
