package com.kairos.persistance.repository.agreement_template;

import com.kairos.persistance.model.agreement_template.PolicyAgreementTemplate;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.math.BigInteger;

@Repository
public interface PolicyAgreementTemplateRepository extends MongoRepository<PolicyAgreementTemplate,Serializable>,CustomPolicyAgreementTemplateRepository {

    @Query("{'countryId:?0,organizationId:?1,_id':?2,deleted:false}")
    PolicyAgreementTemplate findByIdAndNonDeleted(Long countryId,Long organizationId,BigInteger id);


    PolicyAgreementTemplate findByid(BigInteger id);

}
