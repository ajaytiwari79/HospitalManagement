package com.kairos.service.shift;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.dto.activity.break_settings.BreakSettingsDTO;
import com.kairos.dto.activity.wta.templates.BreakAvailabilitySettings;
import com.kairos.dto.user.country.time_slot.TimeSlotWrapper;
import com.kairos.dto.user.user.staff.StaffAdditionalInfoDTO;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.activity.ActivityWrapper;
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

import static com.kairos.commons.utils.DateUtils.asDate;
import static com.kairos.commons.utils.DateUtils.asZoneDateTime;
import static com.kairos.commons.utils.ObjectUtils.newArrayList;

/**
 * @author pradeep
 * @date - 18/9/18
 */
@Service
public class ShiftBreakService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ShiftBreakService.class);
    @Inject
    private BreakSettingMongoRepository breakSettingMongoRepository;
    @Inject
    private ActivityMongoRepository activityRepository;
    @Inject
    private ExceptionService exceptionService;
    @Inject private ShiftService shiftService;


    public Map<BigInteger, ActivityWrapper> getBreakActivities(BreakSettingsDTO breakSetting, Long unitId) {
        List<ActivityWrapper> breakActivities = activityRepository.findActivitiesAndTimeTypeByParentIdsAndUnitId(newArrayList(breakSetting.getActivityId()), unitId);
        Map<BigInteger, ActivityWrapper> activityWrapperMap =
                breakActivities.stream().collect(Collectors.toMap(key -> key.getActivity().getCountryParentId(), value -> value));  // This map is used for break
        activityWrapperMap.putAll(breakActivities.stream().collect(Collectors.toMap(key -> key.getActivity().getId(), value -> value))); // this map is used for payOut
        return activityWrapperMap;
    }


    public ShiftActivity updateBreakInShift(Shift shift, Map<BigInteger, ActivityWrapper> activityWrapperMap, StaffAdditionalInfoDTO  staffAdditionalInfoDTO, BreakWTATemplate breakWTATemplate, List<TimeSlotWrapper> timeSlot) {
        /*BreakSettingsDTO breakSetting = breakSettingMongoRepository.findAllByDeletedFalseAndExpertiseIdOrderByCreatedAtAsc(staffAdditionalInfoDTO.getEmployment().getExpertise().getId()).get(0);
        activityWrapperMap.putAll(getBreakActivities(breakSetting, shift.getUnitId()));
        boolean placeBreakAnyWhereInShift = true;
        ShiftActivity breakActivity = null;
        DateTimeInterval eligibleBreakInterval;
        Date placeBreakAfterThisDate = shift.getStartDate();
        if(isNotNull(breakWTATemplate)){
            BreakAvailabilitySettings breakAvailabilitySettings = findCurrentBreakAvailability(shift.getStartDate(),timeSlot,breakWTATemplate);
            placeBreakAnyWhereInShift = (breakAvailabilitySettings.getStartAfterMinutes()+breakAvailabilitySettings.getEndBeforeMinutes()) >= shift.getMinutes();
            eligibleBreakInterval = placeBreakAnyWhereInShift ? null : getBreakInterval(shift,breakAvailabilitySettings);
            placeBreakAnyWhereInShift = placeBreakAnyWhereInShift ? placeBreakAnyWhereInShift : eligibleBreakInterval.getMinutes() < breakSetting.getBreakDurationInMinute();
            placeBreakAfterThisDate = asDate(asZoneDateTime(shift.getStartDate()).plusMinutes(shift.getInterval().getMinutes()*breakAvailabilitySettings.getShiftPercentage()/100));
        }
        for (ShiftActivity shiftActivity : shift.getActivities()) {
            if(isCollectionNotEmpty(shiftActivity.getChildActivities())) {
                for (ShiftActivity childActivity : shiftActivity.getChildActivities()) {
                    breakActivity = getBreakActivityAfterCalculation(activityWrapperMap, breakSetting, placeBreakAnyWhereInShift, placeBreakAfterThisDate, childActivity,staffAdditionalInfoDTO);
                }
            }else {
                breakActivity = getBreakActivityAfterCalculation(activityWrapperMap, breakSetting, placeBreakAnyWhereInShift, placeBreakAfterThisDate, shiftActivity,staffAdditionalInfoDTO);
            }
        }
        if(isNull(breakActivity)){
            Date breakEndDate = asDate(asZoneDateTime(placeBreakAfterThisDate).plusMinutes(breakSetting.getBreakDurationInMinute()));
            breakActivity = buildBreakActivity(placeBreakAfterThisDate,breakEndDate,breakSetting,staffAdditionalInfoDTO,activityWrapperMap);
            breakActivity.setBreakNotHeld(true);
        }
        return breakActivity;*/
        return null;
    }

    private ShiftActivity getBreakActivityAfterCalculation(Map<BigInteger, ActivityWrapper> activityWrapperMap, BreakSettingsDTO breakSetting, boolean placeBreakAnyWhereInShift, Date placeBreakAfterThisDate, ShiftActivity childActivity,StaffAdditionalInfoDTO staffAdditionalInfoDTO) {
        ShiftActivity breakActivity = null;
        Activity activity = activityWrapperMap.get(childActivity.getActivityId()).getActivity();
        boolean breakAllowed = activity.getRulesActivityTab().isBreakAllowed();
        if(breakAllowed){
            boolean breakCanbePlace = childActivity.getEndDate().after(placeBreakAfterThisDate);
            breakCanbePlace = breakCanbePlace ? new DateTimeInterval(childActivity.getStartDate().after(placeBreakAfterThisDate) ? childActivity.getStartDate() : placeBreakAfterThisDate,childActivity.getEndDate()).getMinutes()>breakSetting.getBreakDurationInMinute() : breakCanbePlace;
            if(breakCanbePlace && placeBreakAnyWhereInShift){
                Date startDate = childActivity.getStartDate().after(placeBreakAfterThisDate) ? childActivity.getStartDate() : placeBreakAfterThisDate;
                Date endDate = asDate(asZoneDateTime(startDate).plusMinutes(breakSetting.getBreakDurationInMinute()));
                if(endDate.after(childActivity.getEndDate())){
                    endDate = childActivity.getEndDate();
                    startDate = asDate(asZoneDateTime(endDate).minusMinutes(breakSetting.getBreakDurationInMinute()));
                }
                breakActivity = buildBreakActivity(startDate,endDate,breakSetting,staffAdditionalInfoDTO,activityWrapperMap);
            }
        }
        return breakActivity;
    }

    private ShiftActivity buildBreakActivity(Date startDate,Date endDate,BreakSettingsDTO breakSettingsDTO,StaffAdditionalInfoDTO staffAdditionalInfoDTO,Map<BigInteger, ActivityWrapper> activityWrapperMap){
        ActivityWrapper activityWrapper = activityWrapperMap.get(breakSettingsDTO.getActivityId());
        ShiftActivity shiftActivity = new ShiftActivity(activityWrapper.getActivity().getName(),startDate,endDate,activityWrapper.getActivity().getId(),activityWrapper.getTimeType());
        shiftService.updateActivityDetailsInShiftActivity(shiftActivity,activityWrapperMap,staffAdditionalInfoDTO);
        return shiftActivity;
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