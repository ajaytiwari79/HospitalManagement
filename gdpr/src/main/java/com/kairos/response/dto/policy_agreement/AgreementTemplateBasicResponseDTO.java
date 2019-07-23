package com.kairos.response.dto.policy_agreement;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.math.BigInteger;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
public class AgreementTemplateBasicResponseDTO {


    private BigInteger id;
    private String name;
}
