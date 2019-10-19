package com.kairos.dto.planner.planninginfo;

import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;
@Getter
@Setter
public class PlanningSubmissionDTO {
    private List<LocalDate> dates;
    private Long unitId;
    private BigInteger solverConfigId;
}
