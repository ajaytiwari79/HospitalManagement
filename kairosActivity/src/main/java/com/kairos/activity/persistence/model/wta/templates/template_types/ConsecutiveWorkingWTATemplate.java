package com.kairos.activity.persistence.model.wta.templates.template_types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.activity.persistence.enums.WTATemplateType;
import com.kairos.activity.persistence.model.wta.templates.WTABaseRuleTemplate;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * Created by pawanmandhan on 5/8/17.
 * TEMPLATE3
 */
@Document
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ConsecutiveWorkingWTATemplate extends WTABaseRuleTemplate {

    private List<String> balanceType;//multiple check boxes
    private boolean checkAgainstTimeRules;
    private long limit;//no of days
    private WTATemplateType wtaTemplateType = WTATemplateType.ConsecutiveWorking;


    public WTATemplateType getWtaTemplateType() {
        return wtaTemplateType;
    }

    public void setWtaTemplateType(WTATemplateType wtaTemplateType) {
        this.wtaTemplateType = wtaTemplateType;
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

    public long getLimit() {
        return limit;
    }

    public void setLimit(long limit) {
        this.limit = limit;
    }

    public ConsecutiveWorkingWTATemplate(String name, String templateType, boolean disabled, String description,
                                         List<String> balanceType, boolean checkAgainstTimeRules, long limit) {
        this.limit = limit;
        this.balanceType = balanceType;
        this.checkAgainstTimeRules = checkAgainstTimeRules;
        this.name = name;
        this.templateType = templateType;
        this.disabled = disabled;
        this.description = description;

    }
    public ConsecutiveWorkingWTATemplate() {

    }

}
