package com.kairos.activity.persistence.model.counter.chart;

import com.kairos.activity.enums.counter.RepresentationUnit;

import java.util.List;

/*
 * @author: mohit.shakya@oodlestechnologies.com
 * @dated: Jun/27/2018
 */

public class SerialChart extends BaseChart {
    private List<String> valuesX;
    private List<String> valuesY;
    private String maxX;
    private String maxY;
    private String titleX;
    private String titleY;
    private RepresentationUnit unitX;
    private RepresentationUnit unitY;

    public List<String> getValuesX() {
        return valuesX;
    }

    public void setValuesX(List<String> valuesX) {
        this.valuesX = valuesX;
    }

    public List<String> getValuesY() {
        return valuesY;
    }

    public void setValuesY(List<String> valuesY) {
        this.valuesY = valuesY;
    }

    public String getMaxX() {
        return maxX;
    }

    public void setMaxX(String maxX) {
        this.maxX = maxX;
    }

    public String getMaxY() {
        return maxY;
    }

    public void setMaxY(String maxY) {
        this.maxY = maxY;
    }

    public RepresentationUnit getUnitX() {
        return unitX;
    }

    public void setUnitX(RepresentationUnit unitX) {
        this.unitX = unitX;
    }

    public RepresentationUnit getUnitY() {
        return unitY;
    }

    public void setUnitY(RepresentationUnit unitY) {
        this.unitY = unitY;
    }

    public String getTitleX() {
        return titleX;
    }

    public void setTitleX(String titleX) {
        this.titleX = titleX;
    }

    public String getTitleY() {
        return titleY;
    }

    public void setTitleY(String titleY) {
        this.titleY = titleY;
    }
}
