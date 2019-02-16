package com.kairos.dto.activity.counter.configuration;

import com.kairos.dto.activity.counter.data.FilterCriteria;
import com.kairos.dto.activity.counter.enums.ChartType;
import com.kairos.dto.activity.counter.enums.CounterSize;
import com.kairos.enums.FilterType;

import java.math.BigInteger;
import java.util.List;

public class KPIDTO {
    private BigInteger id;
    private String title;
    private boolean counter;
    private CounterSize size;
    private ChartType chart;
    private List<FilterType> filterTypes;
    private String calculationFormula;
    //applicable filter of kpi
    private List<FilterCriteria> defaultFilters;
    //selected filer by staff
    private List<FilterCriteria> selectedFilters;
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

    public List<FilterCriteria> getDefaultFilters() {
        return defaultFilters;
    }

    public void setDefaultFilters(List<FilterCriteria> defaultFilters) {
        this.defaultFilters = defaultFilters;
    }

    public List<FilterCriteria> getSelectedFilters() {
        return selectedFilters;
    }

    public void setSelectedFilters(List<FilterCriteria> selectedFilters) {
        this.selectedFilters = selectedFilters;
    }

    public List<FilterType> getFilterTypes() {
        return filterTypes;
    }

    public void setFilterTypes(List<FilterType> filterTypes) {
        this.filterTypes = filterTypes;
    }
}
