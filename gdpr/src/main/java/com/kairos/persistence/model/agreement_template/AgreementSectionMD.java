package com.kairos.persistence.model.agreement_template;


import com.kairos.persistence.model.clause.ClauseCkEditorVO;
import com.kairos.persistence.model.common.BaseEntity;
import com.kairos.persistence.model.common.MongoBaseEntity;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
public class AgreementSectionMD extends BaseEntity {


    @NotBlank(message = "Section Title cannot be empty")
    private String title;
    private String titleHtml;
    private boolean subSection;
    // clause id are saved in order way
   /* private List<Integer> clauseIdOrderedIndex=new ArrayList<>();

    @OrderColumn
    private Set<ClauseCkEditorVO> clauseCkEditorVOS=new HashSet<>();*/

    @OneToMany(cascade = CascadeType.ALL,mappedBy = "agreementSection")
    private List<AgreementSectionMD> subSections=new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "agreementSection_id")
    private AgreementSectionMD agreementSection;

    private Integer orderedIndex;
    private Long countryId;


    public boolean isSubSection() { return subSection; }

    public void setSubSection(boolean subSection) { this.subSection = subSection; }

    public Long getCountryId() { return countryId; }

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    public List<AgreementSectionMD> getSubSections() {
        return subSections;
    }

    public void setSubSections(List<AgreementSectionMD> subSections) {
        this.subSections = subSections;
    }

    public AgreementSectionMD getAgreementSection() {
        return agreementSection;
    }

    public void setAgreementSection(AgreementSectionMD agreementSection) {
        this.agreementSection = agreementSection;
    }

    public void setCountryId(Long countryId) { this.countryId = countryId; }

  /*  public List<Integer> getClauseIdOrderedIndex() { return clauseIdOrderedIndex; }

    public void setClauseIdOrderedIndex(List<Integer> clauseIdOrderedIndex) { this.clauseIdOrderedIndex = clauseIdOrderedIndex; }*/

    public Integer getOrderedIndex() { return orderedIndex; }

    public void setOrderedIndex(Integer orderedIndex) { this.orderedIndex = orderedIndex; }

    public String getTitleHtml() { return titleHtml; }

    public void setTitleHtml(String titleHtml) { this.titleHtml = titleHtml; }

    /*public Set<ClauseCkEditorVO> getClauseCkEditorVOS() { return clauseCkEditorVOS; }

    public void setClauseCkEditorVOS(Set<ClauseCkEditorVO> clauseCkEditorVOS) { this.clauseCkEditorVOS = clauseCkEditorVOS; }*/

    public AgreementSectionMD(){ }


    public AgreementSectionMD(@NotBlank(message = "Section Title cannot be empty") String title, @NotNull(message = "Clause order is Not defined") Integer orderedIndex, boolean subSection, String titleHtml)
    {
        this.title=title;
        this.orderedIndex=orderedIndex;
        this.subSection=subSection;
        this.titleHtml=titleHtml;
    }
}
