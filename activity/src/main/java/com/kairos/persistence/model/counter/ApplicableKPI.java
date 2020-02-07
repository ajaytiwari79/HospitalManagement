package com.kairos.persistence.model.counter;

import com.kairos.dto.activity.counter.enums.ConfLevel;
import com.kairos.dto.activity.counter.enums.XAxisConfig;
import com.kairos.enums.DurationType;
import com.kairos.enums.kpi.YAxisConfig;
import com.kairos.enums.kpi.KPIRepresentation;
import com.kairos.enums.wta.IntervalUnit;
import com.kairos.persistence.model.common.MongoBaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

import static com.kairos.commons.utils.ObjectUtils.isCollectionNotEmpty;


@Getter
@Setter
@NoArgsConstructor
public class ApplicableKPI extends MongoBaseEntity {
    private BigInteger activeKpiId;
    private BigInteger baseKpiId;
    private Long countryId;
    private Long unitId;
    private Long staffId;
    private ConfLevel level;
    private String title;
    private ApplicableFilter applicableFilter;
    private List<FibonacciKPIConfig> fibonacciKPIConfigs;
    private XAxisConfig xAxisConfig;
    private YAxisConfig yAxisConfig;
    private KPIRepresentation kpiRepresentation;
    private transient LocalDate dateForKPISetCalculation;
    private DurationType frequencyType;
    // frequency value
    private int value;
    private IntervalUnit interval;
    private boolean fibonacciKPI;
    // use for country admin and unit manager if they create copy kpi from bottom instrument of kpi
    private boolean copy;

    public ApplicableKPI(BigInteger activeKpiId, BigInteger baseKpiId, Long countryId, Long unitId, Long staffId, ConfLevel level) {
        this.activeKpiId = activeKpiId;
        this.baseKpiId = baseKpiId;
        this.countryId = countryId;
        this.unitId = unitId;
        this.staffId = staffId;
        this.level = level;
    }

    public ApplicableKPI(BigInteger activeKpiId, BigInteger baseKpiId, Long countryId, Long unitId, Long staffId, ConfLevel level, ApplicableFilter applicableFilter,String title,boolean copy,KPIRepresentation kpiRepresentation ,IntervalUnit interval,int value ,DurationType frequencyType,List<FibonacciKPIConfig> fibonacciKPIConfigs) {
        this.activeKpiId = activeKpiId;
        this.baseKpiId = baseKpiId;
        this.countryId = countryId;
        this.unitId = unitId;
        this.staffId = staffId;
        this.level = level;
        this.applicableFilter=applicableFilter;
        this.copy=copy;
        this.title=title;
        this.kpiRepresentation=kpiRepresentation;
        this.value=value;
        this.interval=interval;
        this.frequencyType=frequencyType;
        this.fibonacciKPIConfigs = fibonacciKPIConfigs;
    }

    public  ApplicableKPI(KPIRepresentation  kpiRepresentation,int value,IntervalUnit interval,DurationType frequencyType){
        this.kpiRepresentation=kpiRepresentation;
        this.value=value;
        this.interval=interval;
        this.frequencyType=frequencyType;
    }

    public ApplicableKPI(BigInteger activeKpiId, BigInteger baseKpiId, Long countryId, Long unitId, Long staffId, ConfLevel level, ApplicableFilter applicableFilter,String title,boolean copy,List<FibonacciKPIConfig> fibonacciKPIConfigs,KPIRepresentation kpiRepresentation) {
        this.activeKpiId = activeKpiId;
        this.baseKpiId = baseKpiId;
        this.countryId = countryId;
        this.unitId = unitId;
        this.staffId = staffId;
        this.level = level;
        this.applicableFilter=applicableFilter;
        this.copy=copy;
        this.title=title;
        this.fibonacciKPIConfigs = fibonacciKPIConfigs;
        this.kpiRepresentation=kpiRepresentation;
    }

    public boolean isFibonacciKPI() {
        return isCollectionNotEmpty(fibonacciKPIConfigs);
    }

    private enum GraphType {
        DATA_PER_TIMESLOT,STACKED,COLUMN


    }
}
