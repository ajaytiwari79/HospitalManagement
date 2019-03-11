package com.kairos.dto.gdpr.agreement_template;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.dto.gdpr.master_data.ClauseBasicDTO;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AgreementSectionDTO {


    private Long id;

    @NotBlank(message = "error.message.name.notNull.orEmpty")
    private String title;
    private Integer orderedIndex;
    private String titleHtml;
    protected Long countryId;
    protected Long organizationId;
    @Valid
    private List<ClauseBasicDTO> clauses = new ArrayList<>();
    @Valid
    private List<AgreementSectionDTO> agreementSubSections = new ArrayList<>();

    public List<AgreementSectionDTO> getAgreementSubSections() {
        return agreementSubSections;
    }

    public void setAgreementSubSections(List<AgreementSectionDTO> agreementSubSections) {
        this.agreementSubSections = agreementSubSections;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<ClauseBasicDTO> getClauses() {
        return clauses;
    }

    public void setClauses(List<ClauseBasicDTO> clauses) {
        this.clauses = clauses;
    }

    public String getTitle() {
        return title.trim();
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getOrderedIndex() {
        return orderedIndex;
    }

    public void setOrderedIndex(Integer orderedIndex) {
        this.orderedIndex = orderedIndex;
    }

    public Long getCountryId() { return countryId; }

    public void setCountryId(Long countryId) { this.countryId = countryId; }

    public Long getOrganizationId() { return organizationId; }

    public void setOrganizationId(Long organizationId) { this.organizationId = organizationId; }

    public String getTitleHtml() {
        if (titleHtml == null) {
            titleHtml = "<p>"+title+"</p>";
        }
        return titleHtml.trim();
    }

    public void setTitleHtml(String titleHtml) {
        this.titleHtml = titleHtml;
    }

    public AgreementSectionDTO() {
    }
}
