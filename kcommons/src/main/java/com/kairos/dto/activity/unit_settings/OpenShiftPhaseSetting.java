package com.kairos.dto.activity.unit_settings;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OpenShiftPhaseSetting {


    private Integer minOpenShiftHours;
    private List<OpenShiftPhase> openShiftPhases;
}
