package com.kairos.util.external_plateform_shift;

import com.kairos.persistence.model.task.TaskReport;

import java.util.List;

/**
 * Created by oodles on 31/1/17.
 */
public class TaskReportWrapper {

    private String staffName;
    private List<TaskReport> taskReports;


    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

    public List<TaskReport> getTaskReports() {
        return taskReports;
    }

    public void setTaskReports(List<TaskReport> taskReports) {
        this.taskReports = taskReports;
    }

    public TaskReportWrapper(String staffName, List<TaskReport> taskReports) {
        this.staffName = staffName;
        this.taskReports = taskReports;
    }
}
