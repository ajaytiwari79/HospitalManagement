package com.kairos.persistence.model.counter;

import com.kairos.enums.kpi.Direction;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.math.BigInteger;
import java.util.Comparator;
import java.util.Objects;

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
    private BigInteger fibonacciKpiCount;
    private int impactWeight;
    private Direction sortingOrder;
    private int orderValueByFiboncci;

    public FibonacciKPICalculation(Long staffId, int value) {
        this.staffId = staffId;
        this.value = value;
    }

    public FibonacciKPICalculation(int orderValueByFiboncci,Long staffId) {
        this.staffId = staffId;
        this.orderValueByFiboncci = orderValueByFiboncci;
        this.fibonacciKpiCount = new BigInteger("0");
    }


    public Integer getValue() {
        return value;
    }

    @Override
    public int compareTo(Object o) {
        FibonacciKPICalculation fibonacciKPICalculation = (FibonacciKPICalculation)o;
        return new CompareToBuilder()
                .append(this.value, fibonacciKPICalculation.value)
                .append(this.staffId, fibonacciKPICalculation.staffId).toComparison();
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof FibonacciKPICalculation)) return false;
        FibonacciKPICalculation that = (FibonacciKPICalculation) o;
        return new EqualsBuilder().append(value, that.value).append(fibonacciKpiCount, that.fibonacciKpiCount).append(impactWeight, that.impactWeight).append(kpiId, that.kpiId).append(staffId, that.staffId).append(sortingOrder, that.sortingOrder).isEquals();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(staffId);
    }
}
