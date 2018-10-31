package com.kairos.dto.activity.activity.activity_tabs.communication_tab;

import com.kairos.enums.DurationType;

import java.io.Serializable;

/**
 * CreatedBy vipulpandey on 6/10/18
 **/
public class FrequencySettings implements Serializable {
    private Integer timeValue;
    private DurationType durationType;

    public FrequencySettings() {

    }

    public Integer getTimeValue() {
        return timeValue;
    }

    public void setTimeValue(Integer timeValue) {
        this.timeValue = timeValue;
    }

    public DurationType getDurationType() {
        return durationType;
    }

    public void setDurationType(DurationType durationType) {
        this.durationType = durationType;
    }

    @Override
    public String toString() {
        return "FrequencySettings{" +
                "timeValue=" + timeValue +
                ", durationType=" + durationType +
                '}';
    }
}
