package com.kairos.persistence.model.query_wrapper;


import com.kairos.persistence.model.user.expertise.Expertise;
import com.kairos.response.dto.web.wta.WTAResponseDTO;
import org.springframework.data.neo4j.annotation.QueryResult;

@QueryResult
public class WTAAndExpertiseQueryResult {
    private WTAResponseDTO workingTimeAgreement;
    private Expertise expertise;

    public WTAResponseDTO getWorkingTimeAgreement() {
        return workingTimeAgreement;
    }

    public void setWorkingTimeAgreement(WTAResponseDTO workingTimeAgreement) {
        this.workingTimeAgreement = workingTimeAgreement;
    }

    public Expertise getExpertise() {
        return expertise;
    }

    public void setExpertise(Expertise expertise) {
        this.expertise = expertise;
    }
}
