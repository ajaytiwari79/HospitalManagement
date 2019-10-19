package com.kairos.dto.user.country.pay_table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Created by vipul on 19/3/18.
 */
@Getter
@Setter
@NoArgsConstructor
public class PayTableUpdateDTO {
    private Long id;
    @NotNull(message = "name can't be null")
    private String name;
    private String shortName;
    private String description;
    @NotNull(message = "Start date can't be null")
    private LocalDate startDateMillis;

    private LocalDate endDateMillis;
    @NotNull(message = "Level can not be null")
    private Long levelId;
    @NotNull(message = "Please provide payment unit type")
    private String paymentUnit;
    private BigDecimal percentageValue;


    public PayTableUpdateDTO(Long id, @NotNull(message = "name can't be null") String name,BigDecimal percentageValue) {
        this.id = id;
        this.name = name;
        this.percentageValue=percentageValue;
    }


}
