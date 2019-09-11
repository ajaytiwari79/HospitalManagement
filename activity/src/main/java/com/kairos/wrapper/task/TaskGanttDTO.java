package com.kairos.wrapper.task;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kairos.persistence.model.task.SkillExpertise;
import com.kairos.persistence.model.task.Task;
import com.kairos.persistence.model.task.TaskStatus;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.kairos.commons.utils.DateUtils.ONLY_DATE;


/**
 * Created by prabjot on 8/6/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TaskGanttDTO {

    private BigInteger id; // Named it just id because Client's DayPilot plugin requirement
    private int visitourId;
    private BigInteger taskDemandId;
    private String taskTypeId;
    private long duration;
    @JsonProperty("status")
    private TaskStatus taskStatus;

    @JsonProperty("isHouseholdTask")
    public boolean isHouseholdTask() {
        return householdTask;
    }

    @JsonProperty("householdTask")
    public void setHouseholdTask(boolean householdTask) {
        this.householdTask = householdTask;
    }

    private boolean householdTask = true; //whether someone else in same household receiving this type of task.
    private String taskTypeIconUrl;
    private int numberOfStaffRequired;
    private int priority;
    private boolean locationChanged;
    private boolean changeLocationNotAllowed;

    @JsonProperty("active")
    public boolean isActive() {
        return isActive;
    }

    @JsonProperty("isActive")
    public void setActive(boolean active) {
        isActive = active;
    }

    private boolean isActive;
    private int startHour;
    private int startMinute;
    private int endHour;
    private int endMinute;
    //@JsonFormat(pattern=ONLY_DATE)
    private String resource;
    private String name;
    private String info1;
    private String info2;
    private String colorForGantt;
    private List<Long> prefferedStaffIdsList;
    private List<Long> forbiddenStaffIdsList;
    private String teamId="";
    private List<String> skills;
    private String joinEventId;
    private Long timeSlotId;
    private BigInteger taskDemandVisitId;
    private long unitId;
    //@JsonFormat(pattern=DATE_FORMAT)
    private String taskStartBoundary;
    //@JsonFormat(pattern=DATE_FORMAT)
    private String taskEndBoundary;
    private Map<String,Object> timeWindow;
    private List<TaskGanttDTO> subTasks;
    private List<Task.ClientException> clientExceptions;
    private boolean singleTask;
    private List<Long> assignedStaffIds;
    private boolean isEditable= true;
    private List<SkillExpertise> skillExpertiseList;

    public boolean isEditable() {
        return isEditable;
    }

    public void setEditable(boolean editable) {
        isEditable = editable;
    }

    public void setSingleTask(boolean singleTask) {
        this.singleTask = singleTask;
    }

    public boolean isSingleTask() {
        return singleTask;
    }

    public void setClientExceptions(List<Task.ClientException> clientExceptions) {
        this.clientExceptions = clientExceptions;
    }

    public List<Task.ClientException> getClientExceptions() {
        return clientExceptions;
    }

    public List<TaskGanttDTO> getSubTasks() {
        return subTasks;
    }

    public void setSubTasks(List<TaskGanttDTO> subTasks) {
        this.subTasks = subTasks;
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public int getVisitourId() {
        return visitourId;
    }

    public void setVisitourId(int visitourId) {
        this.visitourId = visitourId;
    }

    public BigInteger getTaskDemandId() {
        return taskDemandId;
    }

    public void setTaskDemandId(BigInteger taskDemandId) {
        this.taskDemandId = taskDemandId;
    }

    public String getTaskTypeId() {
        return taskTypeId;
    }

    public void setTaskTypeId(String taskTypeId) {
        this.taskTypeId = taskTypeId;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = TimeUnit.MINUTES.toSeconds(duration);
    }

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }


    public String getTaskTypeIconUrl() {
        return taskTypeIconUrl;
    }

    public void setTaskTypeIconUrl(String taskTypeIconUrl) {
        this.taskTypeIconUrl = taskTypeIconUrl;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }


    public int getStartHour() {
        return startHour;
    }

    public void setStartHour(int startHour) {
        this.startHour = startHour;
    }

    public int getStartMinute() {
        return startMinute;
    }

    public void setStartMinute(int startMinute) {
        this.startMinute = startMinute;
    }

    public int getEndHour() {
        return endHour;
    }

    public void setEndHour(int endHour) {
        this.endHour = endHour;
    }

    public int getEndMinute() {
        return endMinute;
    }

    public void setEndMinute(int endMinute) {
        this.endMinute = endMinute;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(Date resource) {
        SimpleDateFormat formatter = new SimpleDateFormat(ONLY_DATE);
        this.resource = formatter.format(resource);
    }

    @JsonProperty("text")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
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

    @JsonProperty("team")
    public String getTeamId() {
        return teamId;
    }
    @JsonProperty("teamId")
    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    @JsonProperty("noOfStaff")
    public int getNumberOfStaffRequired() {
        return numberOfStaffRequired;
    }

    @JsonProperty("numberOfStaffRequired")
    public void setNumberOfStaffRequired(int numberOfStaffRequired) {
        this.numberOfStaffRequired = numberOfStaffRequired;
    }

    @JsonProperty("taskTypeColor")
    public String getColorForGantt() {
        return colorForGantt;
    }

    @JsonProperty("colorForGantt")
    public void setColorForGantt(String colorForGantt) {
        this.colorForGantt = colorForGantt;
    }

    @JsonProperty("prefferedStaff")
    public List<Long> getPrefferedStaffIdsList() {
        return Optional.ofNullable(prefferedStaffIdsList).orElse(Collections.emptyList());
    }

    @JsonProperty("prefferedStaffIdsList")
    public void setPrefferedStaffIdsList(List<Long> prefferedStaffIdsList) {
        this.prefferedStaffIdsList = prefferedStaffIdsList;
    }

    @JsonProperty("forbiddenStaff")
    public List<Long> getForbiddenStaffIdsList() {
        return Optional.ofNullable(forbiddenStaffIdsList).orElse(Collections.emptyList());
    }

    @JsonProperty("forbiddenStaffIdsList")
    public void setForbiddenStaffIdsList(List<Long> forbiddenStaffStaffIdsList) {
        this.forbiddenStaffIdsList = forbiddenStaffStaffIdsList;
    }

    @JsonProperty("skillsList")
    public List<String> getSkills() {
        return Optional.ofNullable(skills).orElse(Collections.emptyList());
    }

    @JsonProperty("skills")
    public void setSkills(String skills) {
        this.skills = skills != null ? Arrays.asList(skills.split(",")) : Collections.EMPTY_LIST;
    }

    @JsonProperty("jointEvents")
    public String getJoinEventId() {
        return joinEventId;
    }

    @JsonProperty("joinEventId")
    public void setJoinEventId(String joinEventId) {
        this.joinEventId = joinEventId;
    }

    public Long getTimeSlotId() {
        return timeSlotId;
    }

    public void setTimeSlotId(Long timeSlotId) {
        this.timeSlotId = timeSlotId;
    }

    public BigInteger getTaskDemandVisitId() {
        return taskDemandVisitId;
    }

    public void setTaskDemandVisitId(BigInteger taskDemandVisitId) {
        this.taskDemandVisitId = taskDemandVisitId;
    }

    public long getUnitId() {
        return unitId;
    }

    public void setUnitId(long unitId) {
        this.unitId = unitId;
    }

    public String getTaskStartBoundary() {
        return taskStartBoundary;
    }

    public void setTaskStartBoundary(Date taskStartBoundary) {
        SimpleDateFormat formatter = new SimpleDateFormat(ONLY_DATE);
        this.taskStartBoundary = (taskStartBoundary!=null)? formatter.format(taskStartBoundary) : null;
    }

    public String getTaskEndBoundary() {
        return taskEndBoundary;
    }

    public void setTaskEndBoundary(Date taskEndBoundary) {
        SimpleDateFormat formatter = new SimpleDateFormat(ONLY_DATE);
        this.taskEndBoundary = (taskEndBoundary!=null)? formatter.format(taskEndBoundary) : null;
    }

    public Map<String, Object> getTimeWindow() {
        return timeWindow;
    }

    public void setTimeWindow(Map<String, Object> timeWindow) {
        this.timeWindow = timeWindow;
    }

    public List<Long> getAssignedStaffIds() {
        return assignedStaffIds;
    }

    public void setAssignedStaffIds(List<Long> assignedStaffIds) {
        this.assignedStaffIds = assignedStaffIds;
    }

    public List<SkillExpertise> getSkillExpertiseList() {
        return skillExpertiseList;
    }

    public void setSkillExpertiseList(List<SkillExpertise> skillExpertiseList) {
        this.skillExpertiseList = skillExpertiseList;
    }

    public boolean isLocationChanged() {
        return locationChanged;
    }

    public void setLocationChanged(boolean locationChanged) {
        this.locationChanged = locationChanged;
    }

    public boolean isChangeLocationNotAllowed() {
        return changeLocationNotAllowed;
    }

    public void setChangeLocationNotAllowed(boolean changeLocationNotAllowed) {
        this.changeLocationNotAllowed = changeLocationNotAllowed;
    }
}
