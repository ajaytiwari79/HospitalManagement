package com.kairos.persistence.model.task_type;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.kairos.dto.activity.task_type.TaskTypeDTO;
import com.kairos.enums.task_type.TaskTypeEnum;
import com.kairos.persistence.model.common.MongoBaseEntity;
import com.kairos.view_handler.json_view_handler.TaskTypeViewHandler;
import org.apache.commons.validator.GenericValidator;
import org.joda.time.DateTime;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigInteger;
import java.util.*;

/**
 * Created by prabjot on 4/10/16.
 */
// Task and task demand
@Document(collection = "task_types")
@JsonIgnoreProperties(ignoreUnknown = true)
public class TaskType extends MongoBaseEntity implements Cloneable {

    private  String title;
    private  String shortName;
    private  String uniqueName;
    private  String colorForGantt;
    private  String colorForMap;
    private  String symbolForMap;
    private  boolean uploadPicture;
    private  String generalDescription;
    private  String icon;
    private int duration;



    private  int slaStartDuration;
    private  int slaEndDuration;
    private  int preProcessingDuration;
    private  int postProcessingDuration;
    private  int setupDuration;
    private  boolean isExportedToVisitour;
    private  String sourceTaskId;
    private  String sourceTaskTypeTitle;
    private boolean isEnabled = true;

    Date startPeriod;
    Date endPeriod;
    //cost income tab
    TaskTypeEnum.CostType costType = TaskTypeEnum.CostType.FIXED_COST;
    boolean expiryCost;
    boolean billable;
    boolean waitingTime;
    boolean drivingTime;
    boolean setup;
    boolean overHead;
    boolean flexiTime;
    boolean overNightStay;
    boolean collectiveAgreement;
    boolean distanceRelatedCost;
    private String visitourId;
    private Set<Long> organizationSubTypes;
    private  boolean vehicleRequired;

    public String getVisitourId() {
        return visitourId;
    }

    public void setVisitourId(String visitourId) {
        this.visitourId = visitourId;
    }

    //rules tab
    List<TaskTypeEnum.TaskTypeCreation> creators = Collections.EMPTY_LIST;
    //definition tab
    TaskTypeDefination definations;
    //resting time tab
    int restingHours;
    int restingMinutes;
    @JsonProperty(defaultValue = "children")
    @JsonView(value = TaskTypeViewHandler.JSONViewHandler.class)
    List<TaskTypeSkill> taskTypeSkills = new ArrayList<>();
    //staff types tab
    List<TaskTypeEnum.TaskTypeStaff> assignee = Collections.EMPTY_LIST;
    boolean hasSubcontractors;
    List<TaskTypeEnum.TaskTypeStaff> employees = Collections.EMPTY_LIST;
    //visitation  tab
    boolean onlyVisitatorCanAssignDuration;
    boolean onlyVisitatorCanTaskFrequency;
    boolean onlyVisitatorCanAssignToClients;

    private String description;
    @Indexed
    private Long subServiceId;
    private Date expiresOn;
    private long organizationId;
    private boolean doesHaveRehabTime;
    private DateTime rehabTime;
    private MapPointer mapPointer;
    private String rootId;
    private boolean ofTypeBreak;
    //agreement tab
    private String union;
    private String agreement;
    private long contractTypeId;
    //balance tab
    private String taskTypeVisibility; // Present, Absent
    private List<TaskTypeEnum.TaskTypeCount> taskTypeCount = Collections.EMPTY_LIST;
    private List<TaskTypeEnum.TaskTypeInclude> taskTypeIncluded = Collections.EMPTY_LIST;

    public String getTaskTypeSchedule() {
        return taskTypeSchedule;
    }

    public void setTaskTypeSchedule(String taskTypeSchedule) {
        this.taskTypeSchedule = taskTypeSchedule;
    }

    private String taskTypeSchedule; // Full day, Partially
    private String taskTypeWorkType;
    private String taskTypeTime;
    private boolean multiDayTask;
    private List<TaskTypeEnum.TaskTypeDate> taskTypeDate = Collections.EMPTY_LIST;
    private boolean hasBidding;
    private boolean hasDutyTime;
    //communication tab
    private boolean reminderBySms;
    private boolean notificationBySms;
    private String serviceLevelDays;
    private String serviceLevelHours;
    //dependencies tab
    private boolean hasFinishToStart;

    private List<Long> organizationTypes;
    private List<Long> organizationServices;

    private Long teamId;

    private boolean isImportedToVisitour;
    private List<Long> organizations;

    private Long countryId;

    private boolean hasDateOfChange;
    private boolean hasDateOfCreation;
    //main task tab
    private boolean isMainTask;
    private boolean hasCompositeShift;
    private List<String> subTask;
    //notification tab
    private boolean isArrival;
    private int hours;
    private int minutes;
    //planner rules tab
    private boolean isAssignedToClipBoard;
    private boolean useInShiftPlanning;
    private List<TaskTypeEnum.ShiftPlanningPhase> shiftPlanningPhases = Collections.EMPTY_LIST;
    //pointMethods tab
    private List<TaskTypeEnum.Points> pointMethods = Collections.EMPTY_LIST;
    //resource tab
//    private List<Long> resources;
    private List<TaskTypeResource> resources = new ArrayList<>();
    //time frames tab
    private TaskTypeEnum.SequenceGroup sequenceGroup;
    private TaskTypeEnum.CauseGroup causeGroup;
    private List<Long> forbiddenDayTypeIds = Collections.EMPTY_LIST;
    private boolean firstVisit;
    private boolean lastVisit;
    private boolean clientPresenceRequired;
    private boolean deliverOutsideUnitHours;
    //time rules tab
    private boolean isCalculateEndTime;
    private TaskTypeEnum.DurationType durationType;
    private List<TaskTypeEnum.TimeTypes> timeTypes = new ArrayList<>();
    private boolean isDivideByWeeklyHours;
    private int weeklyHours;
    private boolean isDivideByFullTime;
    private int fullTimeHours;
    private boolean isTaskResumable;

    //logging/history tab
    private boolean reduceBreakTime;
    private int fixedLengthDuration;
    private int multiplier;
    private String dayType;

    //time care external id
    private String externalId;

    // Expertise required for taskType
    private List<Long> expertiseIds;

    private Integer minutesBeforeSend;

    private Integer minutesBeforeArrival;

    private String notificationSendBeforeArrivalTime;

    private List<BigInteger> tags = new ArrayList<>();

    public List<Long> getExpertiseIds() {
        return expertiseIds;
    }

    public void setExpertiseIds(List<Long> expertiseIds) {
        this.expertiseIds = expertiseIds;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public TaskType(String title, String description, Long subServiceId, Date expiresOn,int duration) {
        this.title = title;
        this.description = description;
        this.subServiceId = subServiceId;
        this.expiresOn = expiresOn;
        this.duration = duration;
    }
    public boolean isExportedToVisitour() {
        return isExportedToVisitour;
    }

    public String getIcon() {
        return icon;
    }

    public int getSetupDuration() {
        return setupDuration;
    }

    public void setSetupDuration(int setupDuration) {
        this.setupDuration = setupDuration;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public void setExportedToVisitour(boolean exportedToVisitour) {
        isExportedToVisitour = exportedToVisitour;
    }
    public String getTaskTypeTime() {
        return taskTypeTime;
    }

    public void setTaskTypeTime(String taskTypeTime) {
        this.taskTypeTime = taskTypeTime;
    }


    public TaskType() {

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

    public int getPreProcessingDuration() {
        return preProcessingDuration;
    }

    public void setPreProcessingDuration(int preProcessingDuration) {
        this.preProcessingDuration = preProcessingDuration;
    }

    public String getColorForGantt() {
        return colorForGantt;
    }

    public int getPostProcessingDuration() {
        return postProcessingDuration;
    }

    public void setPostProcessingDuration(int postProcessingDuration) {
        this.postProcessingDuration = postProcessingDuration;
    }

    public void setColorForGantt(String colorForGantt) {
        this.colorForGantt = colorForGantt;
    }

    public String getShortName() {
        return shortName;
    }

    public boolean isOnlyVisitatorCanTaskFrequency() {
        return onlyVisitatorCanTaskFrequency;
    }

    public void setOnlyVisitatorCanTaskFrequency(boolean onlyVisitatorCanTaskFrequency) {
        this.onlyVisitatorCanTaskFrequency = onlyVisitatorCanTaskFrequency;
    }

    public boolean isOfTypeBreak() {
        return ofTypeBreak;
    }

    public void setOfTypeBreak(boolean ofTypeBreak) {
        this.ofTypeBreak = ofTypeBreak;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public static TaskType getInstance() {
        return new TaskType();
    }

    public boolean isDoesHaveRehabTime() {
        return doesHaveRehabTime;
    }

    public void setDoesHaveRehabTime(boolean doesHaveRehabTime) {
        this.doesHaveRehabTime = doesHaveRehabTime;
    }

    public DateTime getRehabTime() {
        return rehabTime;
    }

    public void setRehabTime(DateTime rehabTime) {
        this.rehabTime = rehabTime;
    }

    public boolean isVehicleRequired() {
        return vehicleRequired;
    }

    public void setVehicleRequired(boolean vehicleRequired) {
        this.vehicleRequired = vehicleRequired;
    }

    public TaskTypeDTO getBasicTaskTypeInfo() {
        TaskTypeDTO taskTypeDTO = new TaskTypeDTO();
        taskTypeDTO.setId(this.getId());
        taskTypeDTO.setDescription(this.description);
        taskTypeDTO.setDuration(this.duration);
        taskTypeDTO.setExpiresOn(this.expiresOn);
        taskTypeDTO.setTags(this.tags);
        taskTypeDTO.setParentTaskTypeId(this.rootId);
        taskTypeDTO.setServiceId(this.subServiceId);
        taskTypeDTO.setTitle(this.title);
        return taskTypeDTO;
    }

    public void saveAgreementSettings(String union, String agreement, Date startPeriod, Date endPeriod) {
        this.union = union;
        this.agreement = agreement;
        this.startPeriod = startPeriod;
        this.endPeriod = endPeriod;
    }

    public Map<String, Object> getAgreementSettings() {
        Map<String, Object> map = new HashMap<>(4);
        map.put("union", this.union);
        map.put("agreement", this.agreement);
        map.put("startPeriod", this.startPeriod);
        map.put("endPeriod", this.endPeriod);
        return map;
    }

    public void setTaskTypeCount(List<TaskTypeEnum.TaskTypeCount> taskTypeCount) {
        this.taskTypeCount = taskTypeCount;
    }

    public void setTaskTypeIncluded(List<TaskTypeEnum.TaskTypeInclude> taskTypeIncluded) {
        this.taskTypeIncluded = taskTypeIncluded;
    }

    public Long getSubServiceId() {
        return subServiceId;
    }

    public void setSubServiceId(Long subServiceId) {
        this.subServiceId = subServiceId;
    }

    public MapPointer getMapPointer() {
        return mapPointer;
    }

    public void setMapPointer(MapPointer mapPointer) {
        this.mapPointer = mapPointer;
    }

    public void setTaskTypeDate(List<TaskTypeEnum.TaskTypeDate> taskTypeDate) {
        this.taskTypeDate = taskTypeDate;
    }

    public String getTaskTypeVisibility() {
        return taskTypeVisibility;
    }

    public void setTaskTypeVisibility(String taskTypeVisibility) {
        this.taskTypeVisibility = taskTypeVisibility;
    }

    public void saveBalanceSettings(Map<String, Object> balanceSettings) {
        this.taskTypeVisibility = (String) balanceSettings.get("taskTypeVisibility");
        this.taskTypeSchedule = (String) balanceSettings.get("taskTypeSchedule");
        this.taskTypeWorkType = (String) balanceSettings.get("taskTypeWorkType");
        this.taskTypeTime = (String) balanceSettings.get("timeTypes");
        this.hasBidding = balanceSettings.get("hasBidding") != null && (boolean) balanceSettings.get("hasBidding");
        this.hasDutyTime = balanceSettings.get("hasDutyTime") != null && (boolean) balanceSettings.get("hasDutyTime");
        this.multiDayTask = balanceSettings.get("multiDayTask") != null && (boolean) balanceSettings.get("multiDayTask");
    }

    public Map<String, Object> getBalanceSettings() {
        Map<String, Object> map = new HashMap<>();
        map.put("taskTypeVisibility", this.taskTypeVisibility);
        List<String> taskTypeCounts = new ArrayList<>();
        for (TaskTypeEnum.TaskTypeCount taskTypeCount : this.taskTypeCount) {
            taskTypeCounts.add(taskTypeCount.value);
        }

        List<String> taskTypeIncludes = new ArrayList<>();
        for (TaskTypeEnum.TaskTypeInclude taskTypeInclude : this.taskTypeIncluded) {
            taskTypeIncludes.add(taskTypeInclude.value);
        }

        List<String> taskTypeDates = new ArrayList<>();
        for (TaskTypeEnum.TaskTypeDate taskTypeDate : this.taskTypeDate) {
            taskTypeDates.add(taskTypeDate.value);
        }
        map.put("taskTypeCount", taskTypeCounts);
        map.put("taskTypeIncluded", taskTypeIncludes);
        map.put("taskTypeSchedule", this.taskTypeSchedule);
        map.put("taskTypeWorkType", this.taskTypeWorkType);
        map.put("timeTypes", this.taskTypeTime);
        map.put("multiDayTask", this.multiDayTask);
        map.put("hasBidding", this.hasBidding);
        map.put("hasDutyTime", this.hasDutyTime);
        map.put("taskTypeDate", taskTypeDates);
        return map;
    }

    public void saveCommunicationSettings(boolean reminderBySms, boolean notificationBySms, String minutesBeforeSend, String minutesBeforeArrival, boolean isArrival, String time) {
        this.reminderBySms = reminderBySms;
        this.notificationBySms = notificationBySms;

        if(GenericValidator.isInt(minutesBeforeSend)){
            this.minutesBeforeSend = Integer.parseInt(minutesBeforeSend);
        }else{
            this.minutesBeforeSend = 0;
        }
        if( GenericValidator.isInt(minutesBeforeSend)){
            this.minutesBeforeArrival = Integer.parseInt(minutesBeforeArrival);
        }else{
            this.minutesBeforeArrival = 0;
        }
        this.isArrival = isArrival;
        this.notificationSendBeforeArrivalTime = time;
    }

    public Map<String, Object> getCommunicationSettings() {
        Map<String, Object> map = new HashMap<>(6);
        map.put("reminderBySms", this.reminderBySms);
        map.put("notificationBySms", this.notificationBySms);
        map.put("minutesBeforeSend", this.minutesBeforeSend);
        map.put("minutesBeforeArrival", this.minutesBeforeArrival);
        map.put("reminderBySmsBeforeArrival", this.isArrival);
        map.put("time", this.notificationSendBeforeArrivalTime);
        return map;
    }

    public String getRootId() {
        return rootId;
    }

    public void setRootId(String rootId) {
        this.rootId = rootId;
    }

    public void saveIncomeCostSettings(Map<String, Object> incomeSettings) {
        this.costType = TaskTypeEnum.CostType.getByValue((String) incomeSettings.get("costType"));
        this.expiryCost = incomeSettings.get("expiryCost") != null && (boolean) incomeSettings.get("expiryCost");
        this.billable = incomeSettings.get("billable") != null && (boolean) incomeSettings.get("billable");
        this.waitingTime = incomeSettings.get("waitingTime") != null && (boolean) incomeSettings.get("waitingTime");
        this.drivingTime = incomeSettings.get("drivingTime") != null && (boolean) incomeSettings.get("drivingTime");
        this.setup = incomeSettings.get("setup") != null && (boolean) incomeSettings.get("setup");
        this.overHead = incomeSettings.get("overHead") != null && (boolean) incomeSettings.get("overHead");
        this.flexiTime = incomeSettings.get("flexiTime") != null && (boolean) incomeSettings.get("flexiTime");
        this.overNightStay = incomeSettings.get("overNightStay") != null && (boolean) incomeSettings.get("overNightStay");
        this.collectiveAgreement = incomeSettings.get("collectiveAgreement") != null && (boolean) incomeSettings.get("collectiveAgreement");
        this.distanceRelatedCost = incomeSettings.get("distanceRelatedCost") != null && (boolean) incomeSettings.get("distanceRelatedCost");
    }

    public Map<String, Object> getIncomeSettings() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("costType", this.costType.value);
        map.put("expiryCost", this.expiryCost);
        map.put("billable", this.billable);
        map.put("waitingTime", this.waitingTime);
        map.put("drivingTime", this.drivingTime);
        map.put("setup", this.setup);
        map.put("overHead", this.overHead);
        map.put("flexiTime", this.flexiTime);
        map.put("overNightStay", this.overNightStay);
        map.put("collectiveAgreement", this.collectiveAgreement);
        map.put("distanceRelatedCost", this.distanceRelatedCost);
        return map;
    }

    public List<TaskTypeEnum.TaskTypeCreation> getCreators() {
        return creators;
    }

    public void setCreators(List<TaskTypeEnum.TaskTypeCreation> creators) {
        this.creators = creators;
    }

    public TaskTypeDefination getDefinations() {
        return definations;
    }

    public void setDefinations(TaskTypeDefination definations) {
        this.definations = definations;
    }

    public boolean isHasFinishToStart() {
        return hasFinishToStart;
    }

    public void setHasFinishToStart(boolean hasFinishToStart) {
        this.hasFinishToStart = hasFinishToStart;
    }


    public void setSourceTaskId(String sourceTaskId) {
        this.sourceTaskId = sourceTaskId;
    }

    public void setSourceTaskTypeTitle(String sourceTaskTypeTitle) {
        this.sourceTaskTypeTitle = sourceTaskTypeTitle;
    }

    public void saveGeneralSettings(Map<String, Object> map) {
        this.duration = (int)map.get("duration");
        this.title = (String) map.get("title");
        this.uniqueName = (String) map.get("uniqueName");
        this.colorForGantt = (String) map.get("colorForGantt");
        this.colorForMap = (String) map.get("colorForMap");
        this.shortName = (String) map.get("shortName");
        this.generalDescription = (String) map.get("generalDescription");
        this.icon = (String) map.get("icon");
        this.organizationTypes = (List<Long>) map.get("organizationTypes");
        this.organizationServices = (List<Long>) map.get("organizationServices");
        this.organizations = (List<Long>) map.get("organizations");
        this.symbolForMap = (String) map.get("symbolForMap");
        this.visitourId = (String) map.get("visitourId");
        this.externalId = (String) map.get("externalId");
    }

    public Map<String, Object> getGeneralSettings(String filePath) {
        Map<String, Object> map = new HashMap<>();
        map.put("duration",this.duration);
        map.put("title", this.title);
        //  map.put("uniqueName", this.uniqueName);
        map.put("colorForGantt", this.colorForGantt);
        map.put("colorForMap", this.colorForMap);
        map.put("shortName", this.shortName);
        map.put("generalDescription", this.generalDescription);
        //   map.put("uploadPicture", this.uploadPicture);
        map.put("icon",  this.icon);
        map.put("filePath",  filePath);
        map.put("organizationTypes", this.organizationTypes);
        map.put("organizationServices", this.organizationServices);
        map.put("isExportedToVisitour", this.isExportedToVisitour);
        map.put("isImportedToVisitour", this.isImportedToVisitour);
        map.put("organizations", this.organizations);
        map.put("symbolForMap", this.symbolForMap);
        map.put("countryId", this.countryId);
        map.put("sourceTaskId", this.sourceTaskId);
        map.put("sourceTaskTypeTitle", this.sourceTaskTypeTitle);
        map.put("visitourId",this.visitourId);
        map.put("externalId",this.externalId);
        //  map.put("slaEndDuration",this.slaEndDuration);
        //  map.put("slaStartDuration", this.slaStartDuration);
        //  map.put("preProcessingDuration", this.preProcessingDuration);
        //  map.put("postProcessingDuration", this.postProcessingDuration);
        //  map.put("setupDuration", this.setupDuration);
        return map;
    }

    public void saveLoggingSettings(boolean hasDateOfChange, boolean hasDateOfCreation) {
        this.hasDateOfChange = hasDateOfChange;
        this.hasDateOfCreation = hasDateOfCreation;
    }

    public Map<String, Object> getLoggingSettings() {
        Map<String, Object> map = new HashMap<>(2);
        map.put("hasDateOfCreation", this.hasDateOfCreation);
        map.put("hasDateOfChange", this.hasDateOfChange);
        return map;
    }

    public void saveMainTaskSettings(boolean isMainTask, boolean hasCompositeShift, List<String> subTask) {
        this.isMainTask = isMainTask;
        this.hasCompositeShift = hasCompositeShift;
        this.subTask = subTask;
    }

    public Map<String, Object> getMainTaskSettings() {
        Map<String, Object> map = new HashMap<>();
        map.put("isMainTask", this.isMainTask);
        map.put("hasCompositeShift", this.hasCompositeShift);
        map.put("finishToStart", this.hasFinishToStart);
        map.put("subTask", this.subTask);
        return map;
    }

    public boolean isArrival() {
        return isArrival;
    }

    public void setArrival(boolean arrival) {
        isArrival = arrival;
    }

    public int getHours() {
        return hours;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public void savePlanningRules(boolean isAssignedToClipBoard, boolean useInShiftPlanning, List<TaskTypeEnum.ShiftPlanningPhase> shiftPlanningPhases) {

        this.isAssignedToClipBoard = isAssignedToClipBoard;
        this.useInShiftPlanning = useInShiftPlanning;
        this.shiftPlanningPhases = shiftPlanningPhases;

    }

    public boolean isAssignedToClipBoard() {
        return isAssignedToClipBoard;
    }

    public boolean isUseInShiftPlanning() {
        return useInShiftPlanning;
    }

    public List<TaskTypeEnum.ShiftPlanningPhase> getShiftPlanningPhases() {
        return shiftPlanningPhases;
    }

    public List<TaskTypeEnum.Points> getPointMethods() {
        return pointMethods;
    }

    public void setPointMethods(List<TaskTypeEnum.Points> pointMethods) {
        this.pointMethods = pointMethods;
    }



    public int getRestingHours() {
        return restingHours;
    }

    public void setRestingHours(int restingHours) {
        this.restingHours = restingHours;
    }

    public int getRestingMinutes() {
        return restingMinutes;
    }

    public void setRestingMinutes(int restingMinutes) {
        this.restingMinutes = restingMinutes;
    }

    public List<TaskTypeSkill> getTaskTypeSkills() {
        return taskTypeSkills;
    }

    public void setTaskTypeSkills(List<TaskTypeSkill> taskTypeSkills) {
        this.taskTypeSkills = taskTypeSkills;
    }

    public void saveTaskTypeStaff(List<TaskTypeEnum.TaskTypeStaff> assignee, boolean hasSubcontractors, List<TaskTypeEnum.TaskTypeStaff> employees) {
        this.assignee = assignee;
        this.hasSubcontractors = hasSubcontractors;
        this.employees = employees;
    }

    public List<TaskTypeEnum.TaskTypeStaff> getAssignee() {
        return assignee;
    }

    public List<TaskTypeEnum.TaskTypeStaff> getEmployees() {
        return employees;
    }

    public boolean isHasSubcontractors() {
        return hasSubcontractors;
    }

    public void saveTimeFrames(List<Long> forbiddenDayTypeIds, boolean clientPresenceRequired, boolean deliverOutsideUnitHours) {

        this.forbiddenDayTypeIds = forbiddenDayTypeIds;
        this.clientPresenceRequired = clientPresenceRequired;
        this.deliverOutsideUnitHours = deliverOutsideUnitHours;

    }

    public TaskTypeEnum.SequenceGroup getSequenceGroup() {
        return sequenceGroup;
    }

    public TaskTypeEnum.CauseGroup getCauseGroup() {
        return causeGroup;
    }

    public List<Long> getForbiddenDayTypeIds() {
        return forbiddenDayTypeIds;
    }

    public boolean isFirstVisit() {
        return firstVisit;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isLastVisit() {
        return lastVisit;
    }


    public void saveTimeRules(TaskTypeEnum.DurationType durationType, List<TaskTypeEnum.TimeTypes> timeTypes, Map<String, Object> data) {

        this.isCalculateEndTime = (boolean) data.get("isCalculateEndTime");
        this.durationType = durationType;
        this.timeTypes = timeTypes;
        this.isDivideByWeeklyHours =  (boolean) data.get("isDivideByWeeklyHours");
        this.weeklyHours = (int) data.get("weeklyHours");
        this.isDivideByFullTime = (boolean) data.get("isDivideByFullTime");
        this.fullTimeHours = (int) data.get("fullTimeHours");
        this.fixedLengthDuration =  (int) data.get("fixedLengthDuration");
        this.multiplier = (int) data.get("multiplier");
        this.dayType = (String) data.get("dayType");
        this.reduceBreakTime =  (boolean) data.get("reduceBreakTime");
        this.isTaskResumable = (boolean) data.get("isTaskResumable");
        if(data.get("preProcessingDuration") != null){
            this.preProcessingDuration = (Integer) data.get("preProcessingDuration");
        }else{
            this.preProcessingDuration = 0;
        }
        if(data.get("postProcessingDuration") != null){
            this.postProcessingDuration = (Integer) data.get("postProcessingDuration");
        }else{
            this.postProcessingDuration = 0;
        }
        if(data.get("setupDuration") != null) {
            this.setupDuration = (Integer) data.get("setupDuration");
        }else{
            this.setupDuration = 0;
        }
    }

    public Map<String, Object> retrieveTimeRules() {

        List<String> timeTypesList = new ArrayList<>(this.timeTypes.size());
        for (TaskTypeEnum.TimeTypes timeType : this.timeTypes) {
            timeTypesList.add(timeType.value);
        }

        Map<String, Object> map = new HashMap<>();
        map.put("isCalculateEndTime", this.isCalculateEndTime);
        map.put("durationType", (this.durationType==null)?null:this.durationType.value);
        map.put("timeTypes", timeTypesList);
        map.put("isDivideByWeeklyHours", this.isDivideByWeeklyHours);
        map.put("weeklyHours", this.weeklyHours);
        map.put("isDivideByFullTime", this.isDivideByFullTime);
        map.put("fullTimeHours", this.fullTimeHours);
        map.put("fixedLengthDuration", this.fixedLengthDuration);
        map.put("multiplier", this.multiplier);
        map.put("dayType", this.dayType);
        map.put("reduceBreakTime", this.reduceBreakTime);
        map.put("isTaskResumable",this.isTaskResumable);
        map.put("preProcessingDuration", this.preProcessingDuration);
        map.put("postProcessingDuration", this.postProcessingDuration);
        map.put("setupDuration", this.setupDuration);
        return map;
    }








    public void saveVisitationSettings(boolean onlyVisitatorCanAssignDuration, boolean onlyVisitatorCanTaskFrequency, boolean onlyVisitatorCanAssignToClients) {
        this.onlyVisitatorCanAssignDuration = onlyVisitatorCanAssignDuration;
        this.onlyVisitatorCanTaskFrequency = onlyVisitatorCanTaskFrequency;
        this.onlyVisitatorCanAssignToClients = onlyVisitatorCanAssignToClients;
    }

    public Map<String, Boolean> getVisitationSettings() {
        Map<String, Boolean> map = new HashMap<>(4);
        map.put("onlyVisitatorCanAssignDuration", this.onlyVisitatorCanAssignDuration);
        map.put("onlyVisitatorCanTaskFrequency", this.onlyVisitatorCanTaskFrequency);
        map.put("onlyVisitatorCanAssignToClients", this.onlyVisitatorCanAssignToClients);
        return map;
    }

    // copy task settings
    public TaskType copyAllSettings(TaskType taskType) {
        this.title = taskType.title;
        this.description = taskType.description;
        this.union = taskType.union;
        this.agreement = taskType.agreement;
        this.startPeriod = taskType.startPeriod;
        this.endPeriod = taskType.endPeriod;
        this.taskTypeVisibility = taskType.taskTypeVisibility;
        this.taskTypeCount = taskType.taskTypeCount;
        this.taskTypeIncluded = taskType.taskTypeIncluded;
        this.taskTypeSchedule = taskType.taskTypeSchedule;
        this.taskTypeWorkType = taskType.taskTypeWorkType;
        this.taskTypeTime = taskType.taskTypeTime;
        this.multiDayTask = taskType.multiDayTask;
        this.taskTypeDate = taskType.taskTypeDate;
        this.hasBidding = taskType.hasDutyTime;
        this.reminderBySms = taskType.reminderBySms;
        this.notificationBySms = taskType.notificationBySms;
        this.serviceLevelDays = taskType.serviceLevelDays;
        this.serviceLevelHours = taskType.serviceLevelHours;
        this.costType = taskType.costType;
        this.expiryCost = taskType.expiryCost;
        this.billable = taskType.billable;
        this.waitingTime = taskType.waitingTime;
        this.drivingTime = taskType.drivingTime;
        this.setup = taskType.setup;
        this.overHead = taskType.overHead;
        this.flexiTime = taskType.flexiTime;
        this.overNightStay = taskType.overNightStay;
        this.collectiveAgreement = taskType.collectiveAgreement;
        this.distanceRelatedCost = taskType.distanceRelatedCost;
        this.creators = taskType.creators;
        this.definations = taskType.definations;
        this.hasFinishToStart = taskType.hasFinishToStart;
        this.uniqueName = taskType.uniqueName;
        this.colorForGantt = taskType.colorForGantt;
        this.colorForMap = taskType.colorForMap;
        this.generalDescription = taskType.generalDescription;
        this.uploadPicture = taskType.uploadPicture;
        this.icon = taskType.icon;
        this.organizationTypes = taskType.organizationTypes;
        this.organizationServices = taskType.organizationServices;
        this.isExportedToVisitour = taskType.isExportedToVisitour;
        this.isImportedToVisitour = taskType.isImportedToVisitour;
        this.organizations = taskType.organizations;
        this.hasDateOfCreation = taskType.hasDateOfCreation;
        this.hasDateOfChange = taskType.hasDateOfChange;
        this.isMainTask = taskType.isMainTask;
        this.hasCompositeShift = taskType.hasCompositeShift;
        this.isArrival = taskType.isArrival;
        this.minutes = taskType.minutes;
        this.hours = taskType.hours;
        this.isAssignedToClipBoard = taskType.isAssignedToClipBoard;
        this.useInShiftPlanning = taskType.useInShiftPlanning;
        this.shiftPlanningPhases = taskType.shiftPlanningPhases;
        this.pointMethods = taskType.pointMethods;
        this.restingHours = taskType.restingHours;
        this.restingMinutes = taskType.restingMinutes;
        //this.taskTypeSkillCategories = taskType.taskTypeSkillCategories;
        this.hasSubcontractors = taskType.hasSubcontractors;
        this.employees = taskType.employees;
        this.sequenceGroup = taskType.sequenceGroup;
        this.causeGroup = taskType.causeGroup;
        this.forbiddenDayTypeIds = taskType.forbiddenDayTypeIds;
        this.deliverOutsideUnitHours = taskType.deliverOutsideUnitHours;
        this.firstVisit = taskType.firstVisit;
        this.lastVisit = taskType.lastVisit;
        this.isCalculateEndTime = taskType.isCalculateEndTime;
        this.durationType = taskType.durationType;
        this.timeTypes = taskType.timeTypes;
        this.isDivideByWeeklyHours = taskType.isDivideByWeeklyHours;
        this.weeklyHours = taskType.weeklyHours;
        this.isDivideByFullTime = taskType.isDivideByFullTime;
        this.fullTimeHours = taskType.fullTimeHours;
        this.reduceBreakTime = taskType.reduceBreakTime;
        this.fixedLengthDuration = taskType.fixedLengthDuration;
        this.multiplier = taskType.multiplier;
        this.dayType = taskType.dayType;
        this.onlyVisitatorCanAssignDuration = taskType.onlyVisitatorCanAssignDuration;
        this.onlyVisitatorCanTaskFrequency = taskType.onlyVisitatorCanTaskFrequency;
        this.onlyVisitatorCanAssignToClients = taskType.onlyVisitatorCanAssignToClients;
        this.isTaskResumable = taskType.isTaskResumable;
        this.visitourId = taskType.visitourId;
        return this;
    }

    public String getUniqueName() {
        return uniqueName;
    }

    public void setUniqueName(String uniqueName) {
        this.uniqueName = uniqueName;
    }

    public String getColorForMap() {
        return colorForMap;
    }

    public void setColorForMap(String colorForMap) {
        this.colorForMap = colorForMap;
    }

    public String getSymbolForMap() {
        return symbolForMap;
    }

    public void setSymbolForMap(String symbolForMap) {
        this.symbolForMap = symbolForMap;
    }

    public boolean isUploadPicture() {
        return uploadPicture;
    }

    public void setUploadPicture(boolean uploadPicture) {
        this.uploadPicture = uploadPicture;
    }

    public String getGeneralDescription() {
        return generalDescription;
    }

    public void setGeneralDescription(String generalDescription) {
        this.generalDescription = generalDescription;
    }

    public String getSourceTaskId() {
        return sourceTaskId;
    }

    public String getSourceTaskTypeTitle() {
        return sourceTaskTypeTitle;
    }

    public Date getStartPeriod() {
        return startPeriod;
    }

    public void setStartPeriod(Date startPeriod) {
        this.startPeriod = startPeriod;
    }

    public Date getEndPeriod() {
        return endPeriod;
    }

    public void setEndPeriod(Date endPeriod) {
        this.endPeriod = endPeriod;
    }

    public TaskTypeEnum.CostType getCostType() {
        return costType;
    }

    public void setCostType(TaskTypeEnum.CostType costType) {
        this.costType = costType;
    }

    public boolean isExpiryCost() {
        return expiryCost;
    }

    public void setExpiryCost(boolean expiryCost) {
        this.expiryCost = expiryCost;
    }

    public boolean isBillable() {
        return billable;
    }

    public void setBillable(boolean billable) {
        this.billable = billable;
    }

    public boolean isWaitingTime() {
        return waitingTime;
    }

    public void setWaitingTime(boolean waitingTime) {
        this.waitingTime = waitingTime;
    }

    public boolean isDrivingTime() {
        return drivingTime;
    }

    public void setDrivingTime(boolean drivingTime) {
        this.drivingTime = drivingTime;
    }

    public boolean isSetup() {
        return setup;
    }

    public void setSetup(boolean setup) {
        this.setup = setup;
    }

    public boolean isOverHead() {
        return overHead;
    }

    public void setOverHead(boolean overHead) {
        this.overHead = overHead;
    }

    public boolean isFlexiTime() {
        return flexiTime;
    }

    public void setFlexiTime(boolean flexiTime) {
        this.flexiTime = flexiTime;
    }

    public boolean isOverNightStay() {
        return overNightStay;
    }

    public void setOverNightStay(boolean overNightStay) {
        this.overNightStay = overNightStay;
    }

    public boolean isCollectiveAgreement() {
        return collectiveAgreement;
    }

    public void setCollectiveAgreement(boolean collectiveAgreement) {
        this.collectiveAgreement = collectiveAgreement;
    }

    public boolean isDistanceRelatedCost() {
        return distanceRelatedCost;
    }

    public void setDistanceRelatedCost(boolean distanceRelatedCost) {
        this.distanceRelatedCost = distanceRelatedCost;
    }

    public void setAssignee(List<TaskTypeEnum.TaskTypeStaff> assignee) {
        this.assignee = assignee;
    }

    public void setHasSubcontractors(boolean hasSubcontractors) {
        this.hasSubcontractors = hasSubcontractors;
    }

    public void setEmployees(List<TaskTypeEnum.TaskTypeStaff> employees) {
        this.employees = employees;
    }

    public boolean isOnlyVisitatorCanAssignDuration() {
        return onlyVisitatorCanAssignDuration;
    }

    public void setOnlyVisitatorCanAssignDuration(boolean onlyVisitatorCanAssignDuration) {
        this.onlyVisitatorCanAssignDuration = onlyVisitatorCanAssignDuration;
    }

    public boolean isOnlyVisitatorCanAssignToClients() {
        return onlyVisitatorCanAssignToClients;
    }

    public void setOnlyVisitatorCanAssignToClients(boolean onlyVisitatorCanAssignToClients) {
        this.onlyVisitatorCanAssignToClients = onlyVisitatorCanAssignToClients;
    }

    public String getUnion() {
        return union;
    }

    public void setUnion(String union) {
        this.union = union;
    }

    public String getAgreement() {
        return agreement;
    }

    public void setAgreement(String agreement) {
        this.agreement = agreement;
    }

    public List<TaskTypeEnum.TaskTypeCount> getTaskTypeCount() {
        return taskTypeCount;
    }

    public List<TaskTypeEnum.TaskTypeInclude> getTaskTypeIncluded() {
        return taskTypeIncluded;
    }

    public String getTaskTypeWorkType() {
        return taskTypeWorkType;
    }

    public void setTaskTypeWorkType(String taskTypeWorkType) {
        this.taskTypeWorkType = taskTypeWorkType;
    }

    public boolean isMultiDayTask() {
        return multiDayTask;
    }

    public void setMultiDayTask(boolean multiDayTask) {
        this.multiDayTask = multiDayTask;
    }

    public List<TaskTypeEnum.TaskTypeDate> getTaskTypeDate() {
        return taskTypeDate;
    }

    public boolean isHasBidding() {
        return hasBidding;
    }

    public void setHasBidding(boolean hasBidding) {
        this.hasBidding = hasBidding;
    }

    public boolean isHasDutyTime() {
        return hasDutyTime;
    }

    public void setHasDutyTime(boolean hasDutyTime) {
        this.hasDutyTime = hasDutyTime;
    }

    public boolean isReminderBySms() {
        return reminderBySms;
    }

    public void setReminderBySms(boolean reminderBySms) {
        this.reminderBySms = reminderBySms;
    }

    public boolean isNotificationBySms() {
        return notificationBySms;
    }

    public void setNotificationBySms(boolean notificationBySms) {
        this.notificationBySms = notificationBySms;
    }

    public String getServiceLevelDays() {
        return serviceLevelDays;
    }

    public void setServiceLevelDays(String serviceLevelDays) {
        this.serviceLevelDays = serviceLevelDays;
    }

    public String getServiceLevelHours() {
        return serviceLevelHours;
    }

    public void setServiceLevelHours(String serviceLevelHours) {
        this.serviceLevelHours = serviceLevelHours;
    }

    public List<Long> getOrganizationTypes() {
        return organizationTypes;
    }

    public void setOrganizationTypes(List<Long> organizationTypes) {
        this.organizationTypes = organizationTypes;
    }

    public List<Long> getOrganizationServices() {
        return organizationServices;
    }

    public void setOrganizationServices(List<Long> organizationServices) {
        this.organizationServices = organizationServices;
    }

    public boolean isImportedToVisitour() {
        return isImportedToVisitour;
    }

    public void setImportedToVisitour(boolean importedToVisitour) {
        isImportedToVisitour = importedToVisitour;
    }

    public List<Long> getOrganizations() {
        return organizations;
    }

    public void setOrganizations(List<Long> organizations) {
        this.organizations = organizations;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }

    public boolean isHasDateOfChange() {
        return hasDateOfChange;
    }

    public void setHasDateOfChange(boolean hasDateOfChange) {
        this.hasDateOfChange = hasDateOfChange;
    }

    public boolean isHasDateOfCreation() {
        return hasDateOfCreation;
    }

    public void setHasDateOfCreation(boolean hasDateOfCreation) {
        this.hasDateOfCreation = hasDateOfCreation;
    }

    public boolean isMainTask() {
        return isMainTask;
    }

    public void setMainTask(boolean mainTask) {
        isMainTask = mainTask;
    }

    public boolean isHasCompositeShift() {
        return hasCompositeShift;
    }

    public void setHasCompositeShift(boolean hasCompositeShift) {
        this.hasCompositeShift = hasCompositeShift;
    }

    public List<String> getSubTask() {
        return subTask;
    }

    public void setSubTask(List<String> subTask) {
        this.subTask = subTask;
    }

    public void setAssignedToClipBoard(boolean assignedToClipBoard) {
        isAssignedToClipBoard = assignedToClipBoard;
    }

    public void setUseInShiftPlanning(boolean useInShiftPlanning) {
        this.useInShiftPlanning = useInShiftPlanning;
    }

    public void setShiftPlanningPhases(List<TaskTypeEnum.ShiftPlanningPhase> shiftPlanningPhases) {
        this.shiftPlanningPhases = shiftPlanningPhases;
    }

    public void setResources(List<TaskTypeResource> resources) {
        this.resources = resources;
    }

    public void setSequenceGroup(TaskTypeEnum.SequenceGroup sequenceGroup) {
        this.sequenceGroup = sequenceGroup;
    }

    public void setCauseGroup(TaskTypeEnum.CauseGroup causeGroup) {
        this.causeGroup = causeGroup;
    }

    public void setForbiddenDayTypeIds(List<Long> forbiddenDayTypeIds) {
        this.forbiddenDayTypeIds = forbiddenDayTypeIds;
    }

    public void setFirstVisit(boolean firstVisit) {
        this.firstVisit = firstVisit;
    }

    public void setLastVisit(boolean lastVisit) {
        this.lastVisit = lastVisit;
    }

    public void setClientPresenceRequired(boolean clientPresenceRequired) {
        this.clientPresenceRequired = clientPresenceRequired;
    }

    public void setDeliverOutsideUnitHours(boolean deliverOutsideUnitHours) {
        this.deliverOutsideUnitHours = deliverOutsideUnitHours;
    }

    public boolean isCalculateEndTime() {
        return isCalculateEndTime;
    }

    public void setCalculateEndTime(boolean calculateEndTime) {
        isCalculateEndTime = calculateEndTime;
    }

    public TaskTypeEnum.DurationType getDurationType() {
        return durationType;
    }

    public void setDurationType(TaskTypeEnum.DurationType durationType) {
        this.durationType = durationType;
    }

    public List<TaskTypeEnum.TimeTypes> getTimeTypes() {
        return timeTypes;
    }

    public void setTimeTypes(List<TaskTypeEnum.TimeTypes> timeTypes) {
        this.timeTypes = timeTypes;
    }

    public boolean isDivideByWeeklyHours() {
        return isDivideByWeeklyHours;
    }

    public void setDivideByWeeklyHours(boolean divideByWeeklyHours) {
        isDivideByWeeklyHours = divideByWeeklyHours;
    }

    public int getWeeklyHours() {
        return weeklyHours;
    }

    public void setWeeklyHours(int weeklyHours) {
        this.weeklyHours = weeklyHours;
    }

    public boolean isDivideByFullTime() {
        return isDivideByFullTime;
    }

    public void setDivideByFullTime(boolean divideByFullTime) {
        isDivideByFullTime = divideByFullTime;
    }

    public int getFullTimeHours() {
        return fullTimeHours;
    }

    public void setFullTimeHours(int fullTimeHours) {
        this.fullTimeHours = fullTimeHours;
    }

    public boolean isReduceBreakTime() {
        return reduceBreakTime;
    }

    public void setReduceBreakTime(boolean reduceBreakTime) {
        this.reduceBreakTime = reduceBreakTime;
    }

    public int getFixedLengthDuration() {
        return fixedLengthDuration;
    }

    public void setFixedLengthDuration(int fixedLengthDuration) {
        this.fixedLengthDuration = fixedLengthDuration;
    }

    public int getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(int multiplier) {
        this.multiplier = multiplier;
    }

    public String getDayType() {
        return dayType;
    }

    public void setDayType(String dayType) {
        this.dayType = dayType;
    }

    public Integer getMinutesBeforeSend() {
        return minutesBeforeSend;
    }

    public void setMinutesBeforeSend(Integer minutesBeforeSend) {
        this.minutesBeforeSend = minutesBeforeSend;
    }

    public Integer getMinutesBeforeArrival() {
        return minutesBeforeArrival;
    }

    public void setMinutesBeforeArrival(Integer minutesBeforeArrival) {
        this.minutesBeforeArrival = minutesBeforeArrival;
    }

    public String getNotificationSendBeforeArrivalTime() {
        return notificationSendBeforeArrivalTime;
    }

    public void setNotificationSendBeforeArrivalTime(String notificationSendBeforeArrivalTime) {
        this.notificationSendBeforeArrivalTime = notificationSendBeforeArrivalTime;
    }

    public long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(long organizationId) {
        this.organizationId = organizationId;
    }

    public Long getCountryId() {
        return countryId;
    }

    public boolean isClientPresenceRequired() {
        return clientPresenceRequired;
    }

    public boolean isDeliverOutsideUnitHours() {
        return deliverOutsideUnitHours;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public Long getTeamId() {
        return teamId;
    }

    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }

    public long getContractTypeId() {
        return contractTypeId;
    }

    public void setContractTypeId(long contractTypeId) {
        this.contractTypeId = contractTypeId;
    }

    public boolean isTaskResumable() {
        return isTaskResumable;
    }

    public void setTaskResumable(boolean taskResumable) {
        isTaskResumable = taskResumable;
    }






    public Set<Long> getOrganizationSubTypes() {
        return Optional.ofNullable(organizationSubTypes).orElse(new HashSet<>());
    }

    public void setOrganizationSubTypes(Set<Long> organizationSubTypes) {
        this.organizationSubTypes = organizationSubTypes;
    }

    public void addResources(List<TaskTypeResource> resources){
        List<TaskTypeResource> resourceList = new ArrayList<>();
        resourceList.addAll(resources);
        this.resources = resourceList;
    }

    public List<TaskTypeResource> getResources() {
        return resources;
    }

    /*public void addResources(List<Long> resourcesIds){
        List<Long> resourceList = new ArrayList<>();
        resourceList.addAll(resourcesIds);
        this.resources = resourceList;
    }

    public List<Long> getResources() {
        return resources;
    }*/

    public Date getExpiresOn() {
        return expiresOn;
    }

    public void setExpiresOn(Date expiresOn) {
        this.expiresOn = expiresOn;
    }

    @Override
    public TaskType clone() throws CloneNotSupportedException {
        TaskType taskType = (TaskType) super.clone();
        taskType.id = null;
        List<TaskTypeSkill> skills = new ArrayList<>(this.taskTypeSkills.size());
        this.taskTypeSkills.forEach(taskTypeSkill -> {
            try {
                skills.add(taskTypeSkill.clone());
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException("Clone not supported for task type");
            }
        });
        taskType.taskTypeSkills = skills;
        taskType.definations = (this.definations == null)?null:this.definations.clone();
        return taskType;
    }

    public List<BigInteger> getTags() {
        return tags;
    }

    public void setTags(List<BigInteger> tags) {
        this.tags = tags;
    }
}


