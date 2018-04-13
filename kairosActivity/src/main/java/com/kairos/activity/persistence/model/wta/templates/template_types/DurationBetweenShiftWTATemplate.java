package com.kairos.activity.persistence.model.wta.templates.template_types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.activity.persistence.enums.WTATemplateType;
import com.kairos.activity.persistence.model.wta.templates.WTABaseRuleTemplate;
import org.springframework.data.mongodb.core.mapping.Document;


import java.util.List;

/**
 * Created by pawanmandhan on 5/8/17.
 * TEMPLATE16
 */
@Document
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DurationBetweenShiftWTATemplate extends WTABaseRuleTemplate {

    private List<String> balanceType;
    private long minimumDurationBetweenShifts;
    private WTATemplateType wtaTemplateType = WTATemplateType.DURATION_BETWEEN_SHIFTS;


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

    public long getMinimumDurationBetweenShifts() {
        return minimumDurationBetweenShifts;
    }

    public void setMinimumDurationBetweenShifts(long minimumDurationBetweenShifts) {
        this.minimumDurationBetweenShifts = minimumDurationBetweenShifts;
    }

    public DurationBetweenShiftWTATemplate(String name, String templateType, boolean disabled,
                                           String description, List<String> balanceType, long minimumDurationBetweenShifts) {
        this.name = name;
        this.templateType = templateType;
        this.disabled = disabled;
        this.description = description;
        this.balanceType= balanceType;
        this.minimumDurationBetweenShifts=minimumDurationBetweenShifts;

    }
    public DurationBetweenShiftWTATemplate() {
    }
    }