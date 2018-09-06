package com.kairos.persistance.repository.risk_management;

import com.kairos.persistance.model.risk_management.Risk;
import com.kairos.persistance.repository.custom_repository.MongoBaseRepository;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;


@Repository
@JaversSpringDataAuditable
public interface RiskMongoRepository extends MongoBaseRepository<Risk,BigInteger> {


    @Query("{deleted:false,countryId:?0,_id:?1}")
    Risk findByIdAndCountryId(Long countryId,BigInteger riskId);


    @Query("{deleted:false,_id:?0}")
    Risk findByIdAndNonDeleted(BigInteger riskId);




}
