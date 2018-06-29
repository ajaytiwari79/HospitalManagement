package com.kairos.activity.task_type;

import java.util.List;

/**
 * @author pradeep
 * @date - 11/6/18
 */

public class TaskTypeSettingWrapper {

    private List<TaskTypeDTO> taskTypeList;
    private List<TaskTypeSettingDTO> taskTypeSettings;

    public TaskTypeSettingWrapper() {
    }

    public TaskTypeSettingWrapper(List<TaskTypeDTO> taskTypeList, List<TaskTypeSettingDTO> taskTypeSettings) {
        this.taskTypeList = taskTypeList;
        this.taskTypeSettings = taskTypeSettings;
    }

    public List<TaskTypeDTO> getTaskTypeList() {
        return taskTypeList;
    }

    public void setTaskTypeList(List<TaskTypeDTO> taskTypeList) {
        this.taskTypeList = taskTypeList;
    }

    public List<TaskTypeSettingDTO> getTaskTypeSettings() {
        return taskTypeSettings;
    }

    public void setTaskTypeSettings(List<TaskTypeSettingDTO> taskTypeSettings) {
        this.taskTypeSettings = taskTypeSettings;
    }
}
