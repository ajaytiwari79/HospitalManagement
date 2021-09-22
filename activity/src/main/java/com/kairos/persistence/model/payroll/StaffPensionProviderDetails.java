package com.kairos.persistence.model.payroll;

import com.kairos.persistence.model.common.MongoBaseEntity;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigInteger;

/**
 * @author pradeep
 * @date - 14/1/19
 */
@Document
public class StaffPensionProviderDetails extends MongoBaseEntity{

    private static final long serialVersionUID = -7556137674158286848L;
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
