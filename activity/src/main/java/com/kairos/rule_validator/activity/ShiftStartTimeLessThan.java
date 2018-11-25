package com.kairos.rule_validator.activity;

import com.kairos.dto.activity.open_shift.DurationField;
import com.kairos.commons.utils.DateUtils;
import com.kairos.dto.activity.shift.ShiftActivityDTO;
import com.kairos.enums.DurationType;
import com.kairos.utils.ShiftValidatorService;
import com.kairos.wrapper.shift.ShiftWithActivityDTO;

import java.time.Duration;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class ShiftStartTimeLessThan extends AbstractActivitySpecification<ShiftWithActivityDTO> {


    public ShiftStartTimeLessThan() {
    }

    @Override
    public boolean isSatisfied(ShiftWithActivityDTO shiftWithActivityDTO) {
        return false;
    }

    @Override
    public void validateRules(ShiftWithActivityDTO shift) {
        shift.getActivities().forEach(shiftActivityDTO -> {
            Duration duration = Duration.between(DateUtils.getLocalDateTime(), DateUtils.asLocalDateTime(shiftActivityDTO.getStartDate()));
            int calculatedValue = DurationType.DAYS.equals(shiftActivityDTO.getActivity().getRulesActivityTab().getPqlSettings().getApprovalTimeInAdvance().getType()) ? (int)duration.toDays() : (int)duration.toHours();
            if (shiftActivityDTO.getActivity().getRulesActivityTab().getPqlSettings().getApprovalTimeInAdvance().getValue()!=null && calculatedValue < shiftActivityDTO.getActivity().getRulesActivityTab().getPqlSettings().getApprovalTimeInAdvance().getValue()) {
                ShiftValidatorService.throwException("message.shift.plannedTime.less");
            }
        });
    }

    @Override
    public List<String> isSatisfiedString(ShiftWithActivityDTO shift) {
        List<String> messages = new ArrayList<>();
        for (ShiftActivityDTO shiftActivityDTO : shift.getActivities()) {
            Duration duration = Duration.between(DateUtils.getLocalDateTime(), DateUtils.asLocalDateTime(shiftActivityDTO.getStartDate()));
            int calculatedValue = shiftActivityDTO.getActivity().getRulesActivityTab().getPqlSettings().getApprovalTimeInAdvance().getType().equals(DurationType.DAYS) ? (int)duration.toDays() : (int)duration.toHours();
            if (calculatedValue < shiftActivityDTO.getActivity().getRulesActivityTab().getPqlSettings().getApprovalTimeInAdvance().getValue()) {
                messages = Collections.singletonList("message.shift.plannedTime.less");
            }
        }
        return messages;
    }
}
