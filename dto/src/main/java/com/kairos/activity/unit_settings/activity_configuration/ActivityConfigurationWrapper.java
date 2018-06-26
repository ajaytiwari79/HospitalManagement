package com.kairos.activity.unit_settings.activity_configuration;


import com.kairos.response.dto.web.cta.PhaseResponseDTO;
import com.kairos.response.dto.web.cta.TimeTypeResponseDTO;
import com.kairos.response.dto.web.presence_type.PresenceTypeDTO;

import java.util.ArrayList;
import java.util.List;

public class ActivityConfigurationWrapper {
    List<PhaseResponseDTO> phases = new ArrayList<>();
    List<TimeTypeResponseDTO> topLevelTimeTypes = new ArrayList<>();
    List<PresenceTypeDTO> plannedTimeTypes = new ArrayList<>();

    public ActivityConfigurationWrapper(List<PhaseResponseDTO> phases, List<TimeTypeResponseDTO> topLevelTimeTypes, List<PresenceTypeDTO> plannedTimeTypes) {
        this.phases = phases;
        this.topLevelTimeTypes = topLevelTimeTypes;
        this.plannedTimeTypes = plannedTimeTypes;
    }

    public List<PhaseResponseDTO> getPhases() {
        return phases;
    }

    public void setPhases(List<PhaseResponseDTO> phases) {
        this.phases = phases;
    }

    public List<TimeTypeResponseDTO> getTopLevelTimeTypes() {
        return topLevelTimeTypes;
    }

    public void setTopLevelTimeTypes(List<TimeTypeResponseDTO> topLevelTimeTypes) {
        this.topLevelTimeTypes = topLevelTimeTypes;
    }

    public List<PresenceTypeDTO> getPlannedTimeTypes() {
        return plannedTimeTypes;
    }

    public void setPlannedTimeTypes(List<PresenceTypeDTO> plannedTimeTypes) {
        this.plannedTimeTypes = plannedTimeTypes;
    }
}
