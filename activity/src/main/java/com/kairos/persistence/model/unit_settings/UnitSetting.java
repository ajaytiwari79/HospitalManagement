package com.kairos.persistence.model.unit_settings;


import com.kairos.dto.activity.unit_settings.OpenShiftPhaseSetting;
import com.kairos.enums.time_slot.TimeSlotMode;
import com.kairos.persistence.model.common.MongoBaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.time.ZoneId;

import static com.kairos.enums.time_slot.TimeSlotMode.STANDARD;

@Getter
@Setter
public class UnitSetting extends MongoBaseEntity {
    private OpenShiftPhaseSetting openShiftPhaseSetting;
    private FlexibleTimeSettings flexibleTimeSettings;
    private Long unitId;
    private TimeSlotMode timeSlotMode = STANDARD;
    private ZoneId timeZone;
    private int dayShiftTimeDeduction = 4;
    private int nightShiftTimeDeduction = 7; //in percentage
    private int endTimeDeduction = 5; //in percentage

    public UnitSetting(OpenShiftPhaseSetting openShiftPhaseSetting, Long unitId) {
        this.openShiftPhaseSetting = openShiftPhaseSetting;
        this.unitId = unitId;
    }
}
