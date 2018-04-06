package com.kairos.client.dto;

import com.kairos.response.dto.web.cta.PhaseDTO;
import com.kairos.response.dto.web.cta.TimeTypeDTO;

public class PhaseAndActivityTypeWrapper {
    private PhaseDTO phaseDTO;
    private TimeTypeDTO timeTypeDTO;

    public PhaseDTO getPhaseDTO() {
        return phaseDTO;
    }

    public void setPhaseDTO(PhaseDTO phaseDTO) {
        this.phaseDTO = phaseDTO;
    }

    public TimeTypeDTO getTimeTypeDTO() {
        return timeTypeDTO;
    }

    public void setTimeTypeDTO(TimeTypeDTO timeTypeDTO) {
        this.timeTypeDTO = timeTypeDTO;
    }
}
