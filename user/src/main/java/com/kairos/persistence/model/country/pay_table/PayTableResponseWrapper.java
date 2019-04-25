package com.kairos.persistence.model.country.pay_table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.persistence.model.country.functions.FunctionDTO;
import com.kairos.persistence.model.pay_table.PayTableResponse;
import com.kairos.persistence.model.user.pay_group_area.PayGroupAreaQueryResult;

import java.util.List;

/**
 * Created by vipul on 15/3/18.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PayTableResponseWrapper {
    private List<PayGroupAreaQueryResult> payGroupArea;
    private PayTableResponse payTables;
    private List<FunctionDTO> functions;

    public PayTableResponseWrapper() {
        // default cons
    }

    public PayTableResponse getPayTables() {
        return payTables;
    }

    public void setPayTables(PayTableResponse payTables) {
        this.payTables = payTables;
    }

    public List<PayGroupAreaQueryResult> getPayGroupArea() {
        return payGroupArea;
    }

    public void setPayGroupArea(List<PayGroupAreaQueryResult> payGroupArea) {
        this.payGroupArea = payGroupArea;
    }

    public List<FunctionDTO> getFunctions() {
        return functions;
    }

    public void setFunctions(List<FunctionDTO> functions) {
        this.functions = functions;
    }

    public PayTableResponseWrapper(List<PayGroupAreaQueryResult> payGroupArea, PayTableResponse payTables, List<FunctionDTO> functions) {
        this.functions = functions;
        this.payGroupArea = payGroupArea;
        this.payTables = payTables;
    }
}
