package com.kairos.persistence.model.task;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kairos.dto.activity.task.AbsencePlanningStatus;
import com.kairos.enums.task_type.TaskTypeEnum;
import com.kairos.persistence.enums.task_type.DelayPenalty;
import com.kairos.persistence.model.task_demand.TaskDemand;
import com.kairos.persistence.model.task_type.AddressCode;
import org.springframework.beans.BeanUtils;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigInteger;
import java.util.*;

/**
 * Created by prabjot on 3/11/16.
 */
@Document(collection = "tasks")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Task extends TaskDemand {


    private String name;
    private Integer visitourId; // denotes the Visitour Call ID for this Kairos.
    private BigInteger taskDemandId;
    private Date executionDate;
    private Integer duration;
    private TaskStatus taskStatus;
    private String externalId;
    private String timeCareExternalId; // TimeCare Shift ExternalId
    private String monacoExterenalId; // Monaco Shift ExternalId
    private Date fixedDate;
    private Boolean isFixed;
    private Long staffId;
    private Boolean isStaffAnonymous;
    private Boolean isTaskTypeAnonymous;
    private Long staffAnonymousId;
    private Long taskTypeAnonymousId;
    private String info1;
    private String info2;
    private TaskAddress address;

    private Date dateFrom;
    private Date dateTo;
    private Date timeFrom;
    private Date timeTo;
    private Integer distance;
    private Integer drivingTime;

    private TaskTypeEnum.TaskOriginator taskOriginator;
    private AbsencePlanningStatus absencePlanningStatus;
    private AddressCode startAddress;
    private AddressCode endAddress;
    private boolean isActive = true;
    private String staffAnonymousGender;
    private String skills;      //Required skills. Comma separated list of the skill IDs (integers) according to the VISITOUR master data.
    private String teamId;       //TeamID	string	ID of the team according to the VISITOUR master data.
    private String forbiddenFieldManagerID;
    private String preferredFieldmanagerID;
    private String preferredFieldmanagerID2;
    private String visitourTaskTypeID;

    private Integer numberOfStaffRequired;
    private String relatedTaskId;

    private String joinEventId; // this field is required in kairos planner gantt, to update tasks of same day repeatation.

    private List<BigInteger> subTaskIds;
    private Boolean isSubTask = false;

    private List<Long> prefferedStaffIdsList;
    private List<Long> forbiddenStaffIdsList;

    private String colorForGantt;

    private String lastSyncJobId; // this field is required to record last sync job id for importing tasks from TimeCare etc,

    private Long timeSlotId; //Id of time-slot in which this task is dropped in gantt view.
    private BigInteger taskDemandVisitId; //Task Demand Visit Id for which this task is dropped in gantt view.

    private boolean hasActualTask;
    private BigInteger parentTaskId;

    private int slaStartDuration;
    private int slaEndDuration;

    private Date taskStartBoundary;
    private Date taskEndBoundary;
    private Boolean isMultiStaffTask;
    private List<ClientException> clientExceptions;
    private boolean singleTask;
    private List<Long> assignedStaffIds;
    private DelayPenalty delayPenalty;
    private Integer extraPenalty;

    private int preProcessingDuration;
    private int postProcessingDuration;

    private String kmdTaskExternalId; // KMD task ExternalId

    private List<SkillExpertise> skillExpertiseList;

    private boolean locationChanged;

    //Vrp settings
    private Long installationNumber;
    private String citizenName;

    public String getCitizenName() {
        return citizenName;
    }

    public void setCitizenName(String citizenName) {
        this.citizenName = citizenName;
    }

    public Long getInstallationNumber() {
        return installationNumber;
    }

    public void setInstallationNumber(Long installationNumber) {
        this.installationNumber = installationNumber;
    }

    public Integer getExtraPenalty() {
        return extraPenalty;
    }

    public void setExtraPenalty(Integer extraPenalty) {
        this.extraPenalty = extraPenalty;
    }

    public DelayPenalty getDelayPenalty() {
        return delayPenalty;
    }

    public void setDelayPenalty(DelayPenalty delayPenalty) {
        this.delayPenalty = delayPenalty;
    }

    public boolean isSingleTask() {
        return singleTask;
    }

    public void setSingleTask(boolean singleTask) {
        this.singleTask = singleTask;
    }

    public void setClientExceptions(List<ClientException> clientExceptions) {
        this.clientExceptions = clientExceptions;
    }

    public List<ClientException> getClientExceptions() {

        return Optional.ofNullable(clientExceptions).orElse(new ArrayList<>());
    }

    public Task() {
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public Integer getVisitourId() {
        return visitourId;
    }

    public void setVisitourId(Integer visitourId) {
        this.visitourId = visitourId;
    }

    public Date getDateTo() {
        return dateTo;
    }

    public void setDateTo(Date dateTo) {
        this.dateTo = dateTo;
    }

    public Date getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(Date dateFrom) {
        this.dateFrom = dateFrom;
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

    public Long getStaffId() {
        return staffId;
    }

    public void setStaffId(Long staffId) {
        this.staffId = staffId;
    }

    public Date getFixedDate() {
        return fixedDate;
    }

    public void setFixedDate(Date fixedDate) {
        this.fixedDate = fixedDate;
    }

    public Boolean getFixed() {
        return isFixed;
    }

    public void setFixed(Boolean fixed) {
        isFixed = fixed;
    }

    public String getExternalId() {
        return externalId;
    }

    public String getTimeCareExternalId() {
        return timeCareExternalId;
    }

    public void setTimeCareExternalId(String timeCareExternalId) {
        this.timeCareExternalId = timeCareExternalId;
    }

    public String getMonacoExterenalId() {
        return monacoExterenalId;
    }

    public void setMonacoExterenalId(String monacoExterenalId) {
        this.monacoExterenalId = monacoExterenalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }

    public Boolean getStaffAnonymous() {
        return isStaffAnonymous;
    }

    public void setStaffAnonymous(Boolean staffAnonymous) {
        isStaffAnonymous = staffAnonymous;
    }

    public Boolean getTaskTypeAnonymous() {
        return isTaskTypeAnonymous;
    }

    public void setTaskTypeAnonymous(Boolean taskTypeAnonymous) {
        isTaskTypeAnonymous = taskTypeAnonymous;
    }

    public Long getStaffAnonymousId() {
        return staffAnonymousId;
    }

    public void setStaffAnonymousId(Long staffAnonymousId) {
        this.staffAnonymousId = staffAnonymousId;
    }

    public Long getTaskTypeAnonymousId() {
        return taskTypeAnonymousId;
    }

    public void setTaskTypeAnonymousId(Long taskTypeAnonymousId) {
        this.taskTypeAnonymousId = taskTypeAnonymousId;
    }

    public TaskTypeEnum.TaskOriginator getTaskOriginator() {
        return taskOriginator;
    }

    public void setTaskOriginator(TaskTypeEnum.TaskOriginator taskOriginator) {
        this.taskOriginator = taskOriginator;
    }

    public String getStaffAnonymousGender() {
        return staffAnonymousGender;
    }

    public void setStaffAnonymousGender(String staffAnonymousGender) {
        this.staffAnonymousGender = staffAnonymousGender;
    }

    public AbsencePlanningStatus getAbsencePlanningStatus() {
        return absencePlanningStatus;
    }

    public void setAbsencePlanningStatus(AbsencePlanningStatus absencePlanningStatus) {
        this.absencePlanningStatus = absencePlanningStatus;
    }

    public AddressCode getStartAddress() {
        return startAddress;
    }

    public void setStartAddress(AddressCode startAddress) {
        this.startAddress = startAddress;
    }

    public AddressCode getEndAddress() {
        return endAddress;
    }

    public void setEndAddress(AddressCode endAddress) {
        this.endAddress = endAddress;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    @JsonProperty("isActive")
    public boolean isActive() {

        return isActive;
    }

    public Task(BigInteger taskTypeId, Date executionDate, long timeSlotId, int duration, String supplier,
                long organizationId, long citizenId, long staffId, String taskDescription, int priorityLevel, BigInteger taskDemandId) {

        this.taskTypeId = taskTypeId;
        this.setExecutionDate(executionDate);
        this.setDuration(duration);
        //  this.organizationId = organizationId;
        this.citizenId = citizenId;
        //this.staffId = staffId;
        this.priority = priorityLevel;
        this.taskDemandId = taskDemandId;
    }

    /**
     * @Pre_Kairos create task by beacons
     */
    public Task(BigInteger taskTypeId, int duration,
                long citizenId, long staffId, long unitId, Date startTime, TaskStatus taskStatus) {
        this.taskTypeId = taskTypeId;
        this.dateFrom = startTime;
        this.timeFrom = startTime;
        this.setDuration(duration);
        this.unitId = unitId;
        this.citizenId = citizenId;
        this.taskDemandId = BigInteger.ONE.negate();
        this.taskStatus = taskStatus;
        taskOriginator = TaskTypeEnum.TaskOriginator.PRE_KAIROS;
    }


    public BigInteger getTaskTypeId() {
        return taskTypeId;
    }

    public void setTaskTypeId(BigInteger taskTypeId) {
        this.taskTypeId = taskTypeId;
    }


    public BigInteger getTaskDemandId() {
        return taskDemandId;
    }

    public void setTaskDemandId(BigInteger taskDemandId) {
        this.taskDemandId = taskDemandId;
    }

    public Date getExecutionDate() {
        return executionDate;
    }

    public void setExecutionDate(Date executionDate) {
        this.executionDate = executionDate;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
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

    public TaskAddress getAddress() {
        return address;
    }

    public void setAddress(TaskAddress address) {
        this.address = address;
    }

    public Integer getDistance() {
        return distance;
    }

    public void setDistance(Integer distance) {
        this.distance = distance;
    }

    public Integer getDrivingTime() {
        return drivingTime;
    }

    public void setDrivingTime(Integer drivingTime) {
        this.drivingTime = drivingTime;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    public String getForbiddenFieldManagerID() {
        return forbiddenFieldManagerID;
    }

    public void setForbiddenFieldManagerID(String forbiddenFieldManagerID) {
        this.forbiddenFieldManagerID = forbiddenFieldManagerID;
    }

    public String getPreferredFieldmanagerID() {
        return preferredFieldmanagerID;
    }

    public void setPreferredFieldmanagerID(String preferredFieldmanagerID) {
        this.preferredFieldmanagerID = preferredFieldmanagerID;
    }

    public String getPreferredFieldmanagerID2() {
        return preferredFieldmanagerID2;
    }

    public void setPreferredFieldmanagerID2(String preferredFieldmanagerID2) {
        this.preferredFieldmanagerID2 = preferredFieldmanagerID2;
    }

    public String getVisitourTaskTypeID() {
        return visitourTaskTypeID;
    }

    public void setVisitourTaskTypeID(String visitourTaskTypeID) {
        this.visitourTaskTypeID = visitourTaskTypeID;
    }

    public Integer getNumberOfStaffRequired() {
        return numberOfStaffRequired;
    }

    public void setNumberOfStaffRequired(Integer numberOfStaffRequired) {
        this.numberOfStaffRequired = numberOfStaffRequired;
    }

    public String getRelatedTaskId() {
        return relatedTaskId;
    }

    public void setRelatedTaskId(String relatedTaskId) {
        this.relatedTaskId = relatedTaskId;
    }

    public String getJoinEventId() {
        return joinEventId;
    }

    public void setJoinEventId(String joinEventId) {
        this.joinEventId = joinEventId;
    }

    public List<Long> getPrefferedStaffIdsList() {
        return Optional.ofNullable(prefferedStaffIdsList).orElse(new ArrayList<>());
    }

    public void setPrefferedStaffIdsList(List<Long> prefferedStaffIdsList) {
        this.prefferedStaffIdsList = prefferedStaffIdsList;
    }

    public List<Long> getForbiddenStaffIdsList() {
        return Optional.ofNullable(forbiddenStaffIdsList).orElse(new ArrayList<>());
    }

    public void setForbiddenStaffIdsList(List<Long> forbiddenStaffIdsList) {
        this.forbiddenStaffIdsList = forbiddenStaffIdsList;
    }

    public String getColorForGantt() {
        return colorForGantt;
    }

    public void setColorForGantt(String colorForGantt) {
        this.colorForGantt = colorForGantt;
    }

    public String getLastSyncJobId() {
        return lastSyncJobId;
    }

    public void setLastSyncJobId(String lastSyncJobId) {
        this.lastSyncJobId = lastSyncJobId;
    }


    public List<BigInteger> getSubTaskIds() {
        return Optional.ofNullable(subTaskIds).orElse(new ArrayList<>());
    }

    public void setSubTaskIds(List<BigInteger> subTaskIds) {
        this.subTaskIds = subTaskIds;
    }

    public Boolean getSubTask() {
        return isSubTask;
    }

    public void setSubTask(Boolean subTask) {
        isSubTask = subTask;
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

    public boolean isHasActualTask() {
        return hasActualTask;
    }

    public void setHasActualTask(boolean hasActualTask) {
        this.hasActualTask = hasActualTask;
    }

    public BigInteger getParentTaskId() {
        return parentTaskId;
    }

    public void setParentTaskId(BigInteger parentTaskId) {
        this.parentTaskId = parentTaskId;
    }

    public int getSlaStartDuration() {
        return slaStartDuration;
    }

    public void setSlaStartDuration(int slaStartDuration) {
        this.slaStartDuration = slaStartDuration;
    }

    public int getSlaEndDuration() {
        return slaEndDuration;
    }

    public void setSlaEndDuration(int slaEndDuration) {
        this.slaEndDuration = slaEndDuration;
    }


    public Date getTaskStartBoundary() {
        return taskStartBoundary;
    }

    public void setTaskStartBoundary(Date taskStartBoundary) {
        this.taskStartBoundary = taskStartBoundary;
    }

    public Date getTaskEndBoundary() {
        return taskEndBoundary;
    }

    public void setTaskEndBoundary(Date taskEndBoundary) {
        this.taskEndBoundary = taskEndBoundary;
    }

    public Boolean getMultiStaffTask() {
        return isMultiStaffTask;
    }

    public void setMultiStaffTask(Boolean multiStaffTask) {
        isMultiStaffTask = multiStaffTask;
    }

    public List<Long> getAssignedStaffIds() {

        return Optional.ofNullable(assignedStaffIds).orElse(new ArrayList<>());

    }

    public void setAssignedStaffIds(List<Long> assignedStaffIds) {
            this.assignedStaffIds = assignedStaffIds;
    }

    private Map<String, Object> actualPlanningTask;

    public void setActualPlanningTask(Map<String, Object> actualPlanningTask) {
        this.actualPlanningTask = actualPlanningTask;
    }

    public Map<String, Object> getActualPlanningTask() {

        return actualPlanningTask;
    }

    public int getPreProcessingDuration() {
        return preProcessingDuration;
    }

    public void setPreProcessingDuration(int preProcessingDuration) {
        this.preProcessingDuration = preProcessingDuration;
    }

    public int getPostProcessingDuration() {
        return postProcessingDuration;
    }

    public void setPostProcessingDuration(int postProcessingDuration) {
        this.postProcessingDuration = postProcessingDuration;
    }

    public String getKmdTaskExternalId() {
        return kmdTaskExternalId;
    }

    public void setKmdTaskExternalId(String kmdTaskExternalId) {
        this.kmdTaskExternalId = kmdTaskExternalId;
    }

    public List<SkillExpertise> getSkillExpertiseList() {
        return skillExpertiseList;
    }

    public void setSkillExpertiseList(List<SkillExpertise> skillExpertiseList) {
        this.skillExpertiseList = skillExpertiseList;
    }

    public static Task getInstance() {
        return new Task();
    }

    public static Task copyProperties(Task source, Task target) {
        BeanUtils.copyProperties(source, target);
        return target;
    }

    public boolean isLocationChanged() {
        return locationChanged;
    }

    public void setLocationChanged(boolean locationChanged) {
        this.locationChanged = locationChanged;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ClientException {
        private BigInteger id;
        private Date fromTime;
        private Date toTime;
        private boolean exceptionHandled;
        private BigInteger exceptionTypeId;
        private String name;
        private String value;

        public void setFromTime(Date fromTime) {
            this.fromTime = fromTime;
        }

        public void setToTime(Date toTime) {
            this.toTime = toTime;
        }


        public Date getFromTime() {

            return fromTime;

        }

        public Date getToTime() {
            return toTime;
        }

        public void setId(BigInteger id) {
            this.id = id;
        }

        public BigInteger getId() {

            return id;
        }

        public void setExceptionHandled(boolean exceptionHandled) {
            this.exceptionHandled = exceptionHandled;
        }

        public boolean isExceptionHandled() {

            return exceptionHandled;
        }

        public BigInteger getExceptionTypeId() {
            return exceptionTypeId;
        }

        public void setExceptionTypeId(BigInteger exceptionTypeId) {
            this.exceptionTypeId = exceptionTypeId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    @Override
    public String toString() {
        return "{Task={" +
                "name='" + name + '\'' +
                ", visitourId=" + visitourId +
                ", taskDemandId=" + taskDemandId +
                ", executionDate=" + executionDate +
                ", duration=" + duration +
                ", taskStatus=" + taskStatus +
                ", externalId='" + externalId + '\'' +
                ", timeCareExternalId='" + timeCareExternalId + '\'' +
                ", monacoExterenalId='" + monacoExterenalId + '\'' +
                ", fixedDate=" + fixedDate +
                ", isFixed=" + isFixed +
                ", staffId=" + staffId +
                ", isStaffAnonymous=" + isStaffAnonymous +
                ", isTaskTypeAnonymous=" + isTaskTypeAnonymous +
                ", staffAnonymousId=" + staffAnonymousId +
                ", taskTypeAnonymousId=" + taskTypeAnonymousId +
                ", info1='" + info1 + '\'' +
                ", info2='" + info2 + '\'' +
                ", address=" + address +
                ", dateFrom=" + dateFrom +
                ", dateTo=" + dateTo +
                ", timeFrom=" + timeFrom +
                ", timeTo=" + timeTo +
                ", distance=" + distance +
                ", drivingTime=" + drivingTime +
                ", taskOriginator=" + taskOriginator +
                ", absencePlanningStatus=" + absencePlanningStatus +
                ", startAddress=" + startAddress +
                ", endAddress=" + endAddress +
                ", isActive=" + isActive +
                ", staffAnonymousGender='" + staffAnonymousGender + '\'' +
                ", skills='" + skills + '\'' +
                ", teamId='" + teamId + '\'' +
                ", forbiddenFieldManagerID='" + forbiddenFieldManagerID + '\'' +
                ", preferredFieldmanagerID='" + preferredFieldmanagerID + '\'' +
                ", preferredFieldmanagerID2='" + preferredFieldmanagerID2 + '\'' +
                ", visitourTaskTypeID='" + visitourTaskTypeID + '\'' +
                ", numberOfStaffRequired=" + numberOfStaffRequired +
                ", relatedTaskId='" + relatedTaskId + '\'' +
                ", joinEventId='" + joinEventId + '\'' +
                ", subTaskIds=" + subTaskIds +
                ", isSubTask=" + isSubTask +
                ", prefferedStaffIdsList=" + prefferedStaffIdsList +
                ", forbiddenStaffIdsList=" + forbiddenStaffIdsList +
                ", colorForGantt='" + colorForGantt + '\'' +
                ", lastSyncJobId='" + lastSyncJobId + '\'' +
                ", timeSlotId=" + timeSlotId +
                ", taskDemandVisitId=" + taskDemandVisitId +
                ", hasActualTask=" + hasActualTask +
                ", parentTaskId=" + parentTaskId +
                ", slaStartDuration=" + slaStartDuration +
                ", slaEndDuration=" + slaEndDuration +
                ", taskStartBoundary=" + taskStartBoundary +
                ", taskEndBoundary=" + taskEndBoundary +
                ", isMultiStaffTask=" + isMultiStaffTask +
                ", actualPlanningTask=" + actualPlanningTask +
                '}' +
                '}';
    }
}
