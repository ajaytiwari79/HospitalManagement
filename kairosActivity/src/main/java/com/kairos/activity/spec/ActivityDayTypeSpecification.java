package com.kairos.activity.spec;

import com.kairos.activity.client.dto.DayType;
import com.kairos.activity.custom_exception.InvalidRequestException;
import com.kairos.activity.persistence.model.activity.Activity;
import com.kairos.activity.persistence.model.staffing_level.Day;

import java.util.*;

import static com.kairos.activity.persistence.model.staffing_level.Day.*;

/**
 * Created by oodles on 30/11/17.
 */
public class ActivityDayTypeSpecification extends AbstractActivitySpecification<Activity> {


    private List<DayType> dayTypes;
    private Set<Day> days = new HashSet<>();
    private Date shiftStartDateTime;

    public ActivityDayTypeSpecification(List<DayType> dayTypes, Date shiftStartDateTime) {
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
            throw new InvalidRequestException("Activity cannot be created on this day");
        }
    }

    private Day getDay(Date activityDate) {

        Calendar c = Calendar.getInstance();
        c.setTime(activityDate);
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        Day activityDay;
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
                throw new InternalError("Invalid day");
        }
        return activityDay;

    }
}
