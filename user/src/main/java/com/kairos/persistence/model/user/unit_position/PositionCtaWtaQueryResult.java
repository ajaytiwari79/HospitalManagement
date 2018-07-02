package com.kairos.persistence.model.user.unit_position;

import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.agreement.cta.CostTimeAgreement;
import com.kairos.persistence.model.user.expertise.Expertise;
import com.kairos.persistence.model.user.expertise.Response.SeniorityLevelQueryResult;
import com.kairos.activity.wta.basic_details.WTAResponseDTO;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.List;

/**
 * Created by prabjot on 3/1/18.
 */
@QueryResult
public class PositionCtaWtaQueryResult {

    private List<CostTimeAgreement> cta;
    private List<WTAResponseDTO> wta;
    private Expertise expertise;
    private SeniorityLevelQueryResult applicableSeniorityLevel;
    private Organization union;

    public PositionCtaWtaQueryResult() {
        //Default Constructor
    }

    public List<CostTimeAgreement> getCta() {
        return cta;
    }

    public void setCta(List<CostTimeAgreement> cta) {
        this.cta = cta;
    }

    public List<WTAResponseDTO> getWta() {
        return wta;
    }

    public void setWta(List<WTAResponseDTO> wta) {
        this.wta = wta;
    }

    public Expertise getExpertise() {
        return expertise;
    }

    public void setExpertise(Expertise expertise) {
        this.expertise = expertise;
    }

    public SeniorityLevelQueryResult getApplicableSeniorityLevel() {
        return applicableSeniorityLevel;
    }

    public void setApplicableSeniorityLevel(SeniorityLevelQueryResult applicableSeniorityLevel) {
        this.applicableSeniorityLevel = applicableSeniorityLevel;
    }

    public Organization getUnion() {
        return union;
    }

    public void setUnion(Organization union) {
        this.union = union;
    }
}
