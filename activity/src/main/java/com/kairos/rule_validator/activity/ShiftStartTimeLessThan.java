package com.kairos.rule_validator.activity;

import com.kairos.activity.open_shift.DurationField;
import com.kairos.rule_validator.activity.AbstractActivitySpecification;
import com.kairos.util.DateUtils;
import com.kairos.util.ShiftValidatorService;
import com.kairos.wrapper.shift.ShiftWithActivityDTO;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class ShiftStartTimeLessThan extends AbstractActivitySpecification<ShiftWithActivityDTO> {

    private ZoneId zoneId;
    private Date shiftStartDateTime;
    private DurationField durationField;

    public ShiftStartTimeLessThan(ZoneId zoneId, Date shiftStartDateTime, DurationField durationField) {
        this.zoneId = zoneId;
        this.shiftStartDateTime = shiftStartDateTime;
        this.durationField = durationField;
    }

    @Override
    public boolean isSatisfied(ShiftWithActivityDTO shiftWithActivityDTO) {
        return false;
    }

    @Override
    public void validateRules(ShiftWithActivityDTO shift) {
        if ((int) Duration.between(DateUtils.getLocalDateTimeFromZoneId(zoneId), DateUtils.asLocalDateTime(shiftStartDateTime)).toHours() < durationField.getValue()) {
            ShiftValidatorService.throwException("message.shift.plannedTime.less");
        }
    }

    @Override
    public List<String> isSatisfiedString(ShiftWithActivityDTO shiftWithActivityDTO) {
        if ((int) Duration.between(DateUtils.getLocalDateTimeFromZoneId(zoneId), DateUtils.asLocalDateTime(shiftStartDateTime)).toHours() < durationField.getValue()) {
            return Collections.singletonList("message.shift.plannedTime.less");
        }
        return Collections.emptyList();
    }
}
