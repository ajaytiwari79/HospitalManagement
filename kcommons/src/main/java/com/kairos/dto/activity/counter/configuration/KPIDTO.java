package com.kairos.dto.activity.counter.configuration;

import com.kairos.dto.activity.counter.enums.ChartType;
import com.kairos.dto.activity.counter.enums.CounterSize;

import java.math.BigInteger;

public class KPIDTO {
    private BigInteger id;
    private String title;
    private boolean counter;
    private CounterSize size;
    private ChartType chart;

    public KPIDTO(){

    }

    public ChartType getChart() {
        return chart;
    }

    public void setChart(ChartType chart) {
        this.chart = chart;
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isCounter() {
        return counter;
    }

    public void setCounter(boolean counter) {
        this.counter = counter;
    }

    public CounterSize getSize() {
        return size;
    }

    public void setSize(CounterSize size) {
        this.size = size;
    }
}
