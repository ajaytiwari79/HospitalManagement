package com.kairos.persistence.model.counter;

import com.kairos.ApplicableFor;
import com.kairos.activity.counter.FilterCriteria;
import com.kairos.activity.enums.counter.ChartType;
import com.kairos.activity.enums.counter.CounterSize;
import com.kairos.enums.CounterType;
import com.kairos.persistence.model.counter.chart.BaseChart;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

/*
 * @author: mohit.shakya@oodlestechnologies.com
 * @dated: Jun/27/2018
 */

@Document(collection = "counter")
public class KPI extends Counter {
    private BaseChart chart;
    private CounterSize size;

    public KPI(String title, BaseChart chart, CounterSize size, CounterType type, boolean treatAsCounter, BigInteger primaryCounter){
        super(title, type, treatAsCounter, primaryCounter);
        this.chart = chart;
        this.size = size;
    }

    public KPI(String title, BaseChart chart, CounterSize size, CounterType type, boolean treatAsCounter, BigInteger primaryCounter, Set<ApplicableFor> applicableFor){
        super(title, type, treatAsCounter, primaryCounter,applicableFor);
        this.chart = chart;
        this.size = size;
    }

    public BaseChart getChart() {
        return chart;
    }

    public void setChart(BaseChart chart) {
        this.chart = chart;
    }
}
