package com.kairos.service.shift;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.DateUtils;
import com.kairos.dto.activity.shift.ShiftActivity;
import com.kairos.dto.activity.shift.StaffUnitPositionDetails;
import com.kairos.enums.shift.BreakPaymentSetting;
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


    public List<ShiftActivity> addBreakInShifts(Map<BigInteger, ActivityWrapper> activityWrapperMap, Shift shift, StaffUnitPositionDetails unitPositionDetails) {
        Long shiftDurationInMinute = new DateTimeInterval(shift.getStartDate(), shift.getEndDate()).getMinutes();
        List<BreakSettings> breakSettings = breakSettingMongoRepository.findAllByDeletedFalseAndUnitIdAndShiftDurationInMinuteLessThanEqualOrderByCreatedAtAsc(shift.getUnitId(), shiftDurationInMinute);
        List<ShiftActivity> breakActivities = new ArrayList<>();
        if (!breakSettings.isEmpty()) {
            Map<BigInteger, Activity> breakActivitiesMap = getBreakActivities(breakSettings);
            boolean paid = Optional.ofNullable(unitPositionDetails.getExpertise().getBreakPaymentSetting()).isPresent() &&
                    BreakPaymentSetting.PAID.equals(unitPositionDetails.getExpertise().getBreakPaymentSetting());
            breakActivities = getBreaks(activityWrapperMap, shift, breakSettings, breakActivitiesMap, paid);
        }
        return breakActivities;
    }

    private List<ShiftActivity> getBreaks(Map<BigInteger, ActivityWrapper> activityWrapperMap, Shift shift, List<BreakSettings> breakSettings, Map<BigInteger, Activity> breakActivitiesMap, Boolean paid) {
        List<ShiftActivity> breakActivities = new ArrayList<>();
        BreakSettings lastBreakSetting = breakSettings.get(breakSettings.size()-1);
        ZonedDateTime shiftStart = DateUtils.asZoneDateTime(shift.getStartDate());
        ZonedDateTime shiftEnd = DateUtils.asZoneDateTime(shift.getEndDate());
        Long shiftDurationInMinute = 0L;
        for (BreakSettings breakSetting : breakSettings) {
            shiftDurationInMinute = new DateTimeInterval(shiftStart, shiftEnd).getMinutes();
            Object[] objects = createBreakActivity(breakSetting,shiftDurationInMinute,breakActivitiesMap,paid,shiftStart,shiftEnd,shift,activityWrapperMap);
            ShiftActivity shiftActivity = (ShiftActivity)objects[0];
            if(shiftActivity!=null) {
                breakActivities.add(shiftActivity);
            }
            shiftStart = (ZonedDateTime) objects[1];
        }
        while (lastBreakSetting.getShiftDurationInMinute() > shiftDurationInMinute){
            shiftDurationInMinute = new DateTimeInterval(shiftStart, shiftEnd).getMinutes();
            Object[] objects = createBreakActivity(lastBreakSetting,shiftDurationInMinute,breakActivitiesMap,paid,shiftStart,shiftEnd,shift,activityWrapperMap);
            ShiftActivity shiftActivity = (ShiftActivity)objects[0];
            if(shiftActivity!=null) {
                breakActivities.add(shiftActivity);
            }
            shiftStart = (ZonedDateTime) objects[1];

        }
        return breakActivities;
    }

    private Object[] createBreakActivity(BreakSettings breakSettings, Long shiftDurationInMinute, Map<BigInteger, Activity> breakActivitiesMap, Boolean paid, ZonedDateTime shiftStart, ZonedDateTime shiftEnd, Shift shift, Map<BigInteger, ActivityWrapper> activityWrapperMap){
        ShiftActivity shiftActivity = null;
        if (breakSettings.getShiftDurationInMinute() < shiftDurationInMinute) {
            Activity breakActivity = breakActivitiesMap.get(paid ? breakSettings.getPaidActivityId() : breakSettings.getUnpaidActivityId());
            ZonedDateTime breakStart = shiftStart.plusMinutes(breakSettings.getShiftDurationInMinute());
            ZonedDateTime breakEnd = breakStart.plusMinutes(breakSettings.getShiftDurationInMinute());
            if (breakEnd.isAfter(shiftEnd)) {
                breakEnd = shiftEnd;
            }
            updateShiftActivityAndScheduledAndDurationMinutes(shift,breakStart,paid,activityWrapperMap,false);
            updateShiftActivityAndScheduledAndDurationMinutes(shift,breakEnd,paid,activityWrapperMap,true);
            shiftActivity =  new ShiftActivity(breakActivity.getName(), DateUtils.asDate(breakStart), DateUtils.asDate(breakEnd), breakActivity.getId());
            shiftActivity.setId(mongoSequenceRepository.nextSequence(ShiftActivity.class.getSimpleName()));
            shiftStart = breakEnd;
        }
        return new Object[]{shiftActivity,shiftStart};
    }

    private void updateShiftActivityAndScheduledAndDurationMinutes(Shift shift, ZonedDateTime breakDateTime, boolean paid, Map<BigInteger, ActivityWrapper> activityWrapperMap,boolean updateStart){
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
    }


    public Map<BigInteger, Activity> getBreakActivities(List<BreakSettings> breakSettings) {
        Set<BigInteger> breakActivityIds = breakSettings.stream().flatMap(a -> Stream.of(a.getPaidActivityId(), a.getUnpaidActivityId())).collect(Collectors.toSet());
        List<Activity> breakActivities = activityRepository.findAllActivitiesByIds(breakActivityIds);
        return breakActivities.stream().collect(Collectors.toMap(key -> key.getId(), value -> value));
    }

}
