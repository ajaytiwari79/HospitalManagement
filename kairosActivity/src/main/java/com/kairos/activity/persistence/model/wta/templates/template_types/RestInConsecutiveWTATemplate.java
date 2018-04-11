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
public class RestInConsecutiveWTATemplate extends WTABaseRuleTemplate {

    private List<String> balanceType;
    private long minimumRest;//hh:mm
    private long daysWorked;
    private WTATemplateType wtaTemplateType = WTATemplateType.RestInConsecutiveDays;


    public WTATemplateType getWtaTemplateType() {
        return wtaTemplateType;
    }

    public void setWtaTemplateType(WTATemplateType wtaTemplateType) {
        this.wtaTemplateType = wtaTemplateType;
    }
    public long getMinimumRest() {
        return minimumRest;
    }

    public List<String> getBalanceType() {
        return balanceType;
    }

    public void setBalanceType(List<String> balanceType) {
        this.balanceType = balanceType;
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


    public RestInConsecutiveWTATemplate(String name, String templateType, boolean disabled, String description, long minimumRest, long daysWorked) {
        this.name=name;
        this.templateType=templateType;
        this.disabled=disabled;
        this.description=description;

        this.minimumRest = minimumRest;
        this.daysWorked = daysWorked;

    }
    public RestInConsecutiveWTATemplate() {
    }

}
