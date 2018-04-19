package com.kairos.persistence.model.user.employment;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.validation.constraints.NotNull;

/**
 * Created by yatharth on 19/4/18.
 */
public class EmploymentDTO {
    @NotNull
    private String endDate;

    public String getEndDate() {
        return endDate;
    }
    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

}
