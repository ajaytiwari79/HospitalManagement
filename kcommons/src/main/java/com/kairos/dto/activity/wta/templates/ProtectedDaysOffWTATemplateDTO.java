package com.kairos.dto.activity.wta.templates;

import com.kairos.dto.activity.wta.basic_details.WTABaseRuleTemplateDTO;
import com.kairos.enums.wta.WTATemplateType;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;
@Getter
@Setter
public class ProtectedDaysOffWTATemplateDTO extends WTABaseRuleTemplateDTO {
    @NotNull(message = "message.mismatched-ids")
    private BigInteger activityId;

    public ProtectedDaysOffWTATemplateDTO() {
        this.wtaTemplateType = WTATemplateType.PROTECTED_DAYS_OFF;;
    }
}
