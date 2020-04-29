package com.kairos.dto.activity.pay_out;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CTAIntervalDTO {

    private String compensationType;
    private int compensationValue;
    private int startTime;
    private int endTime;

}
