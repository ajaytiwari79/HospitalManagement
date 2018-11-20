package com.kairos.persistence.repository.agreement_template;

import com.kairos.response.dto.policy_agreement.AgreementSectionResponseDTO;

import java.math.BigInteger;

public interface CustomAgreementSectionRepository {

    AgreementSectionResponseDTO getAgreementSectionWithDataById(Long countryId,BigInteger id);

    //todo required if we have to find section containing clausesId
   // Set<BigInteger> getClauseIdListPresentInAgreementSectionAndSubSectionsByCountryIdAndClauseIds(Long countryId, Set<BigInteger> clauseIds);



}
