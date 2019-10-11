package com.kairos.persistence.model.user.employment.query_result;

import com.kairos.dto.activity.cta.CTAResponseDTO;
import com.kairos.dto.activity.wta.basic_details.WTAResponseDTO;
import com.kairos.dto.user.country.experties.ExpertiseDTO;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.user.expertise.response.SeniorityLevelQueryResult;
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
    private ExpertiseDTO expertise;
    private SeniorityLevelQueryResult applicableSeniorityLevel;
    private Organization union;


    public CtaWtaQueryResult(List<CTAResponseDTO> cta, List<WTAResponseDTO> wta, ExpertiseDTO expertise, SeniorityLevelQueryResult applicableSeniorityLevel, Organization union) {
        this.cta = cta;
        this.wta = wta;
        this.expertise = expertise;
        this.applicableSeniorityLevel = applicableSeniorityLevel;
        this.union = union;
    }
}
