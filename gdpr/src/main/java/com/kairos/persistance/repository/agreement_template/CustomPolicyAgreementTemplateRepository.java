package com.kairos.persistance.repository.agreement_template;

import com.kairos.persistance.model.agreement_template.PolicyAgreementTemplate;
import com.kairos.response.dto.policy_agreement.AgreementSectionResponseDTO;
import com.kairos.response.dto.policy_agreement.PolicyAgreementTemplateResponseDTO;

import java.math.BigInteger;
import java.util.List;

public interface CustomPolicyAgreementTemplateRepository {



    List<PolicyAgreementTemplateResponseDTO>  getAllPolicyAgreementTemplateByCountryId(Long countryId,Long unitId);

    PolicyAgreementTemplate findByName(Long countryId,Long organizationId,String templateName);

    List<AgreementSectionResponseDTO> getAgreementTemplateWithSectionsAndSubSections(Long countryId, Long unitId, BigInteger agreementTemplateId);


}
