package com.kairos.persistence.model.user.employment;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.validation.constraints.NotNull;

/**
 * Created by yatharth on 19/4/18.
 */
public class EmploymentDTO {
    @NotNull
    private String endDate;
    private Long reasonCodeId;
    private Long accessGroupIdOnEmploymentEnd;

    public Long getAccessGroupIdOnEmploymentEnd() {
        return accessGroupIdOnEmploymentEnd;
    }

    public void setAccessGroupIdOnEmploymentEnd(Long accessGroupIdOnEmploymentEnd) {
        this.accessGroupIdOnEmploymentEnd = accessGroupIdOnEmploymentEnd;
    }


    public Long getReasonCodeId() {
        return reasonCodeId;
    }

    public void setReasonCodeId(Long reasonCodeId) {
        this.reasonCodeId = reasonCodeId;
    }

    public String getEndDate() {
        return endDate;
    }
    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

}
