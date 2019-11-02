package com.kairos.dto.activity.wta;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IntervalBalance {

    private float total;
    private float scheduled;
    private float available;
    private LocalDate startDate;
    private LocalDate endDate;
    private float approved;
}
