package com.kairos.response.dto.web.employment_dto;

import java.time.LocalDate;

public class EmploymentOverlapDTO {
    private LocalDate mainEmploymentStartDate;
    private LocalDate mainEmploymentEndDate;
    private LocalDate afterChangeStartDate;
    private LocalDate afterChangeEndDate;
    //private boolean hasMainEmployment;

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

    public LocalDate getAfterChangeStartDate() {
        return afterChangeStartDate;
    }

    public void setAfterChangeStartDate(LocalDate afterChangeStartDate) {
        this.afterChangeStartDate = afterChangeStartDate;
    }

    public LocalDate getAfterChangeEndDate() {
        return afterChangeEndDate;
    }

    public void setAfterChangeEndDate(LocalDate afterChangeEndDate) {
        this.afterChangeEndDate = afterChangeEndDate;
    }

//    public boolean isHasMainEmployment() {
//        return hasMainEmployment;
//    }
//
//    public void setHasMainEmployment(boolean hasMainEmployment) {
//        this.hasMainEmployment = hasMainEmployment;
//    }
}
