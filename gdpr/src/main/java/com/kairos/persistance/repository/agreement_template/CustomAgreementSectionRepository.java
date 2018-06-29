package com.kairos.persistance.repository.agreement_template;

import com.kairos.response.dto.master_data.AgreementSectionResponseDTO;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

public interface CustomAgreementSectionRepository {

    AgreementSectionResponseDTO getAgreementSectionWithDataById(BigInteger id);

    List<AgreementSectionResponseDTO> getAllAgreementSectionWithData(Long countryId);

    List<AgreementSectionResponseDTO> getAgreementSectionWithDataList(Long countryId, Set<BigInteger> ids);

}
