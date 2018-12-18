package com.kairos.dto.activity.counter.data;

import com.kairos.dto.activity.counter.chart.KpiDataUnit;
import com.kairos.dto.activity.counter.enums.ChartType;
import com.kairos.dto.activity.counter.enums.DisplayUnit;
import com.kairos.dto.activity.counter.enums.RepresentationUnit;
import java.math.BigInteger;
import java.util.List;

public class RawRepresentationData {
    private BigInteger counterId;
    private String title;
    private ChartType chartType;
    private String displayUnit;
    private RepresentationUnit unit;
    private List<KpiDataUnit> dataList;

    public RawRepresentationData(){

    }

    public RawRepresentationData(BigInteger counterId, String title, ChartType chartType, DisplayUnit displayUnit, RepresentationUnit unit, List<KpiDataUnit> dataList){
        this.counterId = counterId;
        this.title = title;
        this.chartType = chartType;
        this.displayUnit = displayUnit.getDisplayValue();
        this.unit = unit;
        this.dataList = dataList;
    }

    public BigInteger getCounterId() {
        return counterId;
    }

    public void setCounterId(BigInteger counterId) {
        this.counterId = counterId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDisplayUnit() {
        return displayUnit;
    }

    public void setDisplayUnit(String displayUnit) {
        this.displayUnit = displayUnit;
    }

    public RepresentationUnit getUnit() {
        return unit;
    }

    public void setUnit(RepresentationUnit unit) {
        this.unit = unit;
    }

    public List<KpiDataUnit> getDataList() {
        return dataList;
    }

    public void setDataList(List<KpiDataUnit> dataList) {
        this.dataList = dataList;
    }

    public ChartType getChartType() {
        return chartType;
    }

    public void setChartType(ChartType chartType) {
        this.chartType = chartType;
    }
}
