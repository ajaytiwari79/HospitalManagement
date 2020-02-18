package com.planner.domain.constraint.unit;

import com.kairos.commons.planning_setting.PlanningSetting;
import com.kairos.enums.constraint.ConstraintSubType;
import com.planner.domain.common.MongoBaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;


@Getter
@Setter
@NoArgsConstructor
@Document
public class UnitConstraint extends MongoBaseEntity {
    private Long unitId;
    private PlanningSetting planningSetting;
    private ConstraintSubType constraintSubType;
    private Boolean  mandatory;


}
