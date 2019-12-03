package com.kairos.dto.planner.solverconfig;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.enums.constraint.ConstraintCategory;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

/**
 * @author pradeep
 * @date - 20/6/18
 */
@Deprecated
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class ConstraintDTO {

    private BigInteger id;
    private String name;
    private String description;
    private ConstraintCategory category;
    private boolean constraintValueRequired;
    private boolean penalityValueRequired;
    private boolean disabled;
}
