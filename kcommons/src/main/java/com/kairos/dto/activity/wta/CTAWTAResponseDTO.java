package com.kairos.dto.activity.wta;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CTAWTAResponseDTO {

    private BigInteger ctaId;
    private String ctaName;
    private BigInteger wtaId;
    private String wtaName;
    private Long employmentId;
}
