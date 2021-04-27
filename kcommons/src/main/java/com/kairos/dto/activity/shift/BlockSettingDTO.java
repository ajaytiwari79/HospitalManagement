package com.kairos.dto.activity.shift;

import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Map;
import java.util.Set;

/**
 * Created By G.P.Ranjan on 3/12/19
 **/
@Getter
@Setter
public class BlockSettingDTO {
    private BigInteger id;
    private Long unitId;
    private LocalDate date;
    private Map<Long, Set<BigInteger>> blockDetails;
    private Set<Long> blockedStaffForCoverShift;
    private LocalDate endDate;
    private boolean unblockStaffs;
}
