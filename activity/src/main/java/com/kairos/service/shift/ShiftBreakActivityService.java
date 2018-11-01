package com.kairos.service.shift;

import com.kairos.commons.utils.DateUtils;
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

import static com.kairos.constants.AppConstants.BREAK;
import static com.kairos.constants.AppConstants.SHIFT;
import static javax.management.timer.Timer.ONE_MINUTE;

/**
 * @author pradeep
 * @date - 18/9/18
 */
@Service
public class ShiftBreakActivityService {
    private static  final Logger logger=LoggerFactory.getLogger(ShiftBreakActivityService.class);
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
        if (activityWrapperMap.get(mainShift.getActivities().get(0).getActivityId()).getActivity().getRulesActivityTab().isBreakAllowed()) {
            Long shiftDurationInMinute = (mainShift.getEndDate().getTime() - mainShift.getStartDate().getTime()) / ONE_MINUTE;
            List<BreakSettings> breakSettings = breakSettingMongoRepository.findAllByDeletedFalseAndExpertiseIdAndShiftDurationInMinuteLessThanEqualOrderByCreatedAtAsc(unitPositionDetails.getExpertise().getId(), shiftDurationInMinute);
            Map<BigInteger, ActivityWrapper> breakActivitiesMap = getBreakActivities(breakSettings, mainShift.getUnitId());
            activityWrapperMap.putAll(breakActivitiesMap);
            if (Optional.ofNullable(breakSettings).isPresent() && breakSettings.size() > 0) {
                activityWrapperMap.putAll(breakActivitiesMap);
                return addBreakInShifts(mainShift, breakSettings, shiftDurationInMinute, breakActivitiesMap, breakWTATemplate, timeSlot);
            }
        }
        return Collections.emptyList();

    }

    private BreakAvailabilitySettings findCurrentBreakAvailability(Date startDate, List<TimeSlotWrapper> timeSlots, BreakWTATemplate breakWTATemplate) {
        BreakAvailabilitySettings breakAvailabilitySettings=null;
        TimeSlotWrapper currentTimeSlot =   timeSlots.stream().filter(current -> (current.getStartHour() < startDate.getHours() && current.getEndHour() > startDate.getHours())).findFirst().orElse(null);
        if (currentTimeSlot!=null && breakWTATemplate !=null && !breakWTATemplate.isDisabled()){
            breakAvailabilitySettings= breakWTATemplate.getBreakAvailability().stream().filter(currentAvailability->(currentAvailability.getTimeSlot().toString().equalsIgnoreCase(currentTimeSlot.getName()))).findFirst().get();
        }
        return breakAvailabilitySettings;
    }

    private List<ShiftActivity> addBreakInShifts(Shift mainShift, List<BreakSettings> breakSettings, Long shiftDurationInMinute, Map<BigInteger, ActivityWrapper> breakActivitiesMap, BreakWTATemplate breakWTATemplate, List<TimeSlotWrapper> timeSlot) {
        Long startDateMillis = mainShift.getStartDate().getTime();
        Long endDateMillis = null;
        Long breakAllowedWithShiftMinute = 0L;
        Long allowedBreakDurationInMinute = 0L;
        Long workedShiftDuration = 0L;
        Long remainingShiftDuration=0L;
        Long estimatedShiftEnd=0L;
        Long completeShiftEnd=0L;
        String lastItemAdded = null;
        List<ShiftActivity> shifts = new ArrayList<>();
        Activity breakActivity = null;

        for (int i = 0; i < breakSettings.size(); i++) {
            /**
             * The first eligible break hours after.It specifies you can take first break when your shift duration is equals or greater than  this duration
             **/
            breakAllowedWithShiftMinute = breakSettings.get(i).getShiftDurationInMinute();
            allowedBreakDurationInMinute = breakSettings.get(i).getBreakDurationInMinute();

            if (shiftDurationInMinute >= breakAllowedWithShiftMinute) {

                BreakAvailabilitySettings breakAvailability = findCurrentBreakAvailability(mainShift.getActivities().get(0).getStartDate(), timeSlot,breakWTATemplate);
                if (breakAvailability!=null){
                    workedShiftDuration=workedShiftDuration+breakAvailability.getStartAfterMinutes();
                    remainingShiftDuration=shiftDurationInMinute-workedShiftDuration;
                    if (remainingShiftDuration>=breakAvailability.getEndBeforeMinutes()){
                        // add shift and break both
                        endDateMillis = startDateMillis + (workedShiftDuration * ONE_MINUTE);
                        shifts.add(getShiftObject(mainShift.getActivities().get(0).getActivityName(), mainShift.getActivities().get(0).getActivityId(), new Date(startDateMillis), new Date(endDateMillis), false));
                        // we have added a sub shift now adding the break for remaining period
                        shiftDurationInMinute = remainingShiftDuration;
                        // if still after subtraction the shift is greater than

                            ActivityWrapper currentActivity = breakActivitiesMap.get(breakSettings.get(i).getActivityId());
                            if (!Optional.ofNullable(currentActivity).isPresent()) {
                                exceptionService.dataNotFoundException("error.activity.notAssigned", breakSettings.get(i).getActivityId());
                            }

                            breakActivity = currentActivity.getActivity();
                            startDateMillis = endDateMillis;  // setting previous end as new start
                            endDateMillis = endDateMillis + (allowedBreakDurationInMinute * ONE_MINUTE);
                            shifts.add(getShiftObject(breakActivity.getName(), breakActivity.getId(), new Date(startDateMillis), new Date(endDateMillis), true));
                            shiftDurationInMinute = shiftDurationInMinute - allowedBreakDurationInMinute;
                            startDateMillis = endDateMillis;
                            lastItemAdded = BREAK;

                    }else{
                        // add only shift
                        endDateMillis = mainShift.getEndDate().getTime();
                        shifts.add(getShiftObject(mainShift.getActivities().get(0).getActivityName(), mainShift.getActivities().get(0).getActivityId(), new Date(startDateMillis), new Date(endDateMillis), false));
                        // we have added a sub shift now adding the break for remaining period
                        shiftDurationInMinute = shiftDurationInMinute - ((endDateMillis - startDateMillis) / ONE_MINUTE);
                        // if still after subtraction the shift is greater than
                        lastItemAdded = SHIFT;
                    }

                }else{
                    // no break settings add only shift

                }

            } else {
                break;
            }
        }

        // Sometimes the break is greater than your shift
        if (shiftDurationInMinute > 0 && shiftDurationInMinute <= breakAllowedWithShiftMinute && SHIFT.equals(lastItemAdded)) {
            // handle later
            startDateMillis = endDateMillis;
            endDateMillis = endDateMillis + (shiftDurationInMinute * ONE_MINUTE);

            shifts.add(getShiftObject(breakActivity.getName(), breakActivity.getId(), new Date(startDateMillis), new Date(endDateMillis), true));
        } else if (shiftDurationInMinute > 0 && shiftDurationInMinute <= breakAllowedWithShiftMinute && BREAK.equals(lastItemAdded)) {
            startDateMillis = endDateMillis;
            endDateMillis = startDateMillis + (shiftDurationInMinute * ONE_MINUTE);
            shifts.add(getShiftObject(mainShift.getActivities().get(0).getActivityName(), mainShift.getActivities().get(0).getActivityId(), new Date(startDateMillis), new Date(endDateMillis), false));

        }
        return shifts;
    }

    private ShiftActivity getShiftObject(String name, BigInteger activityId, Date startDate, Date endDate, boolean breakShift) {
        ShiftActivity childShift = new ShiftActivity(name, startDate, endDate, activityId, breakShift);
        childShift.setStatus(Collections.singleton(ShiftStatus.UNPUBLISHED));
        childShift.setId(mongoSequenceRepository.nextSequence(ShiftActivity.class.getSimpleName()));
        return childShift;

    }

}
