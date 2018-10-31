package com.kairos.persistence.model.counter;

import com.kairos.dto.activity.counter.enums.CounterType;
import com.kairos.dto.activity.counter.enums.CounterSize;
import com.kairos.dto.activity.counter.chart.BaseChart;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigInteger;

/*
 * @author: mohit.shakya@oodlestechnologies.com
 * @dated: Jun/27/2018
 */

@Document(collection = "counter")
public class KPI extends Counter {
    private BaseChart chart;
    private CounterSize size;

    public KPI() {
        //Default Constructor
    }

    public KPI(String title, BaseChart chart, CounterSize size, CounterType type, boolean counter, BigInteger primaryCounter){
        super(title, type, counter, primaryCounter);
        this.chart = chart;
        this.size = size;
    }


    public BaseChart getChart() {
        return chart;
    }

    public void setChart(BaseChart chart) {
        this.chart = chart;
    }

    public CounterSize getSize() {
        return size;
    }

    public void setSize(CounterSize size) {
        this.size = size;
    }
}
