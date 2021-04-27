package com.kairos.service.shift;

import com.kairos.commons.custom_exception.DataNotFoundByIdException;
import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.TimeInterval;
import com.kairos.custom_exception.InvalidRequestException;
import com.kairos.dto.activity.shift.ShiftActivityDTO;
import com.kairos.dto.activity.shift.ShiftWithActivityDTO;
import com.kairos.dto.activity.wta.templates.BreakAvailabilitySettings;
import com.kairos.dto.user.country.time_slot.TimeSlotDTO;
import com.kairos.dto.user.user.staff.StaffAdditionalInfoDTO;
import com.kairos.enums.BreakAction;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.activity.ActivityWrapper;
import com.kairos.persistence.model.break_settings.BreakSettings;
import com.kairos.persistence.model.phase.Phase;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.model.shift.ShiftActivity;
import com.kairos.persistence.model.wta.templates.template_types.BreakWTATemplate;
import com.kairos.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.persistence.repository.break_settings.BreakSettingMongoRepository;
import com.kairos.persistence.repository.common.MongoSequenceRepository;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.service.counter.KPIBuilderCalculationService;
import com.kairos.service.counter.KPICalculationRelatedInfo;
import com.kairos.service.counter.KPIService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.phase.PhaseService;
import com.kairos.service.time_bank.TimeBankService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.CommonsExceptionUtil.convertMessage;
import static com.kairos.commons.utils.DateUtils.*;
import static com.kairos.commons.utils.ObjectUtils.*;
import static com.kairos.constants.ActivityMessagesConstants.*;
import static com.kairos.constants.AppConstants.*;
import static com.kairos.dto.activity.counter.enums.XAxisConfig.PERCENTAGE;
import static com.kairos.utils.counter.KPIUtils.getValueWithDecimalFormat;

/**
 * @author pradeep
 * @date - 18/9/18
 */
@Service
public class ShiftBreakService implements KPIService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ShiftBreakService.class);
    @Inject
    private BreakSettingMongoRepository breakSettingMongoRepository;
    @Inject
    private ActivityMongoRepository activityRepository;
    @Inject
    private ExceptionService exceptionService;
    @Inject
    private ShiftService shiftService;
    @Inject
    private MongoSequenceRepository mongoSequenceRepository;
    @Inject
    private ShiftMongoRepository shiftMongoRepository;
    @Inject
    private PhaseService phaseService;
    @Inject private KPIBuilderCalculationService kpiBuilderCalculationService;
    @Inject private TimeBankService timeBankService;


    public Map<BigInteger, ActivityWrapper> getBreakActivities(BreakSettings breakSetting, Long unitId) {
        List<ActivityWrapper> breakActivities = activityRepository.findActivitiesAndTimeTypeByParentIdsAndUnitId(newArrayList(breakSetting.getActivityId()), unitId);
        if (isCollectionEmpty(breakActivities) || breakActivities.size() > 1) {
            exceptionService.dataNotFoundException(ERROR_BREAKSACTIVITY_NOT_CONFIGURED, unitId);
        }
        Map<BigInteger, ActivityWrapper> activityWrapperMap =
                breakActivities.stream().collect(Collectors.toMap(key -> key.getActivity().getCountryParentId(), value -> value));  // This map is used for break
        activityWrapperMap.putAll(breakActivities.stream().collect(Collectors.toMap(key -> key.getActivity().getId(), value -> value))); // this map is used for payOut
        return activityWrapperMap;
    }



    public List<ShiftActivity> updateBreakInShift(boolean shiftUpdated, Shift shift, Map<BigInteger, ActivityWrapper> activityWrapperMap, StaffAdditionalInfoDTO staffAdditionalInfoDTO, BreakWTATemplate breakWTATemplate, List<TimeSlotDTO> timeSlot, Shift dbShift, Phase phase) {
         phase = phase!=null?phase:phaseService.getCurrentPhaseByUnitIdAndDate(shift.getUnitId(), shift.getStartDate(), shift.getEndDate());
        if (TIME_AND_ATTENDANCE.equals(phase.getName()) && isCollectionNotEmpty(shift.getBreakActivities())) {
            validateBreakDuration(shift);
            shift.getBreakActivities().forEach(shiftActivity -> {
                //if(isNull(shiftActivity.getId())) {
                    shiftActivity.setId(mongoSequenceRepository.nextSequence(ShiftActivity.class.getSimpleName()));
                //}
            });
            return shift.getBreakActivities();
            //return getBreakActivity(shift, dbShift, activityWrapperMap);
        }
        BreakSettings breakSettings = breakSettingMongoRepository.findAllByDeletedFalseAndExpertiseId(staffAdditionalInfoDTO.getEmployment().getExpertise().getId());
        List<ShiftActivity> breakActivities = new ArrayList<>();
        if (isNotNull(breakSettings)) {
            activityWrapperMap.putAll(getBreakActivities(breakSettings, shift.getUnitId()));
            boolean placeBreakAnyWhereInShift = false;
            ShiftActivity breakActivity = null;
            DateTimeInterval eligibleBreakInterval = new DateTimeInterval(shift.getStartDate(), shift.getEndDate());
            Date placeBreakAfterThisDate = shift.getStartDate();
            if (isNotNull(breakSettings) && shift.getMinutes() >= breakSettings.getShiftDurationInMinute()) {
                if (isCollectionEmpty(shift.getBreakActivities()) || shiftUpdated) {
                    breakActivity = getBreakOnUpdateOrCreateShift(shift, activityWrapperMap, staffAdditionalInfoDTO, breakWTATemplate, timeSlot, breakSettings, placeBreakAnyWhereInShift, breakActivity, placeBreakAfterThisDate);
                } else if (isCollectionNotEmpty(shift.getBreakActivities()) && !shiftUpdated) {
                    breakActivity = validateBreakOnUpdateShift(shift, eligibleBreakInterval, placeBreakAfterThisDate, breakSettings);
                }
                if (isNotNull(breakActivity)) {
                    //if (breakActivity.getId() == null) {
                        breakActivity.setId(mongoSequenceRepository.nextSequence(ShiftActivity.class.getSimpleName()));
                    //}
                    updateBreakHeldInShift(breakActivity, shift, dbShift, activityWrapperMap);
                    breakActivities.add(breakActivity);
                }
            }
        }
        return breakActivities;
    }

    private ShiftActivity getBreakOnUpdateOrCreateShift(Shift shift, Map<BigInteger, ActivityWrapper> activityWrapperMap, StaffAdditionalInfoDTO staffAdditionalInfoDTO, BreakWTATemplate breakWTATemplate, List<TimeSlotDTO> timeSlot, BreakSettings breakSettings, boolean placeBreakAnyWhereInShift, ShiftActivity breakActivity, Date placeBreakAfterThisDate) {
        DateTimeInterval eligibleBreakInterval;
        if (isNotNull(breakWTATemplate)) {
            BreakAvailabilitySettings breakAvailabilitySettings = findCurrentBreakAvailability(shift.getStartDate(), timeSlot, breakWTATemplate);
            if (isNotNull(breakAvailabilitySettings) && (breakAvailabilitySettings.getShiftPercentage() <= 0 || breakAvailabilitySettings.getShiftPercentage() >= 100)) {
                exceptionService.actionNotPermittedException(SHIFT_PERCENTAGE_IN_BREAK_RULETEMPLATE, breakAvailabilitySettings.getShiftPercentage());
            }
            placeBreakAnyWhereInShift = (breakAvailabilitySettings.getStartAfterMinutes() + breakAvailabilitySettings.getEndBeforeMinutes()) >= shift.getMinutes();
            eligibleBreakInterval = placeBreakAnyWhereInShift ? null : getBreakInterval(shift, breakAvailabilitySettings, breakSettings, activityWrapperMap);
            placeBreakAnyWhereInShift = placeBreakAnyWhereInShift ? placeBreakAnyWhereInShift : eligibleBreakInterval.getMinutes() < breakSettings.getBreakDurationInMinute();
            placeBreakAfterThisDate = isNotNull(eligibleBreakInterval) ? roundDateByMinutes(eligibleBreakInterval.getStartDate(), 15) : placeBreakAfterThisDate;
        }
        breakActivity = getBreakByShiftActivity(shift, activityWrapperMap, staffAdditionalInfoDTO, breakSettings, placeBreakAnyWhereInShift, breakActivity, placeBreakAfterThisDate);
        if (isNull(breakActivity)) {
            Date breakStartDate = placeBreakAfterThisDate;
            Optional<ShiftActivity> shiftActivityOptional = shift.getActivities().stream().filter(shiftActivity -> shiftActivity.getInterval().containsAndEqualsEndDate(breakStartDate)).findAny();
            Date breakEndDate = asDate(asZonedDateTime(placeBreakAfterThisDate).plusMinutes(breakSettings.getBreakDurationInMinute()));
            breakActivity = buildBreakActivity(placeBreakAfterThisDate, breakEndDate, breakSettings, staffAdditionalInfoDTO, activityWrapperMap);
            ActivityWrapper activityWrapper = activityWrapperMap.get(shiftActivityOptional.get().getActivityId());
            breakActivity.setBreakNotHeld(!activityWrapper.getActivity().getActivityRulesSettings().isBreakAllowed());
            if (shiftActivityOptional.isPresent() && breakActivity.isBreakNotHeld() && !activityWrapper.getTimeTypeInfo().isBreakNotHeldValid()) {
                breakActivity = null;
            }
        }
        return breakActivity;
    }

    private void validateBreakDuration(Shift shift) {
        shift.getBreakActivities().forEach(k->{
            if(!shift.getInterval().contains(k.getInterval())){
                LOGGER.info("Shift Interval {}",shift.getInterval().toString());
                LOGGER.info("Activity Interval {}",k.getInterval().toString());
                exceptionService.actionNotPermittedException(BREAK_NOT_VALID);
            }
        });
    }

    public List<ShiftActivity> getBreakActivity(Shift shift, Shift dbShift, Map<BigInteger, ActivityWrapper> activityWrapperMap) {
        shift.getBreakActivities().forEach(k -> updateBreakHeldInShift(k, shift, dbShift, activityWrapperMap));
        return shift.getBreakActivities();
    }

    private ShiftActivity validateBreakOnUpdateShift(Shift shift, DateTimeInterval eligibleBreakInterval, Date placeBreakAfterThisDate, BreakSettings breakSettings) {
        ShiftActivity breakActivity = shift.getBreakActivities().get(0);
        Date startdate = placeBreakAfterThisDate.after(eligibleBreakInterval.getStartDate()) ? eligibleBreakInterval.getStartDate() : placeBreakAfterThisDate;
        Date endDate = placeBreakAfterThisDate.after(eligibleBreakInterval.getEndDate()) ? placeBreakAfterThisDate : eligibleBreakInterval.getEndDate();
        eligibleBreakInterval = new DateTimeInterval(startdate, endDate);
        if (!eligibleBreakInterval.containsInterval(breakActivity.getInterval()) || !breakSettings.getBreakDurationInMinute().equals(breakActivity.getInterval().getMinutes())) {
            exceptionService.actionNotPermittedException(BREAK_NOT_VALID);
        }
        return breakActivity;
    }

    private ShiftActivity getBreakByShiftActivity(Shift shift, Map<BigInteger, ActivityWrapper> activityWrapperMap, StaffAdditionalInfoDTO staffAdditionalInfoDTO, BreakSettings breakSetting, boolean placeBreakAnyWhereInShift, ShiftActivity breakActivity, Date placeBreakAfterThisDate) {
        for (ShiftActivity shiftActivity : shift.getActivities()) {
            //if(activityWrapperMap.get(shiftActivity.getActivityId()).getTimeTypeInfo().isBreakNotHeldValid()) {
            if (isCollectionNotEmpty(shiftActivity.getChildActivities())) {
                for (ShiftActivity childActivity : shiftActivity.getChildActivities()) {
                    breakActivity = getBreakActivityAfterCalculation(activityWrapperMap, breakSetting, placeBreakAnyWhereInShift, placeBreakAfterThisDate, childActivity, staffAdditionalInfoDTO);
                }
            } else {
                breakActivity = getBreakActivityAfterCalculation(activityWrapperMap, breakSetting, placeBreakAnyWhereInShift, placeBreakAfterThisDate, shiftActivity, staffAdditionalInfoDTO);
            }
            //}
        }
        return breakActivity;
    }

    private ShiftActivity getBreakActivityAfterCalculation(Map<BigInteger, ActivityWrapper> activityWrapperMap, BreakSettings breakSetting, boolean placeBreakAnyWhereInShift, Date placeBreakAfterThisDate, ShiftActivity shiftActivity, StaffAdditionalInfoDTO staffAdditionalInfoDTO) {
        ShiftActivity breakActivity = null;
        Activity activity = activityWrapperMap.get(shiftActivity.getActivityId()).getActivity();
        boolean breakAllowed = activity.getActivityRulesSettings().isBreakAllowed();
        if (breakAllowed) {
            boolean breakCanbePlace = shiftActivity.getEndDate().after(placeBreakAfterThisDate);
            breakCanbePlace = breakCanbePlace ? new DateTimeInterval(shiftActivity.getStartDate().after(placeBreakAfterThisDate) ? shiftActivity.getStartDate() : placeBreakAfterThisDate, shiftActivity.getEndDate()).getMinutes() > breakSetting.getBreakDurationInMinute() : breakCanbePlace;
            if (breakCanbePlace && !placeBreakAnyWhereInShift) {
                Date startDate = roundDateByMinutes(shiftActivity.getStartDate().after(placeBreakAfterThisDate) ? shiftActivity.getStartDate() : placeBreakAfterThisDate, 15);
                Date endDate = asDate(asZonedDateTime(startDate).plusMinutes(breakSetting.getBreakDurationInMinute()));
                if (endDate.after(shiftActivity.getEndDate())) {
                    endDate = shiftActivity.getEndDate();
                    startDate = asDate(asZonedDateTime(endDate).minusMinutes(breakSetting.getBreakDurationInMinute()));
                }
                breakActivity = buildBreakActivity(startDate, endDate, breakSetting, staffAdditionalInfoDTO, activityWrapperMap);
            }
        }
        return breakActivity;
    }

    private ShiftActivity buildBreakActivity(Date startDate, Date endDate, BreakSettings breakSettings, StaffAdditionalInfoDTO staffAdditionalInfoDTO, Map<BigInteger, ActivityWrapper> activityWrapperMap) {
        ActivityWrapper activityWrapper = activityWrapperMap.get(breakSettings.getActivityId());
        ShiftActivity shiftActivity = new ShiftActivity(activityWrapper.getActivity().getName(), startDate, endDate, activityWrapper.getActivity().getId(), activityWrapper.getTimeType(),activityWrapper.getActivity().getActivityGeneralSettings().getUltraShortName(),activityWrapper.getActivity().getActivityGeneralSettings().getShortName());
        timeBankService.updateScheduledHoursAndActivityDetailsInShiftActivity(shiftActivity, activityWrapperMap, staffAdditionalInfoDTO);
        shiftActivity.setTimeTypeId(activityWrapper.getTimeTypeInfo().getId());
        shiftActivity.setSecondLevelTimeType(activityWrapper.getTimeTypeInfo().getSecondLevelType());
        shiftActivity.setTimeType(activityWrapper.getTimeTypeInfo().getTimeTypes().toValue());
        shiftActivity.setMethodForCalculatingTime(activityWrapper.getActivity().getActivityTimeCalculationSettings().getMethodForCalculatingTime());
        return shiftActivity;
    }

    private DateTimeInterval getBreakInterval(Shift shift, BreakAvailabilitySettings breakAvailabilitySettings, BreakSettings breakSettings, Map<BigInteger, ActivityWrapper> activityWrapperMap) {
        ZonedDateTime endDate = asZonedDateTime(shift.getEndDate()).minusMinutes(breakAvailabilitySettings.getEndBeforeMinutes());
        ZonedDateTime startDateWithShiftPercentage = asZonedDateTime(shift.getStartDate()).plusMinutes(shift.getMinutes() * breakAvailabilitySettings.getShiftPercentage() / 100);
        ZonedDateTime startDate = isEqualOrAfter(startDateWithShiftPercentage, endDate.minusMinutes(breakSettings.getBreakDurationInMinute())) ? endDate.minusMinutes(breakSettings.getBreakDurationInMinute()) : startDateWithShiftPercentage;
        if (shift.getActivities().size() > 1 && shift.getActivities().stream().anyMatch(k -> !activityWrapperMap.get(k.getActivityId()).getTimeTypeInfo().isBreakNotHeldValid() && !activityWrapperMap.get(k.getActivityId()).getActivity().getActivityRulesSettings().isBreakAllowed() && k.getInterval().contains(startDate))) {
            return getDateTimeInterval(shift, breakSettings, activityWrapperMap, endDate, startDate);
        }
        return new DateTimeInterval(startDate, endDate);
    }

    private DateTimeInterval getDateTimeInterval(Shift shift, BreakSettings breakSettings, Map<BigInteger, ActivityWrapper> activityWrapperMap, ZonedDateTime endDate, ZonedDateTime startDate) {
        ShiftActivity nextShiftActivity;
        ShiftActivity previousShiftActivity;
        for (int i = 0; i < shift.getActivities().size(); i++) {
            if (!activityWrapperMap.get(shift.getActivities().get(i).getActivityId()).getTimeTypeInfo().isBreakNotHeldValid() && !activityWrapperMap.get(shift.getActivities().get(i).getActivityId()).getActivity().getActivityRulesSettings().isBreakAllowed() && shift.getActivities().get(i).getInterval().contains(startDate)) {
                if (i > 0) {
                    previousShiftActivity = shift.getActivities().get(i - 1);
                    if (activityWrapperMap.get(previousShiftActivity.getActivityId()).getTimeTypeInfo().isBreakNotHeldValid() || activityWrapperMap.get(previousShiftActivity.getActivityId()).getActivity().getActivityRulesSettings().isBreakAllowed()) {
                        endDate = asZonedDateTime(previousShiftActivity.getEndDate());
                        startDate = endDate.minusMinutes(breakSettings.getBreakDurationInMinute());
                    }
                } else if (i < shift.getActivities().size() - 1) {
                    nextShiftActivity = shift.getActivities().get(i + 1);
                    if (activityWrapperMap.get(nextShiftActivity.getActivityId()).getTimeTypeInfo().isBreakNotHeldValid() || activityWrapperMap.get(nextShiftActivity.getActivityId()).getActivity().getActivityRulesSettings().isBreakAllowed()) {
                        endDate = asZonedDateTime(nextShiftActivity.getEndDate());
                        startDate = endDate.minusMinutes(breakSettings.getBreakDurationInMinute());
                    }
                }
            }
        }
        return new DateTimeInterval(startDate, endDate);
    }

    private BreakAvailabilitySettings findCurrentBreakAvailability(Date startDate, List<TimeSlotDTO> timeSlots, BreakWTATemplate breakWTATemplate) {
        BreakAvailabilitySettings breakAvailabilitySettings = null;
        TimeSlotDTO currentTimeSlot = timeSlots.stream().filter(current -> new TimeInterval((current.getStartHour() * ONE_HOUR_MINUTES) + current.getStartMinute(), (current.getEndHour() * ONE_HOUR_MINUTES) + current.getEndMinute() - 1).contains(asZonedDateTime(startDate).get(ChronoField.MINUTE_OF_DAY))).findFirst().orElse(null);
        if (currentTimeSlot != null && breakWTATemplate != null && !breakWTATemplate.isDisabled()) {
            breakAvailabilitySettings = breakWTATemplate.getBreakAvailability().stream().filter(currentAvailability -> (currentAvailability.getTimeSlot().toString().equalsIgnoreCase(currentTimeSlot.getName()))).findFirst().get();
        }
        return breakAvailabilitySettings;
    }

    public boolean interruptBreak(BigInteger shiftId, BreakAction breakAction) {
        Shift shift = shiftMongoRepository.findById(shiftId).orElseThrow(() -> new DataNotFoundByIdException(convertMessage(MESSAGE_SHIFT_ID, shiftId)));
        Phase phase = phaseService.getCurrentPhaseByUnitIdAndDate(shift.getUnitId(), shift.getStartDate(), shift.getEndDate());
        if (TIME_AND_ATTENDANCE.equals(phase.getName()) || REALTIME.equals(phase.getName())) {
            switch (breakAction) {
                case INTERRUPT:
                    shift.getBreakActivities().forEach(shiftActivity -> shiftActivity.setBreakInterrupt(true));
                    break;
                case NOT_HELD:
                    shift.getBreakActivities().forEach(shiftActivity -> shiftActivity.setBreakNotHeld(true));
                    break;
                case UNINTERRUPT:
                    shift.getBreakActivities().forEach(shiftActivity -> shiftActivity.setBreakInterrupt(false));
                    break;
                default:
                    break;
            }
        }
        shiftMongoRepository.save(shift);
        return true;
    }

    private void updateBreakHeldInShift(ShiftActivity breakActivity, Shift shift, Shift dbShift, Map<BigInteger, ActivityWrapper> activityWrapperMap) {
        if (isCollectionNotEmpty(dbShift.getBreakActivities()) || breakActivity.isBreakNotHeld()) {
            ShiftActivity shiftActivity = shift.getActivities().stream().filter(k -> new DateTimeInterval(k.getStartDate(), k.getEndDate()).contains(breakActivity.getStartDate())).findFirst().orElseThrow(()->new InvalidRequestException(convertMessage(BREAK_NOT_VALID)));
            if (!activityWrapperMap.get(shiftActivity.getActivityId()).getTimeTypeInfo().isBreakNotHeldValid() && !activityWrapperMap.get(shiftActivity.getActivityId()).getActivity().getActivityRulesSettings().isBreakAllowed()) {
                exceptionService.actionNotPermittedException(BREAK_NOT_VALID);
            }
            breakActivity.setBreakNotHeld(!activityWrapperMap.get(shiftActivity.getActivityId()).getActivity().getActivityRulesSettings().isBreakAllowed());
        }
    }

    public double getNumberOfBreakInterrupt(Long staffId, DateTimeInterval dateTimeInterval, KPICalculationRelatedInfo kpiCalculationRelatedInfo) {
        List<ShiftWithActivityDTO> shiftWithActivityDTOS = kpiCalculationRelatedInfo.getShiftsByStaffIdAndInterval(staffId, dateTimeInterval,true);
        KPIBuilderCalculationService.ShiftActivityCriteria shiftActivityCriteria = kpiBuilderCalculationService.getShiftActivityCriteria(kpiCalculationRelatedInfo);
        KPIBuilderCalculationService.FilterShiftActivity filterShiftActivity = kpiBuilderCalculationService.new FilterShiftActivity(shiftWithActivityDTOS, shiftActivityCriteria, false).invoke();
        long interruptShift = filterShiftActivity.getShifts().stream().filter(k -> k.getBreakActivities().stream().anyMatch(ShiftActivityDTO::isBreakInterrupt)).count();
        if (PERCENTAGE.equals(kpiCalculationRelatedInfo.getXAxisConfigs().get(0))) {
            return isCollectionNotEmpty(filterShiftActivity.getShifts()) ? getValueWithDecimalFormat((interruptShift * 100.0d) / filterShiftActivity.getShifts().size()) : 0;
        } else
            return interruptShift;
    }



    public void updateBreak(Shift shift, Shift shift1, ShiftActivity shiftActivity) {
        ShiftActivity breakActivity = shift.getBreakActivities().stream().filter(k -> k.getStartDate().before(shiftActivity.getEndDate())).findFirst().orElse(null);
        shift.getBreakActivities().remove(breakActivity);
        if (breakActivity != null) {
            shift1.setBreakActivities(Arrays.asList(breakActivity));
        }
    }


    @Override
    public <T> double get(Long staffId, DateTimeInterval dateTimeInterval, KPICalculationRelatedInfo kpiCalculationRelatedInfo, T t) {
        return getNumberOfBreakInterrupt(staffId, dateTimeInterval, kpiCalculationRelatedInfo);
    }
}