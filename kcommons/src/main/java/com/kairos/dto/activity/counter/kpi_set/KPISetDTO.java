package com.kairos.dto.activity.counter.kpi_set;
/*
 *Created By Pavan on 29/4/19
 *
 */

import com.kairos.dto.activity.counter.enums.ConfLevel;
import com.kairos.enums.TimeTypeEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.util.Set;
@Getter
@Setter
@NoArgsConstructor
public class KPISetDTO {
    private BigInteger id;
    private String name;
    private Set<BigInteger> kpiIds;
    private TimeTypeEnum timeType;
    private BigInteger phaseId;
    private Long referenceId;
    private ConfLevel confLevel;
}
