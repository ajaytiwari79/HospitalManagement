package com.kairos.dto.activity.payroll;

import javax.validation.constraints.Email;
import java.math.BigInteger;

/**
 * @author pradeep
 * @date - 14/1/19
 */

public class OrganizationBankDetailsDTO {
    private Long organizationId;
    private BigInteger bankId;
    private Long accountNumber;
    @Email(message = "email.invalid.format")
    private String email;

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
