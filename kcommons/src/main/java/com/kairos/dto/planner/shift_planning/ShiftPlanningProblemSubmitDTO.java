package com.kairos.dto.planner.shift_planning;

import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author pradeep
 * @date - 23/11/18
 */
@Getter
@Setter
public class ShiftPlanningProblemSubmitDTO {

    private BigInteger planningProblemId;
    private List<Long> staffIds=new ArrayList<>();
    private Long unitId;
    private BigInteger planningPeriodId;
    private LocalDate startDate;
    private LocalDate endDate;



}
