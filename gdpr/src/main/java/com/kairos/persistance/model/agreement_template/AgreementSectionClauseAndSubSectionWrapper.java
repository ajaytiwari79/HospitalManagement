package com.kairos.persistance.model.agreement_template;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.dto.gdpr.master_data.AgreementSectionDTO;
import com.kairos.dto.gdpr.master_data.ClauseBasicDTO;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AgreementSectionClauseAndSubSectionWrapper {

    private List<ClauseBasicDTO> clauses;

    private List<AgreementSection> subSections;

    public List<ClauseBasicDTO> getClauses() { return clauses; }

    public void setClauses(List<ClauseBasicDTO> clauses) { this.clauses = clauses; }

    public List<AgreementSection> getSubSections() {
        return subSections;
    }

    public void setSubSections(List<AgreementSection> subSections) {
        this.subSections = subSections;
    }
}
