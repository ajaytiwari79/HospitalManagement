package com.kairos.persistance.repository.agreement_template;

import com.kairos.response.dto.master_data.PolicyAgreementTemplateResponseDto;

import java.math.BigInteger;
import java.util.List;

public interface CustomPolicyAgreementTemplateRepository {

    PolicyAgreementTemplateResponseDto getPolicyAgreementWithDataById(Long countryId,BigInteger id);

    List<PolicyAgreementTemplateResponseDto> getPolicyAgreementWithData(Long countryId);

}
