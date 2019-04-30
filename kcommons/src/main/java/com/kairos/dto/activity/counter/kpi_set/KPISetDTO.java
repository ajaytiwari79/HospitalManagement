package com.kairos.dto.activity.counter.kpi_set;
/*
 *Created By Pavan on 29/4/19
 *
 */

import com.kairos.commons.utils.NotNullOrEmpty;
import com.kairos.dto.activity.counter.enums.ConfLevel;
import com.kairos.enums.TimeTypeEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.Set;
@Getter
@Setter
@NoArgsConstructor
public class KPISetDTO {
    private BigInteger id;
    private String name;
    @NotNullOrEmpty(message = "message.kpi.absent")
    private Set<BigInteger> kpiIds;
    @NotNull(message = "time_type.absent")
    private TimeTypeEnum timeType;
    private BigInteger phaseId;
    private Long referenceId;
    private ConfLevel confLevel;
}
