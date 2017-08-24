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
public class WtaTemplate9 extends WTABaseRuleTemplate {

    private List<String> balanceType;//multiple check boxes
    private long nightsWorked;
    private long interval;
    private long validationStartDate;

    public List<String> getBalanceType() {
        return balanceType;
    }

    public void setBalanceType(List<String> balanceType) {
        this.balanceType = balanceType;
    }

    public long getNightsWorked() {
        return nightsWorked;
    }

    public void setNightsWorked(long nightsWorked) {
        this.nightsWorked = nightsWorked;
    }

    public long getInterval() {
        return interval;
    }

    public void setInterval(long interval) {
        this.interval = interval;
    }

    public long getValidationStartDate() {
        return validationStartDate;
    }

    public void setValidationStartDate(long validationStartDate) {
        this.validationStartDate = validationStartDate;
    }

    public WtaTemplate9(String name, String templateType,  boolean isActive, String description, List<String> balanceType, long nightsWorked, long interval, long validationStartDate) {
        this.nightsWorked = nightsWorked;
        this.balanceType = balanceType;
        this.interval=interval;
        this.validationStartDate=validationStartDate;;
        this.name = name;
        this.templateType = templateType;
        this.isActive = isActive;
        this.description = description;

    }
    public WtaTemplate9() {
    }



}
