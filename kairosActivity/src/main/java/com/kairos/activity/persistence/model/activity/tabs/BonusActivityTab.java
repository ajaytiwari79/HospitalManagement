package com.kairos.activity.persistence.model.activity.tabs;

import java.io.Serializable;

/**
 * Created by vipul on 24/8/17.
 */
public class BonusActivityTab implements Serializable{
    private String bonusHoursType;
    private boolean overRuleCtaWta;


    public BonusActivityTab() {
        //default constructor
    }

    public BonusActivityTab(String bonusHoursType, boolean overRuleCtaWta) {
        this.bonusHoursType = bonusHoursType;
        this.overRuleCtaWta = overRuleCtaWta;
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
}
