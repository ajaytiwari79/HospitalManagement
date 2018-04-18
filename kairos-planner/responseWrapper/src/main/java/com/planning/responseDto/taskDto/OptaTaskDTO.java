package com.planning.responseDto.taskDto;


import com.planning.responseDto.commonDto.BaseDTO;
import com.planning.responseDto.locationDto.OptaLocationDTO;
import com.planning.responseDto.skillDto.OptaSkillDTO;
import com.planning.responseDto.staffDto.OptaStaffDTO;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.joda.time.DateTime;

import java.util.Date;
import java.util.List;

public class OptaTaskDTO extends BaseDTO{

    private Long citizenId;
    private String name;
    private Long staffId;
    private Integer duration;
    private Integer priority;
    private String timeCareExternalId;
    private OptaLocationDTO address;
    private Date dateFrom;
    private Date dateTo;
    private Date timeFrom;
    private Date timeTo;
    private Integer slaStartDuration;
    private Integer slaEndDuration;
    private List<OptaSkillDTO> optaSkills;
    private OptaStaffDTO optaStaffDTO;
    private OptaTaskTypeDTO taskType;
    private Long taskTypeId;
    private Date startDate;
    private Date endDate;
    private String taskStatus;
    private Long relatedTaskid;
    private String relatedOptaTaskId;
    private Boolean isMultiStaffTask;
    private Date plannedStartTime;
    private Date plannedEndTime;
    private Integer setupTime;
    private Integer preProcessingTime;
    private Integer postProcessingTime;
    private Double cost;
    private Boolean isActive;
    private Boolean isMergedTask;

    public Boolean getMergedTask() {
        return isMergedTask;
    }

    public void setMergedTask(Boolean mergedTask) {
        isMergedTask = mergedTask;
    }


    public Long getCitizenId() {
        return citizenId;
    }

    public void setCitizenId(Long citizenId) {
        this.citizenId = citizenId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public String getTimeCareExternalId() {
        return timeCareExternalId;
    }

    public void setTimeCareExternalId(String timeCareExternalId) {
        this.timeCareExternalId = timeCareExternalId;
    }

    public Long getStaffId() {
        return staffId;
    }

    public void setStaffId(Long staffId) {
        this.staffId = staffId;
    }

    public OptaLocationDTO getAddress() {
        return address;
    }

    public void setAddress(OptaLocationDTO address) {
        this.address = address;
    }

    public String getRelatedOptaTaskId() {
        return relatedOptaTaskId;
    }

    public void setRelatedOptaTaskId(String relatedOptaTaskId) {
        this.relatedOptaTaskId = relatedOptaTaskId;
    }

    public Date getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(Date dateFrom) {
        this.dateFrom = dateFrom;
    }

    public Date getDateTo() {
        return dateTo;
    }

    public void setDateTo(Date dateTo) {
        this.dateTo = dateTo;
    }

    public Date getTimeFrom() {
        return timeFrom;
    }

    public void setTimeFrom(Date timeFrom) {
        this.timeFrom = timeFrom;
    }

    public Date getTimeTo() {
        return timeTo;
    }

    public void setTimeTo(Date timeTo) {
        this.timeTo = timeTo;
    }

    public Integer getSlaStartDuration() {
        return slaStartDuration;
    }

    public void setSlaStartDuration(Integer slaStartDuration) {
        this.slaStartDuration = slaStartDuration;
    }

    public Integer getSlaEndDuration() {
        return slaEndDuration;
    }

    public void setSlaEndDuration(Integer slaEndDuration) {
        this.slaEndDuration = slaEndDuration;
    }

    public List<OptaSkillDTO> getOptaSkills() {
        return optaSkills;
    }

    public void setOptaSkills(List<OptaSkillDTO> optaSkills) {
        this.optaSkills = optaSkills;
    }

    public OptaStaffDTO getOptaStaffDTO() {
        return optaStaffDTO;
    }

    public void setOptaStaffDTO(OptaStaffDTO optaStaffDTO) {
        this.optaStaffDTO = optaStaffDTO;
    }

    public OptaTaskTypeDTO getTaskType() {
        return taskType;
    }

    public void setTaskType(OptaTaskTypeDTO taskType) {
        this.taskType = taskType;
    }

    public Long getTaskTypeId() {
        return taskTypeId;
    }

    public void setTaskTypeId(Long taskTypeId) {
        this.taskTypeId = taskTypeId;
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

    public String getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(String taskStatus) {
        this.taskStatus = taskStatus;
    }

    public Long getRelatedTaskid() {
        return relatedTaskid;
    }

    public void setRelatedTaskid(Long relatedTaskid) {
        this.relatedTaskid = relatedTaskid;
    }

    public Boolean getMultiStaffTask() {
        return isMultiStaffTask;
    }

    public void setMultiStaffTask(Boolean multiStaffTask) {
        isMultiStaffTask = multiStaffTask;
    }

    public Date getPlannedStartTime() {
        return plannedStartTime;
    }

    public void setPlannedStartTime(Date plannedStartTime) {
        this.plannedStartTime = plannedStartTime;
    }

    public Date getPlannedEndTime() {
        return plannedEndTime;
    }

    public void setPlannedEndTime(Date plannedEndTime) {
        this.plannedEndTime = plannedEndTime;
    }

    public Integer getSetupTime() {
        return setupTime;
    }

    public void setSetupTime(Integer setupTime) {
        this.setupTime = setupTime;
    }

    public Integer getPreProcessingTime() {
        return preProcessingTime;
    }

    public void setPreProcessingTime(Integer preProcessingTime) {
        this.preProcessingTime = preProcessingTime;
    }

    public Integer getPostProcessingTime() {
        return postProcessingTime;
    }

    public void setPostProcessingTime(Integer postProcessingTime) {
        this.postProcessingTime = postProcessingTime;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

}
