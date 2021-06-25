package com.kairos.dto.activity.shift;

import com.kairos.dto.activity.pay_out.PayOutPerShiftCTADistributionDTO;
import com.kairos.dto.activity.time_bank.TimeBankCTADistributionDTO;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class CoverShiftDTO {
    private BigInteger id;
    private String commentForPlanner;
    private String commentForCandidates;
    private ApprovalBy approvalBy;
    private Map<Long, Date> requestedStaffs;
    private Map<Long, StaffInterest> interestedStaffs;
    private Long staffId;
    private BigInteger shiftId;
    private LocalDate date;
    private Map<Long,Date> declinedStaffIds;
    private int deltaTimeBankMinutes;
    private long totalPayOutMinutes;
    private List<PayOutPerShiftCTADistributionDTO> payOutPerShiftCTADistributions = new ArrayList<>();
    private List<TimeBankCTADistributionDTO> timeBankCTADistributionList=new ArrayList<>();

    private enum ApprovalBy{
        SELF,AUTO_PICK,PLANNER
    }
}
