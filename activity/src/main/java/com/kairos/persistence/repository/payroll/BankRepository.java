package com.kairos.persistence.repository.payroll;/*
 *Created By Pavan on 17/12/18
 *
 */

import com.kairos.persistence.model.payroll.Bank;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
public interface BankRepository extends MongoBaseRepository<Bank,BigInteger> {
}
