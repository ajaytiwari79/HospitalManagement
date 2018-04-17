package com.kairos.activity.persistence.model.wta.templates.template_types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.activity.persistence.enums.WTATemplateType;
import com.kairos.activity.persistence.model.wta.templates.WTABaseRuleTemplate;

import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created by pawanmandhan on 5/8/17.
 * TEMPLATE3
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ConsecutiveWorkWTATemplate extends WTABaseRuleTemplate {

    private boolean checkAgainstTimeRules;
    private long limitCount;//no of days
    private WTATemplateType wtaTemplateType = WTATemplateType.CONSECUTIVE_WORKING_PARTOFDAY;




    public WTATemplateType getWtaTemplateType() {
        return wtaTemplateType;
    }

    public void setWtaTemplateType(WTATemplateType wtaTemplateType) {
        this.wtaTemplateType = wtaTemplateType;
    }


    public boolean isCheckAgainstTimeRules() {
        return checkAgainstTimeRules;
    }

    public void setCheckAgainstTimeRules(boolean checkAgainstTimeRules) {
        this.checkAgainstTimeRules = checkAgainstTimeRules;
    }

    public long getLimitCount() {
        return limitCount;
    }

    public void setLimitCount(long limitCount) {
        this.limitCount = limitCount;
    }

    public ConsecutiveWorkWTATemplate() {

    }

    public ConsecutiveWorkWTATemplate(String name, boolean minimum, String description, boolean checkAgainstTimeRules, long limitCount) {
        super(name, minimum, description);
        this.checkAgainstTimeRules = checkAgainstTimeRules;
        this.limitCount = limitCount;
    }
}
