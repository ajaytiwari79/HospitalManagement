package com.planner.responseDto.config;

import com.planner.responseDto.commonDto.BaseDTO;

import java.util.List;

public class RuleDTO extends BaseDTO {

    private String ruleName;
    private String ruleCondition;
    private String pattern;
    private int noOfruleValues;
    private int salience;
    private List<String> outputValues;
    private boolean disabled;

    public String getRuleCondition() {
        return ruleCondition;
    }

    public void setRuleCondition(String ruleCondition) {
        this.ruleCondition = ruleCondition;
    }

    public int getNoOfruleValues() {
        return noOfruleValues;
    }

    public void setNoOfruleValues(int noOfruleValues) {
        this.noOfruleValues = noOfruleValues;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public int getSalience() {
        return salience;
    }

    public void setSalience(int salience) {
        this.salience = salience;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
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
}
