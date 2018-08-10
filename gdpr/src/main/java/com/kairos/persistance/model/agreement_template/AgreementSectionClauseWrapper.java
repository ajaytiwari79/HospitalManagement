package com.kairos.persistance.model.agreement_template;

import com.kairos.dto.master_data.AgreementSectionDTO;
import com.kairos.dto.master_data.ClauseBasicDTO;

import java.util.ArrayList;
import java.util.List;

public class AgreementSectionClauseWrapper {


    private List<AgreementSection> agreementSubSections=new ArrayList<>();

    private List<ClauseBasicDTO> changedClausesList=new ArrayList<>();

    private List<ClauseBasicDTO> newClauses=new ArrayList<>();

    public List<AgreementSection> getAgreementSubSections() { return agreementSubSections; }

    public void setAgreementSubSections(List<AgreementSection> agreementSubSections) { this.agreementSubSections = agreementSubSections; }

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

    public AgreementSectionClauseWrapper(List<AgreementSection> agreementSubSections, List<ClauseBasicDTO> changedClausesList, List<ClauseBasicDTO> newClauses) {
        this.agreementSubSections = agreementSubSections;
        this.changedClausesList = changedClausesList;
        this.newClauses = newClauses;
    }

    public AgreementSectionClauseWrapper() {
    }
}
