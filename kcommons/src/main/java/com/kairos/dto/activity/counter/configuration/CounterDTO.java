package com.kairos.dto.activity.counter.configuration;

//Created By Pavan on 3/8/18

import com.kairos.dto.activity.counter.data.FilterCriteria;
import com.kairos.dto.activity.counter.enums.CounterType;
import com.kairos.dto.activity.counter.enums.ModuleType;
import com.kairos.enums.DurationType;
import com.kairos.enums.kpi.KPIRepresentation;
import com.kairos.enums.wta.IntervalUnit;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.math.BigInteger;
import java.util.List;
import java.util.Set;

import static com.kairos.constants.CommonMessageConstants.FREQUENCY_VALUE_IS_NOT_POSITIVE;

@Getter
@Setter
@NoArgsConstructor
public class CounterDTO {
    private BigInteger id;
    private CounterType type;
    @NotBlank(message = "error.name.notnull")
    private String title;
    private boolean counter;
    private BigInteger primaryCounter;
    private BigInteger categoryId;
    //calculation formula of per KPI
    private String calculationFormula;
    //applicable filter of kpi
    private List<FilterCriteria> criteriaList;
    //selected filer by staff
    private List<FilterCriteria> selectedFilters;
    private Set<ModuleType> supportedModuleTypes;
    private KPIRepresentation kpiRepresentation;
    private DurationType frequencyType;
    // frequency value
    @Positive(message = FREQUENCY_VALUE_IS_NOT_POSITIVE)
    private int value;
    private IntervalUnit interval;
}
