package com.kairos.persistence.model.user.pay_table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.response.dto.web.pay_table.PayGradeMatrixDTO;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.List;

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
    private List<PayGradeMatrixDTO> payGrades;


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

    public List<PayGradeMatrixDTO> getPayGrades() {
        return payGrades;
    }

    public void setPayGrades(List<PayGradeMatrixDTO> payGrades) {
        this.payGrades = payGrades;
    }

    public PayGradeQueryResult(Long payTableId, Long payGradeLevel, Long payGradeId, List<PayGradeMatrixDTO> payGrades) {
        this.payTableId = payTableId;
        this.payGradeLevel = payGradeLevel;
        this.payGradeId = payGradeId;
        this.payGrades = payGrades;
    }
}
