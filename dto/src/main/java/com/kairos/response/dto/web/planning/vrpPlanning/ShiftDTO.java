package com.kairos.response.dto.web.planning.vrpPlanning;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author pradeep
 * @date - 20/6/18
 */

public class ShiftDTO {

    private String id;
    private EmployeeDTO employee;
    private LocalDate localDate;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private List<TaskDTO> tasks;

    public ShiftDTO(String id, EmployeeDTO employee, LocalDate localDate, LocalDateTime startTime, LocalDateTime endTime) {
        this.id = id;
        this.employee = employee;
        this.localDate = localDate;
        this.startTime = startTime;
        this.endTime = endTime;
    }


    public ShiftDTO() {
    }

    public List<TaskDTO> getTasks() {
        return tasks;
    }

    public void setTasks(List<TaskDTO> tasks) {
        this.tasks = tasks;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public EmployeeDTO getEmployee() {
        return employee;
    }

    public void setEmployee(EmployeeDTO employee) {
        this.employee = employee;
    }

    public LocalDate getLocalDate() {
        return localDate;
    }

    public void setLocalDate(LocalDate localDate) {
        this.localDate = localDate;
    }
}
