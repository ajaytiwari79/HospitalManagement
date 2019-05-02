package com.kairos.persistence.model.counter;

import com.kairos.dto.activity.counter.enums.ConfLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

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
//        this.setType(CounterType.FIBONACCI);
//        this.setFibonacciKPI(true);
    }
}
