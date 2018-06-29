package com.kairos.activity.night_worker;

import com.kairos.activity.shift.ShiftQueryResult;

import java.util.List;
import java.util.Map;

public class ShiftAndExpertiseNightWorkerSettingDTO {

    private List<ShiftQueryResult> shifts;
    private Map<Long, ExpertiseNightWorkerSettingDTO> nightWorkerSettings;

    public ShiftAndExpertiseNightWorkerSettingDTO(){
        // default constructor
    }

    public ShiftAndExpertiseNightWorkerSettingDTO(List<ShiftQueryResult> shifts, Map<Long, ExpertiseNightWorkerSettingDTO> nightWorkerSettings){
        this.shifts = shifts;
        this.nightWorkerSettings = nightWorkerSettings;
    }

    public List<ShiftQueryResult> getShifts() {
        return shifts;
    }

    public void setShifts(List<ShiftQueryResult> shifts) {
        this.shifts = shifts;
    }

    public Map<Long, ExpertiseNightWorkerSettingDTO> getNightWorkerSettings() {
        return nightWorkerSettings;
    }

    public void setNightWorkerSettings(Map<Long, ExpertiseNightWorkerSettingDTO> nightWorkerSettings) {
        this.nightWorkerSettings = nightWorkerSettings;
    }
}
