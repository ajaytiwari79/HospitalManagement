package com.kairos.persistence.repository.payroll_system;

import com.kairos.persistence.model.payroll_system.PayRollSystem;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
public interface PayRollSystemRepository extends MongoBaseRepository<PayRollSystem, BigInteger> {

}
