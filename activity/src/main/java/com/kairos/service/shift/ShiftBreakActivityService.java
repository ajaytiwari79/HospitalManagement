package com.kairos.service.shift;

import com.kairos.dto.activity.shift.ShiftActivity;
import com.kairos.dto.activity.shift.StaffUnitPositionDetails;
import com.kairos.enums.shift.BreakPaymentSetting;
import com.kairos.enums.shift.ShiftStatus;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.activity.ActivityWrapper;
import com.kairos.persistence.model.break_settings.BreakSettings;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.persistence.repository.break_settings.BreakSettingMongoRepository;
import com.kairos.persistence.repository.common.MongoSequenceRepository;
import com.kairos.service.exception.ExceptionService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
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
    @Inject private ExceptionService exceptionService;



    public Map<BigInteger, ActivityWrapper> getBreakActivities(List<BreakSettings> breakSettings,Long unitId) {
        List<BigInteger> breakActivityIds = breakSettings.stream().map(BreakSettings::getActivityId).collect(Collectors.toList());// These are country activity ids
        List<ActivityWrapper> breakActivities = activityRepository.findActivitiesAndTimeTypeByParentIdsAndUnitId(breakActivityIds,unitId);
        Map<BigInteger, ActivityWrapper> activityWrapperMap=
        breakActivities.stream().collect(Collectors.toMap(key -> key.getActivity().getParentId(), value -> value));  // THis map is used for break
        activityWrapperMap.putAll(breakActivities.stream().collect(Collectors.toMap(key -> key.getActivity().getId(), value -> value))); // this map is used for payOut
        return activityWrapperMap;
    }

    public List<ShiftActivity> addBreakInShifts(Map<BigInteger, ActivityWrapper> activityWrapperMap, Shift mainShift, StaffUnitPositionDetails unitPositionDetails) {
        if (activityWrapperMap.get(mainShift.getActivities().get(0).getActivityId()).getActivity().getRulesActivityTab().isBreakAllowed()) {
            Long shiftDurationInMinute = (mainShift.getEndDate().getTime() - mainShift.getStartDate().getTime()) / ONE_MINUTE;
            List<BreakSettings> breakSettings = breakSettingMongoRepository.findAllByDeletedFalseAndExpertiseIdAndShiftDurationInMinuteLessThanEqualOrderByCreatedAtAsc(unitPositionDetails.getExpertise().getId(), shiftDurationInMinute);
            Map<BigInteger, ActivityWrapper> breakActivitiesMap = getBreakActivities(breakSettings,mainShift.getUnitId());
            activityWrapperMap.putAll(breakActivitiesMap);
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

                ActivityWrapper currentActivity=breakActivitiesMap.get(breakSettings.get(i).getActivityId());
                if (!Optional.ofNullable(currentActivity).isPresent()){
                    exceptionService.dataNotFoundException("error.activity.notAssigned",breakSettings.get(i).getActivityId());
                }
                breakActivity =  currentActivity.getActivity();

                endDateMillis = startDateMillis + (breakAllowedAfterMinute * ONE_MINUTE);
                shifts.add(getShiftObject(mainShift.getActivities().get(0).getActivityName(), mainShift.getActivities().get(0).getActivityId(), new Date(startDateMillis), new Date(endDateMillis),false));
                // we have added a sub shift now adding the break for remaining period
                shiftDurationInMinute = shiftDurationInMinute - ((endDateMillis - startDateMillis) / ONE_MINUTE);
                // if still after subtraction the shift is greater than
                allowedBreakDurationInMinute = breakSettings.get(i).getBreakDurationInMinute();
                startDateMillis = endDateMillis;
                lastItemAdded = SHIFT;
                if (shiftDurationInMinute >= allowedBreakDurationInMinute) {
                    endDateMillis = endDateMillis + (allowedBreakDurationInMinute * ONE_MINUTE);
                    shifts.add(getShiftObject( breakActivity.getName(), breakActivity.getId(), new Date(startDateMillis), new Date(endDateMillis),true));
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
         * This Repeat is disabled for in task KP-5156
         * still shift is greater than break We need to repeat last break until shift duration is less
         *

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
         **/

        // Sometimes the break is
        if (shiftDurationInMinute > 0 && shiftDurationInMinute <= breakAllowedAfterMinute && SHIFT.equals(lastItemAdded)) {
            // handle later
            startDateMillis = endDateMillis;
            endDateMillis = endDateMillis + (shiftDurationInMinute * ONE_MINUTE);
            totalBreakAllotedInMinute += ((endDateMillis - startDateMillis) / ONE_MINUTE);
            shifts.add(getShiftObject(breakActivity.getName(), breakActivity.getId(), new Date(startDateMillis), new Date(endDateMillis),true));
        } else if (shiftDurationInMinute > 0 && shiftDurationInMinute <= breakAllowedAfterMinute && BREAK.equals(lastItemAdded)) {
            startDateMillis = endDateMillis;
            endDateMillis = startDateMillis + (shiftDurationInMinute * ONE_MINUTE);
            shifts.add(getShiftObject( mainShift.getActivities().get(0).getActivityName(), mainShift.getActivities().get(0).getActivityId(), new Date(startDateMillis), new Date(endDateMillis),false));

        }
        return shifts;
    }

    private ShiftActivity getShiftObject(String name, BigInteger activityId, Date startDate, Date endDate,boolean breakShift) {
        ShiftActivity childShift = new ShiftActivity( name, startDate, endDate, activityId,breakShift);
        childShift.setStatus(Collections.singleton(ShiftStatus.UNPUBLISHED));
        childShift.setId(mongoSequenceRepository.nextSequence(ShiftActivity.class.getSimpleName()));
        return childShift;

    }

}
