package com.kairos.dto.activity.counter.fibonacci_kpi;

import com.kairos.enums.kpi.Direction;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

/**
 * pradeep
 * 12/4/19
 */
@Getter
@Setter
public class FibinacciKPIConfigDTO {

    private BigInteger kpiId;
    private int impactWeight;
    private Direction sortingOrder;


}
