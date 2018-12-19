package com.kairos.persistence.repository.shift;

import com.kairos.persistence.model.shift.ShiftViolatedRules;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

@Repository
public interface ShiftViolatedRulesMongoRepository extends MongoBaseRepository<ShiftViolatedRules,BigInteger> {

    @Query(value = "{'shift.id':{'$in':?0}}",fields ="{'workTimeAgreements':1,'shift.id':1}" )
    List<ShiftViolatedRules> findAllViolatedRulesByShiftIds(List<BigInteger> shiftIds);
}
