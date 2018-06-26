package com.kairos.client.dto;


import com.kairos.response.dto.web.cta.PhaseResponseDTO;

import com.kairos.response.dto.web.cta.TimeTypeResponseDTO;

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
