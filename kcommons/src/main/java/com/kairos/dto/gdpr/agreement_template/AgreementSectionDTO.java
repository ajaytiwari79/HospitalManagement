package com.kairos.dto.gdpr.agreement_template;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.dto.gdpr.master_data.ClauseBasicDTO;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AgreementSectionDTO {


    private Long id;

    @NotBlank(message = "error.message.name.notNull.orEmpty")
    private String title;
    private Integer orderedIndex;
    private String titleHtml;
    @Valid
    private List<ClauseBasicDTO> clauses = new ArrayList<>();
    @Valid
    private List<AgreementSectionDTO> subSections = new ArrayList<>();

    public List<AgreementSectionDTO> getSubSections() {
        return subSections;
    }

    public void setSubSections(List<AgreementSectionDTO> subSections) {
        this.subSections = subSections;
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
