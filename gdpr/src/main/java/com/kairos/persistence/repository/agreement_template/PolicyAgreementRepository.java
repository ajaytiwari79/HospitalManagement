package com.kairos.persistence.repository.agreement_template;

import com.kairos.persistence.model.agreement_template.PolicyAgreementTemplate;
import com.kairos.persistence.model.agreement_template.PolicyAgreementTemplateMD;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import com.kairos.persistence.repository.master_data.processing_activity_masterdata.CustomGenericRepository;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
//@JaversSpringDataAuditable
public interface PolicyAgreementRepository extends CustomGenericRepository<PolicyAgreementTemplateMD> {


}
