package com.kairos.activity.persistence.model.wta.templates.template_types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.activity.persistence.enums.WTATemplateType;
import com.kairos.activity.persistence.model.wta.templates.WTABaseRuleTemplate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;


/**
 * Created by pawanmandhan on 5/8/17.
 * TEMPLATE4
 */
@Document
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ConsecutiveRestPartOfDayWTATemplate extends WTABaseRuleTemplate {

    private long minimumRest;//hh:mm
    private long daysWorked;
    private WTATemplateType wtaTemplateType = WTATemplateType.REST_IN_CONSECUTIVE_DAYS_AND_NIGHTS;


    public WTATemplateType getWtaTemplateType() {
        return wtaTemplateType;
    }

    public void setWtaTemplateType(WTATemplateType wtaTemplateType) {
        this.wtaTemplateType = wtaTemplateType;
    }
    public long getMinimumRest() {
        return minimumRest;
    }


    public void setMinimumRest(long minimumRest) {
        this.minimumRest = minimumRest;
    }

    public long getDaysWorked() {
        return daysWorked;
    }

    public void setDaysWorked(long daysWorked) {
        this.daysWorked = daysWorked;
    }


    public ConsecutiveRestPartOfDayWTATemplate(String name, boolean disabled, String description, long minimumRest, long daysWorked) {
        this.name=name;
        this.disabled=disabled;
        this.description=description;
        this.minimumRest = minimumRest;
        this.daysWorked = daysWorked;

    }
    public ConsecutiveRestPartOfDayWTATemplate() {
    }

}
