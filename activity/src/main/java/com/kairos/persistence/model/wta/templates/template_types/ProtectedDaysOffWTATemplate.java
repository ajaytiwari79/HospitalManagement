package com.kairos.persistence.model.wta.templates.template_types;

import com.kairos.persistence.model.wta.templates.WTABaseRuleTemplate;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

/**
 * Created by pradeep
 * Created at 29/7/19
 **/

@Getter
@Setter
public class ProtectedDaysOffWTATemplate extends WTABaseRuleTemplate {
    private BigInteger activityId;

}
