package com.kairos.persistence.model.user.agreement.wta.templates;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.neo4j.ogm.annotation.NodeEntity;

import java.util.List;

/**
 * Created by pawanmandhan on 5/8/17.
 */
@NodeEntity
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)

public class WtaTemplate7 extends WTABaseRuleTemplate {


    private List<String> balanceType;//multiple check boxes
    private boolean checkAgainstTimeRules;
    private long nightsWorked;//no of days


    public List<String> getBalanceType() {
        return balanceType;
    }

    public void setBalanceType(List<String> balanceType) {
        this.balanceType = balanceType;
    }

    public boolean isCheckAgainstTimeRules() {
        return checkAgainstTimeRules;
    }

    public void setCheckAgainstTimeRules(boolean checkAgainstTimeRules) {
        this.checkAgainstTimeRules = checkAgainstTimeRules;
    }

    public long getNightsWorked() {
        return nightsWorked;
    }

    public void setNightsWorked(long nightsWorked) {
        this.nightsWorked = nightsWorked;
    }

    public WtaTemplate7(String name, String templateType,  boolean isActive, String description, List<String> balanceType, boolean checkAgainstTimeRules, long nightsWorked) {
        this.nightsWorked = nightsWorked;
        this.balanceType = balanceType;
        this.checkAgainstTimeRules = checkAgainstTimeRules;
        this.name = name;
        this.templateType = templateType;
       this.isActive = isActive;
        this.description = description;

    }

    public WtaTemplate7() {
    }



}
