package com.kairos.persistence.model.organization.company;

import org.springframework.data.neo4j.annotation.QueryResult;


@QueryResult
public class CompanyValidationQueryResult {
    private Boolean name;
    private Boolean desiredUrl;
    private String kairosCompanyId;

    public Boolean getName() {
        return name;
    }

    public void setName(Boolean name) {
        this.name = name;
    }

    public Boolean getDesiredUrl() {
        return desiredUrl;
    }

    public void setDesiredUrl(Boolean desiredUrl) {
        this.desiredUrl = desiredUrl;
    }

    public String getKairosCompanyId() {
        return kairosCompanyId;
    }

    public void setKairosCompanyId(String kairosCompanyId) {
        this.kairosCompanyId = kairosCompanyId;
    }
}
