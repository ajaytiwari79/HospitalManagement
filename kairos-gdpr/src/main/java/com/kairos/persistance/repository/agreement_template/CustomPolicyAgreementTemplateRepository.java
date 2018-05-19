package com.kairos.persistance.repository.agreement_template;

import com.kairos.response.dto.agreement_template.AgreementQueryResult;

import java.math.BigInteger;

public interface CustomPolicyAgreementTemplateRepository {

    AgreementQueryResult findById(BigInteger id);

}
