package com.kairos.utils;
import com.kairos.enums.task_type.TaskTypeEnum;
import com.kairos.persistence.model.task.Task;
import com.kairos.persistence.model.task.TaskStatus;

import java.time.LocalDate;
import java.util.Date;

import static java.time.ZoneId.systemDefault;

/**
 * Created by oodles on 21/8/17.
 */
public class TaskUtil {

    public static Task copyPropertiesOfTask(Task task){
        Task task1 = new Task();
        task1.setName(task.getName());
        LocalDate taskDateFromLocalDate = task.getDateFrom().toInstant().atZone(systemDefault()).toLocalDate();
        LocalDate taskDateToLocalDate = task.getDateTo().toInstant().atZone(systemDefault()).toLocalDate();
        LocalDate taskTimeFromLocalDate = task.getTimeFrom().toInstant().atZone(systemDefault()).toLocalDate();
        LocalDate taskTimeToLocalDate = task.getTimeTo().toInstant().atZone(systemDefault()).toLocalDate();
        LocalDate taskStartBoundaryLocalDate = task.getTaskStartBoundary().toInstant().atZone(systemDefault()).toLocalDate();
        LocalDate taskEndBoundaryLocalDate = task.getTaskEndBoundary().toInstant().atZone(systemDefault()).toLocalDate();
        task1.setDateFrom( Date.from(taskDateFromLocalDate.plusYears(1).atStartOfDay(systemDefault()).toInstant()));
        task1.setDateTo( Date.from(taskDateToLocalDate.plusYears(1).atStartOfDay(systemDefault()).toInstant()));
        task1.setTimeFrom( Date.from(taskTimeFromLocalDate.plusYears(1).atStartOfDay(systemDefault()).toInstant()));
        task1.setTimeTo( Date.from(taskTimeToLocalDate.plusYears(1).atStartOfDay(systemDefault()).toInstant()));
        task1.setTaskDemandId(task.getTaskDemandId());
        task1.setCitizenId(task.getCitizenId());
        task1.setActive(task.isActive());
        task1.setAddress(task.getAddress());
        task1.setTaskTypeId(task.getTaskTypeId());
        task1.setTaskOriginator(TaskTypeEnum.TaskOriginator.PRE_PLANNING);
        task1.setJoinEventId(task.getJoinEventId());
        task1.setColorForGantt(task.getColorForGantt());
        task1.setPriority(task.getPriority());
        task1.setSetupDuration(task.getSetupDuration());
        task1.setTaskStatus(TaskStatus.GENERATED);
        task1.setNumberOfStaffRequired(task.getNumberOfStaffRequired());
        task1.setPrefferedStaffIdsList(task.getPrefferedStaffIdsList());
        task1.setForbiddenStaffIdsList(task.getForbiddenStaffIdsList());
        task1.setVisitourTaskTypeID(task.getVisitourTaskTypeID());
        task1.setPreProcessingDuration(task.getPreProcessingDuration());
        task1.setPostProcessingDuration(task.getPostProcessingDuration());
        task1.setTeamId(task.getTeamId());
        task1.setTaskStartBoundary(Date.from(taskStartBoundaryLocalDate.plusYears(1).atStartOfDay().atZone(systemDefault()).toInstant()));
        task1.setTaskEndBoundary(Date.from(taskEndBoundaryLocalDate.plusYears(1).atStartOfDay().atZone(systemDefault()).toInstant()));
        task1.setSkills(task.getSkills());
        task1.setTimeSlotId(task.getTimeSlotId());
        task1.setTaskDemandVisitId(task.getTaskDemandVisitId());
        task1.setDuration(task.getDuration());
        task1.setUnitId(task.getUnitId());
        task1.setSlaStartDuration(task.getSlaStartDuration());
        return task1;
    }

}
