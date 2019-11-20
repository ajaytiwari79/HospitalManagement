package com.kairos.persistence.model.country.pay_table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created by vipul on 16/3/18.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PayGradeDTO {
    @NotNull(message = "Pay Table  can not be null")
    private Long payTableId;
    @NotNull(message = "Pay Grade Level can not be null")
    private Long payGradeLevel;
    private Long payGradeId;
    @Valid
    private List<PayGroupAreaDTO> payGroupAreas;

    public PayGradeDTO() {
        // default
    }

    public Long getPayTableId() {
        return payTableId;
    }

    public void setPayTableId(Long payTableId) {
        this.payTableId = payTableId;
    }

    public Long getPayGradeLevel() {
        return payGradeLevel;
    }

    public void setPayGradeLevel(Long payGradeLevel) {
        this.payGradeLevel = payGradeLevel;
    }

    public List<PayGroupAreaDTO> getPayGroupAreas() {
        return payGroupAreas;
    }

    public void setPayGroupAreas(List<PayGroupAreaDTO> payGroupAreas) {
        this.payGroupAreas = payGroupAreas;
    }

    public Long getPayGradeId() {
        return payGradeId;
    }

    public void setPayGradeId(Long payGradeId) {
        this.payGradeId = payGradeId;
    }
}
