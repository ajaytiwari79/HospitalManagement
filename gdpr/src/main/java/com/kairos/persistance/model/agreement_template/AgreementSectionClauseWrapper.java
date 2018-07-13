package com.kairos.persistance.model.agreement_template;

import com.kairos.dto.master_data.AgreementSectionDTO;
import com.kairos.dto.master_data.ClauseBasicDTO;

import java.util.List;

public class AgreementSectionClauseWrapper {


    private List<AgreementSectionDTO> agreementSubSections;

    private List<ClauseBasicDTO> changedClausesList;

    private List<ClauseBasicDTO> newClauses;

    public List<AgreementSectionDTO> getAgreementSubSections() { return agreementSubSections; }

    public void setAgreementSubSections(List<AgreementSectionDTO> agreementSubSections) { this.agreementSubSections = agreementSubSections; }

    public List<ClauseBasicDTO> getChangedClausesList() {
        return changedClausesList;
    }

    public void setChangedClausesList(List<ClauseBasicDTO> changedClausesList) { this.changedClausesList = changedClausesList;}

    public List<ClauseBasicDTO> getNewClauses() {
        return newClauses;
    }

    public void setNewClauses(List<ClauseBasicDTO> newClauses) {
        this.newClauses = newClauses;
    }

    public AgreementSectionClauseWrapper(List<ClauseBasicDTO> changedClausesList, List<ClauseBasicDTO> newClauses) {
        this.changedClausesList = changedClausesList;
        this.newClauses = newClauses;
    }

    public AgreementSectionClauseWrapper(List<AgreementSectionDTO> agreementSubSections, List<ClauseBasicDTO> changedClausesList, List<ClauseBasicDTO> newClauses) {
        this.agreementSubSections = agreementSubSections;
        this.changedClausesList = changedClausesList;
        this.newClauses = newClauses;
    }

    public AgreementSectionClauseWrapper() {
    }
}
