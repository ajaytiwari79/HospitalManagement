package com.kairos.service.shift;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.DateUtils;
import com.kairos.dto.activity.break_settings.BreakSettingsDTO;
import com.kairos.dto.activity.shift.StaffEmploymentDetails;
import com.kairos.dto.activity.wta.templates.BreakAvailabilitySettings;
import com.kairos.dto.user.country.time_slot.TimeSlotWrapper;
import com.kairos.dto.user.user.staff.StaffAdditionalInfoDTO;
import com.kairos.enums.shift.ShiftStatus;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.activity.ActivityWrapper;
import com.kairos.persistence.model.break_settings.BreakSettings;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.model.shift.ShiftActivity;
import com.kairos.persistence.model.wta.templates.template_types.BreakWTATemplate;
import com.kairos.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.persistence.repository.break_settings.BreakSettingMongoRepository;
import com.kairos.service.exception.ExceptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.asZoneDateTime;
import static com.kairos.commons.utils.ObjectUtils.isCollectionNotEmpty;
import static com.kairos.commons.utils.ObjectUtils.isNotNull;
import static com.kairos.constants.ActivityMessagesConstants.ERROR_ACTIVITY_NOTASSIGNED;
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


    public Map<BigInteger, ActivityWrapper> getBreakActivities(List<BreakSettingsDTO> breakSettings, Long unitId) {
        List<BigInteger> breakActivityIds = breakSettings.stream().map(BreakSettingsDTO::getActivityId).collect(Collectors.toList());// These are country activity ids
        List<ActivityWrapper> breakActivities = activityRepository.findActivitiesAndTimeTypeByParentIdsAndUnitId(breakActivityIds, unitId);
        Map<BigInteger, ActivityWrapper> activityWrapperMap =
                breakActivities.stream().collect(Collectors.toMap(key -> key.getActivity().getCountryParentId(), value -> value));  // This map is used for break
        activityWrapperMap.putAll(breakActivities.stream().collect(Collectors.toMap(key -> key.getActivity().getId(), value -> value))); // this map is used for payOut
        return activityWrapperMap;
    }


    public ShiftActivity updateBreakInShift(Shift shift, Map<BigInteger, ActivityWrapper> activityWrapperMap, Long  expertiseId, BreakWTATemplate breakWTATemplate, List<TimeSlotWrapper> timeSlot) {
        List<BreakSettingsDTO> breakSettings = breakSettingMongoRepository.findAllByDeletedFalseAndExpertiseIdOrderByCreatedAtAsc(expertiseId);
        activityWrapperMap.putAll(getBreakActivities(breakSettings, shift.getUnitId()));
        boolean placeBreakAnyWhereInShift = true;
        ShiftActivity breakActivity;
        DateTimeInterval eligibleBreakInterval;
        if(isNotNull(breakWTATemplate)){
            BreakAvailabilitySettings breakAvailabilitySettings = findCurrentBreakAvailability(shift.getStartDate(),timeSlot,breakWTATemplate);
            placeBreakAnyWhereInShift = (breakAvailabilitySettings.getStartAfterMinutes()+breakAvailabilitySettings.getEndBeforeMinutes()) >= shift.getMinutes();
            eligibleBreakInterval = placeBreakAnyWhereInShift ? null : getBreakInterval(shift,breakAvailabilitySettings);
            placeBreakAnyWhereInShift = eligibleBreakInterval.getMinutes()<breakSettings.get(0).getBreakDurationInMinute();
        }
        for (ShiftActivity shiftActivity : shift.getActivities()) {
            Activity activity = activityWrapperMap.get(shiftActivity.getActivityId()).getActivity();
            boolean breakAllowed = activity.getRulesActivityTab().isBreakAllowed();
            if(breakAllowed) {
                for (ShiftActivity childActivity : shiftActivity.getChildActivities()) {
                    activity = activityWrapperMap.get(shiftActivity.getActivityId()).getActivity();
                    breakAllowed = activity.getRulesActivityTab().isBreakAllowed();
                }
            }
        }
        return null;//breakActivities.get(0);
    }

    private DateTimeInterval getBreakInterval(Shift shift,BreakAvailabilitySettings breakAvailabilitySettings){
        ZonedDateTime startDate = asZoneDateTime(shift.getStartDate()).plusMinutes(breakAvailabilitySettings.getStartAfterMinutes());
        ZonedDateTime endDate = asZoneDateTime(shift.getStartDate()).minusMinutes(breakAvailabilitySettings.getEndBeforeMinutes());
        return new DateTimeInterval(startDate,endDate);
    }

    private BreakAvailabilitySettings findCurrentBreakAvailability(Date startDate, List<TimeSlotWrapper> timeSlots, BreakWTATemplate breakWTATemplate) {
        BreakAvailabilitySettings breakAvailabilitySettings = null;
        TimeSlotWrapper currentTimeSlot = timeSlots.stream().filter(current -> (current.getStartHour() < startDate.getHours() && current.getEndHour() > startDate.getHours())).findFirst().orElse(null);
        if (currentTimeSlot != null && breakWTATemplate != null && !breakWTATemplate.isDisabled()) {
            breakAvailabilitySettings = breakWTATemplate.getBreakAvailability().stream().filter(currentAvailability -> (currentAvailability.getTimeSlot().toString().equalsIgnoreCase(currentTimeSlot.getName()))).findFirst().get();
        }
        return breakAvailabilitySettings;
    }


}