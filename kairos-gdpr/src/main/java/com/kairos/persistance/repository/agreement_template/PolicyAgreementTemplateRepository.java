package com.kairos.persistance.repository.agreement_template;

import com.kairos.persistance.model.agreement_template.PolicyAgreementTemplate;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.math.BigInteger;

@Repository
public interface PolicyAgreementTemplateRepository extends MongoRepository<PolicyAgreementTemplate,Serializable>,CustomPolicyAgreementTemplateRepository {

    @Query("{'_id':?0,deleted:false}")
    PolicyAgreementTemplate findByIdAndNonDeleted(BigInteger id);

    PolicyAgreementTemplate findByName(String name);

    PolicyAgreementTemplate findByid(BigInteger id);

}
