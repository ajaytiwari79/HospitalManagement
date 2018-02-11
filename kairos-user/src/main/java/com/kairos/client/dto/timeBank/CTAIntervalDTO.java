package com.kairos.client.dto.timeBank;


import com.kairos.util.TimeInterval;

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
}
