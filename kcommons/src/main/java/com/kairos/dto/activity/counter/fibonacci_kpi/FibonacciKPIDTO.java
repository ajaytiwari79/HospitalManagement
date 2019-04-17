package com.kairos.dto.activity.counter.fibonacci_kpi;

import com.kairos.dto.activity.counter.configuration.KPIDTO;
import com.kairos.dto.activity.counter.data.FilterCriteria;
import com.kairos.dto.activity.counter.enums.ConfLevel;
import com.kairos.enums.FilterType;
import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
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

    public void setTitle(String title) {
        this.title = title.trim();
    }
}
