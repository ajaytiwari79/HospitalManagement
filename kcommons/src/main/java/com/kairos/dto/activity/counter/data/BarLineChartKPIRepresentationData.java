package com.kairos.dto.activity.counter.data;

import com.kairos.dto.activity.counter.chart.CommonKpiDataUnit;
import com.kairos.dto.activity.counter.enums.ChartType;
import com.kairos.dto.activity.counter.enums.DisplayUnit;
import com.kairos.dto.activity.counter.enums.RepresentationUnit;

import java.math.BigInteger;
import java.util.List;

public class BarLineChartKPIRepresentationData extends CommonRepresentationData {
    private String xAxis;
    private String yAxis;
    private String lineField;

    public BarLineChartKPIRepresentationData() {
    }

    public BarLineChartKPIRepresentationData(String xAxis, String yAxis, String lineField) {
        this.xAxis = xAxis;
        this.yAxis = yAxis;
        this.lineField = lineField;
    }

    public BarLineChartKPIRepresentationData(BigInteger counterId, String title, ChartType chartType, DisplayUnit displayUnit, RepresentationUnit unit, List<CommonKpiDataUnit> dataList, String xAxis, String yAxis, String lineField) {
        super(counterId, title, chartType, displayUnit, unit, dataList);
        this.xAxis = xAxis;
        this.yAxis = yAxis;
        this.lineField = lineField;
    }

    public String getxAxis() {
        return xAxis;
    }

    public void setxAxis(String xAxis) {
        this.xAxis = xAxis;
    }

    public String getyAxis() {
        return yAxis;
    }

    public void setyAxis(String yAxis) {
        this.yAxis = yAxis;
    }

    public String getLineField() {
        return lineField;
    }

    public void setLineField(String lineField) {
        this.lineField = lineField;
    }
}
