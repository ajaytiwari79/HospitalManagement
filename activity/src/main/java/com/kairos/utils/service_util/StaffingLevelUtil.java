package com.kairos.utils.service_util;

import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.custom_exception.InvalidRequestException;
import com.kairos.dto.activity.common.UserInfo;
import com.kairos.dto.activity.staffing_level.*;
import com.kairos.dto.activity.staffing_level.absence.AbsenceStaffingLevelDto;
import com.kairos.dto.activity.staffing_level.presence.PresenceStaffingLevelDto;
import com.kairos.dto.user_context.UserContext;
import com.kairos.enums.SkillLevel;
import com.kairos.persistence.model.staffing_level.StaffingLevel;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.getCurrentDate;
import static com.kairos.commons.utils.ObjectUtils.*;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StaffingLevelUtil {

    public static StaffingLevel buildPresenceStaffingLevels(PresenceStaffingLevelDto presenceStaffingLevelDTO, Long orgId) {
        StaffingLevel staffingLevel = new StaffingLevel(presenceStaffingLevelDTO.getCurrentDate(), presenceStaffingLevelDTO.getWeekCount()
                , orgId, presenceStaffingLevelDTO.getPhaseId(), presenceStaffingLevelDTO.getStaffingLevelSetting());

        Set<StaffingLevelInterval> staffingLevelIntervals = new LinkedHashSet<>();
        for (StaffingLevelInterval staffingLevelInterval : presenceStaffingLevelDTO.getPresenceStaffingLevelInterval()) {
            StaffingLevelInterval presenceStaffingLevelInterval = new StaffingLevelInterval(staffingLevelInterval.getSequence(), staffingLevelInterval.getStaffingLevelDuration());
            if (presenceStaffingLevelDTO.isDraft()) {
                initializeUserWiseLogs(presenceStaffingLevelInterval);
            } else {
                presenceStaffingLevelInterval.addStaffLevelActivity(staffingLevelInterval.getStaffingLevelActivities());
                presenceStaffingLevelInterval.addStaffLevelSkill(staffingLevelInterval.getStaffingLevelSkills());
                presenceStaffingLevelInterval.setMinNoOfStaff(presenceStaffingLevelInterval.getStaffingLevelActivities().stream().collect(Collectors.summingInt(k -> k.getMinNoOfStaff())));
                presenceStaffingLevelInterval.setMaxNoOfStaff(presenceStaffingLevelInterval.getStaffingLevelActivities().stream().collect(Collectors.summingInt(k -> k.getMaxNoOfStaff())));

            }
            staffingLevelIntervals.add(presenceStaffingLevelInterval);
        }

        staffingLevel.addStaffingLevelTimeSlot(staffingLevelIntervals);
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


    private static Set<StaffingLevelActivity> getStaffingLevelActivities(Map<BigInteger, BigInteger> childAndParentActivityIdMap, StaffingLevelInterval staffingLevelTimeSlotDTO, Map<BigInteger, StaffingLevelActivity> staffingLevelActivityMap, Map<BigInteger, StaffingLevelActivity> staffingLevelActivityMapOfLogs) {
        Set<StaffingLevelActivity> staffingLevelActivities = new HashSet<>();
        Map<BigInteger, StaffingLevelStaffMinMax> activityIdStaffMinMaxMap = new HashMap<>();
        for (StaffingLevelActivity staffingLevelActivity : staffingLevelTimeSlotDTO.getStaffingLevelActivities()) {
            validateParentChildActivityStaffingLevelMinMaxNumberOfStaff(childAndParentActivityIdMap, activityIdStaffMinMaxMap, staffingLevelActivity);
            StaffingLevelActivity staffingLevelActivityNew = new StaffingLevelActivity(staffingLevelActivity.getActivityId(), staffingLevelActivity.getName(),
                    staffingLevelActivity.getMinNoOfStaff(), staffingLevelActivity.getMaxNoOfStaff());
            if (staffingLevelActivityMap.containsKey(staffingLevelActivity.getActivityId())) {
                staffingLevelActivityNew.setAvailableNoOfStaff(staffingLevelActivityMap.get(staffingLevelActivity.getActivityId()).getAvailableNoOfStaff());
            }
            if (isEmpty(staffingLevelActivityMapOfLogs) || staffingLevelActivityMapOfLogs.get(staffingLevelActivity.getActivityId()).getMaxNoOfStaff() != staffingLevelActivity.getMaxNoOfStaff()) {
                staffingLevelActivityNew.setMaxUpdatedAt(getCurrentDate());
            } else {
                staffingLevelActivityNew.setMaxUpdatedAt(staffingLevelActivityMapOfLogs.get(staffingLevelActivity.getActivityId()).getMaxUpdatedAt());
            }
            if (isEmpty(staffingLevelActivityMapOfLogs) || staffingLevelActivityMapOfLogs.get(staffingLevelActivity.getActivityId()).getMinNoOfStaff() != staffingLevelActivity.getMinNoOfStaff()) {
                staffingLevelActivityNew.setMinUpdatedAt(getCurrentDate());
            } else {
                staffingLevelActivityNew.setMinUpdatedAt(staffingLevelActivityMapOfLogs.get(staffingLevelActivity.getActivityId()).getMinUpdatedAt());
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


    public static void setUserWiseLogs(StaffingLevel staffingLevel, PresenceStaffingLevelDto presenceStaffingLevelDTO) {
        Set<ActivityRemoveLog> activityRemoveLogs = new HashSet<>();
        Set<SkillRemoveLog> skillRemoveLogs = new HashSet<>();
        prepareIntervals(staffingLevel, presenceStaffingLevelDTO, activityRemoveLogs, skillRemoveLogs);
        staffingLevel.setStaffingLevelSetting(presenceStaffingLevelDTO.getStaffingLevelSetting());
        staffingLevel.setPhaseId(presenceStaffingLevelDTO.getPhaseId());
        List<StaffingLevelInterval> staffingLevelIntervals = presenceStaffingLevelDTO.getPresenceStaffingLevelInterval();
        for (int i = 0; i < staffingLevelIntervals.size(); i++) {
            StaffingLevelIntervalLog staffingLevelIntervalLog = staffingLevel.getPresenceStaffingLevelInterval().get(i).getStaffingLevelIntervalLogs().stream().filter(k -> k.getUserInfo().getId().equals(UserContext.getUserDetails().getId())).findFirst().orElse(new StaffingLevelIntervalLog());
            Map<BigInteger, StaffingLevelActivity> staffingLevelActivityMap = staffingLevelIntervals.get(i).getStaffingLevelActivities().stream().collect(Collectors.toMap(StaffingLevelActivity::getActivityId, v -> v));
            StaffingLevelIntervalLog lastStaffingLevelIntervalLog = isCollectionEmpty(staffingLevel.getPresenceStaffingLevelInterval().get(i).getStaffingLevelIntervalLogs()) ? new StaffingLevelIntervalLog() : staffingLevel.getPresenceStaffingLevelInterval().get(i).getStaffingLevelIntervalLogs().last();
            Map<BigInteger, StaffingLevelActivity> staffingLevelActivityMapOfLogs = lastStaffingLevelIntervalLog.getStaffingLevelActivities().stream().collect(Collectors.toMap(StaffingLevelActivity::getActivityId, v -> v));
            Set<StaffingLevelActivity> staffingLevelActivities = getStaffingLevelActivities(new HashMap<>(), staffingLevelIntervals.get(i), staffingLevelActivityMap, staffingLevelActivityMapOfLogs);
            staffingLevelIntervalLog.setStaffingLevelActivities(staffingLevelActivities);
            updateStaffingLevelSkills(presenceStaffingLevelDTO.getPresenceStaffingLevelInterval().get(i).getStaffingLevelSkills());
            staffingLevelIntervalLog.setStaffingLevelSkills(presenceStaffingLevelDTO.getPresenceStaffingLevelInterval().get(i).getStaffingLevelSkills());
            staffingLevelIntervalLog.setMinNoOfStaff(staffingLevelIntervalLog.getStaffingLevelActivities().stream().collect(Collectors.summingInt(k -> k.getMinNoOfStaff())));
            staffingLevelIntervalLog.setMaxNoOfStaff(staffingLevelIntervalLog.getStaffingLevelActivities().stream().collect(Collectors.summingInt(k -> k.getMaxNoOfStaff())));
            staffingLevelIntervalLog.setUserInfo(new UserInfo(UserContext.getUserDetails().getId(), UserContext.getUserDetails().getEmail(), UserContext.getUserDetails().getFullName()));
            staffingLevelIntervalLog.setUpdatedAt(getCurrentDate());
            staffingLevelIntervalLog.setActivityRemoveLogs(activityRemoveLogs);
            staffingLevelIntervalLog.setSkillRemoveLogs(skillRemoveLogs);
            staffingLevel.getPresenceStaffingLevelInterval().get(i).getStaffingLevelIntervalLogs().add(staffingLevelIntervalLog);
        }
    }

    private static void prepareIntervals(StaffingLevel staffingLevel, PresenceStaffingLevelDto presenceStaffingLevelDTO, Set<ActivityRemoveLog> activityRemoveLogs, Set<SkillRemoveLog> skillRemoveLogs) {
        List<StaffingLevelInterval> staffingLevelIntervals = new ArrayList<>();
        for (int i = 0; i < 96; i++) {
            StaffingLevelIntervalLog staffingLevelIntervalLog = isCollectionEmpty(staffingLevel.getPresenceStaffingLevelInterval().get(i).getStaffingLevelIntervalLogs()) ? null : staffingLevel.getPresenceStaffingLevelInterval().get(i).getStaffingLevelIntervalLogs().last();
            StaffingLevelInterval interval = ObjectMapperUtils.copyPropertiesByMapper(isNull(staffingLevelIntervalLog) ?staffingLevel.getPresenceStaffingLevelInterval().get(i):staffingLevelIntervalLog, StaffingLevelInterval.class);
            if (presenceStaffingLevelDTO.getPresenceStaffingLevelInterval().get(0).getSequence() == i) {
                staffingLevelIntervals.add(presenceStaffingLevelDTO.getPresenceStaffingLevelInterval().get(0));
            } else {
                interval.getStaffingLevelActivities().addAll(presenceStaffingLevelDTO.getPresenceStaffingLevelInterval().get(0).getStaffingLevelActivities());
                if (presenceStaffingLevelDTO.getPresenceStaffingLevelInterval().get(0).getStaffingLevelActivities().size() < interval.getStaffingLevelActivities().size()) {
                    for (Iterator<StaffingLevelActivity> iterator = interval.getStaffingLevelActivities().iterator(); iterator.hasNext(); ) {
                        StaffingLevelActivity staffingLevelActivity = iterator.next();
                        if (!presenceStaffingLevelDTO.getPresenceStaffingLevelInterval().get(0).getStaffingLevelActivities().contains(staffingLevelActivity)) {
                            iterator.remove();
                            activityRemoveLogs.add(new ActivityRemoveLog(staffingLevelActivity.getActivityId(), new Date(), UserContext.getUserDetails().getFirstName()));
                        }
                    }
                }
                if (isNotNull(staffingLevelIntervalLog))
                    activityRemoveLogs.addAll(staffingLevelIntervalLog.getActivityRemoveLogs());
                interval.getStaffingLevelSkills().addAll(presenceStaffingLevelDTO.getPresenceStaffingLevelInterval().get(0).getStaffingLevelSkills());
                if (presenceStaffingLevelDTO.getPresenceStaffingLevelInterval().get(0).getStaffingLevelSkills().size() < interval.getStaffingLevelSkills().size()) {
                    for (Iterator<StaffingLevelSkill> iterator = interval.getStaffingLevelSkills().iterator(); iterator.hasNext(); ) {
                        StaffingLevelSkill staffingLevelSkill = iterator.next();
                        if (!presenceStaffingLevelDTO.getPresenceStaffingLevelInterval().get(0).getStaffingLevelSkills().contains(staffingLevelSkill)) {
                            iterator.remove();
                            skillRemoveLogs.add(new SkillRemoveLog(staffingLevelSkill.getSkillId(), new Date(), UserContext.getUserDetails().getFirstName()));
                        }
                    }
                }
                if (isNotNull(staffingLevelIntervalLog))
                    skillRemoveLogs.addAll(staffingLevelIntervalLog.getSkillRemoveLogs());
                staffingLevelIntervals.add(interval);
            }
            if (presenceStaffingLevelDTO.getPresenceStaffingLevelInterval().get(0).getStaffingLevelActivities().size() > interval.getStaffingLevelActivities().size()) {
                updateAddedSkillAndActivity(staffingLevel, presenceStaffingLevelDTO, i, activityRemoveLogs, skillRemoveLogs);
            }
        }
        presenceStaffingLevelDTO.setPresenceStaffingLevelInterval(staffingLevelIntervals);

    }

    private static void updateAddedSkillAndActivity(StaffingLevel staffingLevel, PresenceStaffingLevelDto presenceStaffingLevelDTO, int i, Set<ActivityRemoveLog> activityRemoveLogs, Set<SkillRemoveLog> skillRemoveLogs) {
        Map<BigInteger, StaffingLevelActivity> staffingLevelActivityMap = staffingLevel.getPresenceStaffingLevelInterval().get(i).getStaffingLevelActivities().stream().collect(Collectors.toMap(StaffingLevelActivity::getActivityId, v -> v));
        for (StaffingLevelActivity staffingLevelActivity : presenceStaffingLevelDTO.getPresenceStaffingLevelInterval().get(0).getStaffingLevelActivities()) {
            if (!staffingLevelActivityMap.containsKey(staffingLevelActivity.getActivityId())) {
                staffingLevel.getPresenceStaffingLevelInterval().get(i).getStaffingLevelActivities().add(staffingLevelActivity);
                activityRemoveLogs.removeIf(k -> k.getActivityId().equals(staffingLevelActivity.getActivityId()));
            }
        }

        Map<Long, StaffingLevelSkill> staffingLevelSkillMap = staffingLevel.getPresenceStaffingLevelInterval().get(i).getStaffingLevelSkills().stream().collect(Collectors.toMap(StaffingLevelSkill::getSkillId, v -> v));
        for (StaffingLevelSkill staffingLevelSkill : presenceStaffingLevelDTO.getPresenceStaffingLevelInterval().get(0).getStaffingLevelSkills()) {
            if (!staffingLevelSkillMap.containsKey(staffingLevelSkill.getSkillId())) {
                staffingLevel.getPresenceStaffingLevelInterval().get(i).getStaffingLevelSkills().add(staffingLevelSkill);
                skillRemoveLogs.removeIf(k -> k.getSkillId().equals(staffingLevelSkill.getSkillId()));
            }
        }
    }

    public static void setUserWiseLogsInAbsence(StaffingLevel staffingLevel, AbsenceStaffingLevelDto absenceStaffingLevelDto) {
        staffingLevel.setPhaseId(absenceStaffingLevelDto.getPhaseId());
        staffingLevel.setWeekCount(absenceStaffingLevelDto.getWeekCount());
        StaffingLevelIntervalLog staffingLevelIntervalLog = staffingLevel.getAbsenceStaffingLevelInterval().get(0).getStaffingLevelIntervalLogs().stream().filter(k -> k.getUserInfo().getId().equals(UserContext.getUserDetails().getId())).findFirst().orElse(new StaffingLevelIntervalLog());
        staffingLevelIntervalLog.setStaffingLevelActivities(absenceStaffingLevelDto.getStaffingLevelActivities());
        staffingLevelIntervalLog.setMinNoOfStaff(staffingLevelIntervalLog.getStaffingLevelActivities().stream().collect(Collectors.summingInt(k -> k.getMinNoOfStaff())));
        staffingLevelIntervalLog.setMaxNoOfStaff(staffingLevelIntervalLog.getStaffingLevelActivities().stream().collect(Collectors.summingInt(k -> k.getMaxNoOfStaff())));
        staffingLevelIntervalLog.setUserInfo(new UserInfo(UserContext.getUserDetails().getId(), UserContext.getUserDetails().getEmail(), UserContext.getUserDetails().getFullName()));
        staffingLevelIntervalLog.setUpdatedAt(getCurrentDate());
        staffingLevel.getAbsenceStaffingLevelInterval().get(0).getStaffingLevelIntervalLogs().add(staffingLevelIntervalLog);
    }

    public static void updateStaffingLevelToPublish(StaffingLevelPublishDTO staffingLevelPublishDTO, StaffingLevel staffingLevel) {
        List<StaffingLevelInterval> staffingLevelIntervals = staffingLevel.getPresenceStaffingLevelInterval();
        for (int i = 0; i < staffingLevelIntervals.size(); i++) {
            updateActivities(staffingLevelPublishDTO, staffingLevelIntervals.get(i));
        }
        for (int i = 0; i < staffingLevel.getAbsenceStaffingLevelInterval().size(); i++) {
            updateActivities(staffingLevelPublishDTO, staffingLevelIntervals.get(i));
        }
    }

    private static void updateActivities(StaffingLevelPublishDTO staffingLevelPublishDTO, StaffingLevelInterval staffingLevelInterval) {
        StaffingLevelIntervalLog staffingLevelIntervalLog = staffingLevelInterval.getStaffingLevelIntervalLogs().last();
        Set<StaffingLevelActivity> staffingLevelActivities = staffingLevelIntervalLog.getStaffingLevelActivities().stream().filter(k -> staffingLevelPublishDTO.getActivityIds().contains(k.getActivityId())).collect(Collectors.toSet());
        Map<BigInteger, StaffingLevelActivity> staffingLevelActivityMap = staffingLevelInterval.getStaffingLevelActivities().stream().collect(Collectors.toMap(k -> k.getActivityId(), v -> v));
        staffingLevelActivities.forEach(k -> {
            if (staffingLevelActivityMap.containsKey(k.getActivityId())) {
                staffingLevelInterval.getStaffingLevelActivities().remove(staffingLevelActivityMap.get(k.getActivityId()));
            }
            staffingLevelInterval.getStaffingLevelActivities().add(k);
        });
        removeActivityAndSkills(staffingLevelInterval, staffingLevelIntervalLog);
        staffingLevelInterval.setMaxNoOfStaff(staffingLevelInterval.getStaffingLevelActivities().stream().collect(Collectors.summingInt(k -> k.getMaxNoOfStaff())));
        staffingLevelInterval.setMinNoOfStaff(staffingLevelInterval.getStaffingLevelActivities().stream().collect(Collectors.summingInt(k -> k.getMinNoOfStaff())));
        Set<StaffingLevelSkill> staffingLevelSkills = staffingLevelIntervalLog.getStaffingLevelSkills().stream().filter(k -> staffingLevelPublishDTO.getSkillIds().contains(k.getSkillId())).collect(Collectors.toSet());
        staffingLevelInterval.setStaffingLevelSkills(staffingLevelSkills);
        //resetIntervalLogs(staffingLevelIntervalLog, staffingLevelActivities, staffingLevelSkills);

    }

    private static void resetIntervalLogs(StaffingLevelIntervalLog staffingLevelIntervalLog, Set<StaffingLevelActivity> staffingLevelActivities, Set<StaffingLevelSkill> staffingLevelSkills) {
        staffingLevelIntervalLog.getStaffingLevelActivities().removeAll(staffingLevelActivities);
        staffingLevelIntervalLog.getStaffingLevelSkills().removeAll(staffingLevelSkills);
        staffingLevelIntervalLog.setMinNoOfStaff(staffingLevelIntervalLog.getStaffingLevelActivities().stream().collect(Collectors.summingInt(k -> k.getMinNoOfStaff())));
        staffingLevelIntervalLog.setMaxNoOfStaff(staffingLevelIntervalLog.getStaffingLevelActivities().stream().collect(Collectors.summingInt(k -> k.getMaxNoOfStaff())));
    }

    private static void removeActivityAndSkills(StaffingLevelInterval staffingLevelInterval, StaffingLevelIntervalLog staffingLevelIntervalLog) {
        Set<BigInteger> activityIdsToRemove = staffingLevelInterval.getStaffingLevelActivities().stream().map(k -> k.getActivityId()).collect(Collectors.toSet());
        Map<BigInteger, StaffingLevelActivity> staffingLevelActivityMap = staffingLevelIntervalLog.getStaffingLevelActivities().stream().collect(Collectors.toMap(k -> k.getActivityId(), v -> v));
        for (BigInteger activityId : activityIdsToRemove) {
            if (!staffingLevelActivityMap.containsKey(activityId)) {
                staffingLevelInterval.getStaffingLevelActivities().remove(staffingLevelActivityMap.get(activityId));
            }
        }

        Set<Long> skillIdsToRemove = staffingLevelInterval.getStaffingLevelSkills().stream().map(k -> k.getSkillId()).collect(Collectors.toSet());
        Map<Long, StaffingLevelSkill> staffingLevelSkillMap = staffingLevelIntervalLog.getStaffingLevelSkills().stream().collect(Collectors.toMap(k -> k.getSkillId(), v -> v));
        for (Long skillId : skillIdsToRemove) {
            if (staffingLevelSkillMap.containsKey(skillId)) {
                staffingLevelInterval.getStaffingLevelSkills().remove(staffingLevelSkillMap.get(skillId));
            }
        }
    }

    public static void initializeUserWiseLogs(StaffingLevelInterval staffingLevelInterval) {
        StaffingLevelIntervalLog staffingLevelIntervalLog = new StaffingLevelIntervalLog(new UserInfo(UserContext.getUserDetails().getId(), UserContext.getUserDetails().getEmail(), UserContext.getUserDetails().getFullName()), new Date(), staffingLevelInterval.getStaffingLevelActivities(), staffingLevelInterval.getStaffingLevelSkills());
        staffingLevelIntervalLog.setMinNoOfStaff(staffingLevelIntervalLog.getStaffingLevelActivities().stream().collect(Collectors.summingInt(k -> k.getMinNoOfStaff())));
        staffingLevelIntervalLog.setMaxNoOfStaff(staffingLevelIntervalLog.getStaffingLevelActivities().stream().collect(Collectors.summingInt(k -> k.getMaxNoOfStaff())));
        staffingLevelInterval.getStaffingLevelIntervalLogs().add(staffingLevelIntervalLog);
    }

    private static void updateStaffingLevelSkills(Set<StaffingLevelSkill> staffingLevelSkills) {
        for (StaffingLevelSkill staffingLevelSkill : staffingLevelSkills) {
            Map<SkillLevel, Integer> skillLevelIntegerMap = staffingLevelSkill.getSkillLevelSettings().stream().collect(Collectors.toMap(k -> k.getSkillLevel(), v -> v.getNoOfStaff()));
            for (SkillLevelSetting skillLevelSetting : staffingLevelSkill.getSkillLevelSettings()) {
                if (skillLevelIntegerMap.get(skillLevelSetting.getSkillLevel()) != skillLevelSetting.getNoOfStaff()) {
                    skillLevelSetting.setUpdatedAt(getCurrentDate());
                }
            }
        }
    }
}
