package com.kairos.dto.master_data;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.math.BigInteger;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AgreementSectionDTO {


    private BigInteger id;

    @Pattern(message = "Numbers and special character are not allowed",regexp = "^[a-zA-Z\\s]+$")
    @NotBlank(message = "Section Name can't be Empty")
    private String title;

    private List<ClauseBasicDTO> clauses;

    private List<AgreementSectionDTO> subSections;

    public List<AgreementSectionDTO> getSubSections() {
        return subSections;
    }

    public void setSubSections(List<AgreementSectionDTO> subSections) {
        this.subSections = subSections;
    }

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public AgreementSectionDTO() {
    }
}
