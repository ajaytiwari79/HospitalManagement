package com.kairos.dto.activity.staffing_level;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
public class StaffingLevelInterval {
    private int sequence;
    private int minNoOfStaff;
    private int maxNoOfStaff;
    private int availableNoOfStaff;
    private Duration staffingLevelDuration;
    private Set<StaffingLevelActivity> staffingLevelActivities=new LinkedHashSet<>();
    private Set<StaffingLevelSkill> staffingLevelSkills=new HashSet<>();

    public StaffingLevelInterval() {
        // default constructor
    }

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

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public int getMinNoOfStaff() {
        return minNoOfStaff;
    }

    public void setMinNoOfStaff(Integer minNoOfStaff) {
        this.minNoOfStaff = minNoOfStaff;
    }

    public int getMaxNoOfStaff() {
        return maxNoOfStaff;
    }

    public void setMaxNoOfStaff(Integer maxNoOfStaff) {
        this.maxNoOfStaff = maxNoOfStaff;
    }

    public int getAvailableNoOfStaff() {
        return availableNoOfStaff;
    }

    public void setAvailableNoOfStaff(int availableNoOfStaff) {
        this.availableNoOfStaff = availableNoOfStaff;
    }

    public Duration getStaffingLevelDuration() {
        return staffingLevelDuration;
    }

    public void setStaffingLevelDuration(Duration staffingLevelDuration) {
        this.staffingLevelDuration = staffingLevelDuration;
    }

    public Set<StaffingLevelActivity> getStaffingLevelActivities() {
        return staffingLevelActivities;
    }

    public void setStaffingLevelActivities(Set<StaffingLevelActivity> staffingLevelActivities) {
        this.staffingLevelActivities = staffingLevelActivities;
    }

    public Set<StaffingLevelSkill> getStaffingLevelSkills() {
        return staffingLevelSkills;
    }

    public void setStaffingLevelSkills(Set<StaffingLevelSkill> staffingLevelSkills) {
        this.staffingLevelSkills = staffingLevelSkills;
    }

    public void addStaffLevelActivity(StaffingLevelActivity staffLevelActivity) {
        if (staffLevelActivity == null)
            throw new NullPointerException("Can't add null staffLevelActivity");

        this.getStaffingLevelActivities().add(staffLevelActivity);

    }

    public void addStaffLevelActivity(Set<StaffingLevelActivity> staffLevelActivitys) {
        if (staffLevelActivitys == null)
            throw new NullPointerException("Can't add null staffLevelActivity");

        this.getStaffingLevelActivities().addAll(staffLevelActivitys);

    }


    public void addStaffLevelSkill(StaffingLevelSkill staffLevelSkill) {

        if (staffLevelSkill == null)
            throw new NullPointerException("Can't add null staffLevelActivity");
        this.getStaffingLevelSkills().add(staffLevelSkill);

    }

    public void addStaffLevelSkill(Set<StaffingLevelSkill> staffLevelSkills) {

        if (staffLevelSkills == null)
            throw new NullPointerException("Can't add null staffLevelActivity");
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
