package com.kairos.response.dto.agreement_template;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.persistance.model.clause.Clause;

import java.math.BigInteger;
import java.util.List;


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AgreementSectionResponseDto {

    BigInteger id;
    private String title;
    List<Clause> clauses;

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public List<Clause> getClauses() {
        return clauses;
    }

    public void setClauses(List<Clause> clauses) {
        this.clauses = clauses;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public AgreementSectionResponseDto()
    {

    }

}
