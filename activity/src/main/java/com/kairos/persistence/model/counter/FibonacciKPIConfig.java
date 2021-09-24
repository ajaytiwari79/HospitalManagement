package com.kairos.persistence.model.counter;



import com.kairos.enums.kpi.Direction;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
public class FibonacciKPIConfig {

    private BigInteger kpiId;
    private int impactWeight;
    private Direction sortingOrder;

}
