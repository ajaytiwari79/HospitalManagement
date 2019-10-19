package com.kairos.dto.user.country.pay_table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * Created by vipul on 16/3/18.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class PayTableMatrixDTO {
    @NotNull(message = "Pay Group Area can not be null")
    private Long payGroupAreaId;
    @NotNull(message = "Pay Grade value can not be null")
    private BigDecimal payGroupAreaAmount;
    private Long id;
}
