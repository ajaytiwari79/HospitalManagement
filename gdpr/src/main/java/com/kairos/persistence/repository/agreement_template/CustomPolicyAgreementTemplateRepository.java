package com.kairos.persistence.repository.agreement_template;

import com.kairos.persistence.model.agreement_template.AgreementSection;
import com.kairos.persistence.model.agreement_template.PolicyAgreementTemplate;
import com.kairos.persistence.model.clause.Clause;
import com.kairos.response.dto.policy_agreement.AgreementSectionResponseDTO;
import com.kairos.response.dto.policy_agreement.AgreementTemplateBasicResponseDTO;
import com.kairos.response.dto.policy_agreement.PolicyAgreementTemplateResponseDTO;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

public interface CustomPolicyAgreementTemplateRepository {


    List<PolicyAgreementTemplateResponseDTO> findAllTemplateByCountryIdOrUnitId(Long referenceId, boolean isUnitId);

    PolicyAgreementTemplate findByCountryIdAndName(Long countryId, String templateName);

    //unit
    PolicyAgreementTemplate findByUnitIdAndName(Long unitId, String templateName);


    List<AgreementSectionResponseDTO> getAllAgreementSectionsAndSubSectionByReferenceIdAndAgreementTemplateId(Long countryId, boolean isUnitId, BigInteger agreementTemplateId);

    List<AgreementTemplateBasicResponseDTO> findAgreementTemplateListByCountryIdAndClauseId(Long countryId, BigInteger clauseId);

    List<AgreementSection> getAllAgreementSectionAndSubSectionByCountryIdAndClauseId(Long countryId, Set<BigInteger> agreementTemplateIds, BigInteger clauseId);

    Set<BigInteger> getListOfClausePresentInOtherAgreementTemplateSectionByCountryIdAndClauseId(Long countryId, BigInteger templateId, Set<BigInteger> clauseIds);

}
