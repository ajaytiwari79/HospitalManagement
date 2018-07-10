package com.kairos.dto.master_data;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.validation.constraints.Pattern;
import java.math.BigInteger;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AgreementSectionDTO {


    private BigInteger id;

    @Pattern(message = "Numebers and special character are not allowed",regexp = "^[a-zA-Z\\s]+$")
    private String name;

    private List<BigInteger> clauses;

    private List<ClauseBasicDTO> clauseList;

    public List<ClauseBasicDTO> getClauseList() {
        return clauseList;
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public void setClauseList(List<ClauseBasicDTO> clauseList) {
        this.clauseList = clauseList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<BigInteger> getClauses() {
        return clauses;
    }

    public void setClauses(List<BigInteger> clauses) {
        this.clauses = clauses;
    }

    public AgreementSectionDTO() {
    }
}
