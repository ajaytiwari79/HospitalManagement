package com.kairos.activity.persistence.model.wta.templates.template_types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.activity.persistence.enums.WTATemplateType;
import com.kairos.activity.persistence.model.wta.templates.WTABaseRuleTemplate;
import org.springframework.data.mongodb.core.mapping.Document;


/**
 * Created by pawanmandhan on 5/8/17.
 */
@Document
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class WeeklyRestPeriodWTATemplate extends WTABaseRuleTemplate {

    private long continuousWeekRest;
    private WTATemplateType wtaTemplateType = WTATemplateType.WEEKLY_REST_PERIOD;;


    public WTATemplateType getWtaTemplateType() {
        return wtaTemplateType;
    }

    public void setWtaTemplateType(WTATemplateType wtaTemplateType) {
        this.wtaTemplateType = wtaTemplateType;
    }
    public long getContinuousWeekRest() {
        return continuousWeekRest;
    }

    public void setContinuousWeekRest(long continuousWeekRest) {
        this.continuousWeekRest = continuousWeekRest;
    }

    public WeeklyRestPeriodWTATemplate(String name, String templateType, boolean disabled,
                                       String description, long continuousWeekRest) {
        this.name = name;
        this.templateType = templateType;
        this.disabled = disabled;
        this.description = description;

        this.continuousWeekRest=continuousWeekRest;

    }

    public WeeklyRestPeriodWTATemplate() {
    }
}
