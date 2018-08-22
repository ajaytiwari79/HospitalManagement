package com.kairos.persistance.repository.agreement_template;

import com.kairos.response.dto.policy_agreement.AgreementSectionResponseDTO;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

public interface CustomAgreementSectionRepository {

    AgreementSectionResponseDTO getAgreementSectionWithDataById(Long countryId,BigInteger id);



}
