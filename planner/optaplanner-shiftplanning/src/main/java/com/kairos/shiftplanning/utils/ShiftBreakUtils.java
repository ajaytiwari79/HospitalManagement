package com.kairos.shiftplanning.utils;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.TimeInterval;
import com.kairos.dto.activity.wta.templates.BreakAvailabilitySettings;
import com.kairos.enums.constraint.ConstraintSubType;
import com.kairos.shiftplanning.domain.activity.Activity;
import com.kairos.shiftplanning.domain.activity.ShiftActivity;
import com.kairos.shiftplanning.domain.shift.PlannedTime;
import com.kairos.shiftplanning.domain.shift.ShiftImp;
import com.kairos.shiftplanning.domain.staff.BreakSettings;
import com.kairos.shiftplanning.domain.unit.TimeSlot;
import com.kairos.shiftplanning.domain.wta_ruletemplates.BreakWTATemplate;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static com.kairos.commons.utils.CommonsExceptionUtil.throwException;
import static com.kairos.commons.utils.DateUtils.*;
import static com.kairos.commons.utils.ObjectUtils.*;
import static com.kairos.shiftplanning.constants.ShiftPlanningMessageConstants.SHIFT_PERCENTAGE_IN_BREAK_RULETEMPLATE;

public class ShiftBreakUtils {

    private static final int ONE_HOUR_MINUTES = 60;

    public static void updateBreakInShift(ShiftImp shift) {
        BreakSettings breakSettings = shift.getEmployee().getBreakSettings();
        List<ShiftActivity> breakActivities = new ArrayList<>();
        if (isNotNull(breakSettings)) {
            boolean placeBreakAnyWhereInShift = false;
            ShiftActivity breakActivity = null;
            DateTimeInterval eligibleBreakInterval;
            ZonedDateTime placeBreakAfterThisDate = shift.getStart();
            if (isNotNull(breakSettings) && shift.getMinutes() >= breakSettings.getShiftDurationInMinute()) {
                if (shift.getEmployee().getWtaRuleTemplateMap().get(shift.getStartDate()).containsKey(ConstraintSubType.WTA_FOR_BREAKS_IN_SHIFT)) {
                    BreakWTATemplate breakWTATemplate = (BreakWTATemplate)shift.getEmployee().getWtaRuleTemplateMap().get(shift.getStartDate()).get(ConstraintSubType.WTA_FOR_BREAKS_IN_SHIFT);
                    BreakAvailabilitySettings breakAvailabilitySettings = findCurrentBreakAvailability(shift.getStart(), shift.getEmployee().getUnit().getTimeSlotMap(), breakWTATemplate);
                    if (isNotNull(breakAvailabilitySettings) && (breakAvailabilitySettings.getShiftPercentage() <= 0 || breakAvailabilitySettings.getShiftPercentage() >= 100)) {
                        throwException(SHIFT_PERCENTAGE_IN_BREAK_RULETEMPLATE, breakAvailabilitySettings.getShiftPercentage());
                    }
                    placeBreakAnyWhereInShift = (breakAvailabilitySettings.getStartAfterMinutes() + breakAvailabilitySettings.getEndBeforeMinutes()) >= shift.getMinutes();
                    eligibleBreakInterval = placeBreakAnyWhereInShift ? null : getBreakInterval(shift, breakAvailabilitySettings, breakSettings);
                    placeBreakAnyWhereInShift = placeBreakAnyWhereInShift ? placeBreakAnyWhereInShift : eligibleBreakInterval.getMinutes() < breakSettings.getBreakDurationInMinute();
                    placeBreakAfterThisDate = isNotNull(eligibleBreakInterval) ? roundDateByMinutes(eligibleBreakInterval.getStart(), 15) : placeBreakAfterThisDate;
                }
                breakActivity = getBreakByShiftActivity(shift, breakSettings, placeBreakAnyWhereInShift, breakActivity, placeBreakAfterThisDate);
                if (isNull(breakActivity)) {
                    AtomicReference<ZonedDateTime> atomicReference = new AtomicReference<>(placeBreakAfterThisDate);
                    Optional<ShiftActivity> shiftActivityOptional = shift.getShiftActivities().stream().filter(shiftActivity -> shiftActivity.getInterval().containsAndEqualsEndDate(atomicReference.get())).findAny();
                    //todo break fall in gap then break will insert in first activity
                    if(shiftActivityOptional.isPresent()){
                        ZonedDateTime breakEndDate = placeBreakAfterThisDate.plusMinutes(breakSettings.getBreakDurationInMinute());
                        Optional<PlannedTime> plannedTimeOptional = shiftActivityOptional.get().getPlannedTimes().stream().filter(plannedTime -> plannedTime.getInterval().containsAndEqualsEndDate(atomicReference.get())).findFirst();
                        breakActivity = buildBreakActivity(placeBreakAfterThisDate, breakEndDate, breakSettings, plannedTimeOptional);
                        Activity activity = shiftActivityOptional.get().getActivity();
                        breakActivity.setBreakNotHeld(!activity.isBreakAllowed());
                        if (shiftActivityOptional.isPresent() && breakActivity.isBreakNotHeld() && !activity.getTimeType().isBreakNotHeldValid()) {
                            breakActivity = null;
                        }
                    }
                }
            }
            if (isNotNull(breakActivity)) {
                breakActivities.add(breakActivity);
            }
        }
        shift.setBreakActivities(breakActivities);
    }

    private static ShiftActivity buildBreakActivity(ZonedDateTime startDate, ZonedDateTime endDate, BreakSettings breakSettings,Optional<PlannedTime> plannedTimeOptional) {
        List<PlannedTime> plannedTimes = newArrayList(new PlannedTime(plannedTimeOptional.get().getPlannedTimeId(), startDate, endDate));
        return ShiftActivity.builder().startDate(startDate).activity(breakSettings.getActivity()).endDate(endDate).plannedTimes(plannedTimes).breakNotHeld(false).build();
    }

    private static BreakAvailabilitySettings findCurrentBreakAvailability(ZonedDateTime startDate, Map<String,TimeSlot> timeSlotMap, BreakWTATemplate breakWTATemplate) {
        BreakAvailabilitySettings breakAvailabilitySettings = null;
        TimeSlot currentTimeSlot = timeSlotMap.values().stream().filter(current -> new TimeInterval((current.getStartHour() * ONE_HOUR_MINUTES) + current.getStartMinute(), (current.getEndHour() * ONE_HOUR_MINUTES) + current.getEndMinute() - 1).contains(startDate.get(ChronoField.MINUTE_OF_DAY))).findFirst().orElse(null);
        if (currentTimeSlot != null && breakWTATemplate != null && !breakWTATemplate.isDisabled()) {
            breakAvailabilitySettings = breakWTATemplate.getBreakAvailability().stream().filter(currentAvailability -> (currentAvailability.getTimeSlot().getValue().equalsIgnoreCase(currentTimeSlot.getName()))).findFirst().get();
        }
        return breakAvailabilitySettings;
    }

    private static DateTimeInterval getBreakInterval(ShiftImp shift, BreakAvailabilitySettings breakAvailabilitySettings, BreakSettings breakSettings) {
        ZonedDateTime endDate = shift.getEnd().minus(breakAvailabilitySettings.getEndBeforeMinutes(), ChronoUnit.MINUTES);
        ZonedDateTime startDateWithShiftPercentage = shift.getStart().plusMinutes(shift.getMinutes() * breakAvailabilitySettings.getShiftPercentage() / 100);
        ZonedDateTime startDate = isEqualOrAfter(startDateWithShiftPercentage, endDate.minusMinutes(breakSettings.getBreakDurationInMinute())) ? endDate.minusMinutes(breakSettings.getBreakDurationInMinute()) : startDateWithShiftPercentage;
        if (shift.getShiftActivities().size() > 1 && shift.getShiftActivities().stream().anyMatch(k -> !k.getActivity().getTimeType().isBreakNotHeldValid() && !k.getActivity().isBreakAllowed() && k.getInterval().contains(startDate))) {
            return getDateTimeInterval(shift, breakSettings, endDate, startDate);
        }
        return new DateTimeInterval(startDate, endDate);
    }

    private static DateTimeInterval getDateTimeInterval(ShiftImp shift, BreakSettings breakSettings, ZonedDateTime endDate, ZonedDateTime startDate) {
        ShiftActivity nextShiftActivity;
        ShiftActivity previousShiftActivity;
        for (int i = 0; i < shift.getShiftActivities().size(); i++) {
            if (!shift.getShiftActivities().get(i).getActivity().getTimeType().isBreakNotHeldValid() && !shift.getShiftActivities().get(i).getActivity().isBreakAllowed() && shift.getShiftActivities().get(i).getInterval().contains(startDate)) {
                if (i > 0) {
                    previousShiftActivity = shift.getShiftActivities().get(i - 1);
                    if (previousShiftActivity.getActivity().getTimeType().isBreakNotHeldValid() || previousShiftActivity.getActivity().isBreakAllowed()) {
                        endDate = previousShiftActivity.getEndDate();
                        startDate = endDate.minusMinutes(breakSettings.getBreakDurationInMinute());
                    }
                } else if (i < shift.getShiftActivities().size() - 1) {
                    nextShiftActivity = shift.getShiftActivities().get(i + 1);
                    if (nextShiftActivity.getActivity().getTimeType().isBreakNotHeldValid() || nextShiftActivity.getActivity().isBreakAllowed()) {
                        endDate = nextShiftActivity.getEndDate();
                        startDate = endDate.minusMinutes(breakSettings.getBreakDurationInMinute());
                    }
                }
            }
        }
        return new DateTimeInterval(startDate, endDate);
    }

    private static ShiftActivity getBreakByShiftActivity(ShiftImp shift, BreakSettings breakSetting, boolean placeBreakAnyWhereInShift, ShiftActivity breakActivity, ZonedDateTime placeBreakAfterThisDate) {
        for (ShiftActivity shiftActivity : shift.getShiftActivities()) {
            breakActivity = getBreakActivityAfterCalculation(breakSetting, placeBreakAnyWhereInShift, placeBreakAfterThisDate, shiftActivity);
        }
        return breakActivity;
    }

    private static ShiftActivity getBreakActivityAfterCalculation(BreakSettings breakSetting, boolean placeBreakAnyWhereInShift, ZonedDateTime placeBreakAfterThisDate, ShiftActivity shiftActivity) {
        ShiftActivity breakActivity = null;
        Activity activity = shiftActivity.getActivity();
        boolean breakAllowed = activity.isBreakAllowed();
        if (breakAllowed) {
            boolean breakCanbePlace = shiftActivity.getEndDate().isAfter(placeBreakAfterThisDate);
            ZonedDateTime zonedDateTime = shiftActivity.getStartDate().isAfter(placeBreakAfterThisDate) ? shiftActivity.getStartDate() : placeBreakAfterThisDate;
            breakCanbePlace = breakCanbePlace ? new DateTimeInterval(zonedDateTime, shiftActivity.getEndDate()).getMinutes() > breakSetting.getBreakDurationInMinute() : breakCanbePlace;
            if (breakCanbePlace && !placeBreakAnyWhereInShift) {
                ZonedDateTime startDate = roundDateByMinutes(zonedDateTime, 15);
                AtomicReference<ZonedDateTime> atomicReference = new AtomicReference<>(startDate);
                ZonedDateTime endDate = startDate.plusMinutes(breakSetting.getBreakDurationInMinute());
                if (endDate.isAfter(shiftActivity.getEndDate())) {
                    endDate = shiftActivity.getEndDate();
                    startDate = endDate.minusMinutes(breakSetting.getBreakDurationInMinute());
                }
                Optional<PlannedTime> plannedTimeOptional = shiftActivity.getPlannedTimes().stream().filter(plannedTime -> plannedTime.getInterval().contains(atomicReference.get())).findFirst();
                breakActivity = buildBreakActivity(startDate, endDate, breakSetting,plannedTimeOptional);
            }
        }
        return breakActivity;
    }

}
