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
public class WtaTemplate8 extends WTABaseRuleTemplate {


    private List<String> balanceType;//multiple check boxes
    private String minimumRest;
    private long nightsWorked;

    public List<String> getBalanceType() {
        return balanceType;
    }

    public void setBalanceType(List<String> balanceType) {
        this.balanceType = balanceType;
    }

    public String getMinimumRest() {
        return minimumRest;
    }

    public void setMinimumRest(String minimumRest) {
        this.minimumRest = minimumRest;
    }

    public long getNightsWorked() {
        return nightsWorked;
    }

    public void setNightsWorked(long nightsWorked) {
        this.nightsWorked = nightsWorked;
    }

    public WtaTemplate8(String name, String templateType,  boolean isActive, String description, List<String> balanceType, String minimumRest, long nightsWorked) {
        this.nightsWorked = nightsWorked;
        this.balanceType = balanceType;
        this.minimumRest=minimumRest;
        this.name = name;
        this.templateType = templateType;
        this.isActive = isActive;
        this.description = description;

    }
    public WtaTemplate8() {
    }
}
