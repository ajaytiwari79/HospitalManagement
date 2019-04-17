package com.kairos.dto.activity.counter.fibonacci_kpi;

import com.kairos.dto.activity.counter.configuration.KPIDTO;
import com.kairos.dto.activity.counter.data.FilterCriteria;
import com.kairos.dto.activity.counter.enums.ConfLevel;
import com.kairos.enums.FilterType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.List;

/**
 * pradeep
 * 10/4/19
 */
@Setter
@Getter
public class FibonacciKPIDTO {

    private BigInteger id;
    private String title;
    private String description;
    private Long referenceId;
    private ConfLevel confLevel;
    private List<FilterType> filterTypes;
    private List<FilterCriteria> criteriaList;
    private List<FibonacciKPIConfigDTO> fibonacciKPIConfigs;
    private BigInteger categoryId;
    private boolean fibonacciKPI;


}
