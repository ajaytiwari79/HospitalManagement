package com.kairos.rule_validator.task;

import com.kairos.enums.Day;
import com.kairos.persistence.model.task.Task;
import com.kairos.service.exception.ExceptionService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Calendar;
import java.util.Date;
import java.util.Set;

import static com.kairos.constants.ActivityMessagesConstants.ERROR_DAY_INVALID;
import static com.kairos.enums.Day.*;

/**
 * Created by prabjot on 24/11/17.
 */
public class TaskDaySpecification extends AbstractTaskSpecification<Task> {

    private Set<Day> forbiddenDays;

    public TaskDaySpecification(Set<Day> forbiddenDays) {
        this.forbiddenDays = forbiddenDays;
    }
@Autowired
    ExceptionService exceptionService;
    @Override
    public boolean isSatisfied(Task task) {
        if(forbiddenDays.contains(EVERYDAY)){
            return false;
        }
        return !forbiddenDays.contains(getDay(task.getDateFrom()));
    }

    private Day getDay(Date taskDate){
        Calendar c = Calendar.getInstance();
        c.setTime(taskDate);
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        Day taskDay=null;
        switch (dayOfWeek){
            case 1:
                taskDay = SUNDAY;
                break;
            case 2:
                taskDay = MONDAY;
                break;
            case 3:
                taskDay = TUESDAY;
                break;
            case 4:
                taskDay = WEDNESDAY;
                break;
            case 5:
                taskDay = THURSDAY;
                break;
            case 6:
                taskDay = FRIDAY;
                break;
            case 7:
                taskDay = SATURDAY;
                break;
            default:
                exceptionService.internalError(ERROR_DAY_INVALID);
        }
        return taskDay;

    }
}
