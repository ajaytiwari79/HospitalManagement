package com.kairos.dto.activity.wta.templates;

import com.kairos.dto.activity.wta.basic_details.WTABaseRuleTemplateDTO;
import com.kairos.enums.wta.WTATemplateType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmployeesWithIncreasedRiskWTATemplateDTO extends WTABaseRuleTemplateDTO {


    private int belowAge;
    private int aboveAge;
    private boolean pregnant;
    private boolean restingTimeAllowed;
    private int restingTime;


    public EmployeesWithIncreasedRiskWTATemplateDTO() {
        wtaTemplateType=WTATemplateType.EMPLOYEES_WITH_INCREASE_RISK;
    }
}
