package com.kairos.rule_validator.activity;

import com.kairos.enums.Day;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.rule_validator.AbstractSpecification;
import com.kairos.service.exception.ExceptionService;
import com.kairos.user.country.day_type.DayType;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

import static com.kairos.enums.Day.*;

/**
 * Created by oodles on 30/11/17.
 */
public class DayTypeSpecification extends AbstractSpecification<Activity> {


    private List<DayType> dayTypes;
    private Set<Day> days = new HashSet<>();
    private Date shiftStartDateTime;
    @Autowired
    private ExceptionService exceptionService;

    public DayTypeSpecification(List<DayType> dayTypes, Date shiftStartDateTime) {
        this.dayTypes = dayTypes;
        this.shiftStartDateTime = shiftStartDateTime;
    }

    @Override
    public boolean isSatisfied(Activity activity) {
        for (DayType dayType : dayTypes) {
            days.addAll(dayType.getValidDays());
        }
        if (this.days.contains(EVERYDAY)) {
            return true;
        } else if (this.days.contains(getDay(shiftStartDateTime))) {
            return true;
        } else {
            exceptionService.invalidRequestException("message.activity.day.create");
        }
        return true;
    }

    @Override
    public List<String> isSatisfiedString(Activity activity) {
        return Collections.emptyList();
    }

    private Day getDay(Date activityDate) {

        Calendar c = Calendar.getInstance();
        c.setTime(activityDate);
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        Day activityDay = null;
        switch (dayOfWeek) {
            case 1:
                activityDay = SUNDAY;
                break;
            case 2:
                activityDay = MONDAY;
                break;
            case 3:
                activityDay = TUESDAY;
                break;
            case 4:
                activityDay = WEDNESDAY;
                break;
            case 5:
                activityDay = THURSDAY;
                break;
            case 6:
                activityDay = FRIDAY;
                break;
            case 7:
                activityDay = SATURDAY;
                break;
            default:
                exceptionService.internalError("error.day.invalid");

        }
        return activityDay;

    }
}
