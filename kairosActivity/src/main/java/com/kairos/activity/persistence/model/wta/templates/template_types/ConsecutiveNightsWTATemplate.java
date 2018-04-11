package com.kairos.activity.persistence.model.wta.templates.template_types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.activity.persistence.enums.WTATemplateType;
import com.kairos.activity.persistence.model.wta.templates.WTABaseRuleTemplate;
import org.springframework.data.mongodb.core.mapping.Document;


/**
 * Created by pawanmandhan on 5/8/17.
 * TEMPLATE6
 */
@Document
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ConsecutiveNightsWTATemplate extends WTABaseRuleTemplate {


    private long daysLimit;

    private WTATemplateType wtaTemplateType = WTATemplateType.ConsecutiveNights;


    public WTATemplateType getWtaTemplateType() {
        return wtaTemplateType;
    }

    public void setWtaTemplateType(WTATemplateType wtaTemplateType) {
        this.wtaTemplateType = wtaTemplateType;
    }
    public long getDaysLimit() {
        return daysLimit;
    }

    public void setDaysLimit(long daysLimit) {
        this.daysLimit = daysLimit;
    }

    public ConsecutiveNightsWTATemplate(String name, String templateType, boolean isActive, String description, long daysLimit) {
        super(name, templateType, description);
        this.daysLimit = daysLimit;
    }

    public ConsecutiveNightsWTATemplate() {
    }
}
