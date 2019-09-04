package com.kairos.dto.activity.counter.chart;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CommonKpiDataUnit {
    protected String label;
    protected Number refId;
    public CommonKpiDataUnit(String label) {
        this.label = label;
    }

    public CommonKpiDataUnit(String label, Number refId) {
        this.label = label;
        this.refId = refId;
    }
}
