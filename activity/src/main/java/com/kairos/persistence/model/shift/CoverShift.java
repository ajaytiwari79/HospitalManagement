package com.kairos.persistence.model.shift;

import com.kairos.persistence.model.common.MongoBaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Getter
@Setter
public class CoverShift extends MongoBaseEntity {
    private BigInteger shiftId;
    private Long staffId;
    private String commentForPlanner;
    private String commentForCandidates;
    private ApprovalBy approvalBy;
    private Map<Long, Date> requestedStaffs=new LinkedHashMap<>();
    private Map<Long, Date> interestedStaffs=new LinkedHashMap<>();
    private Long assignedStaffId;

    public enum ApprovalBy{
        SELF,AUTO_PICK,PLANNER
    }
}
