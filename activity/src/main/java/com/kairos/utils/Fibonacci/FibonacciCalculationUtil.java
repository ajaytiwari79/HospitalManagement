package com.kairos.utils.Fibonacci;

import com.kairos.enums.kpi.Direction;
import com.kairos.persistence.model.counter.FibonacciKPICalculation;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeSet;

/**
 * pradeep
 * 22/4/19
 */
public class FibonacciCalculationUtil {

    public static TreeSet<FibonacciKPICalculation> getFibonacciCalculation(Map<Long,Integer> staffIdAndKPIDataMap, Direction sortingOrder){
        Comparator<FibonacciKPICalculation> fibonacciKPICalculationComparator = sortingOrder.isAscending() ? Comparator.naturalOrder() : Comparator.reverseOrder();
        TreeSet<FibonacciKPICalculation> fibonacciKPICalculations = new TreeSet<>(fibonacciKPICalculationComparator);
        int fibonacciFirstCount = 0;
        int fibonacciSecondCount = 1;
        for (Map.Entry<Long, Integer> staffIdAndDurationEntry : staffIdAndKPIDataMap.entrySet()) {
            fibonacciKPICalculations.add(new FibonacciKPICalculation(staffIdAndDurationEntry.getKey(),staffIdAndDurationEntry.getValue(),fibonacciFirstCount+fibonacciSecondCount));
            int fibonacciTempCount = fibonacciFirstCount;
            fibonacciFirstCount =fibonacciSecondCount;
            fibonacciSecondCount = fibonacciTempCount+fibonacciSecondCount;
        }
        return fibonacciKPICalculations;
    }
}
