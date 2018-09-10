package com.kairos.dto.user.employment;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * Created by yatharth on 19/4/18.
 */

public class EmploymentDTO {
    @NotNull
    private String endDate;
    private Long reasonCodeId;
    private Long accessGroupIdOnEmploymentEnd;
    @NotNull(message = "Main employment start date can't be null")
    private LocalDate mainEmploymentStartDate;
    private LocalDate mainEmploymentEndDate;
    private boolean mainEmployment;
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

    public LocalDate getMainEmploymentStartDate() {
        return mainEmploymentStartDate;
    }

    public void setMainEmploymentStartDate(LocalDate mainEmploymentStartDate) {
        this.mainEmploymentStartDate = mainEmploymentStartDate;
    }

    public LocalDate getMainEmploymentEndDate() {
        return mainEmploymentEndDate;
    }

    public void setMainEmploymentEndDate(LocalDate mainEmploymentEndDate) {
        this.mainEmploymentEndDate = mainEmploymentEndDate;
    }


    public boolean isMainEmployment() {
        return mainEmployment;
    }

    public void setMainEmployment(boolean mainEmployment) {
        this.mainEmployment = mainEmployment;
    }
}
