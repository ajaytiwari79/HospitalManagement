package com.kairos.persistance.repository.agreement_template;


import com.kairos.persistance.model.agreement_template.AgreementSection;
import com.kairos.persistance.repository.custom_repository.MongoBaseRepository;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;


@Repository
@JaversSpringDataAuditable
public interface AgreementSectionMongoRepository extends MongoBaseRepository<AgreementSection, BigInteger>, CustomAgreementSectionRepository {

    @Query("{countryId:?0,organizationId:?1,_id:?2,deleted:false}")
    AgreementSection findByIdAndNonDeleted(Long countryId, Long orgId,BigInteger id);

    AgreementSection findByid(BigInteger id);


    @Query("{countryId:?0,organizationId:?1,_id:{$in:?2},deleted:false}")
    List<AgreementSection> findAgreementSectionByIds(Long countryId,Long organizationId,List<BigInteger> ids);


}
