package com.kairos.dto.activity.counter.fibonacci_kpi;

import com.kairos.dto.activity.counter.configuration.KPIDTO;
import com.kairos.dto.activity.counter.data.FilterCriteria;
import com.kairos.dto.activity.counter.enums.ConfLevel;
import com.kairos.dto.activity.counter.enums.CounterType;
import com.kairos.enums.FilterType;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.math.BigInteger;
import java.util.List;

import static com.kairos.commons.utils.ObjectUtils.newArrayList;
import static com.kairos.dto.activity.counter.enums.ChartType.BAR;
import static com.kairos.dto.activity.counter.enums.CounterSize.SIZE_8X2;
import static com.kairos.enums.FilterType.STAFF_IDS;
import static com.kairos.enums.FilterType.TIME_INTERVAL;

/**
 * pradeep
 * 10/4/19
 */
//@Setter
//@Getter
public class FibonacciKPIDTO {

    private BigInteger id;
    @NotEmpty(message = "error.name.notnull")
    private String title;
    private String description;
    private Long referenceId;
    private ConfLevel confLevel;
    private List<FilterType> filterTypes;
    private List<FilterCriteria> criteriaList;
    @Valid
    @Size(min = 2,message = "message.fibonacci.kpi.count")
    private List<FibonacciKPIConfigDTO> fibonacciKPIConfigs;
    private BigInteger categoryId;
    private boolean fibonacciKPI;
    private List<KPIDTO> kpiCounters;

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public String getTitle() {
        return title.trim();
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

    public List<FilterType> getFilterTypes() {
        return filterTypes;
    }

    public void setFilterTypes(List<FilterType> filterTypes) {
        this.filterTypes = newArrayList(STAFF_IDS,TIME_INTERVAL);
    }

    public List<FilterCriteria> getCriteriaList() {
        return criteriaList;
    }

    public void setCriteriaList(List<FilterCriteria> criteriaList) {
        this.criteriaList = criteriaList;
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

    public boolean isFibonacciKPI() {
        return fibonacciKPI;
    }

    public void setFibonacciKPI(boolean fibonacciKPI) {
        this.fibonacciKPI = fibonacciKPI;
    }

    public List<KPIDTO> getKpiCounters() {
        return kpiCounters;
    }

    public void setKpiCounters(List<KPIDTO> kpiCounters) {
        this.kpiCounters = kpiCounters;
    }

    public void setTitle(String title) {
        this.title = title.trim();
    }

    public FibonacciKPIDTO() {
        this.fibonacciKPI = true;
    }
}
