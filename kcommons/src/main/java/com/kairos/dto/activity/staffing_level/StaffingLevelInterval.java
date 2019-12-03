package com.kairos.dto.activity.staffing_level;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
@Getter
@Setter
@NoArgsConstructor
public class StaffingLevelInterval {
    public static final String CAN_T_ADD_NULL_STAFF_LEVEL_ACTIVITY = "Can't add null staffLevelActivity";
    private int sequence;
    private int minNoOfStaff;
    private int maxNoOfStaff;
    private int availableNoOfStaff;
    private Duration staffingLevelDuration;
    private Set<StaffingLevelActivity> staffingLevelActivities=new LinkedHashSet<>();
    private Set<StaffingLevelSkill> staffingLevelSkills=new HashSet<>();



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

    public void addStaffLevelActivity(Set<StaffingLevelActivity> staffLevelActivitys) {
        if (staffLevelActivitys == null)
            throw new NullPointerException(CAN_T_ADD_NULL_STAFF_LEVEL_ACTIVITY);

        this.getStaffingLevelActivities().addAll(staffLevelActivitys);

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
