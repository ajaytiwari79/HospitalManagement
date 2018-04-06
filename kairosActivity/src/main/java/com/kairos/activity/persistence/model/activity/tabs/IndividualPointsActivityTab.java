package com.kairos.activity.persistence.model.activity.tabs;

import java.io.Serializable;

/**
 * Created by pawanmandhan on 23/8/17.
 */
public class IndividualPointsActivityTab implements Serializable{

    //method for calculating individual points
    private String individualPointsCalculationMethod;
    private Double numberOfFixedPoints;


    public IndividualPointsActivityTab() {
    }

    public IndividualPointsActivityTab(String individualPointsCalculationMethod, Double numberOfFixedPoints) {
        this.individualPointsCalculationMethod = individualPointsCalculationMethod;
        this.numberOfFixedPoints = numberOfFixedPoints;
    }

    public String getIndividualPointsCalculationMethod() {
        return individualPointsCalculationMethod;
    }

    public void setIndividualPointsCalculationMethod(String individualPointsCalculationMethod) {
        this.individualPointsCalculationMethod = individualPointsCalculationMethod;
    }

    public Double getNumberOfFixedPoints() {
        return numberOfFixedPoints;
    }

    public void setNumberOfFixedPoints(Double numberOfFixedPoints) {
        this.numberOfFixedPoints = numberOfFixedPoints;
    }
}
