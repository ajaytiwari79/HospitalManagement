package com.kairos.persistence.model.counter;

import com.kairos.enums.kpi.Direction;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.Comparator;

/**
 * pradeep
 * 22/4/19
 */

@Getter
@Setter
public class FibonacciKPICalculation implements Comparable{
    private BigInteger kpiId;
    private Long staffId;
    private int value;
    private int fibonacciKpiCount;
    private int impactWeight;
    private Direction sortingOrder;

    public FibonacciKPICalculation(Long staffId, int value,int fibonacciKpiCount) {
        this.staffId = staffId;
        this.value = value;
        this.fibonacciKpiCount = fibonacciKpiCount;
    }

    @Override
    public int compareTo(Object o) {
        return Comparator.comparingInt(FibonacciKPICalculation::getValue).compare(this,(FibonacciKPICalculation)o);
    }
}
