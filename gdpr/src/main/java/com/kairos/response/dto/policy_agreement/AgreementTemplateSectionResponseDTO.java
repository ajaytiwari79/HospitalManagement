package com.kairos.response.dto.policy_agreement;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.response.dto.clause.ClauseBasicResponseDTO;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AgreementTemplateSectionResponseDTO {


    private String coverPageContent;
    private String coverPageTitle;
    private String coverPageLogoUrl;

    private List<ClauseBasicResponseDTO> clauseListForTemplate = new ArrayList<>();
    private List<AgreementSectionResponseDTO> sections = new ArrayList<>();

    public List<ClauseBasicResponseDTO> getClauseListForTemplate() { return clauseListForTemplate; }

    public void setClauseListForTemplate(List<ClauseBasicResponseDTO> clauseListForTemplate) { this.clauseListForTemplate = clauseListForTemplate; }

    public List<AgreementSectionResponseDTO> getSections() { return sections; }

    public void setSections(List<AgreementSectionResponseDTO> sections) { this.sections = sections; }

    public String getCoverPageContent() { return coverPageContent; }

    public void setCoverPageContent(String coverPageContent) { this.coverPageContent = coverPageContent; }

    public String getCoverPageTitle() { return coverPageTitle; }

    public void setCoverPageTitle(String coverPageTitle) { this.coverPageTitle = coverPageTitle; }

    public String getCoverPageLogoUrl() { return coverPageLogoUrl; }

    public void setCoverPageLogoUrl(String coverPageLogoUrl) { this.coverPageLogoUrl = coverPageLogoUrl; }
}
