package com.kairos.persistence.model.payroll;

import com.kairos.persistence.model.common.MongoBaseEntity;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigInteger;

/**
 * @author pradeep
 * @date - 14/1/19
 */
@Document
public class OrganizationBankDetails extends MongoBaseEntity {


    private Long organizationId;
    private BigInteger bankId;
    private Long accountNumber;
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
