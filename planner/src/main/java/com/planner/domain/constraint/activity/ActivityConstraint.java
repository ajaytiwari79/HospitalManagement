package com.planner.domain.constraint.activity;

import com.kairos.commons.planning_setting.PlanningSetting;
import com.kairos.enums.constraint.ConstraintSubType;
import com.planner.domain.common.MongoBaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigInteger;

@Getter
@Setter
@NoArgsConstructor
@Document
public class ActivityConstraint extends MongoBaseEntity {
    private BigInteger activityId;
    private PlanningSetting planningSetting;
    private ConstraintSubType constraintSubType;

}
