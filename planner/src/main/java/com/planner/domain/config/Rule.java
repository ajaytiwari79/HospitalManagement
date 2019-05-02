package com.planner.domain.config;

import com.planner.domain.common.BaseEntity;

import java.util.List;

////import org.springframework.data.cassandra.core.mapping.Table;

////@Table
public class Rule extends BaseEntity{

    private String ruleName;
    private String ruleCondition;
    //it should be equal to the dynamic and staticRuleValues.
    private int noOfruleValues;
    private int salience;
    private String pattern;
    //output value are those value which is genrate after rule evaluation(it is also used as a dynamic level value).
    private List<String> outputValues;
    private boolean disabled;

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }


    public int getSalience() {
        return salience;
    }

    public void setSalience(int salience) {
        this.salience = salience;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public List<String> getOutputValues() {
        return outputValues;
    }

    public void setOutputValues(List<String> outputValues) {
        this.outputValues = outputValues;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public int getNoOfruleValues() {
        return noOfruleValues;
    }

    public void setNoOfruleValues(int noOfruleValues) {
        this.noOfruleValues = noOfruleValues;
    }

    public String getRuleCondition() {
        return ruleCondition;
    }

    public void setRuleCondition(String ruleCondition) {
        this.ruleCondition = ruleCondition;
    }
}
