package com.kairos.persistence.repository.risk_management;

import com.kairos.persistence.model.risk_management.Risk;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;


@Repository
@JaversSpringDataAuditable
public interface RiskMongoRepository extends MongoBaseRepository<Risk,BigInteger> {


    @Query("{deleted:false,countryId:?0,_id:?1}")
    Risk findByIdAndCountryId(Long countryId,BigInteger riskId);


    @Query("{deleted:false,_id:?0}")
    Risk findByIdAndNonDeleted(BigInteger riskId);

    @Query("{deleted:false,countryId:?0,_id:{$in:?1}}")
    List<Risk> findRiskByCountryIdAndIds(Long countryId, List<BigInteger> riskIds);

    @Query("{deleted:false,organizationId:?0,_id:{$in:?1}}")
    List<Risk> findRiskByUnitIdAndIds(Long unitId, List<BigInteger> riskIds);


    @Query("{deleted:false,organizationId:?0,_id:?1}")
    Risk findUnitIdAndId(Long unitId,BigInteger riskId);


}
