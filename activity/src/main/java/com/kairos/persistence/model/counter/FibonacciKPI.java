package com.kairos.persistence.model.counter;

import com.kairos.dto.activity.counter.data.FilterCriteria;
import com.kairos.dto.activity.counter.enums.ConfLevel;
import com.kairos.dto.activity.counter.enums.CounterType;
import com.kairos.dto.activity.counter.enums.ModuleType;
import com.kairos.enums.FilterType;
import com.kairos.persistence.model.common.MongoBaseEntity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Document(collection = "counter")
public class FibonacciKPI extends Counter{
    private String description;
    private Long referenceId;
    private ConfLevel confLevel;
    private List<FibonacciKPIConfig> fibonacciKPIConfigs;
    private boolean fibonacciKPI;

    public FibonacciKPI() {
        this.setType(CounterType.FIBONACCI);
        this.setFibonacciKPI(true);
    }
}
