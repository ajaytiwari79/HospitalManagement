package com.kairos.persistence.model.user.pay_table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.response.dto.web.pay_table.PayTableMatrixDTO;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.List;
import java.util.Objects;

/**
 * Created by vipul on 16/3/18.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@QueryResult
public class PayGradeQueryResult {
    private Long payTableId;
    private Long payGradeLevel;
    private Long payGradeId;
    private Boolean published;
    private List<PayTableMatrixDTO> payTableMatrix;

    public PayGradeQueryResult() {
        //default
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

    public List<PayTableMatrixDTO> getPayTableMatrix() {
        return payTableMatrix;
    }

    public void setPayTableMatrix(List<PayTableMatrixDTO> payTableMatrix) {
        this.payTableMatrix = payTableMatrix;
    }

    public Boolean getPublished() {
        return published;
    }

    public void setPublished(Boolean published) {
        this.published = published;
    }

    public PayGradeQueryResult(Long payTableId, Long payGradeLevel, Long payGradeId, List<PayTableMatrixDTO> payTableMatrix, Boolean published) {
        this.payTableId = payTableId;
        this.payGradeLevel = payGradeLevel;
        this.payGradeId = payGradeId;
        this.payTableMatrix = payTableMatrix;
        this.published = published;
    }


}
