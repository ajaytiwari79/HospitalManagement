package com.kairos.persistance.repository.agreement_template;

import com.kairos.response.dto.AgreementQueryResult;

import java.math.BigInteger;

public interface CustomAgreementTemplateRepository {

    AgreementQueryResult findById(BigInteger id);

}
