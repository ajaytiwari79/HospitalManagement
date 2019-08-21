package com.kairos.dto.activity.unit_settings.activity_configuration;


import com.kairos.dto.activity.phase.PhaseDTO;
import com.kairos.dto.activity.presence_type.PresenceTypeDTO;
import com.kairos.dto.user.country.agreement.cta.cta_response.EmploymentTypeDTO;
import com.kairos.dto.user.country.agreement.cta.cta_response.PhaseResponseDTO;
import com.kairos.dto.user.country.agreement.cta.cta_response.TimeTypeResponseDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
public class ActivityConfigurationWrapper {
    List<PhaseDTO> phases = new ArrayList<>();
    List<TimeTypeResponseDTO> topLevelTimeTypes = new ArrayList<>();
    List<PresenceTypeDTO> plannedTimeTypes = new ArrayList<>();
    List<EmploymentTypeDTO> employmentTypes;

    public ActivityConfigurationWrapper(List<PhaseDTO> phases, List<TimeTypeResponseDTO> topLevelTimeTypes, List<PresenceTypeDTO> plannedTimeTypes,List<EmploymentTypeDTO> employmentTypes) {
        this.phases = phases;
        this.topLevelTimeTypes = topLevelTimeTypes;
        this.plannedTimeTypes = plannedTimeTypes;
        this.employmentTypes = employmentTypes;
    }


}
