package com.kairos.persistence.model.payroll;

import com.kairos.persistence.model.common.MongoBaseEntity;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigInteger;

/**
 * @author pradeep
 * @date - 14/1/19
 */
@Document
public class StaffBankDetails extends MongoBaseEntity{
    private Long staffId;
    private BigInteger bankId;
    private boolean useNemkontoAccount; //it true so use Default account of this staff
    private Long accountNumber;


    public boolean isUseNemkontoAccount() {
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
