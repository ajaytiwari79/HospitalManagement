package com.kairos.persistence.model.agreement_template;


import com.kairos.persistence.model.clause.ClauseCkEditorVO;
import com.kairos.persistence.model.common.BaseEntity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
public class AgreementSection extends BaseEntity {


    @NotBlank(message = "Section Title cannot be empty")
    @Column(columnDefinition = "text")
    private String title;

    @Column(columnDefinition = "text")
    private String titleHtml;

    private boolean isAgreementSubSection;

    @OrderColumn
    @ElementCollection
    private List<ClauseCkEditorVO> clauses=new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL,mappedBy = "agreementSection")
    private List<AgreementSection> agreementSubSections =new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "agreementSection_id")
    private AgreementSection agreementSection;

    private Integer orderedIndex;
    private Long countryId;
    private Long organizationId;



    public Long getOrganizationId() { return organizationId; }

    public void setOrganizationId(Long organizationId) { this.organizationId = organizationId; }

    public boolean isAgreementSubSection() { return isAgreementSubSection; }

    public void setAgreementSubSection(boolean agreementSubSection) { this.isAgreementSubSection = agreementSubSection; }

    public Long getCountryId() { return countryId; }

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    public List<AgreementSection> getAgreementSubSections() {
        return agreementSubSections;
    }

    public void setAgreementSubSections(List<AgreementSection> agreementSubSections) { this.agreementSubSections = agreementSubSections; }

    public AgreementSection getAgreementSection() {
        return agreementSection;
    }

    public void setAgreementSection(AgreementSection agreementSection) {
        this.agreementSection = agreementSection;
    }

    public void setCountryId(Long countryId) { this.countryId = countryId; }

    public Integer getOrderedIndex() { return orderedIndex; }

    public void setOrderedIndex(Integer orderedIndex) { this.orderedIndex = orderedIndex; }

    public String getTitleHtml() { return titleHtml; }

    public void setTitleHtml(String titleHtml) { this.titleHtml = titleHtml; }

    public List<ClauseCkEditorVO> getClauses() {
        return clauses;
    }

    public void setClauses(List<ClauseCkEditorVO> clauses) {
        this.clauses = clauses;
    }

    public AgreementSection(){ }


    public AgreementSection(@NotBlank(message = "Section Title cannot be empty") String title, @NotNull(message = "Clause order is Not defined") Integer orderedIndex, boolean isAgreementSubSection, String titleHtml)
    {
        this.title=title;
        this.orderedIndex=orderedIndex;
        this.isAgreementSubSection = isAgreementSubSection;
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


    public void linkSubSectionsWithParentSectionAndCountryOrUnitId(boolean isUnitId, Long referenceId){
        this.agreementSubSections.forEach(subSection ->{
            if(isUnitId){
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
