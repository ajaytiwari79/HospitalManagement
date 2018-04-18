package com.kairos.client.dto.time_bank;


public class CTAIntervalDTO {

    private String compensationType;
    private float compensationValue;
    private int startTime;
    private int endTime;

    public CTAIntervalDTO(String compensationType, float compensationValue) {
        this.compensationType = compensationType;
        this.compensationValue = compensationValue;
    }

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

    public float getCompensationValue() {
        return compensationValue;
    }

    public void setCompensationValue(float compensationValue) {
        this.compensationValue = compensationValue;
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
