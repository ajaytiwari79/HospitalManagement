package com.kairos.dto.activity.staffing_level;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.data.annotation.Transient;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.ObjectUtils.isCollectionNotEmpty;

@Getter
@Setter
@NoArgsConstructor
public class StaffingLevelInterval {
    public static final String CAN_T_ADD_NULL_STAFF_LEVEL_ACTIVITY = "Can't add null staffLevelActivity";
    private int sequence;
    private int minNoOfStaff;
    private int maxNoOfStaff;
    @Transient
    private int availableNoOfStaff;
    private Duration staffingLevelDuration;
    private Set<StaffingLevelActivity> staffingLevelActivities=new LinkedHashSet<>();
    private Set<StaffingLevelSkill> staffingLevelSkills=new HashSet<>();
    private TreeSet<StaffingLevelIntervalLog> staffingLevelIntervalLogs=new TreeSet<>();
    private Set<BigInteger> activityIds=new HashSet<>();



    public StaffingLevelInterval(int minNoOfStaff, int maxNoOfStaff, Duration staffingLevelDuration) {
        this.minNoOfStaff = minNoOfStaff;
        this.maxNoOfStaff = maxNoOfStaff;
        this.staffingLevelDuration = staffingLevelDuration;
    }

    public StaffingLevelInterval(int minNoOfStaff, int maxNoOfStaff, Duration staffingLevelDuration, int availableNoOfStaff) {
        this.minNoOfStaff = minNoOfStaff;
        this.maxNoOfStaff = maxNoOfStaff;
        this.staffingLevelDuration = staffingLevelDuration;
        this.availableNoOfStaff = availableNoOfStaff;
    }

    public StaffingLevelInterval(int sequence, int minNoOfStaff, int maxNoOfStaff,
                                 Duration staffingLevelDuration) {
        this.sequence=sequence;
        this.minNoOfStaff = minNoOfStaff;
        this.maxNoOfStaff = maxNoOfStaff;
        this.staffingLevelDuration = staffingLevelDuration;
    }

    public StaffingLevelInterval(int sequence, Duration staffingLevelDuration) {
        this.sequence = sequence;
        this.staffingLevelDuration = staffingLevelDuration;
    }

    public void addStaffLevelActivity(Set<StaffingLevelActivity> staffLevelActivitys) {
        if (staffLevelActivitys == null)
            throw new NullPointerException(CAN_T_ADD_NULL_STAFF_LEVEL_ACTIVITY);

        this.getStaffingLevelActivities().addAll(staffLevelActivitys);

    }

    //This Getter is used for Unity Graph don't remove it
    public StaffingLevelIntervalLog getUnpublishChanges(){
        if(isCollectionNotEmpty(this.staffingLevelIntervalLogs)){
            return this.staffingLevelIntervalLogs.last();
        }
        return null;
    }

    public Set<BigInteger> getActivityIds(){
        return staffingLevelActivities.stream().map(staffingLevelActivity -> staffingLevelActivity.getActivityId()).collect(Collectors.toSet());
    }

    public StaffingLevelActivity getStaffingLevelActivity(BigInteger activityId){
        return staffingLevelActivities.stream().filter(staffingLevelActivity -> staffingLevelActivity.getActivityId().equals(activityId)).findAny().orElse(null);
    }



    public void addStaffLevelSkill(Set<StaffingLevelSkill> staffLevelSkills) {

        if (staffLevelSkills == null)
            throw new NullPointerException(CAN_T_ADD_NULL_STAFF_LEVEL_ACTIVITY);
        this.getStaffingLevelSkills().addAll(staffLevelSkills);

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof StaffingLevelInterval)) return false;

        StaffingLevelInterval that = (StaffingLevelInterval) o;

        return new EqualsBuilder()
                .append(sequence, that.sequence)
                .append(minNoOfStaff, that.minNoOfStaff)
                .append(maxNoOfStaff, that.maxNoOfStaff)
                .append(staffingLevelDuration, that.staffingLevelDuration)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(sequence)
                .append(minNoOfStaff)
                .append(maxNoOfStaff)
                .append(staffingLevelDuration)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("sequence", sequence)
                .append("minNoOfStaff", minNoOfStaff)
                .append("maxNoOfStaff", maxNoOfStaff)
                .append("availableNoOfStaff", availableNoOfStaff)
                .append("staffingLevelDuration", staffingLevelDuration)
                .append("staffingLevelActivities", staffingLevelActivities)
                .append("staffingLevelSkills", staffingLevelSkills)
                .toString();
    }


}
