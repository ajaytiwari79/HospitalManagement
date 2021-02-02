package com.kairos.dto.activity.staffing_level;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.math.BigInteger;
import java.util.Date;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class StaffingLevelActivity {

    private String name;
    private BigInteger activityId;
    private boolean includeInMin;
    private int minNoOfStaff;
    private int maxNoOfStaff;
    private  int availableNoOfStaff;
    private Date minUpdatedAt;
    private Date maxUpdatedAt;
    private int initialUnderStaffing;
    private int initialOverStaffing;
    private int remainingUnderStaffing;
    private int remainingOverStaffing;
    private int solvedUnderStaffing;
    private int solvedOverStaffing;


    public StaffingLevelActivity(BigInteger activityId, int minNoOfStaff, int maxNoOfStaff) {
        this.activityId = activityId;
        this.minNoOfStaff = minNoOfStaff;
        this.maxNoOfStaff = maxNoOfStaff;
    }

    public StaffingLevelActivity(String name, int minNoOfStaff, int maxNoOfStaff) {
        this.name = name;
        this.minNoOfStaff = minNoOfStaff;
        this.maxNoOfStaff = maxNoOfStaff;
    }

    public StaffingLevelActivity(BigInteger activityId, String name, int minNoOfStaff, int maxNoOfStaff) {
        this.activityId = activityId;
        this.name = name;
        this.minNoOfStaff = minNoOfStaff;
        this.maxNoOfStaff = maxNoOfStaff;
    }

    public void setAvailableNoOfStaff(int availableNoOfStaff) {
        this.availableNoOfStaff = Math.max(availableNoOfStaff,0);
    }

    @JsonIgnore
    public void resetValueOnPhaseFlip(){
        this.remainingOverStaffing = Math.max(this.availableNoOfStaff - this.maxNoOfStaff,0);
        this.solvedOverStaffing = 0;
        this.initialOverStaffing = remainingOverStaffing;
        this.remainingUnderStaffing = Math.max(this.minNoOfStaff - this.availableNoOfStaff,0);
        this.solvedUnderStaffing = 0;
        this.initialUnderStaffing = this.remainingUnderStaffing;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof StaffingLevelActivity)) return false;

        StaffingLevelActivity that = (StaffingLevelActivity) o;

        return new EqualsBuilder()
                .append(activityId, that.activityId)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return Objects.hash(activityId);
    }


    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("activityId", activityId)
                .toString();
    }

}
