package com.kairos.persistence.model.user.agreement.wta.templates;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.neo4j.ogm.annotation.NodeEntity;

import java.util.List;

/**
 * Created by pawanmandhan on 4/8/17.
 * Modified by vipul
 * to add method and constructors with property
 */
@NodeEntity
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class WtaTemplate1 extends WTABaseRuleTemplate {

    private String time;
    private List<String> balanceType;//multiple check boxes
    private boolean checkAgainstTimeRules;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

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

    public WtaTemplate1(String name, String templateType,boolean isActive,String description, String time, List<String> balanceType, boolean checkAgainstTimeRules) {
        this.time = time;
        this.balanceType = balanceType;
        this.checkAgainstTimeRules = checkAgainstTimeRules;

        this.name=name;
        this.templateType=templateType;
        this.isActive=isActive;
        this.description=description;

    }
    public WtaTemplate1() {

    }


    @Override
    public String toString() {

        return "WtaTemplate1{" +
                "time='" + time + '\'' +
                ", balanceType=" + balanceType +
                ", checkAgainstTimeRules=" + checkAgainstTimeRules +
                ", templateType='" + templateType +
                 ", isActive=" + isActive +
                ", description='" + description +
                "name='" + name +
                '}';
    }
}
