package com.kairos.service.shift;

import com.kairos.dto.activity.shift.StaffUnitPositionDetails;
import com.kairos.dto.activity.wta.templates.BreakAvailabilitySettings;
import com.kairos.dto.user.country.time_slot.TimeSlotWrapper;
import com.kairos.enums.shift.ShiftStatus;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.activity.ActivityWrapper;
import com.kairos.persistence.model.break_settings.BreakSettings;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.model.shift.ShiftActivity;
import com.kairos.persistence.model.wta.templates.template_types.BreakWTATemplate;
import com.kairos.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.persistence.repository.break_settings.BreakSettingMongoRepository;
import com.kairos.persistence.repository.common.MongoSequenceRepository;
import com.kairos.service.exception.ExceptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.constants.AppConstants.ONE_HOUR_MINUTES;
import static com.kairos.constants.AppConstants.SHIFT_LEAST_GRANULARITY;
import static javax.management.timer.Timer.ONE_MINUTE;

/**
 * @author pradeep
 * @date - 18/9/18
 */
@Service
public class ShiftBreakService {
    private static final Logger logger = LoggerFactory.getLogger(ShiftBreakService.class);
    @Inject
    private BreakSettingMongoRepository breakSettingMongoRepository;
    @Inject
    private ActivityMongoRepository activityRepository;
    @Inject
    private ExceptionService exceptionService;


    public Map<BigInteger, ActivityWrapper> getBreakActivities(List<BreakSettings> breakSettings, Long unitId) {
        List<BigInteger> breakActivityIds = breakSettings.stream().map(BreakSettings::getActivityId).collect(Collectors.toList());// These are country activity ids
        List<ActivityWrapper> breakActivities = activityRepository.findActivitiesAndTimeTypeByParentIdsAndUnitId(breakActivityIds, unitId);
        Map<BigInteger, ActivityWrapper> activityWrapperMap =
                breakActivities.stream().collect(Collectors.toMap(key -> key.getActivity().getCountryParentId(), value -> value));  // This map is used for break
        activityWrapperMap.putAll(breakActivities.stream().collect(Collectors.toMap(key -> key.getActivity().getId(), value -> value))); // this map is used for payOut
        return activityWrapperMap;
    }

    public List<ShiftActivity> addBreakInShifts(Map<BigInteger, ActivityWrapper> activityWrapperMap, Shift mainShift, StaffUnitPositionDetails unitPositionDetails, BreakWTATemplate breakWTATemplate, List<TimeSlotWrapper> timeSlot) {
        return calculateBreakAndShiftDuration(activityWrapperMap, mainShift, unitPositionDetails, breakWTATemplate, timeSlot, false);

    }

    private List<ShiftActivity> calculateBreakAndShiftDuration(Map<BigInteger, ActivityWrapper> activityWrapperMap, Shift mainShift, StaffUnitPositionDetails unitPositionDetails, BreakWTATemplate breakWTATemplate, List<TimeSlotWrapper> timeSlot, boolean updateShift) {
        List<ShiftActivity> shiftActivities=new ArrayList<>();
        if (activityWrapperMap.get(mainShift.getActivities().get(0).getActivityId()).getActivity().getRulesActivityTab().isBreakAllowed()) {
            Long shiftDurationInMinute = (mainShift.getEndDate().getTime() - mainShift.getStartDate().getTime()) / ONE_MINUTE;
            List<BreakSettings> breakSettings = breakSettingMongoRepository.findAllByDeletedFalseAndExpertiseIdOrderByCreatedAtAsc(unitPositionDetails.getExpertise().getId(), shiftDurationInMinute);
            Map<BigInteger, ActivityWrapper> breakActivitiesMap = getBreakActivities(breakSettings, mainShift.getUnitId());
            activityWrapperMap.putAll(breakActivitiesMap);
            shiftActivities= addBreakInShifts(mainShift, breakSettings, shiftDurationInMinute, breakActivitiesMap, breakWTATemplate, timeSlot, updateShift);
        }
        return shiftActivities;
    }

    public List<ShiftActivity> updateBreakInShifts(Map<BigInteger, ActivityWrapper> activityWrapperMap, Shift mainShift, StaffUnitPositionDetails unitPositionDetails, BreakWTATemplate breakWTATemplate, List<TimeSlotWrapper> timeSlot) {
        removeAllPreviouslyAllottedBreaks(mainShift);
        return calculateBreakAndShiftDuration(activityWrapperMap, mainShift, unitPositionDetails, breakWTATemplate, timeSlot, true);

    }

    private BreakAvailabilitySettings findCurrentBreakAvailability(Date startDate, List<TimeSlotWrapper> timeSlots, BreakWTATemplate breakWTATemplate) {
        BreakAvailabilitySettings breakAvailabilitySettings = null;
        TimeSlotWrapper currentTimeSlot = timeSlots.stream().filter(current -> (current.getStartHour() < startDate.getHours() && current.getEndHour() > startDate.getHours())).findFirst().orElse(null);
        if (currentTimeSlot != null && breakWTATemplate != null && !breakWTATemplate.isDisabled()) {
            breakAvailabilitySettings = breakWTATemplate.getBreakAvailability().stream().filter(currentAvailability -> (currentAvailability.getTimeSlot().toString().equalsIgnoreCase(currentTimeSlot.getName()))).findFirst().get();
        }
        return breakAvailabilitySettings;
    }

    private short findNumberOfRequiredBreaks(List<BreakSettings> breakSettings, Long shiftDurationInMinute) {
        short numberOfBreakRequired = 0;
        long totalBreakDurationInMinutes = 0;
        for (BreakSettings breakSetting : breakSettings) {
            totalBreakDurationInMinutes += breakSetting.getShiftDurationInMinute();
            if (totalBreakDurationInMinutes <= shiftDurationInMinute) {
                numberOfBreakRequired++;
            } else {
                break;
            }
        }
        return numberOfBreakRequired;
        // This means that at least we have to add n break.
    }

    /**
     * @param shift
     * @param breakSettings
     * @param shiftDurationInMinute
     * @param breakActivitiesMap
     * @param breakWTATemplate
     * @param timeSlot
     * @return shift with breaks
     */
    private List<ShiftActivity> addBreakInShifts(Shift shift, List<BreakSettings> breakSettings, Long shiftDurationInMinute, Map<BigInteger, ActivityWrapper> breakActivitiesMap, BreakWTATemplate breakWTATemplate, List<TimeSlotWrapper> timeSlot, boolean updateShift) {

        Long startDateMillis = shift.getStartDate().getTime();
        Long endDateMillis;
        Long restrictedEndDateMillis;
        Long lastBreakEndedOnInMillis = 0L;
        Long breakAllowedWithShiftMinute;
        Long allowedBreakDurationInMinute;
        Long workedShiftDuration = 0L;
        Long currentlyAllottedDurationInMinute;
        short itemsAddedFromBeginning = 0;
        short numberOfBreakAllotted = 0;
        boolean lastBlockingShiftAdded = false;
        List<ShiftActivity> shiftActivities = new ArrayList<>();
        Activity breakActivity = null;
        short numberOfBreakRequired = findNumberOfRequiredBreaks(breakSettings, shiftDurationInMinute);
        BreakAvailabilitySettings breakAvailabilitySettings = null;
        if (numberOfBreakRequired > 0) {

            breakAvailabilitySettings = findCurrentBreakAvailability(shift.getActivities().get(0).getStartDate(), timeSlot, breakWTATemplate);
            // this must be break restriction settings
            // with initial block and end block  we need to check start restriction and and end as well

            if (breakAvailabilitySettings == null) { // if availability is null then we are resetting it to zero means no restriction at all
                breakAvailabilitySettings = new BreakAvailabilitySettings((short) 0, (short) 0);
                breakWTATemplate = new BreakWTATemplate();
                breakWTATemplate.setBreakGapMinutes((short) 0);
            }
            if (breakAvailabilitySettings.getStartAfterMinutes() == 0) { // this means no start restriction is set, so we are adding the break at start
                workedShiftDuration = workedShiftDuration + (breakSettings.get(0).getShiftDurationInMinute() / 2);
            } else {
                workedShiftDuration = workedShiftDuration + breakAvailabilitySettings.getStartAfterMinutes();
            }
            currentlyAllottedDurationInMinute = workedShiftDuration;
            endDateMillis = startDateMillis + (workedShiftDuration * ONE_MINUTE);
            shiftActivities.add(getShiftByStartDuration(shift, new Date(startDateMillis), new Date(endDateMillis)));
            shiftDurationInMinute -= workedShiftDuration;
            startDateMillis = endDateMillis; // reassigning next start as end of this

            if (breakAvailabilitySettings.getEndBeforeMinutes() > 0 && shiftDurationInMinute >= breakAvailabilitySettings.getEndBeforeMinutes()) {    // add a shift at last of array we need to shift this to last
                workedShiftDuration += breakAvailabilitySettings.getEndBeforeMinutes();
                shiftDurationInMinute -= breakAvailabilitySettings.getEndBeforeMinutes();
                restrictedEndDateMillis = shift.getEndDate().getTime() - breakAvailabilitySettings.getEndBeforeMinutes() * ONE_MINUTE;// reducing the end date for the rest calculation
                shiftActivities.add(getShiftByStartDuration(shift, new Date(restrictedEndDateMillis), new Date(shift.getEndDate().getTime())));
                lastBlockingShiftAdded = true;
            }
            for (int i = 0; i < numberOfBreakRequired; i++) {
                /**
                 * since we have already calculated how many break is required so we are
                 * The first eligible break hours after.It specifies you can take first break when your shift duration is equals or greater than  this duration
                 **/
                breakAllowedWithShiftMinute = breakSettings.get(i).getShiftDurationInMinute();
                allowedBreakDurationInMinute = breakSettings.get(i).getBreakDurationInMinute();
                ActivityWrapper currentActivity = breakActivitiesMap.get(breakSettings.get(i).getActivityId());
                if (!Optional.ofNullable(currentActivity).isPresent()) {
                    exceptionService.dataNotFoundException("error.activity.notAssigned", breakSettings.get(i).getActivityId());
                }
                breakActivity = currentActivity.getActivity();
                if (!shiftActivities.isEmpty() && i == 0) { // this means we have already added shift for the blocking period then we need to add the shift
                    // we have already added shift now we need to add break for remaining period
                    if (shiftDurationInMinute >= allowedBreakDurationInMinute) {
                        endDateMillis = startDateMillis + (allowedBreakDurationInMinute * ONE_MINUTE);
                        shiftActivities.add(++itemsAddedFromBeginning, getBreakAtCurrentDuration(shift, new Date(startDateMillis), new Date(endDateMillis), breakActivity, allowedBreakDurationInMinute));
                        shiftDurationInMinute -= allowedBreakDurationInMinute;
                        startDateMillis = endDateMillis;
                        lastBreakEndedOnInMillis = endDateMillis;
                        currentlyAllottedDurationInMinute += allowedBreakDurationInMinute;
                        numberOfBreakAllotted++;
                    } else {
                        //in 5 hour user need a break for 30 min intitial block is 3 hour and end block is 1:50 hour, so for the current throwing exception
                        // situation not handled i.e after adding shift for blocking time the break is required for 30 min and only 20 min of duration is left
                        logger.debug("un handled case while adding break");
                        return shiftActivities;
                    }
                    if (currentlyAllottedDurationInMinute <= breakAllowedWithShiftMinute) {
                        // add shift for remaining time
                        endDateMillis = startDateMillis + ((breakAllowedWithShiftMinute - currentlyAllottedDurationInMinute) * ONE_MINUTE); // adding shift for next half
                        shiftActivities.add(++itemsAddedFromBeginning, getShiftByStartDuration(shift, new Date(startDateMillis), new Date(endDateMillis)));
                        shiftDurationInMinute -= (breakAllowedWithShiftMinute - currentlyAllottedDurationInMinute);
                        currentlyAllottedDurationInMinute = 0L;
                        startDateMillis = endDateMillis;
                    }
                } else if (shiftDurationInMinute >= breakAllowedWithShiftMinute) {
                    endDateMillis = startDateMillis + ((breakAllowedWithShiftMinute / 2) * ONE_MINUTE); // adding shift for next half
                    shiftActivities.add(++itemsAddedFromBeginning, getShiftByStartDuration(shift, new Date(startDateMillis), new Date(endDateMillis)));
                    shiftDurationInMinute -= breakAllowedWithShiftMinute / 2;
                    currentlyAllottedDurationInMinute = breakAllowedWithShiftMinute / 2;
                    startDateMillis = endDateMillis;  // setting previous end as new start
                    if (shiftDurationInMinute > 0) {
                        Long gapBetweenBothBreaks = (startDateMillis - lastBreakEndedOnInMillis) / ONE_MINUTE;
                        if (gapBetweenBothBreaks < breakWTATemplate.getBreakGapMinutes()) {
                            // function reduce shift
                            logger.info("GAP is not sufficient as required ");
                        }// we have already added shift now we need to add break for remaining period
                        endDateMillis = endDateMillis + (allowedBreakDurationInMinute * ONE_MINUTE);
                        shiftActivities.add(++itemsAddedFromBeginning, getBreakAtCurrentDuration(shift, new Date(startDateMillis), new Date(endDateMillis), breakActivity, allowedBreakDurationInMinute));
                        shiftDurationInMinute -= allowedBreakDurationInMinute;
                        workedShiftDuration += allowedBreakDurationInMinute;
                        currentlyAllottedDurationInMinute += allowedBreakDurationInMinute;
                        startDateMillis = endDateMillis;
                        numberOfBreakAllotted++;
                    }
                    if (currentlyAllottedDurationInMinute <= breakAllowedWithShiftMinute) {
                        // add shift for remaining time
                        endDateMillis = startDateMillis + ((breakAllowedWithShiftMinute - currentlyAllottedDurationInMinute) * ONE_MINUTE); // adding shift for next half
                        shiftActivities.add(++itemsAddedFromBeginning, getShiftByStartDuration(shift, new Date(startDateMillis), new Date(endDateMillis)));
                        shiftDurationInMinute -= (breakAllowedWithShiftMinute - currentlyAllottedDurationInMinute);
                        currentlyAllottedDurationInMinute = 0L;
                        startDateMillis = endDateMillis;
                    }
                }
            }
        } else {
            //endDateMillis = mainShift.getEndDate().getTime();
            for (ShiftActivity shiftActivity : shift.getActivities()) {
                shiftActivities.add(getShiftObject(shiftActivity.getActivityName(), shiftActivity.getActivityId(), shiftActivity.getStartDate(), shiftActivity.getEndDate(), false, shiftActivity.getAbsenceReasonCodeId(), null));
            }
            shiftDurationInMinute = 0L;
        }

        // Sometimes the we have some time remaining so we are adding shift for that time as well
        if (shiftDurationInMinute > 0) {
            endDateMillis = shift.getEndDate().getTime();
            shiftActivities.add(++itemsAddedFromBeginning, getShiftByStartDuration(shift, new Date(startDateMillis), new Date(endDateMillis)));
        }
        mergeShifts(shiftActivities);// if we have 2 consecutive shift then we will merge them.

        if (numberOfBreakAllotted != numberOfBreakRequired) {
            adjustShiftDurationAndAddRemaingBreaks(breakActivity, shift, shiftActivities, lastBlockingShiftAdded, breakAvailabilitySettings, breakSettings.subList(numberOfBreakAllotted, numberOfBreakRequired), breakWTATemplate.getBreakGapMinutes());
        }
        return shiftActivities;
    }

    private void mergeShifts(List<ShiftActivity> shiftActivities) {

        for (int i = 0; i < shiftActivities.size(); i++) {
            if (i > 0) {
                if (!shiftActivities.get(i - 1).isBreakShift() && !shiftActivities.get(i).isBreakShift()) {
                    // merge and remove
                    shiftActivities.get(i).setStartDate(shiftActivities.get(i - 1).getStartDate());
                    shiftActivities.remove(i - 1);
                    i--;
                }
            }
        }

    }

    private void removeAllPreviouslyAllottedBreaks(Shift shift) {
        for (int i = 0; i < shift.getActivities().size(); i++) {
            if (shift.getActivities().get(i).isBreakShift() && !shift.getActivities().get(i).isBreakReplaced()) { // if this is break and break is not replaced by another
                shift.getActivities().remove(i);
            }
        }
    }

    private void adjustShiftDurationAndAddRemaingBreaks(Activity breakActivity, Shift mainShift, List<ShiftActivity> shiftActivities, boolean lastBlockingShiftAdded, BreakAvailabilitySettings breakAvailability, List<BreakSettings> breakSettings, short gapBetweenBreaks) {
        for (BreakSettings breakSetting : breakSettings) {
            long requiredReduceShiftByMinutes;
            if (gapBetweenBreaks > 0) {
                requiredReduceShiftByMinutes = gapBetweenBreaks + breakSetting.getBreakDurationInMinute();
            } else {
                requiredReduceShiftByMinutes = breakSetting.getShiftDurationInMinute() + breakSetting.getBreakDurationInMinute();
                gapBetweenBreaks = breakSetting.getShiftDurationInMinute().shortValue();

            }
            logger.info(" add shift/break for the following duration" + requiredReduceShiftByMinutes);
            long currentReduceShiftByMinutes = 0L;
            for (int i = shiftActivities.size() - 1; i >= 0; i--) {
                Date previousShiftEndDate = null;
                if (!shiftActivities.get(i).isBreakShift()) {
                    Long currentShiftDuration = (shiftActivities.get(i).getEndDate().getTime() - shiftActivities.get(i).getStartDate().getTime()) / ONE_MINUTE;
                    if (lastBlockingShiftAdded && i == shiftActivities.size() - 1) { // we need to increase the start date of it
                        if (currentShiftDuration > breakAvailability.getEndBeforeMinutes()) {//  shift duration 60 min and blocking 30 min so we can move this start with 30 min
                            if ((currentShiftDuration - breakAvailability.getEndBeforeMinutes()) >= requiredReduceShiftByMinutes) {// current shift duration -blocking is greater or equal what we want duration
                                currentReduceShiftByMinutes = requiredReduceShiftByMinutes;
                            } else {
                                currentReduceShiftByMinutes = currentShiftDuration - breakAvailability.getEndBeforeMinutes();
                            }
                            shiftActivities.get(i).setStartDate(new Date(shiftActivities.get(i).getStartDate().getTime() + (currentReduceShiftByMinutes * ONE_MINUTE)));
                            previousShiftEndDate = shiftActivities.get(i).getStartDate();
                        }
                    } else if (i == 0 && currentShiftDuration > (requiredReduceShiftByMinutes - currentReduceShiftByMinutes)) {
                        // this means that we are going to break blocking time and before blocking time we need to break the minimum gap
                        if (currentShiftDuration <= breakAvailability.getStartAfterMinutes()) {
                            short gapBetweenBreaksReduced = reduceMinimumGapValue(gapBetweenBreaks); // before blocking we need to reduce gap value
                            requiredReduceShiftByMinutes = requiredReduceShiftByMinutes - (gapBetweenBreaks - gapBetweenBreaksReduced);
                            gapBetweenBreaks = gapBetweenBreaksReduced;
                        }
                        if (currentReduceShiftByMinutes <= requiredReduceShiftByMinutes) {
                            shiftActivities.get(i).setEndDate(new Date(shiftActivities.get(i).getEndDate().getTime() - ((requiredReduceShiftByMinutes - currentReduceShiftByMinutes) * ONE_MINUTE)));
                            shiftActivities.get(i + 1).setStartDate(shiftActivities.get(i).getEndDate());// changing next break time as well
                            shiftActivities.get(i + 1).setEndDate(new Date(shiftActivities.get(i + 1).getEndDate().getTime() - ((requiredReduceShiftByMinutes - currentReduceShiftByMinutes) * ONE_MINUTE)));
                            previousShiftEndDate = shiftActivities.get(i + 1).getEndDate();
                            currentReduceShiftByMinutes += (requiredReduceShiftByMinutes - currentReduceShiftByMinutes);
                        }
                    } else if (currentShiftDuration > (requiredReduceShiftByMinutes - currentReduceShiftByMinutes)) {
                        shiftActivities.get(i).setEndDate(new Date(shiftActivities.get(i).getEndDate().getTime() - ((requiredReduceShiftByMinutes - currentReduceShiftByMinutes) * ONE_MINUTE)));
                        shiftActivities.get(i + 1).setStartDate(shiftActivities.get(i).getEndDate());// changing next break time as well
                        shiftActivities.get(i + 1).setEndDate(new Date(shiftActivities.get(i + 1).getEndDate().getTime() - ((requiredReduceShiftByMinutes - currentReduceShiftByMinutes) * ONE_MINUTE)));
                        previousShiftEndDate = shiftActivities.get(i + 1).getEndDate();
                        currentReduceShiftByMinutes += (requiredReduceShiftByMinutes - currentReduceShiftByMinutes);
                    }
                    // now we need to check
                    if (currentReduceShiftByMinutes == requiredReduceShiftByMinutes) {
                        Date shiftStartDate;
                        Date shiftEndDate;
                        if (i == shiftActivities.size() - 1) {
                            shiftEndDate = previousShiftEndDate;
                            shiftStartDate = new Date(shiftEndDate.getTime() - breakSetting.getBreakDurationInMinute() * ONE_MINUTE);
                            shiftActivities.add(i, getShiftObject(breakActivity.getName(), breakActivity.getId(), shiftStartDate,
                                    shiftEndDate, true, null, breakSetting.getBreakDurationInMinute()));

                            shiftEndDate = shiftStartDate;
                            shiftStartDate = new Date(shiftEndDate.getTime() - gapBetweenBreaks * ONE_MINUTE);
                            shiftActivities.add(i, getShiftByStartDuration(mainShift, shiftStartDate, shiftEndDate));

                        } else {
                            shiftStartDate = previousShiftEndDate;
                            shiftEndDate = new Date(previousShiftEndDate.getTime() + gapBetweenBreaks * ONE_MINUTE);
                            shiftActivities.add(i + 2, getShiftByStartDuration(mainShift, shiftStartDate, shiftEndDate));

                            shiftStartDate = shiftEndDate; // setting the previous end as next start
                            shiftEndDate = new Date(shiftStartDate.getTime() + breakSetting.getBreakDurationInMinute() * ONE_MINUTE);
                            shiftActivities.add(i + 3, getShiftByStartDuration(mainShift, shiftStartDate, shiftEndDate));
                        }
                        break;
                    }
                }
            }
        }
    }

    private ShiftActivity getShiftObject(String name, BigInteger activityId, Date startDate, Date endDate, boolean breakShift, Long absenceReasonCodeId, Long allowedBreakDurationInMinute) {
        ShiftActivity childShift = new ShiftActivity(name, startDate, endDate, activityId, breakShift, absenceReasonCodeId, allowedBreakDurationInMinute);
        childShift.setStatus(Collections.singleton(ShiftStatus.REQUEST));
        return childShift;
    }

    private ShiftActivity getShiftByStartDuration(Shift shift, Date startDate, Date endDate) {
        ShiftActivity childShift;
        Optional<ShiftActivity> currentShiftActivity = shift.getActivities().stream().filter(shiftActivity -> (shiftActivity.getStartDate().getTime() <= startDate.getTime() && shiftActivity.getEndDate().getTime() > startDate.getTime())).findFirst();
        if (currentShiftActivity.isPresent()) {
            childShift = new ShiftActivity(currentShiftActivity.get().getActivityName(), startDate, endDate, currentShiftActivity.get().getActivityId(), false, currentShiftActivity.get().getAbsenceReasonCodeId(), null);
        } else {
            childShift = new ShiftActivity(shift.getActivities().get(0).getActivityName(), startDate, endDate, shift.getActivities().get(0).getActivityId(), false, shift.getActivities().get(0).getAbsenceReasonCodeId(), null);
        }
        return childShift;

    }

    private ShiftActivity getBreakAtCurrentDuration(Shift shift, Date startDate, Date endDate, Activity breakActivity, Long allowedBreakDurationInMinute) {
        ShiftActivity childShift;
        Optional<ShiftActivity> currentShiftActivity = shift.getActivities().stream().filter(shiftActivity -> (shiftActivity.getStartDate().getTime() <= startDate.getTime() && shiftActivity.getEndDate().getTime() > startDate.getTime())).findFirst();
        if (currentShiftActivity.isPresent() && currentShiftActivity.get().isBreakShift()) {
            childShift = new ShiftActivity(currentShiftActivity.get().getActivityName(), startDate, endDate, currentShiftActivity.get().getActivityId(), true, currentShiftActivity.get().getAbsenceReasonCodeId(), allowedBreakDurationInMinute, currentShiftActivity.get().isBreakReplaced());

        } else {
            childShift = new ShiftActivity(breakActivity.getName(), startDate, endDate, breakActivity.getId(), true, shift.getActivities().get(0).getAbsenceReasonCodeId(), allowedBreakDurationInMinute);

        }
        return childShift;
    }

    public List<ShiftActivity> addBreakInShiftsWhileCopy(Map<BigInteger, ActivityWrapper> activityWrapperMap, Shift mainShift, BreakWTATemplate breakWTATemplate, List<TimeSlotWrapper> timeSlot, List<BreakSettings> breakSettings) {
        Long shiftDurationInMinute = (mainShift.getEndDate().getTime() - mainShift.getStartDate().getTime()) / ONE_MINUTE;
        return addBreakInShifts(mainShift, breakSettings, shiftDurationInMinute, activityWrapperMap, breakWTATemplate, timeSlot, false);

    }

    private short reduceMinimumGapValue(short gapBetweenBreaks) {
        short modFactorOfShiftGap = gapBetweenBreaks > ONE_HOUR_MINUTES ? (short) (gapBetweenBreaks % ONE_HOUR_MINUTES) : gapBetweenBreaks; // 1 hour equals to 60 min
        return modFactorOfShiftGap > SHIFT_LEAST_GRANULARITY ? ((short) (gapBetweenBreaks - SHIFT_LEAST_GRANULARITY)) : gapBetweenBreaks;

    }

}