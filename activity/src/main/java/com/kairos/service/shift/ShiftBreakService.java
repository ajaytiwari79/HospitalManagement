package com.kairos.service.shift;

import com.kairos.dto.activity.shift.ShiftActivity;
import com.kairos.dto.activity.shift.StaffUnitPositionDetails;
import com.kairos.dto.activity.wta.templates.BreakAvailabilitySettings;
import com.kairos.dto.user.country.time_slot.TimeSlotWrapper;
import com.kairos.enums.shift.ShiftStatus;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.activity.ActivityWrapper;
import com.kairos.persistence.model.break_settings.BreakSettings;
import com.kairos.persistence.model.shift.Shift;
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
    private MongoSequenceRepository mongoSequenceRepository;
    @Inject
    private ExceptionService exceptionService;


   /* public List<ShiftActivity> addBreakInShifts(Map<BigInteger, ActivityWrapper> activityWrapperMap, Shift shift, StaffUnitPositionDetails unitPositionDetails) {
        Long shiftDurationInMinute = new DateTimeInterval(shift.getStartDate(), shift.getEndDate()).getMinutes();
        List<BreakSettings> breakSettings = breakSettingMongoRepository.findAllByDeletedFalseAndUnitIdAndShiftDurationInMinuteLessThanEqualOrderByCreatedAtAsc(shift.getUnitId(), shiftDurationInMinute);
        List<ShiftActivity> breakActivities = new ArrayList<>();
        if (!breakSettings.isEmpty()) {
            Map<BigInteger, ActivityWrapper> breakActivitiesMap = getBreakActivities(breakSettings);
            boolean paid = Optional.ofNullable(unitPositionDetails.getExpertise().getBreakPaymentSetting()).isPresent() &&
                    BreakPaymentSetting.PAID.equals(unitPositionDetails.getExpertise().getBreakPaymentSetting());
            activityWrapperMap.putAll(breakActivitiesMap);
            breakActivities = getBreaks(activityWrapperMap, shift, breakSettings, breakActivitiesMap, paid);
        }
        return breakActivities;
    }*/

    public Map<BigInteger, ActivityWrapper> getBreakActivities(List<BreakSettings> breakSettings, Long unitId) {
        List<BigInteger> breakActivityIds = breakSettings.stream().map(BreakSettings::getActivityId).collect(Collectors.toList());// These are country activity ids
        List<ActivityWrapper> breakActivities = activityRepository.findActivitiesAndTimeTypeByParentIdsAndUnitId(breakActivityIds, unitId);
        Map<BigInteger, ActivityWrapper> activityWrapperMap =
                breakActivities.stream().collect(Collectors.toMap(key -> key.getActivity().getParentId(), value -> value));  // This map is used for break
        activityWrapperMap.putAll(breakActivities.stream().collect(Collectors.toMap(key -> key.getActivity().getId(), value -> value))); // this map is used for payOut
        return activityWrapperMap;
    }

    public List<ShiftActivity> addBreakInShifts(Map<BigInteger, ActivityWrapper> activityWrapperMap, Shift mainShift, StaffUnitPositionDetails unitPositionDetails, BreakWTATemplate breakWTATemplate, List<TimeSlotWrapper> timeSlot) {
        return calculateBreakAndShiftDuration(activityWrapperMap,  mainShift, unitPositionDetails,  breakWTATemplate, timeSlot,false);

    }
    private List<ShiftActivity> calculateBreakAndShiftDuration(Map<BigInteger, ActivityWrapper> activityWrapperMap, Shift mainShift, StaffUnitPositionDetails unitPositionDetails, BreakWTATemplate breakWTATemplate, List<TimeSlotWrapper> timeSlot,boolean updateShift) {
        if (activityWrapperMap.get(mainShift.getActivities().get(0).getActivityId()).getActivity().getRulesActivityTab().isBreakAllowed()) {
            Long shiftDurationInMinute = (mainShift.getEndDate().getTime() - mainShift.getStartDate().getTime()) / ONE_MINUTE;
            List<BreakSettings> breakSettings = breakSettingMongoRepository.findAllByDeletedFalseAndExpertiseIdOrderByCreatedAtAsc(unitPositionDetails.getExpertise().getId(), shiftDurationInMinute);
            Map<BigInteger, ActivityWrapper> breakActivitiesMap = getBreakActivities(breakSettings, mainShift.getUnitId());
            activityWrapperMap.putAll(breakActivitiesMap);
            if (Optional.ofNullable(breakSettings).isPresent() && breakSettings.size() > 0) {
                activityWrapperMap.putAll(breakActivitiesMap);
                return addBreakInShifts(mainShift, breakSettings, shiftDurationInMinute, breakActivitiesMap, breakWTATemplate, timeSlot,updateShift);
            }
        }
        return Collections.emptyList();
    }
    public List<ShiftActivity> updateBreakInShifts(Map<BigInteger, ActivityWrapper> activityWrapperMap, Shift mainShift, StaffUnitPositionDetails unitPositionDetails, BreakWTATemplate breakWTATemplate, List<TimeSlotWrapper> timeSlot) {
        removeAllPreviouslyAllottedBreaks(mainShift);
        return calculateBreakAndShiftDuration(activityWrapperMap,  mainShift, unitPositionDetails,  breakWTATemplate, timeSlot,true);

    }
    private BreakAvailabilitySettings findCurrentBreakAvailability(Date startDate, List<TimeSlotWrapper> timeSlots, BreakWTATemplate breakWTATemplate) {
        BreakAvailabilitySettings breakAvailabilitySettings = null;
        TimeSlotWrapper currentTimeSlot = timeSlots.stream().filter(current -> (current.getStartHour() < startDate.getHours() && current.getEndHour() > startDate.getHours())).findFirst().orElse(null);
        if (currentTimeSlot != null && breakWTATemplate != null && !breakWTATemplate.isDisabled()) {
            breakAvailabilitySettings = breakWTATemplate.getBreakAvailability().stream().filter(currentAvailability -> (currentAvailability.getTimeSlot().toString().equalsIgnoreCase(currentTimeSlot.getName()))).findFirst().get();
        }
        return breakAvailabilitySettings;
    }

    private short  findNumberOfRequiredBreaks(List<BreakSettings> breakSettings, Long shiftDurationInMinute){
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
     *
     * @param mainShift
     * @param breakSettings
     * @param shiftDurationInMinute
     * @param breakActivitiesMap
     * @param breakWTATemplate
     * @param timeSlot
     * @return shift with breaks
     */
    private List<ShiftActivity> addBreakInShifts(Shift mainShift, List<BreakSettings> breakSettings, Long shiftDurationInMinute, Map<BigInteger, ActivityWrapper> breakActivitiesMap, BreakWTATemplate breakWTATemplate, List<TimeSlotWrapper> timeSlot,boolean updateShift) {

        Long startDateMillis = mainShift.getStartDate().getTime();
        Long endDateMillis ;
        Long restrictedEndDateMillis = mainShift.getEndDate().getTime();
        Long lastBreakEndedOnInMillis=0L;
        Long breakAllowedWithShiftMinute = 0L;
        Long allowedBreakDurationInMinute = 0L;
        Long workedShiftDuration = 0L;
        Long currentlyAllottedDurationInMinute = 0L;
        short itemsAddedFromBeginning = 0;
        short numberOfBreakAllotted = 0;
        boolean lastBlockingShiftAdded=false;
        List<ShiftActivity> shifts = new ArrayList<>();
        Activity breakActivity = null;
        short numberOfBreakRequired = findNumberOfRequiredBreaks(breakSettings,shiftDurationInMinute);
        BreakAvailabilitySettings breakAvailability=null;
        if (numberOfBreakRequired > 0) {

             breakAvailability =findCurrentBreakAvailability(mainShift.getActivities().get(0).getStartDate(), timeSlot, breakWTATemplate);
                    // this must be break restriction settings
            // with initial block and end block  we need to check start restriction and and end as well

            if (breakAvailability == null) { // if availability is null then we are resetting it to zero means no restriction at all
                breakAvailability = new BreakAvailabilitySettings((short) 0, (short) 0);
                breakWTATemplate= new BreakWTATemplate();
                breakWTATemplate.setBreakGapMinutes((short) 0);
            }
            if (breakAvailability.getStartAfterMinutes() == 0) { // this means no start restriction is set, so we are adding the break at start
                workedShiftDuration = workedShiftDuration + (breakSettings.get(0).getShiftDurationInMinute() / 2);
                currentlyAllottedDurationInMinute=workedShiftDuration;
            } else {
                workedShiftDuration = workedShiftDuration + breakAvailability.getStartAfterMinutes();
                currentlyAllottedDurationInMinute=workedShiftDuration;
            }
            currentlyAllottedDurationInMinute=workedShiftDuration;
            endDateMillis = startDateMillis + (workedShiftDuration * ONE_MINUTE);
            shifts.add(updateShift?getShiftByStartDuration(mainShift,new Date(startDateMillis),new Date(endDateMillis)):getShiftObject(mainShift.getActivities().get(0).getActivityName(), mainShift.getActivities().get(0).getActivityId(), new Date(startDateMillis), new Date(endDateMillis), false));
            shiftDurationInMinute -= workedShiftDuration;
            startDateMillis=endDateMillis; // reassigning next start as end of this

            if (breakAvailability.getEndBeforeMinutes() > 0 && shiftDurationInMinute >= breakAvailability.getEndBeforeMinutes()) {    // add a shift at last of array we need to shift this to last
                workedShiftDuration += breakAvailability.getEndBeforeMinutes();
                shiftDurationInMinute -= breakAvailability.getEndBeforeMinutes();
                restrictedEndDateMillis = mainShift.getEndDate().getTime() - breakAvailability.getEndBeforeMinutes() * ONE_MINUTE;// reducing the end date for the rest calculation
                shifts.add(++itemsAddedFromBeginning,getShiftObject(mainShift.getActivities().get(0).getActivityName(), mainShift.getActivities().get(0).getActivityId(), new Date(restrictedEndDateMillis), mainShift.getEndDate(), false));
                lastBlockingShiftAdded=true;
            }
            for (int i = 0; i < numberOfBreakRequired; i++) {
                /**
                 * since we have already calculated how many break is required so we are
                 * The first eligible break hours after.It specifies you can take first break when your shift duration is equals or greater than  this duration
                 **/
                breakAllowedWithShiftMinute = breakSettings.get(i).getShiftDurationInMinute();
                allowedBreakDurationInMinute = breakSettings.get(i).getBreakDurationInMinute();
                ActivityWrapper currentActivity=breakActivitiesMap.get(breakSettings.get(i).getActivityId());
                if (!Optional.ofNullable(currentActivity).isPresent()){
                    exceptionService.dataNotFoundException("error.activity.notAssigned",breakSettings.get(i).getActivityId());
                }
                breakActivity =  currentActivity.getActivity();
                if (!shifts.isEmpty() && i == 0) { // this means we have already added shift for the blocking period then we need to add the shift
                    // we have already added shift now we need to add break for remaining period
                    if (shiftDurationInMinute >= allowedBreakDurationInMinute) {
                        endDateMillis = startDateMillis + (allowedBreakDurationInMinute * ONE_MINUTE);
                        shifts.add(++itemsAddedFromBeginning, getShiftObject(breakActivity.getName(), breakActivity.getId(), new Date(startDateMillis), new Date(endDateMillis), true));
                        shiftDurationInMinute -=  allowedBreakDurationInMinute;
                        startDateMillis=endDateMillis;
                        lastBreakEndedOnInMillis=endDateMillis;
                        currentlyAllottedDurationInMinute += allowedBreakDurationInMinute;
                        numberOfBreakAllotted++;
                    } else {
                        //in 5 hour user need a break for 30 min intitial block is 3 hour and end block is 1:50 hour, so for the current throwing exception
                        // situation not handled i.e after adding shift for blocking time the break is required for 30 min and only 20 min of duration is left
                        logger.debug("un handled case while adding break");
                        return shifts;
                    }
                    if (currentlyAllottedDurationInMinute<=breakAllowedWithShiftMinute){
                        // add shift for remaining time
                        endDateMillis=startDateMillis+((breakAllowedWithShiftMinute-currentlyAllottedDurationInMinute) *ONE_MINUTE); // adding shift for next half
                        shifts.add(++itemsAddedFromBeginning,getShiftObject(mainShift.getActivities().get(0).getActivityName(), mainShift.getActivities().get(0).getActivityId(),
                                new Date(startDateMillis), new Date(endDateMillis), false));
                        shiftDurationInMinute -= (breakAllowedWithShiftMinute-currentlyAllottedDurationInMinute);
                        currentlyAllottedDurationInMinute=0L;
                        startDateMillis = endDateMillis;

                    }

                } else if (shiftDurationInMinute >= breakAllowedWithShiftMinute) {
                    endDateMillis=startDateMillis+((breakAllowedWithShiftMinute/2) *ONE_MINUTE); // adding shift for next half
                    shifts.add(++itemsAddedFromBeginning,getShiftObject(mainShift.getActivities().get(0).getActivityName(), mainShift.getActivities().get(0).getActivityId(),
                            new Date(startDateMillis), new Date(endDateMillis), false));
                    shiftDurationInMinute-=breakAllowedWithShiftMinute/2;
                    currentlyAllottedDurationInMinute=breakAllowedWithShiftMinute/2;
                    startDateMillis = endDateMillis;  // setting previous end as new start
                    if (shiftDurationInMinute > 0) {
                        Long gapBetweenBothBreaks= (startDateMillis-lastBreakEndedOnInMillis)/ONE_MINUTE;
                        if (gapBetweenBothBreaks<breakWTATemplate.getBreakGapMinutes()){
                            // function reduce shift
                            logger.info("GAP is not sufficient as required ");
                        }
                        endDateMillis = endDateMillis + (allowedBreakDurationInMinute * ONE_MINUTE);
                        shifts.add(++itemsAddedFromBeginning,getShiftObject(breakActivity.getName(), breakActivity.getId(), new Date(startDateMillis), new Date(endDateMillis), true));
                        shiftDurationInMinute -= allowedBreakDurationInMinute;
                        workedShiftDuration += allowedBreakDurationInMinute;
                        currentlyAllottedDurationInMinute += allowedBreakDurationInMinute;
                        startDateMillis = endDateMillis;
                        numberOfBreakAllotted++;
                    }
                    if (currentlyAllottedDurationInMinute<=breakAllowedWithShiftMinute){
                        // add shift for remaining time
                        endDateMillis=startDateMillis+((breakAllowedWithShiftMinute-currentlyAllottedDurationInMinute) *ONE_MINUTE); // adding shift for next half
                        shifts.add(++itemsAddedFromBeginning,updateShift?getShiftByStartDuration(mainShift,new Date(startDateMillis),new Date(endDateMillis)):getShiftObject(mainShift.getActivities().get(0).getActivityName(), mainShift.getActivities().get(0).getActivityId(), new Date(startDateMillis), new Date(endDateMillis), false));
                        shiftDurationInMinute -= (breakAllowedWithShiftMinute-currentlyAllottedDurationInMinute);
                        currentlyAllottedDurationInMinute=0L;
                        startDateMillis = endDateMillis;
                    }

                }
            }

        } else {
            endDateMillis = mainShift.getEndDate().getTime();
            shifts.add(getShiftObject(mainShift.getActivities().get(0).getActivityName(), mainShift.getActivities().get(0).getActivityId(), new Date(startDateMillis), new Date(endDateMillis), false));
            shiftDurationInMinute=0L;
        }

        // Sometimes the we have some time remaining so we are adding shift for that time as well
        if (shiftDurationInMinute > 0) {
            endDateMillis = mainShift.getEndDate().getTime();
            shifts.add(++itemsAddedFromBeginning,updateShift?getShiftByStartDuration(mainShift,new Date(startDateMillis),new Date(endDateMillis)):getShiftObject(mainShift.getActivities().get(0).getActivityName(), mainShift.getActivities().get(0).getActivityId(), new Date(startDateMillis), new Date(endDateMillis), false));
        }
        // if we have 2 consecutive shift then we will merge them.
        for (int i=0;i< shifts.size();i++) {
            if (i > 0){
                if (!shifts.get(i-1).isBreakShift() && !shifts.get(i).isBreakShift()){
                    // merge and remove
                    shifts.get(i).setStartDate(shifts.get(i-1).getStartDate());
                    shifts.remove(i-1);
                    i--;
                }
            }
        }
        if (numberOfBreakAllotted != numberOfBreakRequired) {
            reAdjustShiftDuration(breakActivity,mainShift,shifts, lastBlockingShiftAdded, breakAvailability,  breakSettings.subList(numberOfBreakAllotted, numberOfBreakRequired));
        }
        if (updateShift){
            mainShift.getActivities().clear();
        }
        return shifts;
    }
    private void removeAllPreviouslyAllottedBreaks(Shift shift){
        for (int i = 0; i < shift.getActivities().size(); i++) {
            if (shift.getActivities().get(i).isBreakShift()) {
                shift.getActivities().remove(i);
            }
        }
    }
    private  void reAdjustShiftDuration(Activity breakActivity,Shift mainShift,List<ShiftActivity> shifts, boolean lastBlockingShiftAdded, BreakAvailabilitySettings breakAvailability, List<BreakSettings> breakSettings) {
        for (BreakSettings breakSetting : breakSettings) {
            long requiredReduceShiftByMinutes = breakSetting.getShiftDurationInMinute() + breakSetting.getBreakDurationInMinute();
            logger.info(" add shift/break for the following duration"+requiredReduceShiftByMinutes);
            long currentReduceShiftByMinutes = 0l;
            for (int i = shifts.size() - 1; i > 0; i--) {
                if (!shifts.get(i).isBreakShift()) {
                    Long currentShiftDuration = (shifts.get(i).getEndDate().getTime() - shifts.get(i).getStartDate().getTime()) / ONE_MINUTE;
                    if ((lastBlockingShiftAdded && i == shifts.size() - 1)) { // we need to increase the start date of it
                        if (currentShiftDuration > breakAvailability.getEndBeforeMinutes()) {//  shift duration 60 min and blocking 30 min so we can move this start with 30 min
                            currentReduceShiftByMinutes = currentShiftDuration - breakAvailability.getEndBeforeMinutes();
                            shifts.get(i).setStartDate(new Date(shifts.get(i).getStartDate().getTime() + ((currentShiftDuration - breakAvailability.getEndBeforeMinutes()) * ONE_MINUTE)));
                        }
                    } else  if (currentShiftDuration > (requiredReduceShiftByMinutes - currentReduceShiftByMinutes)) {
                        shifts.get(i).setEndDate(new Date(shifts.get(i).getEndDate().getTime() - ((requiredReduceShiftByMinutes - currentReduceShiftByMinutes) * ONE_MINUTE)));
                        shifts.get(i+1).setStartDate(shifts.get(i).getEndDate());// changing next break time as well
                        shifts.get(i+1).setEndDate(new Date(shifts.get(i+1).getEndDate().getTime() - ((requiredReduceShiftByMinutes - currentReduceShiftByMinutes) * ONE_MINUTE)));
                        currentReduceShiftByMinutes+=(requiredReduceShiftByMinutes - currentReduceShiftByMinutes);
                    }
                    // now we need to check
                    if (currentReduceShiftByMinutes == requiredReduceShiftByMinutes) {
                        shifts.add(i+2,getShiftObject(mainShift.getActivities().get(0).getActivityName(),
                                mainShift.getActivities().get(0).getActivityId(),shifts.get(i+1).getEndDate() ,
                                new Date(shifts.get(i+1).getStartDate().getTime() + breakSetting.getShiftDurationInMinute()*ONE_MINUTE), false));
                        shifts.add(i+3,getShiftObject(breakActivity.getName(),breakActivity.getId(),shifts.get(i+2).getEndDate() ,
                                new Date(shifts.get(i+3).getStartDate().getTime()), true));
                        break;

                    }
                }
            }
        }

    }

    private ShiftActivity getShiftObject(String name, BigInteger activityId, Date startDate, Date endDate, boolean breakShift) {
        ShiftActivity childShift = new ShiftActivity(name, startDate, endDate, activityId, breakShift);
        childShift.setStatus(Collections.singleton(ShiftStatus.UNPUBLISHED));
        childShift.setId(mongoSequenceRepository.nextSequence(ShiftActivity.class.getSimpleName()));
        return childShift;

    }
    private ShiftActivity getShiftByStartDuration(Shift shift,Date startDate, Date endDate) {
        ShiftActivity childShift;
        Optional<ShiftActivity> currentShiftActivity=shift.getActivities().stream().filter(shiftActivity -> (shiftActivity.getStartDate().getTime()<=startDate.getTime() && shiftActivity.getEndDate().getTime()>startDate.getTime())).findFirst();
        if (currentShiftActivity.isPresent()){
            childShift= new ShiftActivity(currentShiftActivity.get().getActivityName(), startDate, endDate, currentShiftActivity.get().getActivityId(), false);

        }else {
            childShift= new ShiftActivity(shift.getActivities().get(0).getActivityName(), startDate, endDate, shift.getActivities().get(0).getActivityId(), false);
        }
        return childShift;

    }

}
