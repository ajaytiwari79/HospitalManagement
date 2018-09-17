package com.kairos.persistence.repository.agreement_template;


import com.kairos.persistence.model.agreement_template.AgreementSection;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;


@Repository
@JaversSpringDataAuditable
public interface AgreementSectionMongoRepository extends MongoBaseRepository<AgreementSection, BigInteger>, CustomAgreementSectionRepository {

    @Query("{countryId:?0,_id:?1,deleted:false}")
    AgreementSection findByIdAndCountryId(Long countryId, BigInteger id);

    AgreementSection findByid(BigInteger id);


    @Query("{countryId:?0,_id:{$in:?1},deleted:false}")
    List<AgreementSection> findAgreementSectionByIds(Long countryId,List<BigInteger> ids);


}
