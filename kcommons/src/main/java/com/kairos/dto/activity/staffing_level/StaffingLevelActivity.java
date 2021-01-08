package com.kairos.dto.activity.staffing_level;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.data.annotation.Transient;

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

    public int getInitialUnderStaffing() {
        return Math.min(initialUnderStaffing,availableNoOfStaff);
    }

    public int getInitialOverStaffing() {
        return Math.min(initialOverStaffing,availableNoOfStaff);
    }

    public StaffingLevelActivity(BigInteger activityId, int minNoOfStaff, int maxNoOfStaff) {
        this.activityId = activityId;
        this.minNoOfStaff = minNoOfStaff;
        this.maxNoOfStaff = maxNoOfStaff;
    }

    public int getRemainingUnderStaffingToResolve(){
        return Math.max(minNoOfStaff-availableNoOfStaff,0);
    }

    public int getRemainingOverStaffingToResolve(){
        return Math.max(availableNoOfStaff-maxNoOfStaff,0);
    }


    public int getResolvedUnderStaffingAfterPublish(){
        return Math.min(availableNoOfStaff - initialUnderStaffing,0);
    }

    public int getResolvedOverStaffingAfterPublish(){
        return Math.min(initialOverStaffing-availableNoOfStaff,0);
    }

    public void setAvailableNoOfStaff(int availableNoOfStaff) {
        this.availableNoOfStaff = Math.max(availableNoOfStaff,0);
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
