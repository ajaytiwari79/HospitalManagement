package com.kairos.dto.activity.payroll;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

import java.math.BigInteger;

/**
 * @author pradeep
 * @date - 14/1/19
 */
@Getter
@Setter
public class StaffBankDetailsDTO {

    private Long staffId;
    private BigInteger bankId;
    private boolean useNemkontoAccount;
    @Range(message = "accountNumber.greater_than.provided_value")
    private Long accountNumber;
}
