package com.kairos.dto.activity.shift;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.HashMap;
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
    @NotNull(message = "error.startDate.notnull")
    private LocalDate date;
    private Map<Long, Set<BigInteger>> blockDetails=new HashMap<>();
    private Set<Long> blockedStaffForCoverShift;
    @NotNull(message = "error.startDate.notnull")
    private LocalDate endDate;
    private boolean unblockStaffs;
}
