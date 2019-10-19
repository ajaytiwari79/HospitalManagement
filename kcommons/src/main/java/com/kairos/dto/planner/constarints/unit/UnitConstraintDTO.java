package com.kairos.dto.planner.constarints.unit;

import com.kairos.dto.planner.constarints.ConstraintDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UnitConstraintDTO extends ConstraintDTO {

    private Long unitId;
    private Long parentCountryConstraintId;
}
