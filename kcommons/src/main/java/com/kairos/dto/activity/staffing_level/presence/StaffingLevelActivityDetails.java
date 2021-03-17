package com.kairos.dto.activity.staffing_level.presence;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
@NoArgsConstructor
public class StaffingLevelActivityDetails {
    private BigInteger activityId;
    private int availableCount;
    private int initialUnderStaffing;
    private int initialOverStaffing;
    private int remainingUnderStaffing;
    private int remainingOverStaffing;
    private int solvedUnderStaffing;
    private int solvedOverStaffing;
    private int minNoOfStaff;
    private int maxNoOfStaff;

    @JsonIgnore
    public void resetValueOnPhaseFlip(int availableNoOfStaff,int minNoOfStaff,int maxNoOfStaff){
        this.availableCount = availableNoOfStaff;
        this.minNoOfStaff = minNoOfStaff;
        this.maxNoOfStaff = maxNoOfStaff;
        this.remainingOverStaffing = Math.max(availableNoOfStaff - maxNoOfStaff,0);
        this.solvedOverStaffing = 0;
        this.initialOverStaffing = remainingOverStaffing;
        this.remainingUnderStaffing = Math.max(minNoOfStaff - availableNoOfStaff,0);
        this.solvedUnderStaffing = 0;
        this.initialUnderStaffing = this.remainingUnderStaffing;
    }

    public StaffingLevelActivityDetails(BigInteger activityId) {
        this.activityId = activityId;
        this.availableCount = 0;
        this.initialUnderStaffing = 0;
        this.initialOverStaffing = 0;
        this.remainingUnderStaffing = 0;
        this.remainingOverStaffing = 0;
        this.solvedUnderStaffing = 0;
        this.solvedOverStaffing = 0;
        this.minNoOfStaff = 0;
        this.maxNoOfStaff = 0;
    }
}
