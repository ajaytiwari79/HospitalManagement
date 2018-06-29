package com.kairos.user.country.pay_table;

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

        if(value == null){
            return true;
        }
        DateTime dateAsUtc = new DateTime(value).plusHours(5).plusMinutes(30);
        DateTime requestedDate = dateAsUtc.withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0);
        DateTime currentDate = new DateTime().withTimeAtStartOfDay();//new DateTime(DateUtil.getCurrentDate()).withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0);
        int dateValue = requestedDate.compareTo(currentDate);
        if(dateValue <0){
            return false;
        }
        return true;
    }
}
