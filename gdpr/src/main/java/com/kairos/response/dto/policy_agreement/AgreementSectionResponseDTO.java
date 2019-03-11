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
    protected Long countryId;
    protected Long organizationId;
    //private List<BigInteger> clauseIdOrderedIndex;
    //private List<ClauseBasicResponseDTO> clauses;
    private List<AgreementSectionClause> clauses;
    private List<AgreementSectionResponseDTO> agreementSubSections = new ArrayList<>();

    public Integer getOrderedIndex() {
        return orderedIndex;
    }

    public void setOrderedIndex(Integer orderedIndex) {
        this.orderedIndex = orderedIndex;
    }

    /*public List<BigInteger> getClauseIdOrderedIndex() { return clauseIdOrderedIndex; }

    public void setClauseIdOrderedIndex(List<BigInteger> clauseIdOrderedIndex) { this.clauseIdOrderedIndex = clauseIdOrderedIndex; }

    */
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<AgreementSectionResponseDTO> getAgreementSubSections() {
        return agreementSubSections;
    }

    public void setAgreementSubSections(List<AgreementSectionResponseDTO> agreementSubSections) {
        this.agreementSubSections = agreementSubSections;
    }

    /*public List<ClauseBasicResponseDTO> getClauses() {
        return clauses;
    }

    public void setClauses(List<ClauseBasicResponseDTO> clauses) {
        this.clauses = clauses;
    }*/

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

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

    public AgreementSectionResponseDTO(Long id, @NotBlank String title, String titleHtml, Integer orderedIndex, Long countryId, Long organizationId) {
        this.id = id;
        this.title = title;
        this.titleHtml = titleHtml;
        this.orderedIndex = orderedIndex;
        this.countryId = countryId;
        this.organizationId = organizationId;
    }
}
