package com.kairos.persistance.repository.agreement_template;

import com.kairos.response.dto.agreement_template.PolicyAgreementTemplateResponseDto;

import java.math.BigInteger;

public interface CustomPolicyAgreementTemplateRepository {

    PolicyAgreementTemplateResponseDto getpolicyAgreementWithData(BigInteger id);

}
