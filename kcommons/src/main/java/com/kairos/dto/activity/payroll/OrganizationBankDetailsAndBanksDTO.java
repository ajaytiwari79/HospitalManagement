package com.kairos.dto.activity.payroll;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * @author pradeep
 * @date - 14/1/19
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationBankDetailsAndBanksDTO {

    private List<BankDTO> bankList;
    private OrganizationBankDetailsDTO organizationBankDetails;
}
