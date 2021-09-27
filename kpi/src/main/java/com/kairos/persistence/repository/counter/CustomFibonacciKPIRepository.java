package com.kairos.persistence.repository.counter;

import com.kairos.dto.activity.counter.configuration.KPIDTO;
import com.kairos.dto.activity.counter.enums.ConfLevel;

import java.math.BigInteger;

public interface CustomFibonacciKPIRepository {

    KPIDTO getOneByfibonacciId(BigInteger fibonacciId,Long referenceId,ConfLevel confLevel);
    boolean existByName(BigInteger fibonacciId, String title, ConfLevel confLevel, Long referenceId);
}
