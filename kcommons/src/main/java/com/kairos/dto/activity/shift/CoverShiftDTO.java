package com.kairos.dto.activity.shift;

import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Date;
import java.util.Map;

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
    private Map<Long,Date> declinedStaffIds;
    private int deltaTimeBankMinutes;
    private long totalPayOutMinutes;

    private enum ApprovalBy{
        SELF,AUTO_PICK,PLANNER
    }
}
