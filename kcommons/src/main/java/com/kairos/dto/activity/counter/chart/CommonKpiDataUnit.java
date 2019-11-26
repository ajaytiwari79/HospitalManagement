package com.kairos.dto.activity.counter.chart;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CommonKpiDataUnit {
    protected String label;
    protected String date;
    protected Number refId;
    public CommonKpiDataUnit(String label) {
        this.label = label;
    }

    public CommonKpiDataUnit(String date,String label) {
        this.label = label;
        this.date = date;
    }

    public CommonKpiDataUnit(String label, Number refId) {
        this.label = label;
        this.refId = refId;
    }
}
