package com.kairos.persistence.model.agreement_template;


import com.kairos.persistence.model.clause.ClauseCkEditorVO;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class AgreementSectionDeprecated {


    @NotBlank(message = "Section Title cannot be empty")
    private String title;
    private String titleHtml;
    private boolean subSection;
    // clause id are saved in order way
    private List<BigInteger> clauseIdOrderedIndex=new ArrayList<>();
    private Set<ClauseCkEditorVO> clauseCkEditorVOS=new HashSet<>();
    private List<BigInteger> subSections=new ArrayList<>();
    private Integer orderedIndex;
    private Long countryId;


    public boolean isSubSection() { return subSection; }

    public void setSubSection(boolean subSection) { this.subSection = subSection; }

    public Long getCountryId() { return countryId; }

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    public List<BigInteger> getSubSections() { return subSections; }

    public void setSubSections(List<BigInteger> subSections) { this.subSections = subSections; }

    public void setCountryId(Long countryId) { this.countryId = countryId; }

    public List<BigInteger> getClauseIdOrderedIndex() { return clauseIdOrderedIndex; }

    public void setClauseIdOrderedIndex(List<BigInteger> clauseIdOrderedIndex) { this.clauseIdOrderedIndex = clauseIdOrderedIndex; }

    public Integer getOrderedIndex() { return orderedIndex; }

    public void setOrderedIndex(Integer orderedIndex) { this.orderedIndex = orderedIndex; }

    public String getTitleHtml() { return titleHtml; }

    public void setTitleHtml(String titleHtml) { this.titleHtml = titleHtml; }

    public Set<ClauseCkEditorVO> getClauseCkEditorVOS() { return clauseCkEditorVOS; }

    public void setClauseCkEditorVOS(Set<ClauseCkEditorVO> clauseCkEditorVOS) { this.clauseCkEditorVOS = clauseCkEditorVOS; }

    public AgreementSectionDeprecated(){ }


    public AgreementSectionDeprecated(@NotBlank(message = "Section Title cannot be empty") String title, @NotNull(message = "Clause order is Not defined") Integer orderedIndex, boolean subSection, String titleHtml)
    {
        this.title=title;
        this.orderedIndex=orderedIndex;
        this.subSection=subSection;
        this.titleHtml=titleHtml;
    }
}
