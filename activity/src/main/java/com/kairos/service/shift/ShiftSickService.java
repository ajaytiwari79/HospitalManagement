package com.kairos.service.shift;

import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.constants.CommonConstants;
import com.kairos.dto.activity.cta.CTAResponseDTO;
import com.kairos.dto.activity.period.PeriodDTO;
import com.kairos.dto.activity.shift.*;
import com.kairos.dto.activity.staffing_level.Duration;
import com.kairos.dto.user.user.staff.StaffAdditionalInfoDTO;
import com.kairos.dto.user_context.UserContext;
import com.kairos.enums.EmploymentSubType;
import com.kairos.enums.TimeCalaculationType;
import com.kairos.enums.TimeTypeEnum;
import com.kairos.enums.shift.ShiftActionType;
import com.kairos.enums.shift.ShiftStatus;
import com.kairos.enums.shift.ShiftType;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.activity.ActivityWrapper;
import com.kairos.persistence.model.activity.tabs.TimeCalculationActivityTab;
import com.kairos.persistence.model.activity.tabs.rules_activity_tab.RulesActivityTab;
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
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.*;
import static com.kairos.commons.utils.ObjectUtils.*;
import static com.kairos.commons.utils.ObjectUtils.isCollectionEmpty;
import static com.kairos.constants.ActivityMessagesConstants.*;
import static com.kairos.constants.AppConstants.*;
import static com.kairos.enums.shift.ShiftType.SICK;
import static com.kairos.enums.sickness.ReplaceSickShift.*;
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
    @Inject
    private AbsenceShiftService absenceShiftService;


    public List<ShiftWithViolatedInfoDTO> createSicknessShiftsOfStaff(ShiftDTO shiftDTO,StaffAdditionalInfoDTO staffAdditionalInfoDTO,ActivityWrapper activityWrapper) {
        byte shiftNeedsToAddForDays = activityWrapper.getActivity().getRulesActivityTab().getRecurrenceDays();
        int i=0;
        Date startDate = asDate(shiftDTO.getShiftDate(), LocalTime.MIDNIGHT);
        Date endDate = asDate(shiftDTO.getShiftDate().plusDays(CommonConstants.FULL_WEEK.equals(activityWrapper.getActivity().getTimeCalculationActivityTab().getMethodForCalculatingTime()) ? 7 : 1), LocalTime.MIDNIGHT);
        List<ShiftDTO> shiftDTOS = new ArrayList<>();
        if(activityWrapper.getActivity().getRulesActivityTab().isAllowedAutoAbsence() && shiftNeedsToAddForDays!=0 && i < shiftNeedsToAddForDays){
            endDate = asDate(shiftDTO.getShiftDate().plusDays(CommonConstants.FULL_WEEK.equals(activityWrapper.getActivity().getTimeCalculationActivityTab().getMethodForCalculatingTime()) ? 7 * shiftNeedsToAddForDays : 1 * shiftNeedsToAddForDays), LocalTime.MIDNIGHT);
            while (shiftNeedsToAddForDays != 0 && i<shiftNeedsToAddForDays) {
                ShiftDTO shiftDTO1 = ObjectMapperUtils.copyPropertiesByMapper(shiftDTO,ShiftDTO.class);
                if(!(CommonConstants.FULL_WEEK.equals(activityWrapper.getActivity().getTimeCalculationActivityTab().getMethodForCalculatingTime()) || CommonConstants.FULL_DAY_CALCULATION.equals(activityWrapper.getActivity().getTimeCalculationActivityTab().getMethodForCalculatingTime()))){
                    ShiftActivityDTO shiftActivityDTO = shiftDTO1.getActivities().get(0);
                    shiftActivityDTO.setStartDate(plusDays(shiftActivityDTO.getStartDate(),i));
                    shiftActivityDTO.setEndDate(plusDays(shiftActivityDTO.getEndDate(),i));
                    shiftDTO1.setStartDate(plusDays(shiftDTO1.getStartDate(),i));
                    shiftDTO1.setEndDate(plusDays(shiftDTO1.getEndDate(),i));
                }
                shiftDTO1.setShiftDate(shiftDTO1.getShiftDate().plusDays(i));
                shiftDTOS.add(shiftDTO1);
                i++;
            }
        }else {
            shiftDTOS.add(shiftDTO);
        }
        updateShiftWithSetting(activityWrapper, startDate, endDate, shiftDTO);
        return shiftService.createShifts(shiftDTO.getUnitId(),shiftDTOS,ShiftActionType.SAVE);

    }

    private void updateShiftWithSetting(ActivityWrapper activityWrapper, Date startDate, Date endDate,ShiftDTO shiftDTO) {
        List<Shift> shifts = shiftMongoRepository.findAllShiftsByEmploymentIdBetweenDate(shiftDTO.getEmploymentId(),startDate,endDate);
        SicknessSetting sicknessSetting = activityWrapper.getActivity().getRulesActivityTab().getSicknessSetting();
        for (Shift shift : shifts) {
            if(shift.getShiftStatuses().contains(ShiftStatus.PUBLISH)){
                updateShiftOnTheBasisOfLayerSetting(sicknessSetting.isShowAslayerOnTopOfPublishedShift(), shift);
            }else {
                updateShiftOnTheBasisOfLayerSetting(sicknessSetting.isShowAslayerOnTopOfUnPublishedShift(), shift);
            }
        }
    }

    private void updateShiftOnTheBasisOfLayerSetting(boolean isLayerSetting, Shift shift) {
        if(isLayerSetting) {
            shift.setDisabled(true);
        }else {
            shift.setDeleted(true);
        }
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
                shift.setShiftType(SICK);
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
            shift.setFunctionId(dateLongMap.get(asLocalDate(shift.getStartDate())));
            shifts.add(shift);
        }
        //shifts.addAll(staffOriginalShiftsOfDates);
        if (!shifts.isEmpty()) {
            Set<LocalDateTime> dateTimes = shifts.stream().map(s -> DateUtils.asLocalDateTime(s.getActivities().get(0).getStartDate())).collect(Collectors.toSet());
            Map<Date, Phase> phaseListByDate = phaseService.getPhasesByDates(shifts.get(0).getUnitId(), dateTimes);
            //shiftMongoRepository.saveEntities(shifts);

            Map<BigInteger, ActivityWrapper> activityWrapperMap = shiftService.getActivityWrapperMap(shifts, null);
            for (Shift shift : shifts) {
                staffAdditionalInfoDTO = userIntegrationService.verifyUnitEmploymentOfStaff(asLocalDate(shift.getActivities().get(0).getStartDate()), shifts.get(0).getStaffId(), shifts.get(0).getEmploymentId(), Collections.emptySet());
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
    public void createSicknessShiftsOfStaff(Long staffId, Long unitId, Activity activity, StaffEmploymentDetails staffEmploymentDetails, List<Shift> staffOriginalShiftsOfDates, Duration duration, PlanningPeriod planningPeriod) {
        StaffAdditionalInfoDTO staffAdditionalInfoDTO = userIntegrationService.verifyUnitEmploymentOfStaff(null, staffId, staffEmploymentDetails.getId(), Collections.emptySet());
        List<String> errorMessages=new ArrayList<>();
        List<Activity> protectedDaysOffActivities = activityRepository.findAllBySecondLevelTimeTypeAndUnitIds(TimeTypeEnum.PROTECTED_DAYS_OFF, newHashSet(unitId));
        Map<LocalDate,List<Shift>> localDateListMap=staffOriginalShiftsOfDates.stream().collect(Collectors.groupingBy(o -> asLocalDate(o.getStartDate()),Collectors.toList()));
        short shiftNeedsToAddForDays = activity.getRulesActivityTab().getRecurrenceDays();
        List<Shift> shifts = new ArrayList<>();
        while (shiftNeedsToAddForDays != 0 && activity.getRulesActivityTab().getRecurrenceTimes() > 0) {
            shiftNeedsToAddForDays--;
            ShiftActivity shiftActivity = calculateShiftStartAndEndTime(shiftNeedsToAddForDays, activity.getTimeCalculationActivityTab(), staffEmploymentDetails, duration);
            shiftActivity.setActivityId(activity.getId());
            shiftActivity.setActivityName(activity.getName());
            ShiftType shiftType = TimeTypeEnum.ABSENCE.equals(activity.getBalanceSettingsActivityTab().getTimeType()) ? ShiftType.ABSENCE : ShiftType.PRESENCE;
            // ShiftType shiftType = ((FULL_WEEK.equals(activity.getTimeCalculationActivityTab().getMethodForCalculatingTime()) || FULL_DAY_CALCULATION.equals(activity.getTimeCalculationActivityTab().getMethodForCalculatingTime()))) ? ShiftType.ABSENCE : ShiftType.PRESENCE;
            Shift currentShift = new Shift(shiftActivity.getStartDate(), shiftActivity.getEndDate(), staffId, Arrays.asList(shiftActivity), staffEmploymentDetails.getId(), unitId, planningPeriod.getCurrentPhaseId(), planningPeriod.getId());
            if(localDateListMap.containsKey(asLocalDate(currentShift.getStartDate()))){
                List<Shift> shifts1=localDateListMap.get(asLocalDate(currentShift.getStartDate()));
                shiftService.validateSicknessShift(ObjectMapperUtils.copyPropertiesByMapper(shifts, ShiftWithActivityDTO.class),staffAdditionalInfoDTO,new ActivityWrapper(activity),errorMessages,shifts1,protectedDaysOffActivities);
                if(isCollectionEmpty(errorMessages)){
                    for (Shift shift : shifts1) {
                        shift.setDisabled(true);
                    }
                }

            }
            currentShift.setShiftType(shiftType);
            shifts.add(currentShift);
        }
        if(isCollectionNotEmpty(shifts)){
        absenceShiftService.saveShifts(protectedDaysOffActivities.get(0), staffAdditionalInfoDTO, ObjectMapperUtils.copyPropertiesOfCollectionByMapper(shifts,ShiftDTO.class), ShiftActionType.SAVE);

        }
        //addPreviousShiftAndSaveShift(staffOriginalShiftsOfDates, shifts, dates);
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

    public void disableSicknessShiftsOfStaff(Long staffId, Long unitId,Date startDate) {

        PlanningPeriod planningPeriod = planningPeriodMongoRepository.findCurrentDatePlanningPeriod(unitId, DateUtils.getCurrentLocalDate(), DateUtils.getCurrentLocalDate());
        List<Activity> protectedDaysOffActivities = activityRepository.findAllBySecondLevelTimeTypeAndUnitIds(TimeTypeEnum.PROTECTED_DAYS_OFF, newHashSet(unitId));
        if(isCollectionEmpty(protectedDaysOffActivities)){
            exceptionService.dataNotFoundException(MESSAGE_PROTECTEDDAYSOFF_ACTIVITY_NOT_FOUND);
        }
        StaffAdditionalInfoDTO staffAdditionalInfoDTO = userIntegrationService.verifyUnitEmploymentOfStaff(null, staffId, staffEmploymentDetails.getId(), Collections.emptySet());
        if (!Optional.ofNullable(staffAdditionalInfoDTO).isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_STAFFEMPLOYMENT_NOTFOUND);
        }
        List<Shift> shifts = shiftMongoRepository.findAllDisabledOrSickShiftsByEmploymentIdAndUnitId(staffEmploymentDetails.getId(), unitId, asLocalDate(startDate),SICK);
        Map<LocalDate, List<Shift>> dateAndShiftMap = shifts.stream().filter(shift -> !shift.isSickShift()).collect(Collectors.groupingBy(k -> asLocalDate(k.getStartDate()), Collectors.toList()));
        Map<BigInteger, ActivityWrapper> activityWrapperMap = shiftService.getActivityWrapperMap(shifts,null);

    }


    private Shift updateShift(Shift shift, Map<BigInteger, ActivityWrapper> activityWrapperMap, Map<LocalDate, List<Shift>> dateAndShiftMap, List<Shift> oldShift, List<ShiftDTO> newShift, Activity protectedDaysOffActivity,PlanningPeriod planningPeriod,StaffEmploymentDetails staffEmploymentDetails) {
        if (shift.isSickShift()) {
            List<Shift> shifts = dateAndShiftMap.get(asLocalDate(shift.getStartDate()));
            for (Shift shift1 : shifts) {
                ActivityWrapper activityWrapper = activityWrapperMap.get(shift.getActivities().get(0).getActivityId());
                validateAndUpdateShift(activityWrapper, shift1,oldShift,newShift,protectedDaysOffActivity,planningPeriod,staffEmploymentDetails);
            }
        }
        return shift;
    }

    private void validateAndUpdateShift(ActivityWrapper activityWrapper, Shift shift, List<Shift> oldShift, List<ShiftDTO> newShifts , Activity protectedDaysOffActivity , PlanningPeriod planningPeriod,StaffEmploymentDetails staffEmploymentDetails) {
        ShiftDTO newShift=null;
        switch (activityWrapper.getActivity().getRulesActivityTab().getSicknessSetting().getReplaceSickShift()) {
            case PROTECTED_DAYS_OFF:
              ShiftActivityDTO shiftActivityDTOS=  new ShiftActivityDTO(protectedDaysOffActivity.getId(),protectedDaysOffActivity.getName(),newHashSet(ShiftStatus.APPROVE));
                shiftActivityDTOS.setStartDate(shift.getStartDate());
                shiftActivityDTOS.setEndDate(shift.getEndDate());
                newShift= new ShiftDTO(shift.getStartDate(), shift.getEndDate(), shift.getStaffId(), Arrays.asList(shiftActivityDTOS), staffEmploymentDetails.getId(), shift.getUnitId(), planningPeriod.getCurrentPhaseId(), planningPeriod.getId());
                newShift.setShiftType(ShiftType.NON_WORKING);
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
        if(isNotNull(newShift)){
            newShifts.add(newShift);
        }
        oldShift.add(shift);
    }
}









