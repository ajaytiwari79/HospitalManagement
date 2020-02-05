package com.kairos.service.dashboard;

import com.kairos.commons.utils.DateUtils;
import com.kairos.dto.activity.activity.ActivityDTO;
import com.kairos.dto.activity.dashboard.UserSickDataWrapper;
import com.kairos.dto.activity.period.PeriodDTO;
import com.kairos.dto.user.staff.staff.StaffResultDTO;
import com.kairos.dto.user_context.UserContext;
import com.kairos.enums.DurationType;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.attendence_setting.SickSettings;
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
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.ObjectUtils.*;
import static com.kairos.constants.ActivityMessagesConstants.ERROR_EMPTY_STAFF_OR_UNIT_SETTING;
import static com.kairos.constants.ActivityMessagesConstants.MESSAGE_STAFF_NOTFOUND;

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
        Set<BigInteger> sickTimeTypeIds = timeTypeMongoRepository.findAllSickTimeTypes().stream().map(timeType -> timeType.getId()).collect(Collectors.toSet());
        List<ActivityDTO> activities = activityMongoRepository.findAllByTimeTypeIdAndUnitId(sickTimeTypeIds, unitId);
        userSickDataWrapper.setActivities(activities);
        return userSickDataWrapper;
    }

    public Map<String, Long> markUserAsFine(Long employmentId, Long unitId, LocalDate startDate) {
        Map<String, Long> response = new HashMap<>();
        if (isNull(unitId) || isNull(employmentId)) {
            exceptionService.actionNotPermittedException(ERROR_EMPTY_STAFF_OR_UNIT_SETTING);
        }
        shiftSickService.disableSicknessShiftsOfStaff(employmentId, unitId,startDate);
        sickSettingsRepository.markUserAsFine(employmentId, unitId);  //set end date of user sick table.
        response.put("unitId", unitId);
        response.put("employmentId", employmentId);
        return response;
    }

    public void checkStatusOfUserAndUpdateStatus(Long unitId) {
//        List<SickSettings> sickSettings = sickSettingsRepository.findAllSickUsersOfUnit(unitId);
//        if (!sickSettings.isEmpty()) {
//            Set<BigInteger> activityIds = sickSettings.stream().map(SickSettings::getActivityId).collect(Collectors.toSet());
//            List<Activity> activities = activityMongoRepository.findAllActivitiesByIds(activityIds);
//            Map<BigInteger, Activity> activityMap = activities.stream().collect(Collectors.toMap(MongoBaseEntity::getId, Function.identity()));
//            short maximumDayDifference = activities.stream().max(Comparator.comparingInt(aa -> aa.getRulesActivityTab().getRecurrenceDays())).get().getRulesActivityTab().getRecurrenceDays();
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
//                if (!activity.getRulesActivityTab().isAllowedAutoAbsence() || differenceOfDaysFromCurrentDateToLastSickDate < 0) {
//                    logger.info("either activity is not allowed for break  {} or days is in -ve {}", activity.getRulesActivityTab().isAllowedAutoAbsence(), differenceOfDaysFromCurrentDateToLastSickDate);
//                    return;
//                }
//
//                for (byte recurrenceTimes = activity.getRulesActivityTab().getRecurrenceTimes(); recurrenceTimes > 0; recurrenceTimes--) {
//                    validRepetitionDays.add((recurrenceTimes * activity.getRulesActivityTab().getRecurrenceDays()) - 1);
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
}
