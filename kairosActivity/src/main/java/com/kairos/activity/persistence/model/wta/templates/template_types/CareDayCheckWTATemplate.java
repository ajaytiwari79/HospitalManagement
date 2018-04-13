package com.kairos.activity.persistence.model.wta.templates.template_types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.activity.persistence.enums.WTATemplateType;
import com.kairos.activity.persistence.model.wta.templates.WTABaseRuleTemplate;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created by pawanmandhan on 5/8/17.
 * TEMPLATE14
 */
@Document
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CareDayCheckWTATemplate extends WTABaseRuleTemplate {

    private long daysLimit;
    private long intervalLength;
    private String intervalUnit;
    private long validationStartDateMillis;
    private WTATemplateType wtaTemplateType = WTATemplateType.CARE_DAYS_CHECK;


    public WTATemplateType getWtaTemplateType() {
        return wtaTemplateType;
    }

    public void setWtaTemplateType(WTATemplateType wtaTemplateType) {
        this.wtaTemplateType = wtaTemplateType;
    }
    public long getIntervalLength() {
        return intervalLength;
    }

    public void setIntervalLength(long intervalLength) {
        this.intervalLength = intervalLength;
    }

    public long getValidationStartDateMillis() {
        return validationStartDateMillis;
    }

    public void setValidationStartDateMillis(long validationStartDateMillis) {
        this.validationStartDateMillis = validationStartDateMillis;
    }

    public long getDaysLimit() {
        return daysLimit;
    }

    public void setDaysLimit(long daysLimit) {
        this.daysLimit = daysLimit;
    }

    public String getIntervalUnit() {
        return intervalUnit;
    }

    public void setIntervalUnit(String intervalUnit) {
        this.intervalUnit = intervalUnit;
    }

    public CareDayCheckWTATemplate(String name, String templateType, boolean isActive,
                                   String description, long intervalLength, long validationStartDateMillis, String intervalUnit, long daysLimit) {
        this.name = name;
        this.templateType = templateType;
        this.description = description;
        this.intervalLength =intervalLength;
        this.intervalUnit= intervalUnit;
        this.validationStartDateMillis =validationStartDateMillis;
        this.daysLimit =daysLimit;
    }

    public CareDayCheckWTATemplate() {

    }
}
