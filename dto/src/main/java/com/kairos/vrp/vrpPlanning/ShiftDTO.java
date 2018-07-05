package com.kairos.vrp.vrpPlanning;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

/**
 * @author pradeep
 * @date - 20/6/18
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ShiftDTO {

    private String id;
    private String kairosShiftId;
    private String name;
    private Long staffId;
    private EmployeeDTO employee;
    private LocalDate localDate;
    private Date startTime;
    private Date endTime;
    private List<TaskDTO> tasks;
    private String color;


    public ShiftDTO(String id,String name, EmployeeDTO employee, LocalDate localDate, Date startTime, Date endTime) {
        this.id = id;
        this.employee = employee;
        this.localDate = localDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.name = name;
    }


    public String getKairosShiftId() {
        return kairosShiftId;
    }

    public void setKairosShiftId(String kairosShiftId) {
        this.kairosShiftId = kairosShiftId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Long getStaffId() {
        return staffId;
    }

    public void setStaffId(Long staffId) {
        this.staffId = staffId;
    }

    public ShiftDTO() {
        this.color = "#efefef";
    }

    public List<TaskDTO> getTasks() {
        return tasks;
    }

    public void setTasks(List<TaskDTO> tasks) {
        this.tasks = tasks;
    }

    public Long getStartTime() {
        return startTime.getTime();
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime.getTime();
    }

    public void setEndTime(Date endTime) {
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
