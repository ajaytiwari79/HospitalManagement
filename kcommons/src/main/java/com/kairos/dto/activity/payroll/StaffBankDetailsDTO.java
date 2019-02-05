package com.kairos.dto.activity.payroll;

import org.hibernate.validator.constraints.Length;

import java.math.BigInteger;

/**
 * @author pradeep
 * @date - 14/1/19
 */

public class StaffBankDetailsDTO {

    private Long staffId;
    private BigInteger bankId;
    private boolean useNemkontoAccount;
    @Length(max = 16,message = "accountNumber.greater_than.provided_value")
    private Long accountNumber;


    public boolean getUseNemkontoAccount() {
        return useNemkontoAccount;
    }

    public void setUseNemkontoAccount(boolean useNemkontoAccount) {
        this.useNemkontoAccount = useNemkontoAccount;
    }

    public Long getStaffId() {
        return staffId;
    }

    public void setStaffId(Long staffId) {
        this.staffId = staffId;
    }

    public BigInteger getBankId() {
        return bankId;
    }

    public void setBankId(BigInteger bankId) {
        this.bankId = bankId;
    }

    public Long getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(Long accountNumber) {
        this.accountNumber = accountNumber;
    }
}
