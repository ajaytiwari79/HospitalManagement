package com.kairos.dto.activity.staffing_level;

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
    private int availableNoOfStaff;
    private Date minUpdatedAt;
    private Date maxUpdatedAt;
    private int initialUnderStaffing;
    private int initialOverStaffing;
    private int remainingUnderStaffingToResolve;
    private int remainingOverStaffingToResolve;
    private int resolvedUnderStaffingAfterPublish;
    private int resolvedOverStaffingAfterPublish;



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

    public StaffingLevelActivity(BigInteger activityId, int minNoOfStaff, int maxNoOfStaff) {
        this.activityId = activityId;
        this.minNoOfStaff = minNoOfStaff;
        this.maxNoOfStaff = maxNoOfStaff;
    }

    public void setInitialStaffingLevelDetails(){
        this.initialUnderStaffing = Math.max(this.minNoOfStaff-this.availableNoOfStaff,0);
        this.initialOverStaffing =  Math.min(this.availableNoOfStaff-this.maxNoOfStaff,0);
        this.remainingUnderStaffingToResolve=0;
        this.remainingOverStaffingToResolve=0;
        this.resolvedUnderStaffingAfterPublish=0;
        this.resolvedOverStaffingAfterPublish=0;

    }

    public void setStaffingLevelDetails(){
        this.remainingUnderStaffingToResolve=Math.max(minNoOfStaff-availableNoOfStaff,0);
        this.remainingOverStaffingToResolve=Math.max(availableNoOfStaff-maxNoOfStaff,0);
        this.resolvedUnderStaffingAfterPublish=Math.min(availableNoOfStaff+initialUnderStaffing-minNoOfStaff,initialUnderStaffing);
        this.resolvedOverStaffingAfterPublish=Math.min(maxNoOfStaff+initialOverStaffing-availableNoOfStaff,initialOverStaffing);
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
