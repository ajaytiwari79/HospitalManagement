package com.planner.domain.solverconfig;

import com.kairos.enums.TimeTypeEnum;
import com.planner.domain.common.MongoBaseEntity;
import com.planner.domain.constraint.constraints.Constraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.kairos.commons.utils.ObjectUtils.isNullOrElse;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "solverConfig")
public class SolverConfig extends MongoBaseEntity {

    private String name;//Unique(but not when copying)
    private String description;
    private Long phaseId;
    private Long planningPeriodId;
    private byte threadCount;
    private short terminationTimeInMinutes;
    private Long planningProblemId;
    private List<Constraint> constraints;
    private BigInteger parentSolverConfigId;//copiedFrom
    private TimeTypeEnum typeOfTimeType;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigInteger parentCountrySolverConfigId;//copiedFrom
    private List<Long> organizationSubServiceIds;
    private Long unitId;
    private Long countryId;

    public List<Constraint> getConstraints() {
        return isNullOrElse(constraints,new ArrayList<>());
    }

    public void setConstraints(List<Constraint> constraints) {
        this.constraints = isNullOrElse(constraints,new ArrayList<>());
    }


}
