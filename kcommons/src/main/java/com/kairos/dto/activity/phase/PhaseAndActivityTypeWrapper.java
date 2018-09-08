package com.kairos.dto.activity.phase;


import com.kairos.dto.user.country.agreement.cta.cta_response.PhaseResponseDTO;

import com.kairos.dto.user.country.agreement.cta.cta_response.TimeTypeResponseDTO;

public class PhaseAndActivityTypeWrapper {
    private PhaseResponseDTO phaseDTO;
    private TimeTypeResponseDTO timeTypeDTO;

    public PhaseAndActivityTypeWrapper() {
        // dv
    }

    public PhaseResponseDTO getPhaseDTO() {
        return phaseDTO;
    }

    public void setPhaseDTO(PhaseResponseDTO phaseDTO) {
        this.phaseDTO = phaseDTO;
    }

    public TimeTypeResponseDTO getTimeTypeDTO() {
        return timeTypeDTO;
    }

    public void setTimeTypeDTO(TimeTypeResponseDTO timeTypeDTO) {
        this.timeTypeDTO = timeTypeDTO;
    }
}
