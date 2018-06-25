package com.kairos.activity.unit_settings.activity_configuration;


import com.kairos.activity.response.dto.activity.TimeTypeDTO;
import com.kairos.response.dto.web.phase.PhaseDTO;
import com.kairos.response.dto.web.presence_type.PresenceTypeDTO;

import java.util.ArrayList;
import java.util.List;

public class ActivityConfigurationWrapper {
    List<PhaseDTO> phases = new ArrayList<>();
    List<TimeTypeDTO> topLevelTimeTypes = new ArrayList<>();
    List<PresenceTypeDTO> plannedTimeTypes = new ArrayList<>();

    public ActivityConfigurationWrapper(List<PhaseDTO> phases, List<TimeTypeDTO> topLevelTimeTypes, List<PresenceTypeDTO> plannedTimeTypes) {
        this.phases = phases;
        this.topLevelTimeTypes = topLevelTimeTypes;
        this.plannedTimeTypes = plannedTimeTypes;
    }

    public List<PhaseDTO> getPhases() {
        return phases;
    }

    public void setPhases(List<PhaseDTO> phases) {
        this.phases = phases;
    }

    public List<TimeTypeDTO> getTopLevelTimeTypes() {
        return topLevelTimeTypes;
    }

    public void setTopLevelTimeTypes(List<TimeTypeDTO> topLevelTimeTypes) {
        this.topLevelTimeTypes = topLevelTimeTypes;
    }

    public List<PresenceTypeDTO> getPlannedTimeTypes() {
        return plannedTimeTypes;
    }

    public void setPlannedTimeTypes(List<PresenceTypeDTO> plannedTimeTypes) {
        this.plannedTimeTypes = plannedTimeTypes;
    }
}
