package com.kairos.dto.gdpr.master_data;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AgreementSectionDTO {


    private BigInteger id;

    @NotBlank(message = "error.message.name.notNull.orEmpty")
    private String title;

    @NotNull(message = "Section order is Not defined")
    private Integer orderedIndex;

    @Valid
    private List<ClauseBasicDTO> clauses=new ArrayList<>();

    private List<AgreementSectionDTO> subSections=new ArrayList<>();

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

    public Integer getOrderedIndex() { return orderedIndex; }

    public void setOrderedIndex(Integer orderedIndex) { this.orderedIndex = orderedIndex; }

    public AgreementSectionDTO() {
    }
}
