package com.kairos.persistance.model.agreement_template;

import com.kairos.dto.master_data.ClauseBasicDTO;

import java.util.List;

public class AgreementSectionClauseWrapper {


    private List<ClauseBasicDTO> changedClausesList;

    private List<ClauseBasicDTO> newClauses;

    public List<ClauseBasicDTO> getChangedClausesList() {
        return changedClausesList;
    }

    public void setChangedClausesList(List<ClauseBasicDTO> changedClausesList) {
        this.changedClausesList = changedClausesList;
    }

    public List<ClauseBasicDTO> getNewClauses() {
        return newClauses;
    }

    public void setNewClauses(List<ClauseBasicDTO> newClauses) {
        this.newClauses = newClauses;
    }

    public AgreementSectionClauseWrapper() {
    }
}
