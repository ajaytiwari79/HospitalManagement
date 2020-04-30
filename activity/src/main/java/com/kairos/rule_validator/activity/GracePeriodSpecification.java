package com.kairos.rule_validator.activity;

import com.kairos.dto.activity.shift.ShiftWithActivityDTO;
import com.kairos.enums.phase.PhaseDefaultName;
import com.kairos.persistence.model.phase.Phase;
import com.kairos.persistence.model.unit_settings.TimeAttendanceGracePeriod;
import com.kairos.rule_validator.AbstractSpecification;

import java.util.List;

import static com.kairos.constants.ActivityMessagesConstants.MESSAGE_SHIFT_CANNOT_UPDATE;

public class GracePeriodSpecification extends AbstractSpecification<ShiftWithActivityDTO> {

    private TimeAttendanceGracePeriod timeAttendanceGracePeriod;

    public GracePeriodSpecification(TimeAttendanceGracePeriod timeAttendanceGracePeriod) {
        this.timeAttendanceGracePeriod = timeAttendanceGracePeriod;
    }

    @Override
    public boolean isSatisfied(ShiftWithActivityDTO shiftWithActivityDTO) {
        return false;
    }

    @Override
    public void validateRules(ShiftWithActivityDTO shift) {
        Phase actualPhases = phaseMongoRepository.findByUnitIdAndPhaseEnum(unitId, PhaseDefaultName.TIME_ATTENDANCE.toString());
        boolean validate = validateGracePeriod(shiftDTO, validatedByStaff, unitId, actualPhases);
        if (!validate) {
            exceptionService.invalidRequestException(MESSAGE_SHIFT_CANNOT_UPDATE);
        }
    }

    @Override
    public List<String> isSatisfiedString(ShiftWithActivityDTO shiftWithActivityDTO) {
        return null;
    }
}
