package com.kairos.dto.planner.solverconfig;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.enums.constraint.ConstraintCategory;

import java.math.BigInteger;

/**
 * @author pradeep
 * @date - 20/6/18
 */
@Deprecated
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConstraintDTO {

    private BigInteger id;
    private String name;
    private String description;
    private ConstraintCategory category;
    private boolean constraintValueRequired;
    private boolean penalityValueRequired;
    private boolean disabled;

    public Boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public boolean isConstraintValueRequired() {
        return constraintValueRequired;
    }

    public void setConstraintValueRequired(boolean constraintValueRequired) {
        this.constraintValueRequired = constraintValueRequired;
    }

    public boolean isPenalityValueRequired() {
        return penalityValueRequired;
    }

    public void setPenalityValueRequired(boolean penalityValueRequired) {
        this.penalityValueRequired = penalityValueRequired;
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
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
