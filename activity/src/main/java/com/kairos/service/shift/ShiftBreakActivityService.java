package com.kairos.service.shift;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.DateUtils;
import com.kairos.dto.activity.shift.ShiftActivity;
import com.kairos.dto.activity.shift.StaffUnitPositionDetails;
import com.kairos.dto.user.user.staff.StaffAdditionalInfoDTO;
import com.kairos.enums.shift.BreakPaymentSetting;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.activity.ActivityWrapper;
import com.kairos.persistence.model.break_settings.BreakSettings;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.persistence.repository.break_settings.BreakSettingMongoRepository;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.ZonedDateTime;
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


    public List<ShiftActivity> addBreakInShifts(Map<BigInteger, ActivityWrapper> activityWrapperMap, Shift shift, StaffUnitPositionDetails unitPositionDetails) {
        Long shiftDurationInMinute = new DateTimeInterval(shift.getEndDate(), shift.getStartDate()).getMinutes();
        List<BreakSettings> breakSettings = breakSettingMongoRepository.findAllByDeletedFalseAndUnitIdAndShiftDurationInMinuteLessThanEqualOrderByCreatedAtAsc(shift.getUnitId(), shiftDurationInMinute);
        List<ShiftActivity> breaksActivites = new ArrayList<>();
        if (!breakSettings.isEmpty()) {
            Map<BigInteger, Activity> breakActivitiesMap = getBreakActivities(breakSettings);
            boolean paid = Optional.ofNullable(unitPositionDetails.getExpertise().getBreakPaymentSetting()).isPresent() &&
                    BreakPaymentSetting.PAID.equals(unitPositionDetails.getExpertise().getBreakPaymentSetting());
            breaksActivites = getBreaks(activityWrapperMap, shift, breakSettings, breakActivitiesMap, paid);
        }
        return breaksActivites;
    }

    private List<ShiftActivity> getBreaks(Map<BigInteger, ActivityWrapper> activityWrapperMap, Shift shift, List<BreakSettings> breakSettings, Map<BigInteger, Activity> breakActivitiesMap, Boolean paid) {
        List<ShiftActivity> breakActvities = new ArrayList<>();
        BreakSettings firstBreakSetting = breakSettings.get(0);
        ZonedDateTime shiftStart = DateUtils.asZoneDateTime(shift.getStartDate());
        ZonedDateTime shiftEnd = DateUtils.asZoneDateTime(shift.getEndDate());
        Long shiftDurationInMinute = 0l;
        for (BreakSettings breakSetting : breakSettings) {
            shiftDurationInMinute = new DateTimeInterval(shiftStart, shiftEnd).getMinutes();
            Object[] objects = createBreakActvitiy(breakSetting,shiftDurationInMinute,breakActivitiesMap,paid,shiftStart,shiftEnd,shift,activityWrapperMap);
            ShiftActivity shiftActivity = (ShiftActivity)objects[0];
            if(shiftActivity!=null) {
                breakActvities.add(shiftActivity);
            }
            shiftStart = (ZonedDateTime) objects[1];
        }
        while (firstBreakSetting.getShiftDurationInMinute() > shiftDurationInMinute){
            shiftDurationInMinute = new DateTimeInterval(shiftStart, shiftEnd).getMinutes();
            Object[] objects = createBreakActvitiy(firstBreakSetting,shiftDurationInMinute,breakActivitiesMap,paid,shiftStart,shiftEnd,shift,activityWrapperMap);
            ShiftActivity shiftActivity = (ShiftActivity)objects[0];
            if(shiftActivity!=null) {
                breakActvities.add(shiftActivity);
            }
            shiftStart = (ZonedDateTime) objects[1];
        }
        return breakActvities;
    }

    private Object[] createBreakActvitiy(BreakSettings breakSettings,Long shiftDurationInMinute,Map<BigInteger, Activity> breakActivitiesMap,Boolean paid,ZonedDateTime shiftStart,ZonedDateTime shiftEnd,Shift shift,Map<BigInteger, ActivityWrapper> activityWrapperMap){
        ShiftActivity shiftActivity = null;
        if (breakSettings.getShiftDurationInMinute() > shiftDurationInMinute) {
            Activity breakActivity = breakActivitiesMap.get(paid ? breakSettings.getPaidActivityId() : breakSettings.getUnpaidActivityId());
            ZonedDateTime breakStart = shiftStart.plusMinutes(breakSettings.getShiftDurationInMinute());
            ZonedDateTime breakEnd = breakStart.plusMinutes(breakSettings.getShiftDurationInMinute());
            if (breakEnd.isAfter(shiftEnd)) {
                breakEnd = shiftEnd;
            }
            ShiftActivity overLappedActivitiyOfShift = getOverLappedActivitiyOfShift(shift, breakStart);
            if (activityWrapperMap.get(overLappedActivitiyOfShift.getActivityId()).getActivity().getRulesActivityTab().isBreakAllowed()) {
                overLappedActivitiyOfShift.setEndDate(DateUtils.asDate(breakStart));
            }
            overLappedActivitiyOfShift = getOverLappedActivitiyOfShift(shift, breakEnd);
            if (activityWrapperMap.get(overLappedActivitiyOfShift.getActivityId()).getActivity().getRulesActivityTab().isBreakAllowed()) {
                overLappedActivitiyOfShift.setStartDate(DateUtils.asDate(breakStart));
            }
            shiftActivity =  new ShiftActivity(breakActivity.getName(), DateUtils.asDate(breakStart), DateUtils.asDate(breakEnd), breakActivity.getId());
            shiftStart = breakEnd;
        }
        return new Object[]{shiftActivity,shiftStart};
    }


    private ShiftActivity getOverLappedActivitiyOfShift(Shift shift, ZonedDateTime breakDateTime) {
        ShiftActivity overLappedActivitiyOfShift = null;
        for (ShiftActivity shiftActivity : shift.getActivities()) {
            DateTimeInterval shiftInterval = new DateTimeInterval(shiftActivity.getStartDate().getTime(), shiftActivity.getEndDate().getTime());
            if (shiftInterval.contains(breakDateTime)) {
                overLappedActivitiyOfShift = shiftActivity;
                break;
            }
        }
        return overLappedActivitiyOfShift;
    }


    public Map<BigInteger, Activity> getBreakActivities(List<BreakSettings> breakSettings) {
        Set<BigInteger> breakActivityIds = breakSettings.stream().flatMap(a -> Stream.of(a.getPaidActivityId(), a.getUnpaidActivityId())).collect(Collectors.toSet());
        List<Activity> breakActivities = activityRepository.findAllActivitiesByIds(breakActivityIds);
        return breakActivities.stream().collect(Collectors.toMap(k -> k.getId(), v -> v));
    }

}
