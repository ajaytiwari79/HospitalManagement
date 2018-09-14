package com.kairos.dto.activity.night_worker;

import com.kairos.dto.activity.shift.ShiftDTO;
import com.kairos.dto.activity.shift.ShiftQueryResult;

import java.util.List;
import java.util.Map;

public class ShiftAndExpertiseNightWorkerSettingDTO {

    private List<ShiftDTO> shifts;
    private Map<Long, ExpertiseNightWorkerSettingDTO> nightWorkerSettings;

    public ShiftAndExpertiseNightWorkerSettingDTO(){
        // default constructor
    }

    public ShiftAndExpertiseNightWorkerSettingDTO(List<ShiftDTO> shifts, Map<Long, ExpertiseNightWorkerSettingDTO> nightWorkerSettings){
        this.shifts = shifts;
        this.nightWorkerSettings = nightWorkerSettings;
    }

    public List<ShiftDTO> getShifts() {
        return shifts;
    }

    public void setShifts(List<ShiftDTO> shifts) {
        this.shifts = shifts;
    }

    public Map<Long, ExpertiseNightWorkerSettingDTO> getNightWorkerSettings() {
        return nightWorkerSettings;
    }

    public void setNightWorkerSettings(Map<Long, ExpertiseNightWorkerSettingDTO> nightWorkerSettings) {
        this.nightWorkerSettings = nightWorkerSettings;
    }
}
