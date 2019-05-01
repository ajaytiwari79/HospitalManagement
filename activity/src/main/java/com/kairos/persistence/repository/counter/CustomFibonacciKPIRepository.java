package com.kairos.persistence.repository.counter;

import com.kairos.dto.activity.counter.enums.ConfLevel;
import com.kairos.dto.activity.counter.fibonacci_kpi.FibonacciKPIDTO;

import java.math.BigInteger;
import java.util.List;

public interface CustomFibonacciKPIRepository {

    FibonacciKPIDTO getOneByfibonacciId(BigInteger fibonacciId);
    boolean existByName(BigInteger fibonacciId, String title, ConfLevel confLevel, Long referenceId);
}
