package com.kairos.dto.activity.shift;

import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
public class CoverShiftDTO {
    private BigInteger id;
    private String commentForPlanner;
    private String commentForCandidates;
    private ApprovalBy approvalBy;
    private Map<Long, Date> requestedStaffs;
    private Map<Long, Date> interestedStaffs;
    private Long staffId;
    private BigInteger shiftId;
    private LocalDate date;
    private Set<Long> declinedStaffIds=new HashSet<>();

    private enum ApprovalBy{
        SELF,AUTO_PICK,PLANNER
    }
}
