package com.kairos.dto.activity.counter.data;

import com.kairos.dto.activity.counter.chart.CommonKpiDataUnit;
import com.kairos.dto.activity.counter.enums.ChartType;
import com.kairos.dto.activity.counter.enums.DisplayUnit;
import com.kairos.dto.activity.counter.enums.RepresentationUnit;

import java.math.BigInteger;
import java.util.List;

public class KPIRepresentationData extends CommonRepresentationData {
    private KPIAxisData xAxis;
    private KPIAxisData yAxis;

    public KPIRepresentationData() {
    }

    public KPIRepresentationData(KPIAxisData xAxis, KPIAxisData yAxis) {
        this.xAxis = xAxis;
        this.yAxis = yAxis;
    }

    public KPIRepresentationData(BigInteger counterId, String title, ChartType chartType, DisplayUnit displayUnit, RepresentationUnit unit, List<CommonKpiDataUnit> dataList, KPIAxisData xAxis, KPIAxisData yAxis) {
        super(counterId, title, chartType, displayUnit, unit, dataList);
        this.xAxis = xAxis;
        this.yAxis = yAxis;
    }


    public KPIAxisData getxAxis() {
        return xAxis;
    }

    public void setxAxis(KPIAxisData xAxis) {
        this.xAxis = xAxis;
    }

    public KPIAxisData getyAxis() {
        return yAxis;
    }

    public void setyAxis(KPIAxisData yAxis) {
        this.yAxis = yAxis;
    }


}
