package com.kairos.persistence.model.wta.templates.template_types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.enums.wta.WTATemplateType;
import com.kairos.persistence.model.wta.templates.WTABaseRuleTemplate;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

/**
 * Created by pradeep
 * Created at 29/7/19
 **/

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class ProtectedDaysOffWTATemplate extends WTABaseRuleTemplate {
    private BigInteger activityId;

    public ProtectedDaysOffWTATemplate() {
        this.wtaTemplateType = WTATemplateType.PROTECTED_DAYS_OFF;
    }

    public ProtectedDaysOffWTATemplate(String name, String description,BigInteger activityId) {
        super(name, description);
        this.activityId=activityId;
    }
}
