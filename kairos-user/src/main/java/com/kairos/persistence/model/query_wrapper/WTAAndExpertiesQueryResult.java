package com.kairos.persistence.model.query_wrapper;


import com.kairos.persistence.model.user.agreement.wta.WorkingTimeAgreement;
import com.kairos.persistence.model.user.expertise.Expertise;
import org.springframework.data.neo4j.annotation.QueryResult;

@QueryResult
public class WTAAndExpertiesQueryResult {
    private WorkingTimeAgreement workingTimeAgreement;
    private Expertise expertise;

    public WorkingTimeAgreement getWorkingTimeAgreement() {
        return workingTimeAgreement;
    }

    public void setWorkingTimeAgreement(WorkingTimeAgreement workingTimeAgreement) {
        this.workingTimeAgreement = workingTimeAgreement;
    }

    public Expertise getExpertise() {
        return expertise;
    }

    public void setExpertise(Expertise expertise) {
        this.expertise = expertise;
    }
}
