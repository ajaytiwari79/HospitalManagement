package com.kairos.dto.activity.activity.activity_tabs;/*
 *Created By Pavan on 6/10/18
 *
 */

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PhaseSettingsActivityTab implements Serializable {

    private BigInteger activityId;
    private List<PhaseTemplateValue> phaseTemplateValues;

    public PhaseSettingsActivityTab() {
        //Default Constructor
    }

    public PhaseSettingsActivityTab(List<PhaseTemplateValue> phaseTemplateValues) {
        this.phaseTemplateValues = phaseTemplateValues;
    }

    public PhaseSettingsActivityTab(BigInteger activityId, List<PhaseTemplateValue> phaseTemplateValues) {
        this.activityId = activityId;
        this.phaseTemplateValues = phaseTemplateValues;
    }

    public BigInteger getActivityId() {
        return activityId;
    }

    public void setActivityId(BigInteger activityId) {
        this.activityId = activityId;
    }

    public List<PhaseTemplateValue> getPhaseTemplateValues() {
        Collections.sort(phaseTemplateValues);
        return phaseTemplateValues;
    }

    public void setPhaseTemplateValues(List<PhaseTemplateValue> phaseTemplateValues) {
        Collections.sort(phaseTemplateValues);
        this.phaseTemplateValues = phaseTemplateValues;
    }
}
