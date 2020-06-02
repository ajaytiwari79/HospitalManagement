package com.kairos.dto.activity.phase;


import com.kairos.dto.user.country.agreement.cta.cta_response.PhaseResponseDTO;
import com.kairos.dto.user.country.agreement.cta.cta_response.TimeTypeResponseDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PhaseAndActivityTypeWrapper {
    private PhaseResponseDTO phaseDTO;
    private TimeTypeResponseDTO timeTypeDTO;
}
