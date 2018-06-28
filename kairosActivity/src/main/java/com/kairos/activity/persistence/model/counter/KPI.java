package com.kairos.activity.persistence.model.counter;

import com.kairos.activity.enums.counter.ChartType;
import com.kairos.activity.enums.counter.CounterSize;
import com.kairos.activity.persistence.model.counter.chart.BaseChart;
import org.springframework.data.mongodb.core.mapping.Document;

/*
 * @author: mohit.shakya@oodlestechnologies.com
 * @dated: Jun/27/2018
 */

@Document(collection = "counter")
public class KPI extends Counter {
    private String title;
    private ChartType chartType;
    private BaseChart chart;
    private CounterSize size;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public BaseChart getChart() {
        return chart;
    }

    public void setChart(BaseChart chart) {
        this.chart = chart;
    }

    public ChartType getChartType() {
        return chartType;
    }

    public void setChartType(ChartType chartType) {
        this.chartType = chartType;
    }
}
