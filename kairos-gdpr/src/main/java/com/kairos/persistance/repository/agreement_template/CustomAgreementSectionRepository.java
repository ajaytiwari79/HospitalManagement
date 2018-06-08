package com.kairos.persistance.repository.agreement_template;

import com.kairos.response.dto.agreement_template.AgreementSectionResponseDto;

import java.math.BigInteger;
import java.util.List;

public interface CustomAgreementSectionRepository {

    AgreementSectionResponseDto getAgreementSectionWithDataById(BigInteger id);

    List<AgreementSectionResponseDto> getAllAgreementSectionWithData(Long countryId);

    List<AgreementSectionResponseDto> getAgreementSectionWithDataList(Long countryId,List<BigInteger> ids);

}
