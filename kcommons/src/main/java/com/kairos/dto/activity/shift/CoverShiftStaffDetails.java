package com.kairos.dto.activity.shift;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CoverShiftStaffDetails {
    private Set<BigInteger> totalInvitations;
    private Set<BigInteger> totalInterests;
    private Set<BigInteger> eligibleShift;
    private Set<BigInteger> declinedRequests;
}
