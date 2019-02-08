package com.kairos.dto.activity.payroll;

import java.util.List;

/**
 * @author pradeep
 * @date - 14/1/19
 */

public class OrganizationBankDetailsAndBanksDTO {

    private List<BankDTO> bankList;
    private OrganizationBankDetailsDTO organizationBankDetails;


    public OrganizationBankDetailsAndBanksDTO() {
    }

    public OrganizationBankDetailsAndBanksDTO(List<BankDTO> bankList, OrganizationBankDetailsDTO organizationBankDetails) {
        this.bankList = bankList;
        this.organizationBankDetails = organizationBankDetails;
    }

    public List<BankDTO> getBankList() {
        return bankList;
    }

    public void setBankList(List<BankDTO> bankList) {
        this.bankList = bankList;
    }

    public OrganizationBankDetailsDTO getOrganizationBankDetails() {
        return organizationBankDetails;
    }

    public void setOrganizationBankDetails(OrganizationBankDetailsDTO organizationBankDetails) {
        this.organizationBankDetails = organizationBankDetails;
    }
}
