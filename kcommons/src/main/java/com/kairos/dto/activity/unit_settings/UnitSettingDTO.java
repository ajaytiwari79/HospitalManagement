package com.kairos.dto.activity.unit_settings;

import com.kairos.enums.time_slot.TimeSlotMode;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

import static com.kairos.enums.time_slot.TimeSlotMode.STANDARD;

@Getter
@Setter
public class UnitSettingDTO {
    private BigInteger id;
    private Long unitId;
    private OpenShiftPhaseSetting openShiftPhaseSetting;
    private FlexibleTimeSettingDTO flexibleTimeSettings;
    private TimeSlotMode timeSlotMode = STANDARD;
}
