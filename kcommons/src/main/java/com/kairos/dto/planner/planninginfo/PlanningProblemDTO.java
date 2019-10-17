package com.kairos.dto.planner.planninginfo;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class PlanningProblemDTO {

    private Long id;
    private String name;
    private String description;
    private String type;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long unitId = -1L;
}
