package com.kairos.dto.activity.staffing_level;

import com.kairos.dto.activity.activity.ActivityDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UnityStaffingLevelRelatedDetails {
    private List<HashMap> activities;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate planningPeriodStartDate;
    private LocalDate planningPeriodEndDate;
    private LocalDate weekStartDate;
    private LocalDate weekEndDate;
}
