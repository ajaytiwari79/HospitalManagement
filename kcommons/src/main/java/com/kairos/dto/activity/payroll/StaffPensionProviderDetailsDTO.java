package com.kairos.dto.activity.payroll;

import java.math.BigInteger;

/**
 * @author pradeep
 * @date - 14/1/19
 */

public class StaffPensionProviderDetailsDTO {
    private Long staffId;
    private BigInteger pensionProviderId;

    public Long getStaffId() {
        return staffId;
    }

    public void setStaffId(Long staffId) {
        this.staffId = staffId;
    }

    public BigInteger getPensionProviderId() {
        return pensionProviderId;
    }

    public void setPensionProviderId(BigInteger pensionProviderId) {
        this.pensionProviderId = pensionProviderId;
    }
}
