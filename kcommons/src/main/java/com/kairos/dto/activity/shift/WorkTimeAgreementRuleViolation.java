package com.kairos.dto.activity.shift;

import com.kairos.enums.DurationType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;

/**
 * @author pradeep
 * @date - 29/8/18
 */

@Getter
@Setter
@NoArgsConstructor
public class WorkTimeAgreementRuleViolation {

    private BigInteger ruleTemplateId;
    private String name;
    private Integer counter;
    private Integer totalCounter;
    private boolean broken;
    private boolean canBeIgnore;
    private DurationType unitType;
    private String unitValue;

    public WorkTimeAgreementRuleViolation(BigInteger ruleTemplateId, String name, Integer counter, boolean broken, boolean canBeIgnore,Integer totalCounter,DurationType unitType,String unitValue) {
        this.ruleTemplateId = ruleTemplateId;
        this.name = name;
        this.counter = counter;
        this.broken = broken;
        this.canBeIgnore = canBeIgnore;
        this.totalCounter = totalCounter;
        this.unitType = unitType;
        this.unitValue = unitValue;
    }


}
