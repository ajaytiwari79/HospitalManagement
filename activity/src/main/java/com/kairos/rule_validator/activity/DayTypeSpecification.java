package com.kairos.rule_validator.activity;

import com.kairos.enums.Day;
import com.kairos.rule_validator.AbstractSpecification;
import com.kairos.service.exception.ExceptionService;
import com.kairos.commons.utils.DateUtils;
import com.kairos.utils.ShiftValidatorService;
import com.kairos.wrapper.shift.ShiftWithActivityDTO;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.DayOfWeek;
import java.util.*;

/**
 * Created by oodles on 30/11/17.
 */
public class DayTypeSpecification extends AbstractSpecification<ShiftWithActivityDTO> {


    private Set<DayOfWeek> validDays;
    private Set<Day> days = new HashSet<>();
    private Date shiftStartDateTime;
    @Autowired
    private ExceptionService exceptionService;

    public DayTypeSpecification(Set<DayOfWeek> validDays, Date shiftStartDateTime) {
        this.validDays = validDays;
        this.shiftStartDateTime = shiftStartDateTime;
    }

    @Override
    public boolean isSatisfied(ShiftWithActivityDTO shift) {
        return validDays.contains(DateUtils.asLocalDate(shiftStartDateTime).getDayOfWeek());
    }

    @Override
    public void validateRules(ShiftWithActivityDTO shift) {
        if(!validDays.contains(DateUtils.asLocalDate(shiftStartDateTime).getDayOfWeek())){
            ShiftValidatorService.throwException("message.activity.dayType");
        }
    }

    @Override
    public List<String> isSatisfiedString(ShiftWithActivityDTO shift) {
        return Collections.emptyList();
    }

}
