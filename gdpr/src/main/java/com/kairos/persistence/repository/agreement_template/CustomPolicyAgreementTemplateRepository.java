package com.kairos.persistence.repository.agreement_template;

import com.kairos.persistence.model.agreement_template.PolicyAgreementTemplate;
import com.kairos.response.dto.policy_agreement.AgreementSectionResponseDTO;
import com.kairos.response.dto.policy_agreement.PolicyAgreementTemplateResponseDTO;

import java.math.BigInteger;
import java.util.List;

public interface CustomPolicyAgreementTemplateRepository {



    List<PolicyAgreementTemplateResponseDTO>  getAllPolicyAgreementTemplateByCountryId(Long countryId);

    PolicyAgreementTemplate findByName(Long countryId,String templateName);

    List<AgreementSectionResponseDTO> getAgreementTemplateWithSectionsAndSubSections(Long countryId, BigInteger agreementTemplateId);

    List<PolicyAgreementTemplate> findAgreementTemplatesByCurrentClauseIdAndCountryId(Long countryId,BigInteger clauseId);


}
