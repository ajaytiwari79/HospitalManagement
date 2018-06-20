package com.kairos.dto.solverconfig;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.enums.solver_config.ConstraintCategory;
import com.kairos.enums.solver_config.ContraintLevel;

import java.math.BigInteger;
import java.util.List;

/**
 * @author pradeep
 * @date - 20/6/18
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConstraintValueDTO {

    private BigInteger constraintId;
    private String name;
    private String description;
    private ConstraintCategory category;

    private ContraintLevel level;
    //it is used for current constraint belongs to which level no in bendableScore like (hard[0],soft[1]).
    private Integer levelNo;
    //it is used for current constraint break how much in no. (like -10,-20) if it is static.
    private Integer levelValue;
    //it is used for making rule conditions like (waiting time not more then 8 hours) It is used when planner doesnt' give
    private List<Integer> staticRuleValues;
    //it is same as static value but it is used when planner give the value for this contraint.
    private List<Integer> dynamicRuleValue;


    public BigInteger getConstraintId() {
        return constraintId;
    }

    public void setConstraintId(BigInteger constraintId) {
        this.constraintId = constraintId;
    }

    public ContraintLevel getLevel() {
        return level;
    }

    public void setLevel(ContraintLevel level) {
        this.level = level;
    }

    public Integer getLevelNo() {
        return levelNo;
    }

    public void setLevelNo(Integer levelNo) {
        this.levelNo = levelNo;
    }

    public Integer getLevelValue() {
        return levelValue;
    }

    public void setLevelValue(Integer levelValue) {
        this.levelValue = levelValue;
    }

    public List<Integer> getStaticRuleValues() {
        return staticRuleValues;
    }

    public void setStaticRuleValues(List<Integer> staticRuleValues) {
        this.staticRuleValues = staticRuleValues;
    }

    public List<Integer> getDynamicRuleValue() {
        return dynamicRuleValue;
    }

    public void setDynamicRuleValue(List<Integer> dynamicRuleValue) {
        this.dynamicRuleValue = dynamicRuleValue;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ConstraintCategory getCategory() {
        return category;
    }

    public void setCategory(ConstraintCategory category) {
        this.category = category;
    }
}
