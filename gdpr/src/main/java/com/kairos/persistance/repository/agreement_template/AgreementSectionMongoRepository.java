package com.kairos.persistance.repository.agreement_template;


import com.kairos.persistance.model.agreement_template.AgreementSection;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;


@Repository
@JaversSpringDataAuditable
public interface AgreementSectionMongoRepository extends MongoRepository<AgreementSection, BigInteger>, CustomAgreementSectionRepository {

    @Query("{_id:?0,deleted:false}")
    AgreementSection findByIdAndNonDeleted(BigInteger id);

    AgreementSection findByid(BigInteger id);


    @Query("{countryId:?0,_id:{$in:?1},deleted:false}")
    List<AgreementSection> findAgreementSectionByIds(Long countryId,Set<BigInteger> ids);


}
