package com.kairos.dto.activity.counter.configuration;

import com.kairos.dto.activity.counter.data.FilterCriteria;
import com.kairos.dto.activity.counter.enums.ChartType;
import com.kairos.dto.activity.counter.enums.CounterSize;

import java.math.BigInteger;
import java.util.List;

public class KPIDTO {
    private BigInteger id;
    private String title;
    private boolean counter;
    private CounterSize size;
    private ChartType chart;
    private String calculationFormula;
    //applicable filter of kpi
    private List<FilterCriteria> criteriaList;
    //selected filer by staff
    private List<FilterCriteria> selectedFilter;
    public KPIDTO(){

    }

    public ChartType getChart() {
        return chart;
    }

    public void setChart(ChartType chart) {
        this.chart = chart;
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isCounter() {
        return counter;
    }

    public void setCounter(boolean counter) {
        this.counter = counter;
    }

    public CounterSize getSize() {
        return size;
    }

    public void setSize(CounterSize size) {
        this.size = size;
    }

    public String getCalculationFormula() {
        return calculationFormula;
    }

    public void setCalculationFormula(String calculationFormula) {
        this.calculationFormula = calculationFormula;
    }

    public List<FilterCriteria> getCriteriaList() {
        return criteriaList;
    }

    public void setCriteriaList(List<FilterCriteria> criteriaList) {
        this.criteriaList = criteriaList;
    }

    public List<FilterCriteria> getSelectedFilter() {
        return selectedFilter;
    }

    public void setSelectedFilter(List<FilterCriteria> selectedFilter) {
        this.selectedFilter = selectedFilter;
    }
}
