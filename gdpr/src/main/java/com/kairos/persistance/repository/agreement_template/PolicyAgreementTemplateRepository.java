package com.kairos.persistance.repository.agreement_template;

import com.kairos.persistance.model.agreement_template.PolicyAgreementTemplate;
import com.kairos.persistance.repository.custom_repository.MongoBaseRepository;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
@JaversSpringDataAuditable
public interface PolicyAgreementTemplateRepository extends MongoBaseRepository<PolicyAgreementTemplate,BigInteger>,CustomPolicyAgreementTemplateRepository {

    @Query("{countryId:?0,organizationId:?1,_id:?2,deleted:false}")
    PolicyAgreementTemplate findByIdAndNonDeleted(Long countryId,Long organizationId,BigInteger id);


    PolicyAgreementTemplate findByid(BigInteger id);

}
