package com.kairos.dto.activity.shift;

public class ShiftCountDTO {

    private Long employmentId;
    private Integer count;

    public Long getEmploymentId() {
        return employmentId;
    }

    public void setEmploymentId(Long employmentId) {
        this.employmentId = employmentId;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}