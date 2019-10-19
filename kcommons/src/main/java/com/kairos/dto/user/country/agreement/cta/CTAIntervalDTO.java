package com.kairos.dto.user.country.agreement.cta;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CTAIntervalDTO {

    private String compensationType;
    private int compensationValue;
    private int startTime;
    private int endTime;
}
