package com.kairos.persistance.repository.agreement_template;

import com.kairos.response.dto.agreement_template.AgreementSectionResponseDto;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

public interface CustomAgreementSectionRepository {

    AgreementSectionResponseDto getAgreementSectionWithDataById(BigInteger id);

    List<AgreementSectionResponseDto> getAllAgreementSectionWithData();

    List<AgreementSectionResponseDto> getAgreementSectionWithDataList(Set<BigInteger> ids);

}
