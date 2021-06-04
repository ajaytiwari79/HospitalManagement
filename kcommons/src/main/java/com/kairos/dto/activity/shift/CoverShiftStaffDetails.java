package com.kairos.dto.activity.shift;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CoverShiftStaffDetails {
    private List<CoverShiftDTO> totalInvitations;
    private List<CoverShiftDTO> totalInterests;
    private List<CoverShiftDTO> eligibleShift;
    private List<CoverShiftDTO> declinedRequests;
}
