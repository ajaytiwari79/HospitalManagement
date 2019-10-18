package com.kairos.persistence.model.task_type;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.activity.task_type.TaskTypeDTO;
import com.kairos.enums.task_type.TaskTypeEnum;
import com.kairos.persistence.model.common.MongoBaseEntity;
import com.kairos.view_handler.json_view_handler.TaskTypeViewHandler;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
@Setter
@Getter
@NoArgsConstructor
public class TaskType extends MongoBaseEntity {

    public static final String HAS_BIDDING = "hasBidding";
    public static final String HAS_DUTY_TIME = "hasDutyTime";
    public static final String MULTI_DAY_TASK = "multiDayTask";
    public static final String TIME_TYPES = "timeTypes";
    public static final String EXPIRY_COST = "expiryCost";
    public static final String TASK_TYPE_BILLABLE = "billable";
    public static final String WAITING_TIME = "waitingTime";
    public static final String DRIVING_TIME = "drivingTime";
    public static final String TASK_TYPE_SETUP = "setup";
    public static final String OVER_HEAD = "overHead";
    public static final String FLEXI_TIME = "flexiTime";
    public static final String OVER_NIGHT_STAY = "overNightStay";
    public static final String COLLECTIVE_AGREEMENT = "collectiveAgreement";
    public static final String DISTANCE_RELATED_COST = "distanceRelatedCost";
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


    public TaskType(String title, String description, Long subServiceId, Date expiresOn,int duration) {
        this.title = title;
        this.description = description;
        this.subServiceId = subServiceId;
        this.expiresOn = expiresOn;
        this.duration = duration;
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

    public void saveBalanceSettings(Map<String, Object> balanceSettings) {
        this.taskTypeVisibility = (String) balanceSettings.get("taskTypeVisibility");
        this.taskTypeSchedule = (String) balanceSettings.get("taskTypeSchedule");
        this.taskTypeWorkType = (String) balanceSettings.get("taskTypeWorkType");
        this.taskTypeTime = (String) balanceSettings.get(TIME_TYPES);
        this.hasBidding = balanceSettings.get(HAS_BIDDING) != null && (boolean) balanceSettings.get(HAS_BIDDING);
        this.hasDutyTime = balanceSettings.get(HAS_DUTY_TIME) != null && (boolean) balanceSettings.get(HAS_DUTY_TIME);
        this.multiDayTask = balanceSettings.get(MULTI_DAY_TASK) != null && (boolean) balanceSettings.get(MULTI_DAY_TASK);
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
        map.put(TIME_TYPES, this.taskTypeTime);
        map.put(MULTI_DAY_TASK, this.multiDayTask);
        map.put(HAS_BIDDING, this.hasBidding);
        map.put(HAS_DUTY_TIME, this.hasDutyTime);
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

    public void saveIncomeCostSettings(Map<String, Object> incomeSettings) {
        this.costType = TaskTypeEnum.CostType.getByValue((String) incomeSettings.get("costType"));
        this.expiryCost = incomeSettings.get(EXPIRY_COST) != null && (boolean) incomeSettings.get(EXPIRY_COST);
        this.billable = incomeSettings.get(TASK_TYPE_BILLABLE) != null && (boolean) incomeSettings.get(TASK_TYPE_BILLABLE);
        this.waitingTime = incomeSettings.get(WAITING_TIME) != null && (boolean) incomeSettings.get(WAITING_TIME);
        this.drivingTime = incomeSettings.get(DRIVING_TIME) != null && (boolean) incomeSettings.get(DRIVING_TIME);
        this.setup = incomeSettings.get(TASK_TYPE_SETUP) != null && (boolean) incomeSettings.get(TASK_TYPE_SETUP);
        this.overHead = incomeSettings.get(OVER_HEAD) != null && (boolean) incomeSettings.get(OVER_HEAD);
        this.flexiTime = incomeSettings.get(FLEXI_TIME) != null && (boolean) incomeSettings.get(FLEXI_TIME);
        this.overNightStay = incomeSettings.get(OVER_NIGHT_STAY) != null && (boolean) incomeSettings.get(OVER_NIGHT_STAY);
        this.collectiveAgreement = incomeSettings.get(COLLECTIVE_AGREEMENT) != null && (boolean) incomeSettings.get(COLLECTIVE_AGREEMENT);
        this.distanceRelatedCost = incomeSettings.get(DISTANCE_RELATED_COST) != null && (boolean) incomeSettings.get(DISTANCE_RELATED_COST);
    }

    public Map<String, Object> getIncomeSettings() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("costType", this.costType.value);
        map.put(EXPIRY_COST, this.expiryCost);
        map.put(TASK_TYPE_BILLABLE, this.billable);
        map.put(WAITING_TIME, this.waitingTime);
        map.put(DRIVING_TIME, this.drivingTime);
        map.put(TASK_TYPE_SETUP, this.setup);
        map.put(OVER_HEAD, this.overHead);
        map.put(FLEXI_TIME, this.flexiTime);
        map.put(OVER_NIGHT_STAY, this.overNightStay);
        map.put(COLLECTIVE_AGREEMENT, this.collectiveAgreement);
        map.put(DISTANCE_RELATED_COST, this.distanceRelatedCost);
        return map;
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

    public void savePlanningRules(boolean isAssignedToClipBoard, boolean useInShiftPlanning, List<TaskTypeEnum.ShiftPlanningPhase> shiftPlanningPhases) {

        this.isAssignedToClipBoard = isAssignedToClipBoard;
        this.useInShiftPlanning = useInShiftPlanning;
        this.shiftPlanningPhases = shiftPlanningPhases;

    }

    public void saveTaskTypeStaff(List<TaskTypeEnum.TaskTypeStaff> assignee, boolean hasSubcontractors, List<TaskTypeEnum.TaskTypeStaff> employees) {
        this.assignee = assignee;
        this.hasSubcontractors = hasSubcontractors;
        this.employees = employees;
    }

    public void saveTimeFrames(List<Long> forbiddenDayTypeIds, boolean clientPresenceRequired, boolean deliverOutsideUnitHours) {

        this.forbiddenDayTypeIds = forbiddenDayTypeIds;
        this.clientPresenceRequired = clientPresenceRequired;
        this.deliverOutsideUnitHours = deliverOutsideUnitHours;

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
        map.put(TIME_TYPES, timeTypesList);
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

    public Set<Long> getOrganizationSubTypes() {
        return Optional.ofNullable(organizationSubTypes).orElse(new HashSet<>());
    }

    public void addResources(List<TaskTypeResource> resources){
        List<TaskTypeResource> resourceList = new ArrayList<>();
        resourceList.addAll(resources);
        this.resources = resourceList;
    }
    public TaskType cloneObject() {
        TaskType taskType = ObjectMapperUtils.copyPropertiesByMapper(this,TaskType.class);
        taskType.id = null;
        List<TaskTypeSkill> skills = new ArrayList<>(this.taskTypeSkills.size());
        this.taskTypeSkills.forEach(taskTypeSkill -> {
                skills.add(taskTypeSkill.cloneObject());
        });
        taskType.taskTypeSkills = skills;
        taskType.definations = (this.definations == null)?null:this.definations.cloneObject();
        return taskType;
    }
}


