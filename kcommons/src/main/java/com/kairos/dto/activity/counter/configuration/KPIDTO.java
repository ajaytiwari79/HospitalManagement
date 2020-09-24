package com.kairos.dto.activity.counter.configuration;

import com.kairos.dto.TranslationInfo;
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
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

@Getter
@Setter
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
    private boolean multiDimensional;
    private Map<String, TranslationInfo> translations;
    public KPIDTO(){
        //Not in use
    }

    public KPIDTO(BigInteger id, CounterSize size) {
        this.id = id;
        this.size = size;
    }
}
