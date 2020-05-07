package com.kairos.dto.activity.payroll;
/*
 *Created By Pavan on 19/12/18
 *
 */

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.math.BigInteger;
@Getter
@Setter
public class PensionProviderDTO {
    private BigInteger id;
    @NotBlank(message = "name.absent")
    private String name;
    @NotBlank(message = "paymentNumber.absent")
    private String paymentNumber;
}
