package com.kairos.dto.activity.open_shift;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class OpenShiftWrapper {
    private int timeBank;
    private int plannedTime;
    private int restingTime;
    private List<OpenShiftResponseDTO> similarOpenShifts;
    private OpenShiftResponseDTO openShift;

    public OpenShiftWrapper(int timeBank, int plannedTime, int restingTime, List<OpenShiftResponseDTO> similarOpenShifts) {
        this.timeBank = timeBank;
        this.plannedTime = plannedTime;
        this.restingTime = restingTime;
        this.similarOpenShifts = similarOpenShifts;

    }

    public OpenShiftWrapper(int timeBank, int plannedTime, int restingTime, List<OpenShiftResponseDTO> similarOpenShifts, OpenShiftResponseDTO openShift) {
        this.timeBank = timeBank;
        this.plannedTime = plannedTime;
        this.restingTime = restingTime;
        this.similarOpenShifts = similarOpenShifts;
        this.openShift = openShift;
    }
}
