package com.kairos.dto.activity.counter.fibonacci_kpi;

import com.kairos.enums.kpi.Direction;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigInteger;

/**
 * pradeep
 * 12/4/19
 */
@Getter
@Setter
public class FibonacciKPIConfigDTO {

    private BigInteger kpiId;
    @Positive(message = "message.fibonacci.impactWeight")
    private int impactWeight;
    @NotNull(message = "message.fibonacci.sorting.order")
    private Direction sortingOrder;


}
