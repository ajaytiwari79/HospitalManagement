package com.kairos.persistence.model.user.employment.query_result;

import com.kairos.dto.activity.cta.CTAResponseDTO;
import com.kairos.dto.activity.wta.basic_details.WTAResponseDTO;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.user.expertise.Expertise;
import com.kairos.persistence.model.user.expertise.Response.SeniorityLevelQueryResult;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Created by prabjot on 3/1/18.
 */

@Getter
@Setter
@NoArgsConstructor
public class CtaWtaQueryResult {

    private List<CTAResponseDTO> cta;
    private List<WTAResponseDTO> wta;
    private Expertise expertise;
    private SeniorityLevelQueryResult applicableSeniorityLevel;
    private Organization union;


    public CtaWtaQueryResult(List<CTAResponseDTO> cta, List<WTAResponseDTO> wta, Expertise expertise, SeniorityLevelQueryResult applicableSeniorityLevel, Organization union) {
        this.cta = cta;
        this.wta = wta;
        this.expertise = expertise;
        this.applicableSeniorityLevel = applicableSeniorityLevel;
        this.union = union;
    }
}
