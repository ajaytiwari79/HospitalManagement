package com.kairos.persistence.model.user.agreement.wta.templates.template_types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.persistence.model.enums.TimeBankTypeEnum;
import com.kairos.persistence.model.user.agreement.wta.templates.WTABaseRuleTemplate;
import org.neo4j.ogm.annotation.NodeEntity;

/**
 * Created by pavan on 20/2/18.
 */
@NodeEntity
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.ALWAYS)
public class MaximumTimeBank extends WTABaseRuleTemplate {
    private TimeBankTypeEnum frequency;
    private Integer yellowZone;
    private boolean forbid;
    private boolean allowExtraActivity;

    public MaximumTimeBank() {
        //Default Constructor
    }

    public MaximumTimeBank(String name, String templateType, boolean disabled, String description, TimeBankTypeEnum frequency, Integer yellowZone, boolean forbid, boolean allowExtraActivity) {
        this.name=name;
        this.templateType=templateType;
        this.disabled=disabled;
        this.description=description;
        this.frequency = frequency;
        this.yellowZone = yellowZone;
        this.forbid = forbid;
        this.allowExtraActivity = allowExtraActivity;
    }

    public TimeBankTypeEnum getFrequency() {
        return frequency;
    }

    public void setFrequency(TimeBankTypeEnum frequency) {
        this.frequency = frequency;
    }

    public Integer getYellowZone() {
        return yellowZone;
    }

    public void setYellowZone(Integer yellowZone) {
        this.yellowZone = yellowZone;
    }

    public boolean isForbid() {
        return forbid;
    }

    public void setForbid(boolean forbid) {
        this.forbid = forbid;
    }

    public boolean isAllowExtraActivity() {
        return allowExtraActivity;
    }

    public void setAllowExtraActivity(boolean allowExtraActivity) {
        this.allowExtraActivity = allowExtraActivity;
    }
}
