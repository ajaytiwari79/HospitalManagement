package com.kairos.service.shift;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.TimeInterval;
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
import com.kairos.persistence.repository.common.MongoSequenceRepository;
import com.kairos.service.exception.ExceptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.asDate;
import static com.kairos.commons.utils.DateUtils.asZoneDateTime;
import static com.kairos.commons.utils.ObjectUtils.*;
import static com.kairos.constants.ActivityMessagesConstants.*;
import static com.kairos.constants.AppConstants.ONE_HOUR_MINUTES;

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
    @Inject private MongoSequenceRepository mongoSequenceRepository;


    public Map<BigInteger, ActivityWrapper> getBreakActivities(BreakSettingsDTO breakSetting, Long unitId) {
        List<ActivityWrapper> breakActivities = activityRepository.findActivitiesAndTimeTypeByParentIdsAndUnitId(newArrayList(breakSetting.getActivityId()), unitId);
        if(isCollectionEmpty(breakActivities) || breakActivities.size() > 1){
            exceptionService.dataNotFoundException(ERROR_BREAKSACTIVITY_NOT_CONFIGURED,unitId);
        }
        Map<BigInteger, ActivityWrapper> activityWrapperMap =
                breakActivities.stream().collect(Collectors.toMap(key -> key.getActivity().getCountryParentId(), value -> value));  // This map is used for break
        activityWrapperMap.putAll(breakActivities.stream().collect(Collectors.toMap(key -> key.getActivity().getId(), value -> value))); // this map is used for payOut
        return activityWrapperMap;
    }


    public List<ShiftActivity> updateBreakInShift(Shift shift, Map<BigInteger, ActivityWrapper> activityWrapperMap, StaffAdditionalInfoDTO  staffAdditionalInfoDTO, BreakWTATemplate breakWTATemplate, List<TimeSlotWrapper> timeSlot) {
        List<BreakSettingsDTO> breakSettings = breakSettingMongoRepository.findAllByDeletedFalseAndExpertiseIdOrderByCreatedAtAsc(staffAdditionalInfoDTO.getEmployment().getExpertise().getId());
        List<ShiftActivity> breakActivities = new ArrayList<>();
        if(isCollectionNotEmpty(breakSettings)) {
            BreakSettingsDTO breakSetting = breakSettings.get(0);
            activityWrapperMap.putAll(getBreakActivities(breakSetting, shift.getUnitId()));
            boolean placeBreakAnyWhereInShift = true;
            ShiftActivity breakActivity = null;
            DateTimeInterval eligibleBreakInterval = new DateTimeInterval(shift.getStartDate(), shift.getEndDate());
            Date placeBreakAfterThisDate = shift.getStartDate();
            if (isNotNull(breakSetting) && shift.getMinutes() >= breakSetting.getShiftDurationInMinute()) {
                if (isCollectionEmpty(shift.getBreakActivities())) {
                    if (isNotNull(breakWTATemplate)) {
                        BreakAvailabilitySettings breakAvailabilitySettings = findCurrentBreakAvailability(shift.getStartDate(), timeSlot, breakWTATemplate);
                        if (isNotNull(breakAvailabilitySettings) && (breakAvailabilitySettings.getShiftPercentage() == 0 || breakAvailabilitySettings.getShiftPercentage() == 100)) {
                            exceptionService.actionNotPermittedException(SHIFT_PERCENTAGE_IN_BREAK_RULETEMPLATE, breakAvailabilitySettings.getShiftPercentage());
                        }
                        placeBreakAnyWhereInShift = (breakAvailabilitySettings.getStartAfterMinutes() + breakAvailabilitySettings.getEndBeforeMinutes()) >= shift.getMinutes();
                        eligibleBreakInterval = placeBreakAnyWhereInShift ? null : getBreakInterval(shift, breakAvailabilitySettings);
                        placeBreakAnyWhereInShift = placeBreakAnyWhereInShift ? placeBreakAnyWhereInShift : eligibleBreakInterval.getMinutes() < breakSetting.getBreakDurationInMinute();
                        placeBreakAfterThisDate = isNotNull(eligibleBreakInterval) ? eligibleBreakInterval.getStartDate() : placeBreakAfterThisDate;
                    }
                    breakActivity = getBreakByShiftActivity(shift, activityWrapperMap, staffAdditionalInfoDTO, breakSetting, placeBreakAnyWhereInShift, breakActivity, placeBreakAfterThisDate);
                    if (isNull(breakActivity)) {
                        Date breakEndDate = asDate(asZoneDateTime(placeBreakAfterThisDate).plusMinutes(breakSetting.getBreakDurationInMinute()));
                        breakActivity = buildBreakActivity(placeBreakAfterThisDate, breakEndDate, breakSetting, staffAdditionalInfoDTO, activityWrapperMap);
                        breakActivity.setBreakNotHeld(true);
                    }
                } else {
                    breakActivity = validateBreakOnUpdateShift(shift, eligibleBreakInterval, placeBreakAfterThisDate);
                }
                if (isNotNull(breakActivity)) {
                    if (breakActivity.getId() == null) {
                        breakActivity.setId(mongoSequenceRepository.nextSequence(ShiftActivity.class.getSimpleName()));
                    }
                    breakActivities.add(breakActivity);
                }
            }
        }
        return breakActivities;
    }

    private ShiftActivity validateBreakOnUpdateShift(Shift shift, DateTimeInterval eligibleBreakInterval, Date placeBreakAfterThisDate) {
        ShiftActivity breakActivity = shift.getBreakActivities().get(0);
        Date startdate = placeBreakAfterThisDate.after(eligibleBreakInterval.getStartDate()) ? eligibleBreakInterval.getStartDate() : placeBreakAfterThisDate;
        Date endDate = placeBreakAfterThisDate.after(eligibleBreakInterval.getEndDate()) ? placeBreakAfterThisDate : eligibleBreakInterval.getEndDate();
        eligibleBreakInterval = new DateTimeInterval(startdate,endDate);
        if(!eligibleBreakInterval.containsInterval(breakActivity.getInterval())){
            exceptionService.actionNotPermittedException(BREAK_NOT_VALID);
        }
        return breakActivity;
    }

    private ShiftActivity getBreakByShiftActivity(Shift shift, Map<BigInteger, ActivityWrapper> activityWrapperMap, StaffAdditionalInfoDTO staffAdditionalInfoDTO, BreakSettingsDTO breakSetting, boolean placeBreakAnyWhereInShift, ShiftActivity breakActivity, Date placeBreakAfterThisDate) {
        for (ShiftActivity shiftActivity : shift.getActivities()) {
            if (isCollectionNotEmpty(shiftActivity.getChildActivities())) {
                for (ShiftActivity childActivity : shiftActivity.getChildActivities()) {
                    breakActivity = getBreakActivityAfterCalculation(activityWrapperMap, breakSetting, placeBreakAnyWhereInShift, placeBreakAfterThisDate, childActivity, staffAdditionalInfoDTO);
                }
            } else {
                breakActivity = getBreakActivityAfterCalculation(activityWrapperMap, breakSetting, placeBreakAnyWhereInShift, placeBreakAfterThisDate, shiftActivity, staffAdditionalInfoDTO);
            }
        }
        return breakActivity;
    }

    private ShiftActivity getBreakActivityAfterCalculation(Map<BigInteger, ActivityWrapper> activityWrapperMap, BreakSettingsDTO breakSetting, boolean placeBreakAnyWhereInShift, Date placeBreakAfterThisDate, ShiftActivity shiftActivity,StaffAdditionalInfoDTO staffAdditionalInfoDTO) {
        ShiftActivity breakActivity = null;
        Activity activity = activityWrapperMap.get(shiftActivity.getActivityId()).getActivity();
        boolean breakAllowed = activity.getRulesActivityTab().isBreakAllowed();
        if(breakAllowed){
            boolean breakCanbePlace = shiftActivity.getEndDate().after(placeBreakAfterThisDate);
            breakCanbePlace = breakCanbePlace ? new DateTimeInterval(shiftActivity.getStartDate().after(placeBreakAfterThisDate) ? shiftActivity.getStartDate() : placeBreakAfterThisDate,shiftActivity.getEndDate()).getMinutes()>breakSetting.getBreakDurationInMinute() : breakCanbePlace;
            if(breakCanbePlace && placeBreakAnyWhereInShift){
                Date startDate = shiftActivity.getStartDate().after(placeBreakAfterThisDate) ? shiftActivity.getStartDate() : placeBreakAfterThisDate;
                Date endDate = asDate(asZoneDateTime(startDate).plusMinutes(breakSetting.getBreakDurationInMinute()));
                if(endDate.after(shiftActivity.getEndDate())){
                    endDate = shiftActivity.getEndDate();
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
        ZonedDateTime endDate = asZoneDateTime(shift.getEndDate()).minusMinutes(breakAvailabilitySettings.getEndBeforeMinutes());
        ZonedDateTime startDateWithShiftPercentage = asZoneDateTime(shift.getStartDate()).plusMinutes(shift.getMinutes() * breakAvailabilitySettings.getShiftPercentage() / 100);
        startDate = startDate.isAfter(startDateWithShiftPercentage) ? startDate : startDateWithShiftPercentage;
        return new DateTimeInterval(startDate,endDate);
    }

    private BreakAvailabilitySettings findCurrentBreakAvailability(Date startDate, List<TimeSlotWrapper> timeSlots, BreakWTATemplate breakWTATemplate) {
        BreakAvailabilitySettings breakAvailabilitySettings = null;
        TimeSlotWrapper currentTimeSlot = timeSlots.stream().filter(current -> new TimeInterval((current.getStartHour()*ONE_HOUR_MINUTES)+current.getStartMinute(),(current.getEndHour()*ONE_HOUR_MINUTES)+current.getEndMinute()-1).contains(asZoneDateTime(startDate).get(ChronoField.MINUTE_OF_DAY))).findFirst().orElse(null);
        if (currentTimeSlot != null && breakWTATemplate != null && !breakWTATemplate.isDisabled()) {
            breakAvailabilitySettings = breakWTATemplate.getBreakAvailability().stream().filter(currentAvailability -> (currentAvailability.getTimeSlot().toString().equalsIgnoreCase(currentTimeSlot.getName()))).findFirst().get();
        }
        return breakAvailabilitySettings;
    }


}