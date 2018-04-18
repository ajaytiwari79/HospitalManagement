package com.planning.responseDto.commonDto;

public class BaseDTO {

    private Long kairosId;
    private Long unitId;
    private String optaPlannerId;
    private String exceptionStatus;

    public Long getKairosId() {
        return kairosId;
    }

    public void setKairosId(Long kairosId) {
        this.kairosId = kairosId;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public String getOptaPlannerId() {
        return optaPlannerId;
    }

    public void setOptaPlannerId(String optaPlannerId) {
        this.optaPlannerId = optaPlannerId;
    }

    public String getExceptionStatus() {
        return exceptionStatus;
    }

    public void setExceptionStatus(String exceptionStatus) {
        this.exceptionStatus = exceptionStatus;
    }
}
