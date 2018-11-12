package com.kairos.response.dto.policy_agreement;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.persistence.model.clause.ClauseCkEditorVO;
import com.kairos.response.dto.clause.ClauseBasicResponseDTO;

import javax.validation.constraints.NotBlank;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;


@JsonIgnoreProperties(ignoreUnknown = true)
public class AgreementSectionResponseDTO {

    private BigInteger id;

    @NotBlank
    private String title;
    private Integer orderedIndex;
    private List<BigInteger> clauseIdOrderedIndex;
    private List<ClauseBasicResponseDTO> clauses;
    private List<ClauseCkEditorVO> clauseCkEditorVOS=new ArrayList<>();
    private List<AgreementSectionResponseDTO> subSections=new ArrayList<>();

    public Integer getOrderedIndex() {
        return orderedIndex;
    }

    public void setOrderedIndex(Integer orderedIndex) {
        this.orderedIndex = orderedIndex;
    }

    public List<BigInteger> getClauseIdOrderedIndex() { return clauseIdOrderedIndex; }

    public void setClauseIdOrderedIndex(List<BigInteger> clauseIdOrderedIndex) { this.clauseIdOrderedIndex = clauseIdOrderedIndex; }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public List<AgreementSectionResponseDTO> getSubSections() {
        return subSections;
    }

    public void setSubSections(List<AgreementSectionResponseDTO> subSections) {
        this.subSections = subSections;
    }

    public List<ClauseBasicResponseDTO> getClauses() {
        return clauses;
    }

    public void setClauses(List<ClauseBasicResponseDTO> clauses) {
        this.clauses = clauses;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<ClauseCkEditorVO> getClauseCkEditorVOS() { return clauseCkEditorVOS; }

    public void setClauseCkEditorVOS(List<ClauseCkEditorVO> clauseCkEditorVOS) { this.clauseCkEditorVOS = clauseCkEditorVOS;
    }
    public AgreementSectionResponseDTO() {

    }

}
