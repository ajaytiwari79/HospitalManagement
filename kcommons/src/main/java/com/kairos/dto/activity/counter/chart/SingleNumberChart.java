package com.kairos.dto.activity.counter.chart;

import com.kairos.dto.activity.counter.enums.ChartType;
import com.kairos.dto.activity.counter.enums.RepresentationUnit;

import java.util.List;

/*
 * @author: mohit.shakya@oodlestechnologies.com
 * @dated: Jun/27/2018
 */

public class SingleNumberChart extends BaseChart {
    private double value;
    private RepresentationUnit unit;
    private String displayUnit;
    private List<KpiDataUnit> dataList;

    public SingleNumberChart(){
        super(ChartType.NUMBER_ONLY);
    }
    public SingleNumberChart(double value, RepresentationUnit unit, String displayUnit) {
        super(ChartType.NUMBER_ONLY);
        this.value = value;
        this.unit = unit;
    }

    public SingleNumberChart(RepresentationUnit unit, String displayUnit, List dataList){
        super(ChartType.NUMBER_ONLY);
        this.unit = unit;
        this.displayUnit = displayUnit;
        this.dataList = dataList;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public RepresentationUnit getUnit() {
        return unit;
    }

    public void setUnit(RepresentationUnit unit) {
        this.unit = unit;
    }

    public String getDisplayUnit() {
        return displayUnit;
    }

    public void setDisplayUnit(String displayUnit) {
        this.displayUnit = displayUnit;
    }

    public List<KpiDataUnit> getDataList() {
        return dataList;
    }

    public void setDataList(List<KpiDataUnit> dataList) {
        this.dataList = dataList;
    }
}
