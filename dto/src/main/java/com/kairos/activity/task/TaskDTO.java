package com.kairos.activity.task;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

/**
 * This
 * Created by oodles on 8/2/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TaskDTO {

    private String id;
    private Long resource;
    private String start;
    private String end;
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss", timezone = "GMT")
    private Date startDate;
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss", timezone = "GMT")
    private Date endDate;
    private String info1;
    private String info2;
    private String taskTypeId;
    private String taskTypeName;
    private Integer duration;
    private Boolean active;
    @NotEmpty(message = "error.TaskDTO.startAddress.notEmpty") @NotNull(message = "error.TaskDTO.startAddress.notnull")
    private String startAddress;
    @NotEmpty(message = "error.TaskDTO.endAddress.notEmpty") @NotNull(message = "error.TaskDTO.endAddress.notnull")
    private String endAddress;
    private Integer priority;
    private Long anonymousStaffId;
    private AbsencePlanningStatus status;
    private List<Long> forbiddenStaff;
    private List<Long> prefferedStaff;
    private List<String> skillsList;
    private String team;
    private List<BigInteger> taskIds;

    public void setTaskIds(List<BigInteger> taskIds) {
        this.taskIds = taskIds;
    }

    public List<BigInteger> getTaskIds() {

        return taskIds;
    }

    public List<Long> getForbiddenStaff() {
        return forbiddenStaff;
    }

    public void setForbiddenStaff(List<Long> forbiddenStaff) {
        this.forbiddenStaff = forbiddenStaff;
    }

    public List<Long> getPrefferedStaff() {
        return prefferedStaff;
    }

    public void setPrefferedStaff(List<Long> prefferedStaff) {
        this.prefferedStaff = prefferedStaff;
    }

    public List<String> getSkillsList() {
        return skillsList;
    }

    public void setSkillsList(List<String> skillsList) {
        this.skillsList = skillsList;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getResource() {
        return resource;
    }

    public void setResource(Long resource) {
        this.resource = resource;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getInfo1() {
        return info1;
    }

    public void setInfo1(String info1) {
        this.info1 = info1;
    }

    public String getInfo2() {
        return info2;
    }

    public void setInfo2(String info2) {
        this.info2 = info2;
    }

    public String getTaskTypeId() {
        return taskTypeId;
    }

    public void setTaskTypeId(String taskTypeId) {
        this.taskTypeId = taskTypeId;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }


    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getStartAddress() {
        return startAddress;
    }

    public void setStartAddress(String startAddress) {
        this.startAddress = startAddress;
    }

    public String getEndAddress() {
        return endAddress;
    }

    public void setEndAddress(String endAddress) {
        this.endAddress = endAddress;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Long getAnonymousStaffId() {
        return anonymousStaffId;
    }

    public void setAnonymousStaffId(Long anonymousStaffId) {
        this.anonymousStaffId = anonymousStaffId;
    }

    public String getTaskTypeName() {
        return taskTypeName;
    }

    public void setTaskTypeName(String taskTypeName) {
        this.taskTypeName = taskTypeName;
    }

    public AbsencePlanningStatus getStatus() {
        return status;
    }

    public void setStatus(AbsencePlanningStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "TaskDTO{" +
                "id='" + id + '\'' +
                ", resource=" + resource +
                ", start='" + start + '\'' +
                ", end='" + end + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", info1='" + info1 + '\'' +
                ", info2='" + info2 + '\'' +
                ", taskTypeId=" + taskTypeId +
                ", duration=" + duration +
                ", active=" + active +
                ", startAddress='" + startAddress + '\'' +
                ", endAddress='" + endAddress + '\'' +
                ", priority=" + priority +
                ", anonymousStaffId=" + anonymousStaffId +
                '}';
    }

}
