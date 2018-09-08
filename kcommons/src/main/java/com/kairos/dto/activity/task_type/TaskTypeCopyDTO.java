package com.kairos.dto.activity.task_type;

import java.util.List;

/**
 * Created by prabjot on 16/11/17.
 */
public class TaskTypeCopyDTO {


    private List<String> taskTypeNames;

    public TaskTypeCopyDTO() {
        //default constructor
    }

    public TaskTypeCopyDTO(List<String> taskTypeNames) {
        this.taskTypeNames = taskTypeNames;
    }

    public List<String> getTaskTypeNames() {
        return taskTypeNames;
    }

    public void setTaskTypeNames(List<String> taskTypeNames) {
        this.taskTypeNames = taskTypeNames;
    }
}
