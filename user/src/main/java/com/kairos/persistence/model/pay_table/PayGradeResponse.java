package com.kairos.persistence.model.pay_table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.persistence.model.country.pay_table.PayGroupAreaDTO;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.List;

/**
 * Created by vipul on 16/3/18.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@QueryResult
public class PayGradeResponse {
    private Long payTableId;
    private Long payGradeLevel;
    private Long payGradeId;
    private Boolean published;
    private List<PayGroupAreaDTO> payGroupAreas;


    public PayGradeResponse() {
        //default
    }

    public PayGradeResponse(Long payTableId, Long payGradeLevel, Long payGradeId, List<PayGroupAreaDTO> payGroupAreas, Boolean published) {
        this.payTableId = payTableId;
        this.payGradeLevel = payGradeLevel;
        this.payGradeId = payGradeId;
        this.payGroupAreas = payGroupAreas;
        this.published = published;
    }

    public PayGradeResponse(Long payTableId) {
        this.payTableId = payTableId;
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

    public Long getPayGradeId() {
        return payGradeId;
    }

    public void setPayGradeId(Long payGradeId) {
        this.payGradeId = payGradeId;
    }

    public List<PayGroupAreaDTO> getPayGroupAreas() {
        return payGroupAreas;
    }

    public void setPayGroupAreas(List<PayGroupAreaDTO> payGroupAreas) {
        this.payGroupAreas = payGroupAreas;
    }

    public Boolean getPublished() {
        return published;
    }

    public void setPublished(Boolean published) {
        this.published = published;
    }
}
