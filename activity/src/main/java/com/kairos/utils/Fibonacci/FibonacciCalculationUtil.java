package com.kairos.utils.Fibonacci;

import com.kairos.enums.kpi.Direction;
import com.kairos.persistence.model.counter.FibonacciKPICalculation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeSet;

/**
 * pradeep
 * 22/4/19
 */
public class FibonacciCalculationUtil {

    private final static Logger LOGGER = LoggerFactory.getLogger(FibonacciCalculationUtil.class);

    public static TreeSet<FibonacciKPICalculation> getFibonacciCalculation(Map<Long,Integer> staffIdAndKPIDataMap, Direction sortingOrder){
        Comparator<FibonacciKPICalculation> fibonacciKPICalculationComparator = sortingOrder.isAscending() ? Comparator.naturalOrder() : Comparator.reverseOrder();
        TreeSet<FibonacciKPICalculation> fibonacciKPICalculations = new TreeSet<>(fibonacciKPICalculationComparator);
        for (Map.Entry<Long, Integer> staffIdAndDurationEntry : staffIdAndKPIDataMap.entrySet()) {
            FibonacciKPICalculation fibonacciKPICalculation = new FibonacciKPICalculation(staffIdAndDurationEntry.getKey(),staffIdAndDurationEntry.getValue());
            fibonacciKPICalculations.add(fibonacciKPICalculation);
        }
        BigInteger fibonacciFirstCount = new BigInteger("0");
        BigInteger fibonacciTotalCount = new BigInteger("1");
        for (FibonacciKPICalculation fibonacciKPICalculation : fibonacciKPICalculations) {
            fibonacciKPICalculation.setFibonacciKpiCount(fibonacciTotalCount);
            LOGGER.info("fibonacci counter {}",fibonacciTotalCount);
            BigInteger fibonacciTempCount = fibonacciFirstCount;
            fibonacciFirstCount = fibonacciTotalCount;
            fibonacciTotalCount = fibonacciTempCount.add(fibonacciTotalCount);
        }
        return fibonacciKPICalculations;
    }
}
