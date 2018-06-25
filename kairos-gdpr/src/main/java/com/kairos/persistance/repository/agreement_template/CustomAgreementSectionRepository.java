package com.kairos.persistance.repository.agreement_template;

import com.kairos.response.dto.master_data.AgreementSectionResponseDto;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

public interface CustomAgreementSectionRepository {

    AgreementSectionResponseDto getAgreementSectionWithDataById(BigInteger id);

    List<AgreementSectionResponseDto> getAllAgreementSectionWithData(Long countryId);

    List<AgreementSectionResponseDto> getAgreementSectionWithDataList(Long countryId,Set<BigInteger> ids);

}
