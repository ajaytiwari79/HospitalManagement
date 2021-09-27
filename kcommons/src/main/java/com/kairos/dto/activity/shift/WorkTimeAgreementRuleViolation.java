package com.kairos.dto.activity.shift;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigInteger;

/**
 * @author pradeep
 * @date - 29/8/18
 */

@Getter
@Setter
@NoArgsConstructor
public class WorkTimeAgreementRuleViolation implements Serializable {

    private BigInteger ruleTemplateId;
    private String name;
    private Integer counter;
    private Integer totalCounter;
    private boolean broken;
    private boolean canBeIgnore;
    private String unitType;
    private String unitValue;

    public WorkTimeAgreementRuleViolation(BigInteger ruleTemplateId, String name, Integer counter, boolean broken, boolean canBeIgnore,Integer totalCounter,String unitType,String unitValue) {
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
