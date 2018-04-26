package com.kairos.activity.response.dto.activity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.activity.persistence.model.activity.tabs.BonusActivityTab;

import java.math.BigInteger;

/**
 * Created by vipul on 24/8/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class BonusActivityDTO {
    private BigInteger activityId;
    private String bonusHoursType;
    private  boolean overRuleCtaWta;
    public BigInteger getActivityId() {
        return activityId;
    }

    public void setActivityId(BigInteger activityId) {
        this.activityId = activityId;
    }

    public String getBonusHoursType() {
        return bonusHoursType;
    }

    public void setBonusHoursType(String bonusHoursType) {
        this.bonusHoursType = bonusHoursType;
    }

    public boolean isOverRuleCtaWta() {
        return overRuleCtaWta;
    }

    public void setOverRuleCtaWta(boolean overRuleCtaWta) {
        this.overRuleCtaWta = overRuleCtaWta;
    }

    public BonusActivityTab buildBonusActivityTab(){
        BonusActivityTab bonusActivityTab =new BonusActivityTab(bonusHoursType,overRuleCtaWta);
        return bonusActivityTab;
    }
}
