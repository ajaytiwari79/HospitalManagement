package com.kairos.activity.spec;

import com.kairos.activity.persistence.model.staffing_level.Day;
import com.kairos.activity.persistence.model.task.Task;
import com.kairos.activity.service.task_type.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static com.kairos.activity.persistence.model.staffing_level.Day.*;

/**
 * Created by prabjot on 24/11/17.
 */
public class TaskDaySpecification extends AbstractTaskSpecification<Task>{

    private Set<Day> forbiddenDays;

    public TaskDaySpecification(Set<Day> forbiddenDays) {
        this.forbiddenDays = forbiddenDays;
    }

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
        Day taskDay;
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
                throw new InternalError("Invalid day");
        }
        return taskDay;

    }
}
