package com.kairos.persistence.model.counter;

import com.kairos.dto.activity.counter.enums.ConfLevel;
import com.kairos.dto.activity.counter.enums.CounterType;
import lombok.Getter;
import lombok.Setter;

import static com.kairos.commons.utils.ObjectUtils.newArrayList;
import static com.kairos.dto.activity.counter.enums.ChartType.BAR;
import static com.kairos.dto.activity.counter.enums.CounterSize.SIZE_8X2;
import static com.kairos.enums.FilterType.STAFF_IDS;
import static com.kairos.enums.FilterType.TIME_INTERVAL;

@Getter
@Setter
public class FibonacciKPI extends KPI{
    private String description;
    private Long referenceId;
    private ConfLevel confLevel;

    private boolean fibonacciKPI;

    public FibonacciKPI() {
        this.type = CounterType.FIBONACCI;
        this.fibonacciKPI = true;
        this.chart = BAR;
        this.size = SIZE_8X2;
        this.filterTypes = newArrayList(STAFF_IDS,TIME_INTERVAL);
    }
}
