package com.kairos.persistence.repository.agreement_template;

import com.kairos.response.dto.policy_agreement.AgreementSectionResponseDTO;

import java.math.BigInteger;
import java.util.Map;
import java.util.Set;

public interface CustomAgreementSectionRepository {

    AgreementSectionResponseDTO getAgreementSectionWithDataById(Long countryId,BigInteger id);

    Set<BigInteger> getClauseIdListPresentInAgreementSectionAndSubSectionsByCountryIdAndClauseIds(Long countryId, Set<BigInteger> clauseIds);



}
