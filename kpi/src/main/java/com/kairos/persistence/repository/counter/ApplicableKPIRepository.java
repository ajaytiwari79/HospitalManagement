package com.kairos.persistence.repository.counter;

import com.kairos.persistence.model.ApplicableKPI;

import java.math.BigInteger;

/**
 * Created by pradeep
 * Created at 3/6/19
 **/
public interface ApplicableKPIRepository extends MongoBaseRepository<ApplicableKPI, BigInteger> {
}
