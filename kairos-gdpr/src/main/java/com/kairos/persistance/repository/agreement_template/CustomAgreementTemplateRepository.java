package com.kairos.persistance.repository.agreement_template;

import com.kairos.persistance.model.agreement_template.AgreementTemplate;
import com.kairos.persistance.model.agreement_template.response.dto.AgreementQueryResult;

import java.math.BigInteger;

public interface CustomAgreementTemplateRepository {

    AgreementQueryResult findById(BigInteger id);

}
