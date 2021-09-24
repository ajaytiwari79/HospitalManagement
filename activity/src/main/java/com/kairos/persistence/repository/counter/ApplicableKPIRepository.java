package com.kairos.persistence.repository.counter;

import com.kairos.persistence.model.counter.ApplicableKPI;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;

import java.math.BigInteger;

/**
 * Created by pradeep
 * Created at 3/6/19
 **/
public interface ApplicableKPIRepository extends MongoBaseRepository<ApplicableKPI, BigInteger> {
}
