package com.kairos.persistance.repository.agreement_template;

import com.kairos.response.dto.master_data.PolicyAgreementTemplateResponseDTO;

import java.math.BigInteger;
import java.util.List;

public interface CustomPolicyAgreementTemplateRepository {

    PolicyAgreementTemplateResponseDTO getPolicyAgreementWithDataById(Long countryId, BigInteger id);

    List<PolicyAgreementTemplateResponseDTO> getPolicyAgreementWithData(Long countryId);

}
