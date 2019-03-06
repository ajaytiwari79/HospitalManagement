package com.kairos.persistence.model.agreement_template;


import com.kairos.persistence.model.clause.ClauseCkEditorVO;
import com.kairos.persistence.model.common.BaseEntity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
public class AgreementSection extends BaseEntity {


    @NotBlank(message = "Section Title cannot be empty")
    @Column(columnDefinition = "text")
    protected String title;

    @Column(columnDefinition = "text")
    protected String titleHtml;

    @OrderColumn
    @ElementCollection
    protected List<ClauseCkEditorVO> clauses=new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL,mappedBy = "agreementSection")
    private List<AgreementSubSection> agreementSubSections =new ArrayList<>();


    protected Integer orderedIndex;
    protected Long countryId;
    protected Long organizationId;

    public List<AgreementSubSection> getAgreementSubSections() {
        return agreementSubSections.stream().filter(subSection -> subSection.isDeleted() == false).collect(Collectors.toList());
    }

    public void setAgreementSubSections(List<AgreementSubSection> agreementSubSections) {
        this.agreementSubSections = agreementSubSections;
    }

    public Long getOrganizationId() { return organizationId; }

    public void setOrganizationId(Long organizationId) { this.organizationId = organizationId; }

    public Long getCountryId() { return countryId; }

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    public void setCountryId(Long countryId) { this.countryId = countryId; }

    public Integer getOrderedIndex() { return orderedIndex; }

    public void setOrderedIndex(Integer orderedIndex) { this.orderedIndex = orderedIndex; }

    public String getTitleHtml() { return titleHtml; }

    public void setTitleHtml(String titleHtml) { this.titleHtml = titleHtml; }

    public List<ClauseCkEditorVO> getClauses() {
        return clauses.stream().filter(clause -> clause.isDeleted() == false).collect(Collectors.toList());
    }

    public void setClauses(List<ClauseCkEditorVO> clauses) {
        this.clauses = clauses;
    }

    public AgreementSection(){ }


    public AgreementSection(@NotBlank(message = "Section Title cannot be empty") String title, @NotNull(message = "Clause order is Not defined") Integer orderedIndex, String titleHtml)
    {
        this.title=title;
        this.orderedIndex=orderedIndex;
        this.titleHtml=titleHtml;
    }

    @Override
    public void delete() {
        super.delete();
        this.getClauses().forEach( clause -> clause.setDeleted(false));
        this.agreementSubSections.forEach(subSection -> {
            subSection.delete();
            subSection.getClauses().forEach( subSectionClause -> subSectionClause.setDeleted(false));
        });
    }


    public void linkSubSectionsWithParentSectionAndCountryOrUnitId(boolean isOrganization, Long referenceId){
        this.agreementSubSections.forEach(subSection ->{
            if(isOrganization){
                this.setOrganizationId(referenceId);
                subSection.setOrganizationId(referenceId);
            }else{
                this.setCountryId(referenceId);
                subSection.setCountryId(referenceId);
            }
            subSection.setAgreementSection(this);
        });
    }
}
