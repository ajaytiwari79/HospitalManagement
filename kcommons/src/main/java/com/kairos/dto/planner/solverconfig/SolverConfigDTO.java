package com.kairos.dto.planner.solverconfig;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.enums.TimeTypeEnum;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static com.kairos.commons.utils.ObjectUtils.isNullOrElse;

/**
 * @author mohit
 * @date - 21/9/18
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class SolverConfigDTO {
    //Common
    protected BigInteger id;
    @NotBlank(message = "error.name.notnull")
    protected String name;//Unique
    protected String description;
    @NotNull(message = "error.phaseId.not.exists")
    protected Long phaseId;
    protected Long planningPeriodId;
    protected byte threadCount;
    protected short terminationTimeInMinutes;
    @NotNull(message = "error.planningproblem.not.exists")
    protected Long planningProblemId;
    protected List<BigInteger> constraintIds;
    protected BigInteger parentSolverConfigId;
    private List<ConstraintDTO> constraints;
    @NotNull(message = "error.timetype.not.exists")
    protected TimeTypeEnum typeOfTimeType;
    private Long unitId;
    private Long parentCountrySolverConfigId;
    private List<Long> organizationSubServiceIds;
    private Long countryId;

    public SolverConfigDTO() {
        this.constraints = new ArrayList<>();
    }

    public SolverConfigDTO(List<ConstraintDTO> constraints) {
        this.constraints = constraints;
    }

    public void setConstraints(List<ConstraintDTO> constraints) {
        this.constraints = isNullOrElse(constraints,new ArrayList<>());
    }
}
