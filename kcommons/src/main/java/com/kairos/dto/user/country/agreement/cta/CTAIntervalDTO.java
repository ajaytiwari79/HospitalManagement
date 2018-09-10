package com.kairos.dto.user.country.agreement.cta;

import com.kairos.commons.utils.TimeInterval;

public class CTAIntervalDTO {

    private String compensationType;
    private int compensationValue;
    private int startTime;
    private int endTime;

    public CTAIntervalDTO(String compensationType, int compensationValue, int startTime, int endTime) {
        this.compensationType = compensationType;
        this.compensationValue = compensationValue;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public CTAIntervalDTO() {
    }

    public String getCompensationType() {
        return compensationType;
    }

    public void setCompensationType(String compensationType) {
        this.compensationType = compensationType;
    }

    public int getCompensationValue() {
        return compensationValue;
    }

    public void setCompensationValue(int compensationValue) {
        this.compensationValue = compensationValue;
    }

    public TimeInterval getInterval() {
        return new TimeInterval(startTime,endTime);
    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public int getEndTime() {
        return endTime;
    }

    public void setEndTime(int endTime) {
        this.endTime = endTime;
    }
}
