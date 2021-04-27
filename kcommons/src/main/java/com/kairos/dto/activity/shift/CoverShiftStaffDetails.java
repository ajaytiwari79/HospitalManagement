package com.kairos.dto.activity.shift;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CoverShiftStaffDetails {
    private int noOfInvitation;
    private int noOfInterest;
    private int noOfEligibleShift;
    private int noOfDeclinedRequests;
}
