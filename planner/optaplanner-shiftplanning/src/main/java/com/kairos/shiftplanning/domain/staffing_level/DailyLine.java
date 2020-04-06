package com.kairos.shiftplanning.domain.staffing_level;


import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public abstract class DailyLine {
    protected LocalDate date;
}
