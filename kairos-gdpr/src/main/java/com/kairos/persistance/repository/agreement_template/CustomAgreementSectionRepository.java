package com.kairos.persistance.repository.agreement_template;

import com.kairos.response.dto.agreement_template.AgreementSectionResponseDto;

import java.math.BigInteger;
import java.util.List;

public interface CustomAgreementSectionRepository {

    AgreementSectionResponseDto getAgreementSectionWithDataById(BigInteger id,Boolean deleted);

    List<AgreementSectionResponseDto> getAllAgreementSectionWithData();

}
