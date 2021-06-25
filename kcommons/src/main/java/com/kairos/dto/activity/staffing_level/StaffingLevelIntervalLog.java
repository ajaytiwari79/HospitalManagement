package com.kairos.dto.activity.staffing_level;

import com.kairos.dto.activity.common.UserInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StaffingLevelIntervalLog implements Comparable<StaffingLevelIntervalLog>{
    private UserInfo userInfo;
    private int minNoOfStaff;
    private int maxNoOfStaff;
    private Date updatedAt;
    private Set<StaffingLevelActivity> staffingLevelActivities=new LinkedHashSet<>();
    private Set<StaffingLevelSkill> staffingLevelSkills=new HashSet<>();
    private Set<ActivityRemoveLog> activityRemoveLogs=new HashSet<>();
    private Set<SkillRemoveLog> skillRemoveLogs=new HashSet<>();
    private Set<BigInteger> newlyAddedActivityIds=new HashSet<>();
    private Set<Long> newlyAddedSkillIds=new HashSet<>();


    public StaffingLevelIntervalLog(UserInfo userInfo, Date updatedAt, Set<StaffingLevelActivity> staffingLevelActivities, Set<StaffingLevelSkill> staffingLevelSkills) {
        this.userInfo = userInfo;
        this.updatedAt = updatedAt;
        this.staffingLevelActivities = staffingLevelActivities;
        this.staffingLevelSkills = staffingLevelSkills;
    }

    public Set<BigInteger> getActivityIds() {
        return staffingLevelActivities.stream().map(staffingLevelActivity -> staffingLevelActivity.getActivityId()).collect(Collectors.toSet());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StaffingLevelIntervalLog that = (StaffingLevelIntervalLog) o;
        return Objects.equals(updatedAt, that.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(updatedAt);
    }

    @Override
    public int compareTo(StaffingLevelIntervalLog staffingLevelIntervalLog) {
        return this.getUpdatedAt().compareTo(staffingLevelIntervalLog.getUpdatedAt());
    }

    public Set<StaffingLevelSkill> getStaffingLevelSkills() {
        staffingLevelSkills.removeIf(Objects::isNull);
        return staffingLevelSkills;
    }
}
