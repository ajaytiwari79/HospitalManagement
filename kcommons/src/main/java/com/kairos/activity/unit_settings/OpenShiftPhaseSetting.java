package com.kairos.activity.unit_settings;

import java.util.List;

public class OpenShiftPhaseSetting {


    private Integer minOpenShiftHours;
    private List<OpenShiftPhase> openShiftPhases;

    public OpenShiftPhaseSetting(Integer minOpenShiftHours, List<OpenShiftPhase> openShiftPhases) {
        this.minOpenShiftHours = minOpenShiftHours;
        this.openShiftPhases = openShiftPhases;
    }

    public OpenShiftPhaseSetting() {
        //Default Constructor
    }

    public Integer getMinOpenShiftHours() {
        return minOpenShiftHours;
    }

    public void setMinOpenShiftHours(Integer minOpenShiftHours) {
        this.minOpenShiftHours = minOpenShiftHours;
    }

    public List<OpenShiftPhase> getOpenShiftPhases() {
        return openShiftPhases;
    }

    public void setOpenShiftPhases(List<OpenShiftPhase> openShiftPhases) {
        this.openShiftPhases = openShiftPhases;
    }
}
