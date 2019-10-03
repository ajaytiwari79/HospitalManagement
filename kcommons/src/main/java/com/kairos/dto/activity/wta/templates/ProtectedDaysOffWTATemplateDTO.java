package com.kairos.dto.activity.wta.templates;

import com.kairos.dto.activity.wta.basic_details.WTABaseRuleTemplateDTO;
import com.kairos.enums.wta.WTATemplateType;

import java.math.BigInteger;

public class ProtectedDaysOffWTATemplateDTO extends WTABaseRuleTemplateDTO {
    private BigInteger activityId;

    public ProtectedDaysOffWTATemplateDTO() {
        this.wtaTemplateType = WTATemplateType.PROTECTED_DAYS_OFF;;
    }

    public BigInteger getActivityId() {
        return activityId;
    }

    public void setActivityId(BigInteger activityId) {
        this.activityId = activityId;
    }
}
