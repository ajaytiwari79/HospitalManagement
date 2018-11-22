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
    AgreementSection findByCountryIdAndId(Long countryId, BigInteger sectionId);

    @Query("{organizationId:?0,_id:?1,deleted:false}")
    AgreementSection findByUnitIdAndId(Long unitId, BigInteger sectionId);

    AgreementSection findByid(BigInteger id);

    @Query("{countryId:?0,_id:{$in:?1},deleted:false}")
    List<AgreementSection> findAllByCountryIdAndIds(Long countryId, List<BigInteger> ids);

    @Query("{organizationId:?0,_id:{$in:?1},deleted:false}")
    List<AgreementSection> findAllByUnitIdAndIds(Long countryId,List<BigInteger> ids);



}
