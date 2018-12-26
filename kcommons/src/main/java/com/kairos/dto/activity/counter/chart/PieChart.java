package com.kairos.dto.activity.counter.chart;

import com.kairos.dto.activity.counter.enums.ChartType;
import com.kairos.dto.activity.counter.enums.RepresentationUnit;

import java.util.List;

/*
 * @author: mohit.shakya@oodlestechnologies.com
 * @dated: 29/Jun/2018
 */

public class PieChart extends BaseChart {
    private RepresentationUnit unit;
    private String displayUnit;
    private List<CommonKpiDataUnit> dataList;

    public PieChart(RepresentationUnit unit, String displayUnit, List dataList) {
        super(ChartType.PIE);
        this.unit = unit;
        this.dataList = dataList;
        this.displayUnit = displayUnit;
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

    public List getDataList() {
        return dataList;
    }

    public void setDataList(List dataList) {
        this.dataList = dataList;
    }
}
