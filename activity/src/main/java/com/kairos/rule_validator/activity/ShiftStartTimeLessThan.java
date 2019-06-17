package com.kairos.rule_validator.activity;

import com.kairos.commons.utils.DateUtils;
import com.kairos.dto.activity.shift.ShiftActivityDTO;
import com.kairos.dto.activity.shift.ShiftWithActivityDTO;
import com.kairos.enums.DurationType;
import com.kairos.service.shift.ShiftValidatorService;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.kairos.constants.ActivityMessagesConstants.MESSAGE_SHIFT_PLANNEDTIME_LESS;

public class ShiftStartTimeLessThan extends AbstractActivitySpecification<ShiftWithActivityDTO> {


    public ShiftStartTimeLessThan() {
        //Not in use
    }

    @Override
    public boolean isSatisfied(ShiftWithActivityDTO shiftWithActivityDTO) {
        return false;
    }

    @Override
    public void validateRules(ShiftWithActivityDTO shift) {
        shift.getActivities().forEach(shiftActivityDTO -> {
            shiftActivityDTO.getChildActivities().forEach(childActivity->validateShiftActivityStartTime(childActivity));
            validateShiftActivityStartTime(shiftActivityDTO);
        });
    }

    private void validateShiftActivityStartTime(ShiftActivityDTO shiftActivityDTO) {
        Duration duration = Duration.between(DateUtils.getLocalDateTime(), DateUtils.asLocalDateTime(shiftActivityDTO.getStartDate()));
        int calculatedValue = DurationType.DAYS.equals(shiftActivityDTO.getActivity().getRulesActivityTab().getPqlSettings().getApprovalTimeInAdvance().getType()) ? (int)duration.toDays() : (int)duration.toHours();
        if (shiftActivityDTO.getActivity().getRulesActivityTab().getPqlSettings().getApprovalTimeInAdvance().getValue()!=null && calculatedValue < shiftActivityDTO.getActivity().getRulesActivityTab().getPqlSettings().getApprovalTimeInAdvance().getValue()) {
            ShiftValidatorService.throwException(MESSAGE_SHIFT_PLANNEDTIME_LESS);
        }
    }

    @Override
    public List<String> isSatisfiedString(ShiftWithActivityDTO shift) {
        List<String> messages = new ArrayList<>();
        for (ShiftActivityDTO shiftActivityDTO : shift.getActivities()) {
            Duration duration = Duration.between(DateUtils.getLocalDateTime(), DateUtils.asLocalDateTime(shiftActivityDTO.getStartDate()));
            int calculatedValue = shiftActivityDTO.getActivity().getRulesActivityTab().getPqlSettings().getApprovalTimeInAdvance().getType().equals(DurationType.DAYS) ? (int)duration.toDays() : (int)duration.toHours();
            if (calculatedValue < shiftActivityDTO.getActivity().getRulesActivityTab().getPqlSettings().getApprovalTimeInAdvance().getValue()) {
                messages = Collections.singletonList(MESSAGE_SHIFT_PLANNEDTIME_LESS);
            }
        }
        return messages;
    }
}
