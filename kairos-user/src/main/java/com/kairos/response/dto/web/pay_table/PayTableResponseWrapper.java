package com.kairos.response.dto.web.pay_table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.persistence.model.user.pay_group_area.PayGroupAreaQueryResult;
import com.kairos.persistence.model.user.pay_table.PayTableResponse;

import java.util.List;

/**
 * Created by vipul on 15/3/18.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PayTableResponseWrapper {
    private List<PayGroupAreaQueryResult> payGroupArea;
    private List<PayTableResponse> payTables;

    public PayTableResponseWrapper() {
    }

    public List<PayGroupAreaQueryResult> getPayGroupArea() {
        return payGroupArea;
    }

    public void setPayGroupArea(List<PayGroupAreaQueryResult> payGroupArea) {
        this.payGroupArea = payGroupArea;
    }

    public List<PayTableResponse> getPayTables() {
        return payTables;
    }

    public void setPayTables(List<PayTableResponse> payTables) {
        this.payTables = payTables;
    }

    public PayTableResponseWrapper(List<PayGroupAreaQueryResult> payGroupArea, List<PayTableResponse> payTables) {
        this.payGroupArea = payGroupArea;
        this.payTables = payTables;
    }
}
