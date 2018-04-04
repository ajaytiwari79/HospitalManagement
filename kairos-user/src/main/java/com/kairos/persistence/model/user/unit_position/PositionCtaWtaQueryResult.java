package com.kairos.persistence.model.user.unit_position;

import com.kairos.persistence.model.user.agreement.cta.CostTimeAgreement;
import com.kairos.persistence.model.user.agreement.wta.WorkingTimeAgreement;
import com.kairos.persistence.model.user.expertise.ExpertiseQueryResult;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.List;

/**
 * Created by prabjot on 3/1/18.
 */
@QueryResult
public class PositionCtaWtaQueryResult {

    private List<CostTimeAgreement> cta;
    private List<WorkingTimeAgreement> wta;
    private ExpertiseQueryResult expertise;

    public PositionCtaWtaQueryResult() {
    }

    public List<CostTimeAgreement> getCta() {
        return cta;
    }

    public void setCta(List<CostTimeAgreement> cta) {
        this.cta = cta;
    }

    public List<WorkingTimeAgreement> getWta() {
        return wta;
    }

    public void setWta(List<WorkingTimeAgreement> wta) {
        this.wta = wta;
    }

    public ExpertiseQueryResult getExpertise() {
        return expertise;
    }

    public void setExpertise(ExpertiseQueryResult expertise) {
        this.expertise = expertise;
    }
}
