package com.kairos.response.dto.web.pay_table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.persistence.model.user.pay_group_area.PayGroupAreaQueryResult;
import com.kairos.persistence.model.user.pay_table.PayTableQueryResult;

import java.util.List;

/**
 * Created by vipul on 15/3/18.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PayTableResponseWrapper {
    private List<PayGroupAreaQueryResult> payGroupArea;
    private List<PayTableQueryResult> payTables;

    public PayTableResponseWrapper() {
    }

    public List<PayGroupAreaQueryResult> getPayGroupArea() {
        return payGroupArea;
    }

    public void setPayGroupArea(List<PayGroupAreaQueryResult> payGroupArea) {
        this.payGroupArea = payGroupArea;
    }

    public List<PayTableQueryResult> getPayTables() {
        return payTables;
    }

    public void setPayTables(List<PayTableQueryResult> payTables) {
        this.payTables = payTables;
    }

    public PayTableResponseWrapper(List<PayGroupAreaQueryResult> payGroupArea, List<PayTableQueryResult> payTables) {
        this.payGroupArea = payGroupArea;
        this.payTables = payTables;
    }
}
