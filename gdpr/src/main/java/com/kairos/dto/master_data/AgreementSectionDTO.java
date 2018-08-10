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

    private List<ClauseBasicDTO> clauses;

    private List<AgreementSectionDTO> subAgreementSections;

    public List<AgreementSectionDTO> getSubAgreementSections() { return subAgreementSections; }

    public void setSubAgreementSections(List<AgreementSectionDTO> subAgreementSections) { this.subAgreementSections = subAgreementSections; }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public List<ClauseBasicDTO> getClauses() {
        return clauses;
    }

    public void setClauses(List<ClauseBasicDTO> clauses) {
        this.clauses = clauses;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public AgreementSectionDTO() {
    }
}
