package com.kairos.dto.activity.shift;
/*
 *Created By Pavan on 29/8/18
 *
 */

import com.kairos.enums.shift.ShiftStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class ActivityAndShiftStatusWrapper {
    private ShiftStatus status;
    private List<ActivityShiftStatusSettingsDTO> activityAndShiftStatusSettings;
}
