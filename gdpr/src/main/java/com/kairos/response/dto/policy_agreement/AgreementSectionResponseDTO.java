package com.kairos.response.dto.policy_agreement;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.persistence.model.clause.AgreementSectionClause;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;


@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AgreementSectionResponseDTO {

    private Long id;

    @NotBlank
    private String title;
    private String titleHtml;
    private Integer orderedIndex;
    private List<AgreementSectionClause> clauses;
    private List<AgreementSectionResponseDTO> agreementSubSections = new ArrayList<>();

    public Integer getOrderedIndex() {
        return orderedIndex;
    }

    public void setOrderedIndex(Integer orderedIndex) {
        this.orderedIndex = orderedIndex;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<AgreementSectionResponseDTO> getAgreementSubSections() {
        return agreementSubSections;
    }

    public void setAgreementSubSections(List<AgreementSectionResponseDTO> agreementSubSections) { this.agreementSubSections = agreementSubSections; }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<AgreementSectionClause> getClauses() {
        return clauses;
    }

    public void setClauses(List<AgreementSectionClause> clauses) {
        this.clauses = clauses;
    }

    public String getTitleHtml() {
        return titleHtml;
    }

    public void setTitleHtml(String titleHtml) {
        this.titleHtml = titleHtml;
    }

    public AgreementSectionResponseDTO() {

    }

    public AgreementSectionResponseDTO(Long id, @NotBlank String title, String titleHtml, Integer orderedIndex) {
        this.id = id;
        this.title = title;
        this.titleHtml = titleHtml;
        this.orderedIndex = orderedIndex;
    }
}
