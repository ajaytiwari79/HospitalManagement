package com.kairos.persistence.repository.shift_violation;

import com.kairos.persistence.model.shift.ShiftViolatedRules;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

/**
 * @author pradeep
 * @date - 30/8/18
 */
@Repository
public interface ShiftViolatedRuleRepository extends MongoBaseRepository<ShiftViolatedRules, BigInteger>{
}
