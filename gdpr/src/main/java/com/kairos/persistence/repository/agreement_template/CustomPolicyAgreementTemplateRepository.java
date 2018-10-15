package com.kairos.persistence.repository.agreement_template;

import com.kairos.persistence.model.agreement_template.AgreementSection;
import com.kairos.persistence.model.agreement_template.PolicyAgreementTemplate;
import com.kairos.response.dto.policy_agreement.AgreementSectionResponseDTO;
import com.kairos.response.dto.policy_agreement.AgreementTemplateBasicResponseDTO;
import com.kairos.response.dto.policy_agreement.PolicyAgreementTemplateResponseDTO;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

public interface CustomPolicyAgreementTemplateRepository {



    List<PolicyAgreementTemplateResponseDTO>  getAllPolicyAgreementTemplateByCountryId(Long countryId);

    PolicyAgreementTemplate findByName(Long countryId,String templateName);

    List<AgreementSectionResponseDTO> getAgreementTemplateWithSectionsAndSubSections(Long countryId, BigInteger agreementTemplateId);

    List<AgreementTemplateBasicResponseDTO> findAgreementTemplateListByCountryIdAndClauseId(Long countryId, BigInteger clauseId);

    List<AgreementSection> getAllAgreementSectionAndSubSectionByCountryIdAndClauseId(Long countryId, Set<BigInteger> agreementTemplateIds,BigInteger clauseId);


}
