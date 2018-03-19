package com.kairos.response.dto.web.pay_table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.validation.constraints.NotNull;

/**
 * Created by vipul on 16/3/18.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PayGradeMatrixDTO {
    @NotNull(message = "Pay Group Area can not be null")
    private Long payGroupAreaId;
    @NotNull(message = "Pay Grade value can not be null")
    private Long payGradeValue;
    private Long id;

    public PayGradeMatrixDTO() {
        //default cons
    }

    public Long getPayGroupAreaId() {
        return payGroupAreaId;
    }

    public void setPayGroupAreaId(Long payGroupAreaId) {
        this.payGroupAreaId = payGroupAreaId;
    }

    public Long getPayGradeValue() {
        return payGradeValue;
    }

    public void setPayGradeValue(Long payGradeValue) {
        this.payGradeValue = payGradeValue;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PayGradeMatrixDTO(Long payGroupAreaId, Long payGradeValue, Long id) {
        this.payGroupAreaId = payGroupAreaId;
        this.payGradeValue = payGradeValue;
        this.id = id;
    }
}
