package com.kairos.dto.activity.pay_out;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PayOutDistributionDTO {

    //cta ruletemplate based distributions
    private Long id;
    private String name;
    private int minutes;


    public PayOutDistributionDTO(Long id, int minutes) {
        this.id = id;
        this.minutes = minutes;
    }

}
