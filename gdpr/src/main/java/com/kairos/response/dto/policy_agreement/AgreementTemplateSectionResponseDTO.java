package com.kairos.response.dto.policy_agreement;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.dto.gdpr.agreement_template.CoverPageVO;
import com.kairos.response.dto.clause.ClauseBasicResponseDTO;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AgreementTemplateSectionResponseDTO {


    private boolean coverPageAdded;
    private CoverPageVO coverPageData;

    private List<ClauseBasicResponseDTO> clauseListForTemplate = new ArrayList<>();
    private List<AgreementSectionResponseDTO> sections = new ArrayList<>();

    public List<ClauseBasicResponseDTO> getClauseListForTemplate() { return clauseListForTemplate; }

    public void setClauseListForTemplate(List<ClauseBasicResponseDTO> clauseListForTemplate) { this.clauseListForTemplate = clauseListForTemplate; }

    public List<AgreementSectionResponseDTO> getSections() { return sections; }

    public void setSections(List<AgreementSectionResponseDTO> sections) { this.sections = sections; }

    public boolean isCoverPageAdded() { return coverPageAdded; }

    public void setCoverPageAdded(boolean coverPageAdded) { this.coverPageAdded = coverPageAdded; }

    public CoverPageVO getCoverPageData() { return coverPageData; }

    public void setCoverPageData(CoverPageVO coverPageData) { this.coverPageData = coverPageData;}

}
