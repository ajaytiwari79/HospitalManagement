package com.kairos.dto.activity.payroll;

import javax.validation.Valid;
import java.util.List;

/**
 * @author pradeep
 * @date - 14/1/19
 */

public class StaffBankAndPensionProviderDetailsDTO {
    private List<BankDTO> bankList;
    @Valid
    private StaffBankDetailsDTO staffOfficialBank;
    private List<PensionProviderDTO> pensionProviders;
    @Valid
    private StaffPensionProviderDetailsDTO staffPensionProvider;

    public StaffBankAndPensionProviderDetailsDTO() {
    }

    public StaffBankAndPensionProviderDetailsDTO(List<BankDTO> bankList, StaffBankDetailsDTO staffOfficialBank, List<PensionProviderDTO> pensionProviders, StaffPensionProviderDetailsDTO staffPensionProvider) {
        this.bankList = bankList;
        this.staffOfficialBank = staffOfficialBank;
        this.pensionProviders = pensionProviders;
        this.staffPensionProvider = staffPensionProvider;
    }

    public List<BankDTO> getBankList() {
        return bankList;
    }

    public void setBankList(List<BankDTO> bankList) {
        this.bankList = bankList;
    }

    public StaffBankDetailsDTO getStaffOfficialBank() {
        return staffOfficialBank;
    }

    public void setStaffOfficialBank(StaffBankDetailsDTO staffOfficialBank) {
        this.staffOfficialBank = staffOfficialBank;
    }

    public List<PensionProviderDTO> getPensionProviders() {
        return pensionProviders;
    }

    public void setPensionProviders(List<PensionProviderDTO> pensionProviders) {
        this.pensionProviders = pensionProviders;
    }

    public StaffPensionProviderDetailsDTO getStaffPensionProvider() {
        return staffPensionProvider;
    }

    public void setStaffPensionProvider(StaffPensionProviderDetailsDTO staffPensionProvider) {
        this.staffPensionProvider = staffPensionProvider;
    }
}
