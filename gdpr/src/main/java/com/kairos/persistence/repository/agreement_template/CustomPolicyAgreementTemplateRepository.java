package com.kairos.persistence.repository.agreement_template;

import com.kairos.persistence.model.agreement_template.AgreementSectionDeprecated;
import com.kairos.persistence.model.agreement_template.PolicyAgreementTemplateDeprecated;
import com.kairos.response.dto.clause.ClauseBasicResponseDTO;
import com.kairos.response.dto.policy_agreement.AgreementSectionResponseDTO;
import com.kairos.response.dto.policy_agreement.AgreementTemplateBasicResponseDTO;
import com.kairos.response.dto.policy_agreement.PolicyAgreementTemplateResponseDTO;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

public interface CustomPolicyAgreementTemplateRepository {


    List<PolicyAgreementTemplateResponseDTO> findAllTemplateByCountryIdOrUnitId(Long referenceId, boolean isUnitId);


    PolicyAgreementTemplateDeprecated findByCountryIdAndName(Long countryId, String templateName);

    PolicyAgreementTemplateDeprecated findByUnitIdAndName(Long unitId, String templateName);

    List<AgreementSectionResponseDTO> getAllAgreementSectionsAndSubSectionByReferenceIdAndAgreementTemplateId(Long referenceId, boolean isUnitId, BigInteger agreementTemplateId);

    List<AgreementTemplateBasicResponseDTO> findAllByReferenceIdAndClauseId(Long referenceId, boolean isUnitId, BigInteger clauseId);

    List<AgreementSectionDeprecated> getAllAgreementSectionAndSubSectionByReferenceIdAndClauseId(Long countryId, boolean isUnitId, Set<BigInteger> agreementTemplateIds, BigInteger clauseId);

    Set<BigInteger> getClauseIdListPresentInOtherTemplateByReferenceIdAndTemplateIdAndClauseIds(Long referenceId, boolean isUnitId, BigInteger templateId, Set<BigInteger> clauseIds);

    List<ClauseBasicResponseDTO> getAllClausesByAgreementTemplateIdNotEquals(Long referenceId, boolean isUnitId, BigInteger agreementTemplateId);

}
