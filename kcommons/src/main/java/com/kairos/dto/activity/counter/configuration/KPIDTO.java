package com.kairos.dto.activity.counter.configuration;

import com.kairos.dto.activity.counter.data.FilterCriteria;
import com.kairos.dto.activity.counter.enums.ChartType;
import com.kairos.dto.activity.counter.enums.ConfLevel;
import com.kairos.dto.activity.counter.enums.CounterSize;
import com.kairos.dto.activity.counter.enums.CounterType;
import com.kairos.dto.activity.counter.fibonacci_kpi.FibonacciKPIConfigDTO;
import com.kairos.enums.DurationType;
import com.kairos.enums.FilterType;
import com.kairos.enums.kpi.KPIRepresentation;
import com.kairos.enums.wta.IntervalUnit;

import java.math.BigInteger;
import java.util.List;

public class KPIDTO {
    private BigInteger id;
    private String title;
    private boolean counter;
    private CounterSize size;
    private ChartType chart;
    private CounterType type;
    private List<FilterType> filterTypes;
    private String calculationFormula;
    //applicable filter of kpi
    private List<FilterCriteria> defaultFilters;
    //selected filer by staff
    private List<FilterCriteria> selectedFilters;
    private ConfLevel applicableFor;
    private boolean fibonacciKPI;
    private KPIRepresentation kpiRepresentation;
    private DurationType frequencyType;
    // frequency value
    private int value;
    private IntervalUnit interval;

    private String description;
    private Long referenceId;
    private BigInteger categoryId;
    private ConfLevel confLevel;
    private List<FibonacciKPIConfigDTO> fibonacciKPIConfigs;
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

    public CounterType getType() {
        return type;
    }

    public void setType(CounterType type) {
        this.type = type;
    }

    public ConfLevel getApplicableFor() {
        return applicableFor;
    }

    public void setApplicableFor(ConfLevel applicableFor) {
        this.applicableFor = applicableFor;
    }

    public boolean isFibonacciKPI() {
        return fibonacciKPI;
    }

    public void setFibonacciKPI(boolean fibonacciKPI) {
        this.fibonacciKPI = fibonacciKPI;
    }

    public DurationType getFrequencyType() {
        return frequencyType;
    }

    public void setFrequencyType(DurationType frequencyType) {
        this.frequencyType = frequencyType;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public IntervalUnit getInterval() {
        return interval;
    }

    public void setInterval(IntervalUnit interval) {
        this.interval = interval;
    }

    public KPIRepresentation getKpiRepresentation() {

        return kpiRepresentation;
    }

    public void setKpiRepresentation(KPIRepresentation kpiRepresentation) {
        this.kpiRepresentation = kpiRepresentation;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(Long referenceId) {
        this.referenceId = referenceId;
    }

    public ConfLevel getConfLevel() {
        return confLevel;
    }

    public void setConfLevel(ConfLevel confLevel) {
        this.confLevel = confLevel;
    }

    public List<FibonacciKPIConfigDTO> getFibonacciKPIConfigs() {
        return fibonacciKPIConfigs;
    }

    public void setFibonacciKPIConfigs(List<FibonacciKPIConfigDTO> fibonacciKPIConfigs) {
        this.fibonacciKPIConfigs = fibonacciKPIConfigs;
    }

    public BigInteger getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(BigInteger categoryId) {
        this.categoryId = categoryId;
    }
}
