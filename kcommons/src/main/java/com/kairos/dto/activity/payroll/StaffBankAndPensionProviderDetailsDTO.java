package com.kairos.dto.activity.payroll;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import java.util.List;

/**
 * @author pradeep
 * @date - 14/1/19
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StaffBankAndPensionProviderDetailsDTO {
    private List<BankDTO> bankList;
    @Valid
    private StaffBankDetailsDTO staffOfficialBank;
    private List<PensionProviderDTO> pensionProviders;
    @Valid
    private StaffPensionProviderDetailsDTO staffPensionProvider;
}
