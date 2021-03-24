package com.kairos.dto.activity.shift;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.Map;

@Getter
@Setter
public class CoverShift {
    private String commentForPlanner;
    private String commentForCandidates;
    private ApprovalBy approvalBy;
    private Map<Long, Date> requestedStaffs;


    private enum ApprovalBy{
        SELF,AUTO_PICK,PLANNER
    }
}
