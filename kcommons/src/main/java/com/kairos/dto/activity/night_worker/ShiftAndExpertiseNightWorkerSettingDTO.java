package com.kairos.dto.activity.night_worker;

import com.kairos.dto.activity.shift.ShiftDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShiftAndExpertiseNightWorkerSettingDTO {
    private List<ShiftDTO> shifts;
    private Map<Long, ExpertiseNightWorkerSettingDTO> nightWorkerSettings;
}
