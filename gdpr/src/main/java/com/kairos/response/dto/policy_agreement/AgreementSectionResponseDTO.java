package com.kairos.response.dto.policy_agreement;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.response.dto.clause.ClauseBasicResponseDTO;

import javax.validation.constraints.NotBlank;
import java.math.BigInteger;
import java.util.List;


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AgreementSectionResponseDTO {

    private BigInteger id;

    @NotBlank
    private String title;

    private List<ClauseBasicResponseDTO> clauses;

    private List<AgreementSectionResponseDTO> subSections;

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public List<AgreementSectionResponseDTO> getSubSections() { return subSections; }

    public void setSubSections(List<AgreementSectionResponseDTO> subSections) { this.subSections = subSections; }

    public List<ClauseBasicResponseDTO> getClauses() {
        return clauses;
    }

    public void setClauses(List<ClauseBasicResponseDTO> clauses) {
        this.clauses = clauses;
    }

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    public AgreementSectionResponseDTO()
    {

    }

}
