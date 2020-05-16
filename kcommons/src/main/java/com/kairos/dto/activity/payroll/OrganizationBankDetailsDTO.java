package com.kairos.dto.activity.payroll;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import java.math.BigInteger;

/**
 * @author pradeep
 * @date - 14/1/19
 */
@Getter
@Setter
public class OrganizationBankDetailsDTO {
    private Long organizationId;
    private BigInteger bankId;
    private Long accountNumber;
    @Email(message = "email.invalid.format")
    private String email;
}
