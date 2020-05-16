package com.planner.domain.constraint.constraints;

import com.kairos.enums.constraint.ConstraintSubType;
import com.kairos.enums.constraint.ConstraintType;
import com.kairos.enums.constraint.ScoreLevel;
import com.planner.domain.common.MongoBaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document
public class Constraint extends MongoBaseEntity {
    private ConstraintSubType constraintSubType;
    private Boolean  mandatory;
    private ScoreLevel scoreLevel;
    private int constraintWeight;
    private ConstraintType constraintType;

}
