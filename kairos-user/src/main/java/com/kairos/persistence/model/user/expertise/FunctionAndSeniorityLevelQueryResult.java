package com.kairos.persistence.model.user.expertise;

import com.kairos.persistence.model.user.pay_group_area.PayGroupArea;
import com.kairos.persistence.model.user.pay_table.PayGrade;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.List;
import java.util.Map;

/**
 * Created by vipul on 29/3/18.
 */
@QueryResult
public class FunctionAndSeniorityLevelQueryResult {
    private List<Map<String, Object>> functions;
    private List<PayGroupArea> payGroupAreas;
    private PayGrade payGrade;
    public FunctionAndSeniorityLevelQueryResult() {
        //default
    }

    public List<Map<String, Object>> getFunctions() {
        return functions;
    }

    public void setFunctions(List<Map<String, Object>> functions) {
        this.functions = functions;
    }

    public List<PayGroupArea> getPayGroupAreas() {
        return payGroupAreas;
    }

    public void setPayGroupAreas(List<PayGroupArea> payGroupAreas) {
        this.payGroupAreas = payGroupAreas;
    }

    public PayGrade getPayGrade() {
        return payGrade;
    }

    public void setPayGrade(PayGrade payGrade) {
        this.payGrade = payGrade;
    }
}
