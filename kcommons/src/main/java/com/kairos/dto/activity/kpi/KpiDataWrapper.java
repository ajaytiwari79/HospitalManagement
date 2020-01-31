package com.kairos.dto.activity.kpi;

import com.kairos.commons.utils.DateTimeInterval;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Created By G.P.Ranjan on 31/1/20
 **/
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KpiDataWrapper {
    private StaffEmploymentTypeDTO staffEmploymentTypeDTO;
    private Long organizationId;
    private List<DateTimeInterval> dateTimeIntervals;
}
