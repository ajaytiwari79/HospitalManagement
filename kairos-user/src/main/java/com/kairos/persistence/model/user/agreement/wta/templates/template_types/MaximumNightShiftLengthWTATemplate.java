package com.kairos.persistence.model.user.agreement.wta.templates.template_types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.persistence.model.user.agreement.wta.templates.WTABaseRuleTemplate;
import org.neo4j.ogm.annotation.NodeEntity;

import java.util.List;

/**
 * Created by pawanmandhan on 5/8/17.
 * TEMPLATE5
 */
@NodeEntity
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MaximumNightShiftLengthWTATemplate extends WTABaseRuleTemplate {

    private long timeLimit;
    private List<String> balanceType;//multiple check boxes
    private boolean checkAgainstTimeRules;

    public long getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(long timeLimit) {
        this.timeLimit = timeLimit;
    }

    public List<String> getBalanceType() {
        return balanceType;
    }

    public void setBalanceType(List<String> balanceType) {
        this.balanceType = balanceType;
    }

    public boolean isCheckAgainstTimeRules() {
        return checkAgainstTimeRules;
    }

    public void setCheckAgainstTimeRules(boolean checkAgainstTimeRules) {
        this.checkAgainstTimeRules = checkAgainstTimeRules;
    }

    public MaximumNightShiftLengthWTATemplate(String name, String templateType, boolean disabled, String description, long timeLimit, List<String> balanceType, boolean checkAgainstTimeRules) {
        this.timeLimit = timeLimit;
        this.balanceType = balanceType;
        this.checkAgainstTimeRules = checkAgainstTimeRules;
        this.name=name;
        this.templateType=templateType;
        this.disabled=disabled;
        this.description=description;

    }
    public MaximumNightShiftLengthWTATemplate() {
    }

}