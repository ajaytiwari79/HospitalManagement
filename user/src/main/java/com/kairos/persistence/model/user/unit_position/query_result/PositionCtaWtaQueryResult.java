package com.kairos.persistence.model.user.unit_position.query_result;

import com.kairos.dto.activity.cta.CTAResponseDTO;
import com.kairos.dto.activity.wta.basic_details.WTAResponseDTO;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.user.expertise.Expertise;
import com.kairos.persistence.model.user.expertise.Response.SeniorityLevelQueryResult;

import java.util.List;

/**
 * Created by prabjot on 3/1/18.
 */

public class PositionCtaWtaQueryResult {

    private List<CTAResponseDTO> cta;
    private List<WTAResponseDTO> wta;
    private Expertise expertise;
    private SeniorityLevelQueryResult applicableSeniorityLevel;
    private Organization union;


    public PositionCtaWtaQueryResult(List<CTAResponseDTO> cta, List<WTAResponseDTO> wta, Expertise expertise, SeniorityLevelQueryResult applicableSeniorityLevel, Organization union) {
        this.cta = cta;
        this.wta = wta;
        this.expertise = expertise;
        this.applicableSeniorityLevel = applicableSeniorityLevel;
        this.union = union;
    }

    public PositionCtaWtaQueryResult() {
        //Default Constructor
    }

    public List<CTAResponseDTO> getCta() {
        return cta;
    }

    public void setCta(List<CTAResponseDTO> cta) {
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
