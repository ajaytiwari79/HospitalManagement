package com.kairos.planner.solverconfig;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.enums.solver_config.ConstraintCategory;

import java.math.BigInteger;

/**
 * @author pradeep
 * @date - 20/6/18
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConstraintDTO {

    private BigInteger id;
    private String name;
    private String description;
    private ConstraintCategory category;

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
