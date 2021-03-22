package com.kairos.dto.activity.shift;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class CoverShift {
    private String commentForPlanner;
    private String commentForCandidates;
    private ApprovalBy approvalBy;
    private Set<Long> requestedStaffIds;


    private enum ApprovalBy{
        SELF,AUTO_PICK,PLANNER
    }
}
