package com.kairos.persistance.repository.agreement_template;

import com.kairos.persistance.model.agreement_template.PolicyAgreementTemplate;
import com.kairos.response.dto.policy_agreement.PolicyAgreementTemplateResponseDTO;

import java.math.BigInteger;
import java.util.List;

public interface CustomPolicyAgreementTemplateRepository {

    PolicyAgreementTemplateResponseDTO getPolicyAgreementWithSectionsAndClausesById(Long countryId,Long orgId, BigInteger id);

    List<PolicyAgreementTemplateResponseDTO> getAllPolicyAgreementWithSectionsAndClauses(Long countryId,Long orgId);

    PolicyAgreementTemplate findByName(Long countryId,Long organizationId,String templateName);

}
