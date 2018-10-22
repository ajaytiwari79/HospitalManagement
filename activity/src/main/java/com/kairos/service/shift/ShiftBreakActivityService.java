package com.kairos.service.shift;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.DateUtils;
import com.kairos.dto.activity.shift.ShiftActivity;
import com.kairos.dto.activity.shift.StaffUnitPositionDetails;
import com.kairos.dto.user.user.staff.StaffAdditionalInfoDTO;
import com.kairos.enums.shift.BreakPaymentSetting;
import com.kairos.enums.shift.ShiftStatus;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.activity.ActivityWrapper;
import com.kairos.persistence.model.break_settings.BreakSettings;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.persistence.repository.break_settings.BreakSettingMongoRepository;
import com.kairos.persistence.repository.common.MongoSequenceRepository;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.kairos.constants.AppConstants.BREAK;
import static com.kairos.constants.AppConstants.SHIFT;
import static javax.management.timer.Timer.ONE_MINUTE;

/**
 * @author pradeep
 * @date - 18/9/18
 */
@Service
public class ShiftBreakActivityService {

    @Inject
    private BreakSettingMongoRepository breakSettingMongoRepository;
    @Inject
    private ActivityMongoRepository activityRepository;
    @Inject
    private MongoSequenceRepository mongoSequenceRepository;


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

    /*private List<ShiftActivity> getBreaks(Map<BigInteger, ActivityWrapper> activityWrapperMap, Shift shift, List<BreakSettings> breakSettings, Map<BigInteger, ActivityWrapper> breakActivitiesMap, Boolean paid) {
        List<ShiftActivity> breakActivities = new ArrayList<>();
        BreakSettings lastBreakSetting = breakSettings.get(breakSettings.size()-1);
        ZonedDateTime shiftStart = DateUtils.asZoneDateTime(shift.getStartDate());
        ZonedDateTime shiftEnd = DateUtils.asZoneDateTime(shift.getEndDate());
        Long shiftDurationInMinute = 0L;
        for (BreakSettings breakSetting : breakSettings) {
            shiftDurationInMinute = new DateTimeInterval(shiftStart, shiftEnd).getMinutes();
            Object[] objects = createBreakActivity(breakSetting,shiftDurationInMinute,breakActivitiesMap,paid,shiftStart,shiftEnd,shift,activityWrapperMap);
            List<ShiftActivity> shiftActivity = (ShiftActivity)objects[0];
            if(shiftActivity!=null) {
                breakActivities.add(shiftActivity);
            }
            shiftStart = (ZonedDateTime) objects[1];
        }
        while (lastBreakSetting.getShiftDurationInMinute() > shiftDurationInMinute){
            shiftDurationInMinute = new DateTimeInterval(shiftStart, shiftEnd).getMinutes();
            Object[] objects = createBreakActivity(lastBreakSetting,shiftDurationInMinute,breakActivitiesMap,paid,shiftStart,shiftEnd,shift,activityWrapperMap);
            List<ShiftActivity> shiftActivity = (ShiftActivity)objects[0];
            if(shiftActivity!=null) {
                breakActivities.add(shiftActivity);
            }
            shiftStart = (ZonedDateTime) objects[1];

        }
        return breakActivities;
    }*/

    /*private Object[] createBreakActivity(BreakSettings breakSettings, Long shiftDurationInMinute, Map<BigInteger, ActivityWrapper> breakActivitiesMap, Boolean paid, ZonedDateTime shiftStart, ZonedDateTime shiftEnd, Shift shift, Map<BigInteger, ActivityWrapper> activityWrapperMap){
        ShiftActivity shiftActivity = null;
        if (breakSettings.getShiftDurationInMinute() < shiftDurationInMinute) {
            Activity breakActivity = breakActivitiesMap.get(paid ? breakSettings.getPaidActivityId() : breakSettings.getUnpaidActivityId()).getActivity();
            ZonedDateTime breakStart = shiftStart.plusMinutes(breakSettings.getShiftDurationInMinute());
            ZonedDateTime breakEnd = breakStart.plusMinutes(breakSettings.getBreakDurationInMinute());
            if (breakEnd.isAfter(shiftEnd)) {
                breakEnd = shiftEnd;
            }

            ShiftActivity shiftActivity1 = updateShiftActivityAndScheduledAndDurationMinutes(shift,breakStart,paid,activityWrapperMap,false);

            shiftActivity1 = updateShiftActivityAndScheduledAndDurationMinutes(shift,breakEnd,paid,activityWrapperMap,true);
            shiftActivity =  new ShiftActivity(breakActivity.getName(), DateUtils.asDate(breakStart), DateUtils.asDate(breakEnd), breakActivity.getId());
            shiftActivity.setId(mongoSequenceRepository.nextSequence(ShiftActivity.class.getSimpleName()));
            shiftStart = breakEnd;
        }
        return new Object[]{shiftActivity,shiftStart};
    }*/

    /*private ShiftActivity updateShiftActivityAndScheduledAndDurationMinutes(Shift shift, ZonedDateTime breakDateTime, boolean paid, Map<BigInteger, ActivityWrapper> activityWrapperMap,boolean updateStart){
        ShiftActivity overLappedActivityOfShift = getOverLappedActivityOfShift(shift, breakDateTime);
        if (overLappedActivityOfShift!=null && activityWrapperMap.get(overLappedActivityOfShift.getActivityId()).getActivity().getRulesActivityTab().isBreakAllowed()) {
            if(!paid){
                long minutes = ChronoUnit.MINUTES.between(DateUtils.asZoneDateTime(overLappedActivityOfShift.getStartDate()),breakDateTime);
                overLappedActivityOfShift.setDurationMinutes(overLappedActivityOfShift.getDurationMinutes()- (int)minutes);
            }
            if(updateStart){
                overLappedActivityOfShift.setStartDate(DateUtils.asDate(breakDateTime));
            }else {
                overLappedActivityOfShift.setEndDate(DateUtils.asDate(breakDateTime));
            }
        }
        return overLappedActivityOfShift;
    }

    private ShiftActivity getOverLappedActivityOfShift(Shift shift, ZonedDateTime breakDateTime) {
        ShiftActivity overLappedActivityOfShift = null;
        for (ShiftActivity shiftActivity : shift.getActivities()) {
            DateTimeInterval shiftInterval = new DateTimeInterval(shiftActivity.getStartDate().getTime(), shiftActivity.getEndDate().getTime());
            if (shiftInterval.contains(breakDateTime)) {
                overLappedActivityOfShift = shiftActivity;
                break;
            }
        }
        return overLappedActivityOfShift;
    }*/


    public Map<BigInteger, ActivityWrapper> getBreakActivities(List<BreakSettings> breakSettings) {
        List<BigInteger> breakActivityIds = breakSettings.stream().flatMap(a -> Stream.of(a.getPaidActivityId(), a.getUnpaidActivityId())).collect(Collectors.toList());
        List<ActivityWrapper> breakActivities = activityRepository.findActivitiesAndTimeTypeByActivityId(breakActivityIds);
        return breakActivities.stream().collect(Collectors.toMap(key -> key.getActivity().getId(), value -> value));
    }

    public List<ShiftActivity> addBreakInShifts(Map<BigInteger, ActivityWrapper> activityWrapperMap, Shift mainShift, StaffUnitPositionDetails unitPositionDetails) {
        if (activityWrapperMap.get(mainShift.getActivities().get(0).getActivityId()).getActivity().getRulesActivityTab().isBreakAllowed()) {
            Long shiftDurationInMinute = (mainShift.getEndDate().getTime() - mainShift.getStartDate().getTime()) / ONE_MINUTE;
            List<BreakSettings> breakSettings = breakSettingMongoRepository.findAllByDeletedFalseAndUnitIdAndShiftDurationInMinuteLessThanEqualOrderByCreatedAtAsc(mainShift.getUnitId(), shiftDurationInMinute);
            Map<BigInteger, ActivityWrapper> breakActivitiesMap = getBreakActivities(breakSettings);

            boolean paid = false;
            if (Optional.ofNullable(unitPositionDetails.getExpertise().getBreakPaymentSetting()).isPresent() &&
                    unitPositionDetails.getExpertise().getBreakPaymentSetting().equals(BreakPaymentSetting.PAID)) {
                paid = true;

            }
            if (Optional.ofNullable(breakSettings).isPresent() && breakSettings.size() > 0) {
                activityWrapperMap.putAll(breakActivitiesMap);
                return addBreakInShifts(mainShift, breakSettings, shiftDurationInMinute, breakActivitiesMap, paid);
            }
        }
        return Collections.emptyList();

    }


    private List<ShiftActivity> addBreakInShifts(Shift mainShift, List<BreakSettings> breakSettings, Long shiftDurationInMinute, Map<BigInteger, ActivityWrapper> breakActivitiesMap, Boolean paid) {
        Long startDateMillis = mainShift.getStartDate().getTime();
        Long endDateMillis = null;
        Long breakAllowedAfterMinute = 0L;
        Long allowedBreakDurationInMinute = 0L;
        Long totalBreakAllotedInMinute = 0L;
        String lastItemAdded = null;
        List<ShiftActivity> shifts = new ArrayList<>();
        Activity breakActivity = null;
        for (int i = 0; i < breakSettings.size(); i++) {
            /**
             * The first eligible break hours after.It specifies you can take first break after this duration
             **/
            breakAllowedAfterMinute = breakSettings.get(i).getShiftDurationInMinute();

            if (shiftDurationInMinute > breakAllowedAfterMinute) {
                if (paid != null) {
                    breakActivity =  breakActivitiesMap.get(paid ?breakSettings.get(i).getPaidActivityId() : breakSettings.get(i).getUnpaidActivityId()).getActivity();
                }
                endDateMillis = startDateMillis + (breakAllowedAfterMinute * ONE_MINUTE);
                shifts.add(getShiftObject(mainShift.getActivities().get(0).getActivityName(), mainShift.getActivities().get(0).getActivityId(), new Date(startDateMillis), new Date(endDateMillis)));
                // we have added a sub shift now adding the break for remaining period
                shiftDurationInMinute = shiftDurationInMinute - ((endDateMillis - startDateMillis) / ONE_MINUTE);
                // if still after subtraction the shift is greater than
                allowedBreakDurationInMinute = breakSettings.get(i).getBreakDurationInMinute();
                startDateMillis = endDateMillis;
                lastItemAdded = SHIFT;
                if (shiftDurationInMinute >= allowedBreakDurationInMinute) {
                    endDateMillis = endDateMillis + (allowedBreakDurationInMinute * ONE_MINUTE);
                    ShiftActivity currentBreakActivity= getShiftObject(breakActivity.getName(), breakActivity.getId(), new Date(startDateMillis), new Date(endDateMillis));
                    currentBreakActivity.setBreakShift(true);
                    shifts.add(currentBreakActivity);


                    shiftDurationInMinute = shiftDurationInMinute - ((endDateMillis - startDateMillis) / ONE_MINUTE);
                    startDateMillis = endDateMillis;
                    totalBreakAllotedInMinute += allowedBreakDurationInMinute;
                    lastItemAdded = BREAK;
                }
            } else {
                break;
            }
        }
        /**
         * still shift is greater than break We need to repeat last break until shift duration is less
         **/
        while (shiftDurationInMinute > breakAllowedAfterMinute && allowedBreakDurationInMinute > 0) {
            // last end date is now start date
            startDateMillis = endDateMillis;
            endDateMillis = startDateMillis + (breakAllowedAfterMinute * ONE_MINUTE);
            shifts.add(getShiftObject(mainShift.getActivities().get(0).getActivityName(), mainShift.getActivities().get(0).getActivityId(), new Date(startDateMillis), new Date(endDateMillis)));
            shiftDurationInMinute = shiftDurationInMinute - breakAllowedAfterMinute;
            lastItemAdded = SHIFT;
            if (shiftDurationInMinute >= allowedBreakDurationInMinute) {
                startDateMillis = endDateMillis;
                endDateMillis = endDateMillis + (allowedBreakDurationInMinute * ONE_MINUTE);
               ShiftActivity currentBreakActivity= getShiftObject(breakActivity.getName(), breakActivity.getId(), new Date(startDateMillis), new Date(endDateMillis));
               currentBreakActivity.setBreakShift(true);
                shifts.add(currentBreakActivity);
                shiftDurationInMinute = shiftDurationInMinute - ((endDateMillis - startDateMillis) / ONE_MINUTE);
                totalBreakAllotedInMinute += allowedBreakDurationInMinute;
                lastItemAdded = BREAK;
            }
        }
        // Sometimes the break is
        if (shiftDurationInMinute > 0 && shiftDurationInMinute <= breakAllowedAfterMinute && SHIFT.equals(lastItemAdded)) {
            // handle later
            startDateMillis = endDateMillis;
            endDateMillis = endDateMillis + (shiftDurationInMinute * ONE_MINUTE);
            totalBreakAllotedInMinute += ((endDateMillis - startDateMillis) / ONE_MINUTE);
            ShiftActivity currentBreakActivity= getShiftObject(breakActivity.getName(), breakActivity.getId(), new Date(startDateMillis), new Date(endDateMillis));
            currentBreakActivity.setBreakShift(true);
            shifts.add(currentBreakActivity);



        } else if (shiftDurationInMinute > 0 && shiftDurationInMinute <= breakAllowedAfterMinute && BREAK.equals(lastItemAdded)) {
            startDateMillis = endDateMillis;
            endDateMillis = startDateMillis + (shiftDurationInMinute * ONE_MINUTE);
            shifts.add(getShiftObject( mainShift.getActivities().get(0).getActivityName(), mainShift.getActivities().get(0).getActivityId(), new Date(startDateMillis), new Date(endDateMillis)));

        }
        return shifts;
    }

    private ShiftActivity getShiftObject(String name, BigInteger activityId, Date startDate, Date endDate) {
        ShiftActivity childShift = new ShiftActivity( name, startDate, endDate, activityId);
        childShift.setStatus(Collections.singleton(ShiftStatus.UNPUBLISHED));
        childShift.setId(mongoSequenceRepository.nextSequence(ShiftActivity.class.getSimpleName()));
        return childShift;

    }

}
