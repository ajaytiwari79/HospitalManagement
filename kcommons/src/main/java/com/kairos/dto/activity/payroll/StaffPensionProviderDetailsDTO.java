package com.kairos.dto.activity.payroll;

import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

/**
 * @author pradeep
 * @date - 14/1/19
 */
@Getter
@Setter
public class StaffPensionProviderDetailsDTO {
    private Long staffId;
    private BigInteger pensionProviderId;
}
