package com.kairos.persistence.model.user.pay_level;

import org.joda.time.DateTime;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Date;

/**
 * Created by prabjot on 29/12/17.
 */
public class FutureDateValidator implements ConstraintValidator<FutureDate,Date> {

    @Override
    public void initialize(FutureDate constraintAnnotation) {

    }

    @Override
    public boolean isValid(Date value, ConstraintValidatorContext context) {



        DateTime requestedDate = new DateTime(value).withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0);
        DateTime currentDate = new DateTime().withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0);
        int dateValue = requestedDate.compareTo(currentDate);
        if(dateValue <0){
            return false;
        }
        return true;
    }
}
