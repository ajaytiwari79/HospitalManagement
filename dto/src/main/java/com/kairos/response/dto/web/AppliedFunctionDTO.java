package com.kairos.response.dto.web;

import java.time.LocalDate;

/**
 * Created by oodles on 1/6/18.
 */
public class AppliedFunctionDTO {

    private LocalDate date;
    private FunctionDTO functionDTO;

    public AppliedFunctionDTO() {

    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public FunctionDTO getFunctionDTO() {
        return functionDTO;
    }

    public void setFunctionDTO(FunctionDTO functionDTO) {
        this.functionDTO = functionDTO;
    }
}
