package com.kairos.persistence.repository.agreement_template;

import com.kairos.persistence.model.agreement_template.PolicyAgreementTemplate;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
@JaversSpringDataAuditable
public interface PolicyAgreementTemplateRepository extends MongoBaseRepository<PolicyAgreementTemplate,BigInteger>,CustomPolicyAgreementTemplateRepository {

    @Query("{countryId:?0,_id:?1,deleted:false}")
    PolicyAgreementTemplate findByIdAndCountryId(Long countryId, BigInteger id);

    PolicyAgreementTemplate findByid(BigInteger id);


}
