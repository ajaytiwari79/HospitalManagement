package com.kairos.persistence.repository.counter;

import com.kairos.persistence.model.common.MongoBaseEntity;
import com.kairos.persistence.model.counter.FibonacciKPI;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;

import java.math.BigInteger;

public interface FibonacciKPIRepository extends MongoBaseRepository<FibonacciKPI, BigInteger> {
}
