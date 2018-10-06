package com.kairos.dto.activity.activity.activity_tabs;/*
 *Created By Pavan on 6/10/18
 *
 */

import com.kairos.dto.activity.activity.activity_tabs.PhaseTemplateValue;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;

public class PhaseSettingsActivityTab implements Serializable {

    private BigInteger activityId;
    private List<PhaseTemplateValue> phaseTemplateValues;

    public PhaseSettingsActivityTab() {
        //Default Constructor
    }

    public PhaseSettingsActivityTab(List<PhaseTemplateValue> phaseTemplateValues) {
        this.phaseTemplateValues = phaseTemplateValues;
    }

    public BigInteger getActivityId() {
        return activityId;
    }

    public void setActivityId(BigInteger activityId) {
        this.activityId = activityId;
    }

    public List<PhaseTemplateValue> getPhaseTemplateValues() {
        return phaseTemplateValues;
    }

    public void setPhaseTemplateValues(List<PhaseTemplateValue> phaseTemplateValues) {
        this.phaseTemplateValues = phaseTemplateValues;
    }
}
