package com.kairos.utils.service_util;

import com.kairos.custom_exception.InvalidRequestException;
import com.kairos.dto.activity.common.UserInfo;
import com.kairos.dto.activity.staffing_level.Duration;
import com.kairos.dto.activity.staffing_level.StaffingLevelActivity;
import com.kairos.dto.activity.staffing_level.StaffingLevelInterval;
import com.kairos.dto.activity.staffing_level.StaffingLevelIntervalLog;
import com.kairos.dto.activity.staffing_level.absence.AbsenceStaffingLevelDto;
import com.kairos.dto.activity.staffing_level.presence.PresenceStaffingLevelDto;
import com.kairos.dto.user_context.UserContext;
import com.kairos.persistence.model.staffing_level.StaffingLevel;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.getCurrentDate;
import static com.kairos.persistence.model.staffing_level.StaffingLevel.Type.PRESENCE;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StaffingLevelUtil {

    public static StaffingLevel buildPresenceStaffingLevels(PresenceStaffingLevelDto presenceStaffingLevelDTO, Long orgId) {
        StaffingLevel staffingLevel = new StaffingLevel(presenceStaffingLevelDTO.getCurrentDate(), presenceStaffingLevelDTO.getWeekCount()
                , orgId, presenceStaffingLevelDTO.getPhaseId(), presenceStaffingLevelDTO.getStaffingLevelSetting());

        Set<StaffingLevelInterval> staffingLevelTimeSlotsList = new LinkedHashSet<>();
        for (StaffingLevelInterval staffingLevelTimeSlotDTO : presenceStaffingLevelDTO.getPresenceStaffingLevelInterval()) {
            StaffingLevelInterval staffingLevelTimeSlot = new StaffingLevelInterval(staffingLevelTimeSlotDTO.getSequence(), staffingLevelTimeSlotDTO.getMinNoOfStaff(),
                    staffingLevelTimeSlotDTO.getMaxNoOfStaff(), staffingLevelTimeSlotDTO.getStaffingLevelDuration()
            );
            staffingLevelTimeSlot.addStaffLevelActivity(staffingLevelTimeSlotDTO.getStaffingLevelActivities());
            staffingLevelTimeSlot.setStaffingLevelSkills(staffingLevelTimeSlotDTO.getStaffingLevelSkills());
            staffingLevelTimeSlotsList.add(staffingLevelTimeSlot);
        }

        staffingLevel.addStaffingLevelTimeSlot(staffingLevelTimeSlotsList);
        return staffingLevel;

    }

    public static StaffingLevel buildAbsenceStaffingLevels(AbsenceStaffingLevelDto absenceStaffingLevelDto, Long unitId) {

        StaffingLevel staffingLevel = new StaffingLevel(absenceStaffingLevelDto.getCurrentDate(), absenceStaffingLevelDto.getWeekCount()
                , unitId, absenceStaffingLevelDto.getPhaseId());

        Duration staffingLevelDuration = new Duration(LocalTime.MIN, LocalTime.MAX);
        List<StaffingLevelInterval> absenceStaffingLevelIntervals = new ArrayList<>();
        StaffingLevelInterval absenceStaffingLevelInterval = new StaffingLevelInterval(0, absenceStaffingLevelDto.getMinNoOfStaff(),
                absenceStaffingLevelDto.getMaxNoOfStaff(), staffingLevelDuration);
        absenceStaffingLevelInterval.setStaffingLevelActivities(absenceStaffingLevelDto.getStaffingLevelActivities());
        absenceStaffingLevelIntervals.add(absenceStaffingLevelInterval);
        staffingLevel.setAbsenceStaffingLevelInterval(absenceStaffingLevelIntervals);
        return staffingLevel;
    }


    private static Set<StaffingLevelActivity> getStaffingLevelActivities(Map<BigInteger, BigInteger> childAndParentActivityIdMap, StaffingLevelInterval staffingLevelTimeSlotDTO, Map<BigInteger, StaffingLevelActivity> staffingLevelActivityMap) {
        Set<StaffingLevelActivity> staffingLevelActivities = new HashSet<>();
        Map<BigInteger, StaffingLevelStaffMinMax> activityIdStaffMinMaxMap = new HashMap<>();
        for (StaffingLevelActivity staffingLevelActivity : staffingLevelTimeSlotDTO.getStaffingLevelActivities()) {
            validateParentChildActivityStaffingLevelMinMaxNumberOfStaff(childAndParentActivityIdMap, activityIdStaffMinMaxMap, staffingLevelActivity);
            StaffingLevelActivity staffingLevelActivityNew = new StaffingLevelActivity(staffingLevelActivity.getActivityId(), staffingLevelActivity.getName(),
                    staffingLevelActivity.getMinNoOfStaff(), staffingLevelActivity.getMaxNoOfStaff());
            if (staffingLevelActivityMap.containsKey(staffingLevelActivity.getActivityId())) {
                staffingLevelActivityNew.setAvailableNoOfStaff(staffingLevelActivityMap.get(staffingLevelActivity.getActivityId()).getAvailableNoOfStaff());
            }
            staffingLevelActivities.add(staffingLevelActivityNew);
        }
        activityIdStaffMinMaxMap.values().forEach(staffingLevelStaffMinMax -> {
            if (staffingLevelStaffMinMax != null && (staffingLevelStaffMinMax.getMinNoOfStaffParentActivity() < staffingLevelStaffMinMax.getMinNoOfStaffChildActivities() || staffingLevelStaffMinMax.getMaxNoOfStaffParentActivity() < staffingLevelStaffMinMax.getMaxNoOfStaffChildActivities())) {
                throw new InvalidRequestException("child staffing level should be less than or equal to parent count for interval : " + staffingLevelTimeSlotDTO.getStaffingLevelDuration().getFrom() + " to " + staffingLevelTimeSlotDTO.getStaffingLevelDuration().getTo());
            }
        });
        return staffingLevelActivities;
    }

    private static StaffingLevelStaffMinMax validateParentChildActivityStaffingLevelMinMaxNumberOfStaff(Map<BigInteger, BigInteger> childAndParentActivityIdMap, Map<BigInteger, StaffingLevelStaffMinMax> activityIdStaffMinMaxMap, StaffingLevelActivity staffingLevelActivity) {
        BigInteger parentActivityId = childAndParentActivityIdMap.getOrDefault(staffingLevelActivity.getActivityId(), null);
        StaffingLevelStaffMinMax staffingLevelStaffMinMax;
        if (staffingLevelActivity.getMinNoOfStaff() > staffingLevelActivity.getMaxNoOfStaff()) {
            throw new InvalidRequestException("Min should be less than max");
        }
        if (parentActivityId == null) {
            staffingLevelStaffMinMax = activityIdStaffMinMaxMap.getOrDefault(staffingLevelActivity.getActivityId(), new StaffingLevelStaffMinMax());
            staffingLevelStaffMinMax.setMinNoOfStaffParentActivity(staffingLevelActivity.getMinNoOfStaff());
            staffingLevelStaffMinMax.setMaxNoOfStaffParentActivity(staffingLevelActivity.getMaxNoOfStaff());
            activityIdStaffMinMaxMap.put(staffingLevelActivity.getActivityId(), staffingLevelStaffMinMax);
        } else {
            staffingLevelStaffMinMax = activityIdStaffMinMaxMap.get(parentActivityId);
            if (staffingLevelStaffMinMax == null) {
                staffingLevelStaffMinMax = new StaffingLevelStaffMinMax(staffingLevelActivity.getMinNoOfStaff(), staffingLevelActivity.getMaxNoOfStaff());
            } else {
                staffingLevelStaffMinMax.setMinNoOfStaffChildActivities(staffingLevelStaffMinMax.getMinNoOfStaffChildActivities() + staffingLevelActivity.getMinNoOfStaff());
                staffingLevelStaffMinMax.setMaxNoOfStaffChildActivities(staffingLevelStaffMinMax.getMaxNoOfStaffChildActivities() + staffingLevelActivity.getMaxNoOfStaff());
            }
            activityIdStaffMinMaxMap.put(parentActivityId, staffingLevelStaffMinMax);
        }
        return staffingLevelStaffMinMax;

    }

    public static StaffingLevel updateAbsenceStaffingLevels(AbsenceStaffingLevelDto absenceStaffingLevelDto,
                                                            Long unitId, StaffingLevel staffingLevel) {

        staffingLevel.setPhaseId(absenceStaffingLevelDto.getPhaseId());
        staffingLevel.setWeekCount(absenceStaffingLevelDto.getWeekCount());
        staffingLevel.setUnitId(unitId);

        Duration staffingLevelDuration = new Duration(LocalTime.MIN, LocalTime.MAX);
        List<StaffingLevelInterval> absenceStaffingLevelIntervals = new ArrayList<>();
        StaffingLevelInterval absenceStaffingLevelInterval = new StaffingLevelInterval(0, absenceStaffingLevelDto.getMinNoOfStaff(),
                absenceStaffingLevelDto.getMaxNoOfStaff(), staffingLevelDuration);
        absenceStaffingLevelInterval.setStaffingLevelActivities(absenceStaffingLevelDto.getStaffingLevelActivities());
        absenceStaffingLevelIntervals.add(absenceStaffingLevelInterval);
        staffingLevel.setAbsenceStaffingLevelInterval(absenceStaffingLevelIntervals);

        return staffingLevel;

    }

    public static List<AbsenceStaffingLevelDto> buildAbsenceStaffingLevelDto(List<StaffingLevel> staffingLevels) {
        List<AbsenceStaffingLevelDto> absenceStaffingLevelDtos = new ArrayList<>();

        for (StaffingLevel staffingLevel : staffingLevels) {
            AbsenceStaffingLevelDto absenceStaffingLevelDto = new AbsenceStaffingLevelDto(staffingLevel.getId(), staffingLevel.getPhaseId(), staffingLevel.getCurrentDate(), staffingLevel.getWeekCount());
            absenceStaffingLevelDto.setMinNoOfStaff(staffingLevel.getAbsenceStaffingLevelInterval().get(0).getMinNoOfStaff());
            absenceStaffingLevelDto.setMaxNoOfStaff(staffingLevel.getAbsenceStaffingLevelInterval().get(0).getMaxNoOfStaff());
            absenceStaffingLevelDto.setAbsentNoOfStaff(staffingLevel.getAbsenceStaffingLevelInterval().get(0).getAvailableNoOfStaff());
            absenceStaffingLevelDto.setStaffingLevelActivities(staffingLevel.getAbsenceStaffingLevelInterval().get(0).getStaffingLevelActivities());
            absenceStaffingLevelDto.setUpdatedAt(staffingLevel.getUpdatedAt());
            absenceStaffingLevelDtos.add(absenceStaffingLevelDto);

        }

        return absenceStaffingLevelDtos;

    }



    public static void setUserWiseLogs(StaffingLevel staffingLevel, PresenceStaffingLevelDto presenceStaffingLevelDTO, StaffingLevel.Type type) {
        staffingLevel.setStaffingLevelSetting(presenceStaffingLevelDTO.getStaffingLevelSetting());
        staffingLevel.setPhaseId(presenceStaffingLevelDTO.getPhaseId());
        List<StaffingLevelInterval> staffingLevelIntervals=PRESENCE.equals(type)?staffingLevel.getPresenceStaffingLevelInterval():staffingLevel.getAbsenceStaffingLevelInterval();
        for (int i = 0; i < staffingLevelIntervals.size(); i++) {
            StaffingLevelIntervalLog staffingLevelIntervalLog=staffingLevelIntervals.get(i).getStaffingLevelIntervalLogs().stream().filter(k->k.getUserInfo().getId().equals(UserContext.getUserDetails().getId())).findFirst().orElse(new StaffingLevelIntervalLog());
            Map<BigInteger, StaffingLevelActivity> staffingLevelActivityMap = staffingLevelIntervals.get(i).getStaffingLevelActivities().stream().collect(Collectors.toMap(StaffingLevelActivity::getActivityId, v -> v));
            Set<StaffingLevelActivity> staffingLevelActivities = getStaffingLevelActivities(new HashMap<>(), staffingLevelIntervals.get(i), staffingLevelActivityMap);
            staffingLevelIntervals.get(i).setStaffingLevelActivities(staffingLevelActivities);
            staffingLevelIntervalLog.setStaffingLevelSkills(presenceStaffingLevelDTO.getPresenceStaffingLevelInterval().get(i).getStaffingLevelSkills());
            staffingLevelIntervalLog.setMinNoOfStaff(staffingLevelIntervalLog.getStaffingLevelActivities().stream().collect(Collectors.summingInt(k->k.getMinNoOfStaff())));
            staffingLevelIntervalLog.setMaxNoOfStaff(staffingLevelIntervalLog.getStaffingLevelActivities().stream().collect(Collectors.summingInt(k->k.getMaxNoOfStaff())));
            staffingLevelIntervalLog.setUserInfo(new UserInfo(UserContext.getUserDetails().getId(),UserContext.getUserDetails().getEmail(),UserContext.getUserDetails().getFullName()));
            staffingLevelIntervalLog.setUpdatedAt(getCurrentDate());
            staffingLevelIntervals.get(i).getStaffingLevelIntervalLogs().add(staffingLevelIntervalLog);
        }
    }

    public static  void updateStaffingLevelToPublish(StaffingLevel staffingLevel, StaffingLevel.Type type){
        List<StaffingLevelInterval> staffingLevelIntervals=PRESENCE.equals(type)?staffingLevel.getPresenceStaffingLevelInterval():staffingLevel.getAbsenceStaffingLevelInterval();
        for (int i = 0; i < staffingLevelIntervals.size(); i++) {
            StaffingLevelIntervalLog staffingLevelIntervalLog=staffingLevelIntervals.get(i).getStaffingLevelIntervalLogs().iterator().next();
            staffingLevelIntervals.get(i).setStaffingLevelActivities(staffingLevelIntervalLog.getStaffingLevelActivities());
            staffingLevelIntervals.get(i).setMaxNoOfStaff(staffingLevelIntervalLog.getMaxNoOfStaff());
            staffingLevelIntervals.get(i).setMinNoOfStaff(staffingLevelIntervalLog.getMinNoOfStaff());
            staffingLevelIntervals.get(i).setStaffingLevelSkills(staffingLevelIntervalLog.getStaffingLevelSkills());
            staffingLevelIntervals.get(i).setStaffingLevelIntervalLogs(null);
        }
    }
}
