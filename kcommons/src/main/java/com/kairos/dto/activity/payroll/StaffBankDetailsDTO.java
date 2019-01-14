package com.kairos.dto.activity.payroll;

import javax.annotation.Nullable;
import javax.validation.Valid;
import java.util.List;

/**
 * @author pradeep
 * @date - 14/1/19
 */

public class StaffBankDetailsDTO {
    private List<BankDTO> bankDTOList;
    @Valid
    private BankDTO staffOfficialBank;
    private List<PensionProviderDTO> pensionProviders;
    @Valid
    private PensionProviderDTO staffPensionProvider;

    public StaffBankDetailsDTO() {
    }

    public StaffBankDetailsDTO(List<BankDTO> bankDTOList, BankDTO staffOfficialBank, List<PensionProviderDTO> pensionProviders,PensionProviderDTO staffPensionProvider) {
        this.bankDTOList = bankDTOList;
        this.staffOfficialBank = staffOfficialBank;
        this.pensionProviders = pensionProviders;
        this.staffPensionProvider = staffPensionProvider;
    }

    public List<BankDTO> getBankDTOList() {
        return bankDTOList;
    }

    public void setBankDTOList(List<BankDTO> bankDTOList) {
        this.bankDTOList = bankDTOList;
    }

    public BankDTO getStaffOfficialBank() {
        return staffOfficialBank;
    }

    public void setStaffOfficialBank(BankDTO staffOfficialBank) {
        this.staffOfficialBank = staffOfficialBank;
    }

    public List<PensionProviderDTO> getPensionProviders() {
        return pensionProviders;
    }

    public void setPensionProviders(List<PensionProviderDTO> pensionProviders) {
        this.pensionProviders = pensionProviders;
    }

    public PensionProviderDTO getStaffPensionProvider() {
        return staffPensionProvider;
    }

    public void setStaffPensionProvider(PensionProviderDTO staffPensionProvider) {
        this.staffPensionProvider = staffPensionProvider;
    }
}
