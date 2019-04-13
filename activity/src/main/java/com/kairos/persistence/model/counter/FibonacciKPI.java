package com.kairos.persistence.model.counter;

import com.kairos.dto.activity.counter.data.FilterCriteria;
import com.kairos.dto.activity.counter.enums.ConfLevel;
import com.kairos.enums.FilterType;
import com.kairos.persistence.model.common.MongoBaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.List;


@Getter
@Setter
public class FibonacciKPI extends MongoBaseEntity {
    private String title;
    private String description;
    private Long referenceId;
    private ConfLevel confLevel;
    private List<FilterType> filterTypes;
    private List<FilterCriteria> criteriaList;
    private List<FibinacciKPIConfig> fibinacciKPIConfigs;
    private BigInteger categoryId;
}
