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
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MinimumTimeBank extends WTABaseRuleTemplate{
    private TimeBankTypeEnum frequency;
    private int yellowZone;
    private boolean forbid;
    private boolean allowExtraActivity;

    public MinimumTimeBank() {
        //Default Constructor
    }

    public MinimumTimeBank(String name, String templateType,boolean disabled, String description, TimeBankTypeEnum frequency, int yellowZone, boolean forbid, boolean allowExtraActivity) {
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

    public int getYellowZone() {
        return yellowZone;
    }

    public void setYellowZone(int yelloZone) {
        this.yellowZone = yelloZone;
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
