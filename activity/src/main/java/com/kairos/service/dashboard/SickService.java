package com.kairos.service.dashboard;

import com.kairos.commons.utils.DateUtils;
import com.kairos.dto.activity.activity.ActivityDTO;
import com.kairos.dto.activity.dashboard.UserSickDataWrapper;
import com.kairos.dto.activity.shift.ShiftDTO;
import com.kairos.dto.activity.tags.TagDTO;
import com.kairos.dto.user.user.staff.StaffAdditionalInfoDTO;
import com.kairos.enums.EmploymentSubType;
import com.kairos.persistence.model.activity.ActivityWrapper;
import com.kairos.persistence.model.activity.tabs.rules_activity_tab.SicknessSetting;
import com.kairos.persistence.model.common.MongoBaseEntity;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.persistence.repository.attendence_setting.SickSettingsRepository;
import com.kairos.persistence.repository.period.PlanningPeriodMongoRepository;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.persistence.repository.time_type.TimeTypeMongoRepository;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.shift.ShiftSickService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.asDate;
import static com.kairos.commons.utils.ObjectUtils.*;
import static com.kairos.constants.ActivityMessagesConstants.*;

/**
 * CreatedBy vipulpandey on 30/8/18
 **/
@Service
@Transactional
public class SickService {
    private static final Logger logger = LoggerFactory.getLogger(SickService.class);
    @Inject
    private UserIntegrationService userIntegrationService;
    @Inject
    private ExceptionService exceptionService;
    @Inject
    private ActivityMongoRepository activityMongoRepository;
    @Inject
    private SickSettingsRepository sickSettingsRepository;
    @Inject
    private ShiftSickService shiftSickService;
    @Inject
    private ShiftMongoRepository shiftMongoRepository;
    @Inject
    private PlanningPeriodMongoRepository planningPeriodMongoRepository;
    @Inject private TimeTypeMongoRepository timeTypeMongoRepository;

    public UserSickDataWrapper getDefaultDataOnUserSick(Long unitId) {
        UserSickDataWrapper userSickDataWrapper = new UserSickDataWrapper();
        Set<BigInteger> sickTimeTypeIds = timeTypeMongoRepository.findAllSickTimeTypes().stream().map(MongoBaseEntity::getId).collect(Collectors.toSet());
        List<ActivityDTO> activities = activityMongoRepository.findAllByTimeTypeIdAndUnitId(sickTimeTypeIds, unitId);
        userSickDataWrapper.setActivities(activities);
        return userSickDataWrapper;
    }

    public Map<String, Object> markUserAsFine(Long staffId, Long unitId, LocalDate startDate, BigInteger activityId) {
        Map<String, Object> response = new HashMap<>();
        if (isNull(unitId) || isNull(staffId)) {
            exceptionService.actionNotPermittedException(ERROR_EMPTY_STAFF_OR_UNIT_SETTING);
        }
        Date endDate = DateUtils.getEndOfDay(DateUtils.plusDays(asDate(startDate),21));
        shiftSickService.disableSicknessShiftsOfStaff(staffId, unitId,startDate,activityId);
        List<ShiftDTO> threeWeeksShift =shiftMongoRepository.findAllShiftsByStaffIdsAndDateAndUnitId(staffId,asDate(startDate),endDate,unitId);
        sickSettingsRepository.markUserAsFine(staffId, unitId);  //set end date of user sick table.
        response.put("unitId", unitId);
        response.put("staffId", staffId);
        response.put("shifts",threeWeeksShift);
        return response;
    }

    public void checkStatusOfUserAndUpdateStatus(Long unitId) {
//        List<SickSettings> sickSettings = sickSettingsRepository.findAllSickUsersOfUnit(unitId);
//        if (!sickSettings.isEmpty()) {
//            Set<BigInteger> activityIds = sickSettings.stream().map(SickSettings::getActivityId).collect(Collectors.toSet());
//            List<Activity> activities = activityMongoRepository.findAllActivitiesByIds(activityIds);
//            Map<BigInteger, Activity> activityMap = activities.stream().collect(Collectors.toMap(MongoBaseEntity::getId, Function.identity()));
//            short maximumDayDifference = activities.stream().max(Comparator.comparingInt(aa -> aa.getActivityRulesSettings().getRecurrenceDays())).get().getActivityRulesSettings().getRecurrenceDays();
//            List<PeriodDTO> planningPeriods = planningPeriodMongoRepository.findAllPeriodsByStartDateAndLastDate(unitId, DateUtils.getCurrentLocalDate(), DateUtils.getLocalDateAfterDays(maximumDayDifference));
//            List<Shift> shifts = shiftMongoRepository.findAllShiftByDynamicQuery(sickSettings, activityMap);
//            Set<Long> staffIds = sickSettings.stream().map(SickSettings::getStaffId).collect(Collectors.toSet());
//            logger.info("last date iso string {} , {}", DateUtils.getDateAfterDaysWithTime((short) 0, LocalTime.MIN), DateUtils.getDateAfterDaysWithTime((short) 0, LocalTime.MAX));
//            List<Shift> previousDaySickShifts = shiftMongoRepository.findAllByStaffIdInAndSickShiftTrueAndDeletedFalseAndStartDateGreaterThanEqualAndEndDateLessThanEqual(staffIds, DateUtils.getDateAfterDaysWithTime((short) 0, LocalTime.MIN), DateUtils.getDateAfterDaysWithTime((short) 0, LocalTime.MAX));
//            Map<Long, Shift> staffWisePreviousDayShiftMap = previousDaySickShifts.stream().collect(Collectors.toMap(Shift::getStaffId, Function.identity(),(firstShift,secondShift)->firstShift));
//            Map<Long, List<Shift>> staffWiseShiftMap = shifts.stream().collect(Collectors.groupingBy(Shift::getStaffId, Collectors.toList()));
//            logger.info("Total number of shifts found {} ", shifts.size());
//
//            sickSettings.forEach(currentSickSettings -> {
//                logger.info("Processing on sickSetting {} with activityId {}" ,currentSickSettings.getId(),currentSickSettings.getActivityId());
//                Activity activity = activityMap.get(currentSickSettings.getActivityId());
//                int differenceOfDaysFromCurrentDateToLastSickDate = DateUtils.getDifferenceBetweenDatesInDays(currentSickSettings.getStartDate(), DateUtils.getCurrentLocalDate(),DurationType.DAYS);
//                List<Integer> validRepetitionDays = new ArrayList<>();
//                if (!activity.getActivityRulesSettings().isAllowedAutoAbsence() || differenceOfDaysFromCurrentDateToLastSickDate < 0) {
//                    logger.info("either activity is not allowed for break  {} or days is in -ve {}", activity.getActivityRulesSettings().isAllowedAutoAbsence(), differenceOfDaysFromCurrentDateToLastSickDate);
//                    return;
//                }
//
//                for (byte recurrenceTimes = activity.getActivityRulesSettings().getRecurrenceTimes(); recurrenceTimes > 0; recurrenceTimes--) {
//                    validRepetitionDays.add((recurrenceTimes * activity.getActivityRulesSettings().getRecurrenceDays()) - 1);
//                }
//                if (validRepetitionDays.contains(differenceOfDaysFromCurrentDateToLastSickDate)) {
//                    logger.info("The current user is still sick so we need to add more shifts {}", differenceOfDaysFromCurrentDateToLastSickDate);
//                    if (staffWisePreviousDayShiftMap.get(currentSickSettings.getStaffId()) != null) {
//                        List<Shift> currentStaffShifts = staffWiseShiftMap.get(currentSickSettings.getStaffId()) != null ? staffWiseShiftMap.get(currentSickSettings.getStaffId()) : new ArrayList<>();
//                        shiftSickService.createSicknessShiftsOfStaff(activity, currentStaffShifts, staffWisePreviousDayShiftMap.get(currentSickSettings.getStaffId()));
//                    }
//                }
//            });
//
//        }
    }

    public void validateSickSettings(StaffAdditionalInfoDTO staffAdditionalInfoDTO, ActivityWrapper activityWrapper, List<Shift> shifts, List<String> errorMessages, SicknessSetting sicknessSetting) {
        if (sicknessSetting.isCanOnlyUsedOnMainEmployment() && !EmploymentSubType.MAIN.equals(staffAdditionalInfoDTO.getEmployment().getEmploymentSubType())) {
            exceptionService.actionNotPermittedException(MESSAGE_STAFF_MAIN_EMPLOYMENT_NOT_FOUND);
        }
        if (isCollectionNotEmpty(activityWrapper.getActivity().getActivityRulesSettings().getStaffTagIds())) {
            Set<BigInteger> tadIds = staffAdditionalInfoDTO.getTags().stream().map(TagDTO::getId).collect(Collectors.toSet());
            if (CollectionUtils.containsAny(tadIds, activityWrapper.getActivity().getActivityRulesSettings().getStaffTagIds())) {
                exceptionService.actionNotPermittedException(STAFF_NOT_ALLOWED_ON_TAG);
            }

        }
        if (sicknessSetting.isValidForChildCare() && isCollectionEmpty(staffAdditionalInfoDTO.getSeniorAndChildCareDays().getChildCareDays())) {
            exceptionService.actionNotPermittedException(MESSAGE_STAFF_CARE_DAYS_NOT_FOUND);
        }
        if (sicknessSetting.isUsedOnFreeDays() && isCollectionNotEmpty(shifts)) {
            exceptionService.actionNotPermittedException(MESSAGE_ACTIVITY_USEDON_FREEDAY);
        }
    }
}
