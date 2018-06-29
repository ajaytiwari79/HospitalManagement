package com.kairos.user.country.pay_table;

import com.kairos.user.country.pay_group_area.PayGroupAreaDTO;
import org.joda.time.DateTime;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Optional;

/**
 * Created by vipul on 10/3/18.
 */
public class DateRangeValidator implements ConstraintValidator<DateRange, PayGroupAreaDTO> {

    @Override
    public void initialize(DateRange constraintAnnotation) {

    }

    @Override
    public boolean isValid(PayGroupAreaDTO payGroupAreaDTO, ConstraintValidatorContext context) {
        if (!Optional.ofNullable(payGroupAreaDTO.getStartDateMillis()).isPresent()) {
            return false;
        }
        if (Optional.ofNullable(payGroupAreaDTO.getEndDateMillis()).isPresent()) {
            DateTime endDateAsUtc = new DateTime(payGroupAreaDTO.getEndDateMillis()).withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0);
            DateTime startDateAsUtc = new DateTime(payGroupAreaDTO.getStartDateMillis()).withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0);
            boolean dateValue = (endDateAsUtc.isBefore(startDateAsUtc)) ? false : true;
            return dateValue;
        }
        return true;
    }
}
