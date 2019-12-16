package com.kairos.service.absencePlanning;

import com.kairos.commons.utils.DateUtils;
import com.kairos.constants.AppConstants;
import com.kairos.dto.activity.task.AbsencePlanningStatus;
import com.kairos.dto.activity.task.TaskDTO;
import com.kairos.dto.activity.task_type.TaskTypeDTO;
import com.kairos.dto.user.organization.OrganizationDTO;
import com.kairos.persistence.model.staff.personal_details.StaffPersonalDetail;
import com.kairos.persistence.model.task.Task;
import com.kairos.persistence.model.task.TaskAddress;
import com.kairos.persistence.model.task.TaskReport;
import com.kairos.persistence.model.task.TaskStatus;
import com.kairos.persistence.model.task_type.AddressCode;
import com.kairos.persistence.model.task_type.TaskType;
import com.kairos.persistence.repository.common.CustomAggregationOperation;
import com.kairos.persistence.repository.common.CustomAggregationQuery;
import com.kairos.persistence.repository.task_type.TaskMongoRepository;
import com.kairos.persistence.repository.task_type.TaskReportMongoRepository;
import com.kairos.persistence.repository.task_type.TaskTypeMongoRepository;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.service.activity_stream.NotificationService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.task_type.TaskService;
import com.kairos.service.tpa.TaskReportService;
import com.mongodb.BasicDBObject;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;

import static com.kairos.constants.AppConstants.STAFF_MISSING_MESSAGE;
import static com.kairos.constants.AppConstants.STAFF_MISSING_STATUS;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;


/**
 * Created by oodles on 30/1/17.
 */
@Service
@Transactional
public class AbsencePlanningService {
    public static final String STAFF_LIST = "staffList";
    public static final String RESOURCE = "resource";
    public static final String TASK_LISTS = "taskLists";
    public static final String TASK_LIST = "taskList";
    public static final String TASK_TYPE_VISIBILITY = "taskTypeVisibility";
    public static final String DOLLOR_ADD_TO_SET = "$addToSet";
    public static final String TASK_TYPE_ID = "taskTypeId";
    public static final String DOLLOR_GROUP = "$group";
    public static final String START_DATE = "startDate";
    public static final String END_DATE = "endDate";
    public static final String INFO_1 = "info1";
    public static final String INFO_2 = "info2";
    public static final String START_ADDRESS = "startAddress";
    public static final String END_ADDRESS = "endAddress";
    public static final String DURATION = "duration";
    public static final String PRIORITY = "priority";
    public static final String EXCEPTION_OCCURS_WHILE_SENDING_PARTIAL_ABSENCES_TASKS_TO_FLS = "Exception occurs while sending Partial Absences Tasks to FLS-----> {}";
    public static final String YYYY_MMM_DD_HH_MM_SS = "yyyy MMM dd, hh:mm:ss";
    @Autowired
    MongoTemplate mongoTemplate;
    @Inject
    private TaskMongoRepository taskMongoRepository;
    @Inject
    private NotificationService notificationService;
    @Inject
    private TaskTypeMongoRepository taskTypeMongoRepository;
    @Inject
    private TaskReportMongoRepository taskReportMongoRepository;
    @Inject
    private TaskReportService taskReportService;
    @Inject
    private TaskService taskService;
    @Inject
    private ExceptionService exceptionService;
    @Autowired
    private UserIntegrationService userIntegrationService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    /**
     * This method returns data required when user just entered in Absence Planning view
     * for left side view
     *
     * @param unitId
     * @param tab
     * @return
     */
    public Map<String, Object> getAbsencePlanningData(Long unitId, String tab) {
        List<Map> data = absencePlanningAggregationData(unitId, false, tab);
        return getAbsencePlanningTasks(unitId, data);
    }

    /**
     * This method returns tasks required when user just entered in Absence Planning view
     *
     * @param organizationId
     * @param mappedResults
     * @return
     */
    public Map<String, Object> getAbsencePlanningTasks(Long organizationId, List<Map> mappedResults) {
        List<Map> tasks = new ArrayList<>();
        List<Map<String, Object>> taskTypes = new ArrayList<>();
        List<Long> staffIds = new ArrayList<>();
        List<Object> staffsAnonymous = new ArrayList<>();
        List<Object> teamStaffList = new ArrayList<>();
        List<Object> staffList = new ArrayList<>();
        Map<String, Object> data = new HashMap();
        for (Map object : mappedResults) {

            // tasks =  (List) object.get("taskLists");
            staffIds = (List) object.get(STAFF_LIST);
            for (Map<String, Object> map : (List<Map<String, Object>>) object.get("taskTypeList")) {
                Map<String, Object> taskTypeData = new HashMap();
                taskTypeData.put("id", map.get(RESOURCE).toString());
                taskTypeData.put("name", map.get("title"));
                taskTypes.add(taskTypeData);
            }

            staffsAnonymous = (List) object.get("staffAnonymousList");
            for (Object o : staffsAnonymous) {
                Map<String, Object> anonymousData = new HashMap();
                anonymousData.put("id", o);
                anonymousData.put("name", "Anonymous");
                anonymousData.put("isAnonyumous", true);
                teamStaffList.add(anonymousData);
            }
            //call via rest API anilm2
            data.put("tasks", taskAggregationData((List<BigInteger>) object.get(TASK_LISTS)));
            data.put("staffs", staffList);
            data.put("taskTypeStaffs", taskTypesAggregationData((List<ObjectId>) object.get(TASK_LISTS)));
        }
        return data;
    }

    /**
     * This method is check partial absences daily and send notification to planner if found missing data
     * which is mandatory to sync with FLS
     */
    public void checkDailyAbsencePlanningData() {
        List<Map> data = absencePlanningAggregationData(null, true, AppConstants.ALL_TAB);
        List<String> tasks = new ArrayList<>();
        for (Map object : data) {
            tasks = (List) object.get(TASK_LISTS);
        }
        for (String taskId : tasks) {
            Task task = taskMongoRepository.findOne(new BigInteger(taskId));
            if (task.getStaffId() == null || task.getTaskTypeId() == null || task.getAddress() == null) {
                String message = "Please update";
                if (task.getStaffId() == null) {
                    message += "staff";
                }
                if (task.getTaskTypeId() == null) {
                    message += ", taskType";
                }
                if (task.getAddress() == null) {
                    message += ", location";
                }
                notificationService.addStaffMissingNotification(task, STAFF_MISSING_MESSAGE, STAFF_MISSING_STATUS);
            }

        }
    }

    /**
     * This method is used to get tasks of given tab.
     *
     * @param unitId
     * @param isDaily
     * @param tab
     * @return
     */
    public List<Map> absencePlanningAggregationData(Long unitId, Boolean isDaily, String tab) {
        Document dbUnwindObj = (Document.parse(CustomAggregationQuery.absencePlanningDataUnwindQuery()));
        Criteria criteria = new Criteria();
        Criteria c = Criteria.where(TASK_LIST).ne(Collections.EMPTY_LIST).and("taskList.timeCareExternalId").exists(true).and("taskList.isDeleted").is(false);
        Document dbObject = new Document(DOLLOR_GROUP,
                new BasicDBObject(TASK_LISTS, new BasicDBObject(DOLLOR_ADD_TO_SET, "$taskList._id")).append("_id", null)
                        .append("taskTypeList", new BasicDBObject(DOLLOR_ADD_TO_SET, new BasicDBObject(RESOURCE, "$_id").append("title", "$title")))
                        .append(STAFF_LIST, new BasicDBObject(DOLLOR_ADD_TO_SET, "$taskList.staffId"))
                        .append("staffAnonymousList", new BasicDBObject(DOLLOR_ADD_TO_SET, "$taskList.staffAnonymousId")));
        if (unitId != null)
            c.and("taskList.unitId").is(unitId);
        if (isDaily) {
            Date date = DateUtils.getDate();
            Date startTimeOfDay = DateUtils.getStartOfDay(date);
            Date endTimeOfDay = DateUtils.getEndOfDay(date);
            c.and("taskList.startDate").gte(startTimeOfDay).lt(endTimeOfDay);
        }
        if (tab.equals(AppConstants.PRESENCE_TAB)) {
            criteria = Criteria.where(TASK_TYPE_VISIBILITY).is(AppConstants.PRESENT);
        }
        if (tab.equals(AppConstants.FULL_DAY_ABSENCE_TAB)) {
            criteria = Criteria.where(TASK_TYPE_VISIBILITY).is(AppConstants.ABSENT).and("taskTypeSchedule").is(AppConstants.FULL_DAY);
        }
        if (tab.equals(AppConstants.PARTIAL_ABSENCE_TAB)) {
            criteria = Criteria.where(TASK_TYPE_VISIBILITY).is(AppConstants.ABSENT).and("taskTypeSchedule").is(AppConstants.PARTIALLY);
        }
        if (tab.equals(AppConstants.ALL_TAB)) {
            List tasTypes = new ArrayList<String>();
            tasTypes.add(AppConstants.ABSENT);
            tasTypes.add(AppConstants.PRESENT);
            criteria = Criteria.where(TASK_TYPE_VISIBILITY).in(tasTypes);
        }
        Aggregation aggregation = Aggregation.newAggregation(
                match(
                        criteria
                ),
                new CustomAggregationOperation(
                        new Document(
                                "$lookup",
                                new Document("from", "tasks")
                                        .append("localField", "_id")
                                        .append("foreignField", TASK_TYPE_ID)
                                        .append("as", TASK_LIST)
                        )
                ),
                new CustomAggregationOperation(dbUnwindObj),
                match(
                        c
                ),
                new CustomAggregationOperation(dbObject)
        );
        AggregationResults<Map> finalResult =
                mongoTemplate.aggregate(aggregation, TaskType.class, Map.class);
        return finalResult.getMappedResults();
    }


    public List<Map> taskAggregationData(List<BigInteger> tasks) {
        List<Map> finalTaskList = new ArrayList<>();
        Document dbProjectionObj = Document.parse(CustomAggregationQuery.absencePlanningProjectionQuery());
        Document dbObject = new Document(DOLLOR_GROUP, new BasicDBObject("_id", "$grouping")
                .append(TASK_LIST, new BasicDBObject("$push", new BasicDBObject(RESOURCE, "$_id")
                        .append(TASK_TYPE_ID, "$taskTypeId").append(START_DATE, "$startDate").append(END_DATE, "$endDate")
                        .append(INFO_1, "$info1").append(INFO_2, "$info2").append(START_ADDRESS, "$startAddress").append(END_ADDRESS, "$endAddress")
                        .append("absencePlanningStatus", "$absencePlanningStatus").append("staffId", "$grouping").append(DURATION, "$duration").append(PRIORITY, "$priority").append("isActive", "$isActive"))));
        Aggregation aggregation = Aggregation.newAggregation(
                match(
                        Criteria.where("_id").in(tasks)
                ),
                new CustomAggregationOperation(dbProjectionObj),
                new CustomAggregationOperation(dbObject)
        );
        AggregationResults<Map> finalResult =
                mongoTemplate.aggregate(aggregation, Task.class, Map.class);

        for (Map map : finalResult.getMappedResults()) {
            List<Map> taskList = (List) map.get(TASK_LIST);
            // this start and end hours are required for frontend
            List<Integer> startHours = new ArrayList();
            startHours.add(00);
            startHours.add(11);
            startHours.add(14);
            startHours.add(17);
            startHours.add(20);
            List<Integer> endHours = new ArrayList<>();
            endHours.add(11);
            endHours.add(14);
            endHours.add(17);
            endHours.add(20);
            endHours.add(23);
            List<Integer> days = new ArrayList<>();
            for (Map<String, Object> task : taskList) {
                Map<String, Object> finalTaskData = new HashMap<>();
                TaskType taskType = taskTypeMongoRepository.findOne(new BigInteger(task.get(TASK_TYPE_ID).toString()));

                finalTaskData.put("id", task.get(RESOURCE).toString());
                finalTaskData.put(TASK_TYPE_ID, task.get(TASK_TYPE_ID).toString());
                finalTaskData.put("taskTypeName", taskType.getTitle());
                finalTaskData.put(START_DATE, new DateTime(task.get(START_DATE), DateTimeZone.forTimeZone(TimeZone.getTimeZone("Denmark"))).toString());
                finalTaskData.put(END_DATE, new DateTime(task.get(END_DATE), DateTimeZone.forTimeZone(TimeZone.getTimeZone("Denmark"))).toString());
                finalTaskData.put(RESOURCE, task.get("staffId"));
                finalTaskData.put(START_ADDRESS, task.get(START_ADDRESS) != null ? task.get(START_ADDRESS).toString() : null);
                finalTaskData.put(END_ADDRESS, task.get(END_ADDRESS) != null ? task.get(END_ADDRESS).toString() : null);
                finalTaskData.put("status", task.get("absencePlanningStatus"));
                finalTaskData.put(INFO_1, task.get(INFO_1));
                finalTaskData.put(INFO_2, task.get(INFO_2));
                finalTaskData.put(DURATION, task.get(DURATION));
                finalTaskData.put(PRIORITY, task.get(PRIORITY));
                finalTaskData.put("active", task.get("isActive"));
                Date startDate = (Date) task.get(START_DATE);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(startDate);
                Integer day = calendar.get(Calendar.DAY_OF_MONTH);
                days.add(day);
                int occurrences = Collections.frequency(days, day);
                if (occurrences == 0) occurrences = 1;
                if (occurrences > 4) occurrences = 4;
                finalTaskData.put("start", DateUtils.addTimeInDate(startDate, startHours.get(occurrences - 1), 00, 00));
                finalTaskData.put("end", DateUtils.addTimeInDate(startDate, endHours.get(occurrences - 1), 00, 00));
                finalTaskList.add(finalTaskData);
            }
        }
        return finalTaskList;
    }


    public List<Map> taskTypesAggregationData(List<ObjectId> tasks) {
        List<Map> finalTaskList = new ArrayList<>();
        Document dbObject = new Document(DOLLOR_GROUP, new BasicDBObject("_id", "$taskTypeId")
                .append(STAFF_LIST, new BasicDBObject(DOLLOR_ADD_TO_SET, "$staffId")));
        Aggregation aggregation = Aggregation.newAggregation(
                match(
                        Criteria.where("_id").in(tasks)
                ),
                new CustomAggregationOperation(dbObject)
        );
        AggregationResults<Map> finalResult =
                mongoTemplate.aggregate(aggregation, Task.class, Map.class);
        for (Map map : finalResult.getMappedResults()) {

            Map<String, Object> finalTaskData = new HashMap<>();
            finalTaskData.put(map.get("_id").toString(), map.get(STAFF_LIST));

            finalTaskList.add(finalTaskData);
        }
        return finalTaskList;
    }

    public List<Map> taskReportsByUnit() {
        Document dbObject = new Document(DOLLOR_GROUP, new Document("_id", "$unitId")
                .append(STAFF_LIST, new Document(DOLLOR_ADD_TO_SET, "$staffId")));

        Aggregation aggregation = Aggregation.newAggregation(
                new CustomAggregationOperation(dbObject)
        );
        AggregationResults<Map> finalResult =
                mongoTemplate.aggregate(aggregation, TaskReport.class, Map.class);

        return finalResult.getMappedResults();
    }

    public List<Map> tasksByUnit() {
        Document dbObject = new Document(DOLLOR_GROUP, new Document("_id", "$unitId")
                .append(TASK_LIST, new Document("$push", new Document(RESOURCE, "$_id"))));
        Criteria c = Criteria.where("timeCareExternalId").exists(true);
        Aggregation aggregation = Aggregation.newAggregation(
                match(c),
                new CustomAggregationOperation(dbObject)
        );
        AggregationResults<Map> finalResult =
                mongoTemplate.aggregate(aggregation, Task.class, Map.class);

        return finalResult.getMappedResults();
    }


    public List<TaskDTO> updateTask(List<TaskDTO> taskList) {

        try {
            OrganizationDTO organization = userIntegrationService.getOrganization();
            for (TaskDTO taskData : taskList) {

                Task task = taskMongoRepository.findOne(new BigInteger(taskData.getId()));
                // create daily report of changes done;
                createReport(task, taskData);
                TaskType taskType1 = taskTypeMongoRepository.findOne(new BigInteger(task.getTaskTypeId().toString()));
                TaskType taskType2 = taskTypeMongoRepository.findOne(new BigInteger(taskData.getTaskTypeId()));
                BigInteger taskTypeId = new BigInteger(taskData.getTaskTypeId());
                // here we are checking if task's taskType is changed then we need to create new task with new taskType and old task remain in system with old taskType & inactive state.
                if (!taskType1.getTaskTypeVisibility().equals(taskType2.getTaskTypeVisibility()) || !taskType1.getTaskTypeSchedule().equals(taskType2.getTaskTypeSchedule())) {
                    Task task2;
                    task2 = taskMongoRepository.findByExternalIdAndIsActive(task.getExternalId(), false);
                    BigInteger tempId = task.getId();
                    task.setId(null);
                    if (task2 != null) {
                        task2.setActive(true);

                        task2.setTaskTypeId(taskTypeId);
                        taskService.save(task2);
                    } else {
                        task2 = Task.copyProperties(task, Task.getInstance());

                        task2.setTaskTypeId(taskTypeId);
                        taskService.save(task2);
                    }
                    task.setId(tempId);
                    task.setActive(false);
                    taskService.save(task);
                } else {

                    task.setDuration(taskData.getDuration());
                    if (taskData.getStartAddress() != null)
                        task.setStartAddress(AddressCode.valueOf(taskData.getStartAddress()));
                    if (taskData.getEndAddress() != null)
                        task.setEndAddress(AddressCode.valueOf(taskData.getEndAddress()));
                    // 0 - home address, -1 - unit address
                    if (taskData.getEndAddress() != null && task.getEndAddress().toString() == "0") {
                        StaffPersonalDetail staff = userIntegrationService.getStaff(taskData.getResource());
                        // Staff staff = staffGraphRepository.findOne(taskData.getResource());
                        if (staff != null) {
                            TaskAddress taskAddress = new TaskAddress();
                            taskAddress.setZip(staff.getContactAddress().getZipCode().getZipCode());
                            taskAddress.setCity(staff.getContactAddress().getCity());
                            taskAddress.setStreet(staff.getContactAddress().getStreet());
                            taskAddress.setHouseNumber(staff.getContactAddress().getHouseNumber());
                            taskAddress.setCountry("DK");
                            task.setAddress(taskAddress);
                        }
                    } else if (taskData.getEndAddress() != null && task.getEndAddress().toString() == "-1") {
                        TaskAddress taskAddress = new TaskAddress();
                        taskAddress.setCountry("DK");
                        taskAddress.setZip(organization.getContactAddress().getZipCode().intValue());
                        taskAddress.setCity(organization.getContactAddress().getCity());
                        taskAddress.setStreet(organization.getContactAddress().getStreet1());
                        taskAddress.setHouseNumber(organization.getContactAddress().getHouseNumber());
                        task.setAddress(taskAddress);
                    }

                    task.setActive(taskData.getActive());
                    task.setPriority(taskData.getPriority());
                    task.setStartDate(taskData.getStartDate());
                    task.setEndDate(taskData.getEndDate());
                    task.setInfo1(taskData.getInfo1());
                    task.setInfo2(taskData.getInfo2());
                    task.setTaskTypeId(taskTypeId);
                    if (taskData.getAnonymousStaffId() != null) {
                        task.setStaffId(taskData.getResource());
                        task.setStaffAnonymous(false);
                        task.setStaffAnonymousId(null);
                    }

                    task.setAbsencePlanningStatus(AbsencePlanningStatus.CHANGES_DONE);
                    taskService.save(task);
                    createAndSyncSubTask(task, organization);

                }
            }
            return taskList;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * This method is used to sync partial absences tasks from Absence Planning view to FLS.
     *
     * @param unitId
     * @param taskList
     * @return
     */
    public List<TaskDTO> syncPartialAbsencesWithFLS(long unitId, List<TaskDTO> taskList) {
        try {
            OrganizationDTO organization = userIntegrationService.getOrganization();
            // Organization organization = organizationGraphRepository.findOne(unitId, 2);

            for (TaskDTO task : taskList) {
                syncPartialAbsencesTask(task, organization);
            }
            return taskList;
        } catch (Exception exception) {
            logger.warn(EXCEPTION_OCCURS_WHILE_SENDING_PARTIAL_ABSENCES_TASKS_TO_FLS, exception);
            return null;
        }
    }


    /**
     * This method is used to sync Present and full day absences tasks from Absence Planning view to FLS.
     *
     * @param unitId
     * @param taskList
     * @return
     */
    public List<TaskDTO> syncPresentFullDayAbsencesWithFLS(Long unitId, List<TaskDTO> taskList) {
        try {
            // Organization organization = organizationGraphRepository.findOne(unitId, 2);
            OrganizationDTO organization = userIntegrationService.getOrganization();
            Map<String, String> flsCredentials = userIntegrationService.getFLSCredentials(unitId);
            //Map<String, String> flsCredentials = integrationService.getFLSCredentials(unitId);
            for (TaskDTO task : taskList) {
                syncPresentFullDayAbsencesTask(task, flsCredentials, organization);
            }
            return taskList;
        } catch (Exception exception) {
            logger.warn("Exception occurs while sending Partial Absences Tasks to FLS-----> " + exception.getMessage());
            return null;
        }
    }

    /**
     * This method is used to send all absences and presences to FLS
     *
     * @param unitId
     * @param taskList
     * @return
     */
    public List<TaskDTO> syncAllAbsencesWithFLS(Long unitId, List<TaskDTO> taskList) {
        try {
            OrganizationDTO organization = userIntegrationService.getOrganization();
            // Organization organization = organizationGraphRepository.findOne(unitId, 2);
            Map<String, String> flsCredentials = userIntegrationService.getFLSCredentials(unitId);
            //Map<String, String> flsCredentials = integrationService.getFLSCredentials(unitId);
            for (TaskDTO task : taskList) {
                TaskType taskType = taskTypeMongoRepository.findOne(new BigInteger(task.getTaskTypeId().toString()));
                if (taskType.getTaskTypeVisibility().equals(AppConstants.ABSENT) && taskType.getTaskTypeSchedule().equals(AppConstants.PARTIALLY)) {
                    syncPartialAbsencesTask(task, organization);
                } else {
                    syncPresentFullDayAbsencesTask(task, flsCredentials, organization);
                }
            }
            return taskList;
        } catch (Exception exception) {
            logger.warn("Exception occurs while sending Partial Absences Tasks to FLS-----> " + exception.getMessage());
            return null;
        }
    }

    /**
     * This method is used to get Left hand side common data of given unit required for further filtering Absence planner tasks.
     *
     * @param unitId
     * @return
     */
    public Map<String, Object> getCommonDataOfOrganization(Long unitId) {
        Map<String, Object> data = new HashMap<String, Object>();
        Map<String, Object> response = userIntegrationService.getCommonDataOfOrganization(unitId);
        List<TaskTypeDTO> taskTypes = new ArrayList<>();
        for (TaskType taskType : taskTypeMongoRepository.findByOrganizationIdAndIsEnabled(unitId, true)) {
            taskTypes.add(getBasicTaskTypeInfo(taskType));
        }
        data.put("taskTypeOfOrganization", taskTypes);
        data.putAll(response);

        return data;
    }

    public TaskTypeDTO getBasicTaskTypeInfo(TaskType taskType) {
        TaskTypeDTO taskTypeDTO = new TaskTypeDTO();
        taskTypeDTO.setId(taskType.getId());
        taskTypeDTO.setDescription(taskType.getDescription());
        taskTypeDTO.setDuration(taskType.getDuration());
        taskTypeDTO.setExpiresOn(taskType.getExpiresOn());
        taskTypeDTO.setTags(taskType.getTags());
        taskTypeDTO.setParentTaskTypeId(taskType.getRootId());
        taskTypeDTO.setServiceId(taskType.getSubServiceId());
        taskTypeDTO.setTitle(taskType.getTitle());
        return taskTypeDTO;
    }

    /**
     * This method is creating reports after every change made by Planner from Absence planner view.
     *
     * @param task
     * @param taskDTO
     */
    public void createReport(Task task, TaskDTO taskDTO) {

        TaskReport taskReport = taskReportMongoRepository.findByTaskId(task.getId());
        if (taskReport == null) taskReport = new TaskReport();
        taskReport.setTaskId(task.getId());
        taskReport.setStaffId(task.getStaffId());
        taskReport.setUnitId(task.getUnitId());
        taskReport.setUpdateDate(DateFormatUtils.format(DateUtils.getDate(), YYYY_MMM_DD_HH_MM_SS));

        //previous data
        DateTime startDateTime = new DateTime(task.getStartDate()).toDateTime(DateTimeZone.UTC);
        DateTime endDateTime = new DateTime(task.getEndDate()).toDateTime(DateTimeZone.UTC);

        taskReport.setPreviousFrom(startDateTime.toString(YYYY_MMM_DD_HH_MM_SS));
        taskReport.setPreviousTo(endDateTime.toString(YYYY_MMM_DD_HH_MM_SS));
        taskReport.setPreviousDuration(task.getDuration().toString());
        taskReport.setPreviousActivity(taskTypeMongoRepository.findOne(new BigInteger(task.getTaskTypeId().toString())).getTitle());

        //current data
        DateTime currentStartDateTime = new DateTime(taskDTO.getStartDate()).toDateTime(DateTimeZone.UTC);
        DateTime currentEndDateTime = new DateTime(taskDTO.getEndDate()).toDateTime(DateTimeZone.UTC);
        if (task.getStartDate().compareTo(taskDTO.getStartDate()) != 0)
            taskReport.setCurrentFrom(currentStartDateTime.toString(YYYY_MMM_DD_HH_MM_SS));
        if (task.getEndDate().compareTo(taskDTO.getEndDate()) != 0)
            taskReport.setCurrentTo(currentEndDateTime.toString(YYYY_MMM_DD_HH_MM_SS));
        if (task.getDuration().compareTo(taskDTO.getDuration()) != 0)
            taskReport.setCurrentDuration(taskDTO.getDuration().toString());
        if (!task.getTaskTypeId().toString().equals(taskDTO.getTaskTypeId().toString()))
            taskReport.setCurrentActivity(taskTypeMongoRepository.findOne(new BigInteger(taskDTO.getTaskTypeId().toString())).getTitle());

        taskReportService.save(taskReport);

    }


    /**
     * This method is invoked by cron job to send Report of all changes made from Absence Planning View by Planner
     * Report send to Organization email
     *
     */
    /*
     Code commented
    public void checkDailyAbsencePlanningReport(){
        List<Map> unitStaffs = taskReportsByUnit();
        Date date = DateUtils.getDate();
        for(Map data : unitStaffs){
            Long organizationId = Long.valueOf(data.get("_id").toString());
            OrganizationDTO organization= absencePlanningServiceRestClient.getOrganization(unitId);
            //Organization organization = organizationGraphRepository.findOne(organizationId);

            List<Long> staffs = (List<Long>) data.get("staffList");

            List<TaskReportWrapper> taskReportWrappers = taskReportService.fetchStaffReports(staffs, DateUtil.getStartOfDay(date), DateUtil.getEndOfDay(date));
            File file = taskReportService.generateReport(taskReportWrappers, "Report_"+DateFormatUtils.format(date, "yyyy-MM-dd"), "Time Care Shift Reports", organization.getName());
            if(organization.getEmail() != null && file != null) {
                String[] emails = new String[1];
                emails[0] = organization.getEmail();
                taskReportService.mailStaffTaskReport(file, emails, "TimeCare Daily Shift Reports", "TimeCare Daily Shift Reports");
            }
        }

    }*/

    /**
     * This method fetch fixed calls from FLS, created from Absence Planning View.
     */
    public void fetchFixedCallsDataFromFLS() {
        List<Map> data = absencePlanningAggregationData(null, false, AppConstants.ALL_TAB);

        List<String> taskIds = new ArrayList<>();
        for (Map object : data) {

            taskIds = (List) object.get(TASK_LISTS);
        }
        List<Task> tasks = taskMongoRepository.getAllTasksByIdsIn(taskIds);
        Map<String, String> flsCredentials = null;
        if (tasks != null && tasks.size() > 0) {
            TaskType taskType = taskTypeMongoRepository.findOne(tasks.get(0).getTaskTypeId());
            flsCredentials = userIntegrationService.getFLSCredentials(taskType.getOrganizationId());
            //flsCredentials = integrationService.getFLSCredentials(taskType.getUnitID());
        }
        for (Task task : tasks) {
            try {
                if (task.getVisitourId() != null) {
                    Map<String, Object> callMetaData = new HashMap<>();
                    callMetaData.put("vtid", task.getVisitourId());
                    callMetaData.put("extID", task.getId());
                    /*CallInfoRec callInfoRec = scheduler.getCallInfo(callMetaData, flsCredentials);

                    Date startDate = DateUtils.getSingleCompleteDate(callInfoRec.getDateFrom().toGregorianCalendar().getTime(), callInfoRec.getTimeFrom().toGregorianCalendar().getTime());

                    Date endDate = DateUtils.getSingleCompleteDate(callInfoRec.getDateTo().toGregorianCalendar().getTime(), callInfoRec.getTimeTo().toGregorianCalendar().getTime());

                    if (startDate.compareTo(task.getStartDate()) != 0) {
                        task.setStartDate(startDate);
                    }
                    if (endDate.compareTo(task.getEndDate()) != 0) {
                        task.setEndDate(endDate);
                    }
                    if (callInfoRec.getFMExtID() != null) task.setStaffId(Long.valueOf(callInfoRec.getFMExtID()));
                    taskService.save(task);*/
                }

            } catch (Exception exception) {

            }

        }

    }

    /**
     * This method is invoked by cron job to send Present,Full day Absences and partial absences to FLS
     * for Present,Full day Absences tasks update engineer's work schedule
     * for Partial Absences tasks, make fixed calls in FLs
     */
    public void sendDataToFLSJob() {
        List<Map> unitTasks = tasksByUnit();
        Date date = DateUtils.getDate();
        for (Map data : unitTasks) {
            Long organizationId = Long.valueOf(data.get("_id").toString());
            Map<String, String> flsCredentials = userIntegrationService.getFLSCredentials(organizationId);
            // Map<String, String> flsCredentials = integrationService.getFLSCredentials(organizationId);
            OrganizationDTO organization = userIntegrationService.getOrganization();
            //Organization organization = organizationGraphRepository.findOne(organizationId);
            for (Map object : (List<Map>) data.get(TASK_LIST)) {
                Task task = taskMongoRepository.findOne(new BigInteger(object.get(RESOURCE).toString()));
                if (task.getTaskTypeId() != null && task.getStaffId() != null && task.getAddress() != null) {
                    TaskType taskType = taskTypeMongoRepository.findOne(task.getTaskTypeId());
                    TaskDTO taskDTO = new TaskDTO();
                    taskDTO.setId(task.getId() + "");
                    if (task.getStaffId() != null) taskDTO.setResource(task.getStaffId());
                    taskDTO.setTaskTypeId(task.getTaskTypeId().toString());
                    taskDTO.setDuration(task.getDuration());
                    taskDTO.setStartDate(task.getStartDate());
                    taskDTO.setEndDate(task.getEndDate());
                    taskDTO.setPriority(task.getPriority());
                    if (task.getStartAddress() != null) taskDTO.setStartAddress(task.getStartAddress().getValue());
                    if (task.getEndAddress() != null) taskDTO.setEndAddress(task.getEndAddress().getValue());
                    if (taskType.getTaskTypeVisibility().equals(AppConstants.ABSENT) && taskType.getTaskTypeSchedule().equals(AppConstants.PARTIALLY)) {
                        syncPartialAbsencesTask(taskDTO, organization);
                    } else {
                        syncPresentFullDayAbsencesTask(taskDTO, flsCredentials, organization);
                    }
                }
            }
        }

    }

    /**
     * This method is used to sync present or full day absences shift from Absence planner view to FLS
     * update work schedule of Engineer in FLS
     *
     * @param task
     */
    public void syncPresentFullDayAbsencesTask(TaskDTO task, Map<String, String> flsCredentials, OrganizationDTO organization) {

        StaffPersonalDetail staff = userIntegrationService.getStaff(task.getResource());
        // Staff staff = staffGraphRepository.findOne(Long.valueOf(task.getResource()));
        TaskType taskType = taskTypeMongoRepository.findOne(new BigInteger(task.getTaskTypeId().toString()));
        int workScheduleResult;
        Boolean sendToFls = false; //Add task to
        Map<String, Object> workScheduleMetaData = new HashMap<>();
        workScheduleMetaData.put("fmvtid", staff.getId());
        if (taskType.getTaskTypeVisibility() == "Absent") {
            Map<String, Object> response = userIntegrationService.getAbsenceTypeByName(taskType.getTitle());
            workScheduleMetaData.putAll(response);
        } else {
            workScheduleMetaData.put("type", -1);
        }
        workScheduleMetaData.put("startLocation", AddressCode.valueOf(task.getStartAddress()).getValue()); //0=Start at home address (default), -1=Start at office address
        workScheduleMetaData.put("endLocation", AddressCode.valueOf(task.getEndAddress()).getValue());
        Map<String, Object> dateTimeInfo = new HashMap<>();
        dateTimeInfo.put(START_DATE, task.getStartDate()); //Assigning Absence starting from tomorrow
        dateTimeInfo.put(END_DATE, task.getEndDate()); //till day after tomorrow
        DateTime startDateTime = new DateTime(task.getStartDate()).toDateTime(DateTimeZone.UTC);
        Date reducedEndTime = DateUtils.getDeductionInTimeDuration(task.getStartDate(), task.getEndDate(), organization.getDayShiftTimeDeduction(), organization.getNightShiftTimeDeduction());
        DateTime endDateTime = new DateTime(reducedEndTime).toDateTime(DateTimeZone.UTC);
        dateTimeInfo.put("startTime", startDateTime.hourOfDay().get()); //Assigning Absence starting from tomorrow
        dateTimeInfo.put("endTime", endDateTime.hourOfDay().get()); //till day after tomorrow
        dateTimeInfo.put("startTimeMinute", startDateTime.minuteOfHour().get()); //Assigning Absence starting from tomorrow
        dateTimeInfo.put("endTimeMinute", endDateTime.minuteOfHour().get()); //till day after tomorrow
        /*workScheduleResult = scheduler.assignAbsencesToFLS(workScheduleMetaData, dateTimeInfo, flsCredentials);
        if (workScheduleResult > 0) {
            logger.info("Working time of Engineer " + staff.getFirstName() + " updated Successfully " + workScheduleResult);
            Task task1 = taskMongoRepository.findOne(new BigInteger(task.getId()));
            task1.setAbsencePlanningStatus(AbsencePlanningStatus.SYNCHRONISED);
            taskService.save(task1);

        } else {
            exceptionService.internalError("error.visitour.worktime.engineer.update");
        }*/
    }

    /**
     * This method is used to sync partial absences task from Absence Planning view to FLS.
     *
     * @param task
     * @param organization
     */
    public void syncPartialAbsencesTask(TaskDTO task, OrganizationDTO organization) {
        Task task1 = taskMongoRepository.findOne(new BigInteger(task.getId()));
        Map<String, String> flsCredentials = userIntegrationService.getFLSCredentials(organization.getId());
        // Map<String, String> flsCredentials = integrationService.getFLSCredentials(organization.getId());
        int vtID;
        if (task.getActive() != null && task.getActive() == false) {
            if (task1.getVisitourId() != null && !task1.getTaskStatus().equals(TaskStatus.CANCELLED)) {
                /*Map<String, Object> callMetaData = new HashMap<>();
                callMetaData.put("functionCode", 4);
                callMetaData.put("extID", task1.getId());
                callMetaData.put("vtid", task1.getVisitourId());
                vtID = scheduler.deleteCall(callMetaData, flsCredentials);
                logger.info("Delete Caal Created Successfully " + vtID);*/
                task1.setTaskStatus(TaskStatus.CANCELLED);
                task1.setAbsencePlanningStatus(AbsencePlanningStatus.SYNCHRONISED);
                taskService.save(task1);
            }

        } else {
            StaffPersonalDetail staff = userIntegrationService.getStaff(task.getResource());
            //Staff staff = staffGraphRepository.findOne(Long.valueOf(task.getResource()));

            checkFullDayPresence(task.getStartDate(), staff.getId(), flsCredentials, organization);
            TaskType taskType = taskTypeMongoRepository.findOne(new BigInteger(task.getTaskTypeId().toString()));
            Map<String, Object> callMetaData = new HashMap<>();
            Map<String, Object> fixedCallMetaData = new HashMap<>();
            callMetaData.put("functionCode", 2); // 2 = Confirmation of an appointment:
            // if FixedDate ist given then the call will be confirmed to this date.
            // Otherwise VISITOUR will find the best day and confirm it. Can be executed without a preceding FunctionCode 0 or 1.
            callMetaData.put("extID", task.getId());
            if (task1 != null && task1.getVisitourId() != null) callMetaData.put("vtid", task1.getVisitourId());
            else callMetaData.put("vtid", 0);
            callMetaData.put("fixedFieldManagerID", staff.getId());
            callMetaData.put("name", staff.getFirstName() + " " + staff.getLastName());
            callMetaData.put("name2", staff.getLastName());
            //    callMetaData.put("title",(staff.getGender().toString().equals("MALE")? "Mr." : "Ms."));
            callMetaData.put("phone1", staff.getContactDetail() != null ? staff.getContactDetail().getPrivatePhone() : "");
            //callMetaData.put("callInfo1",task.getTaskDescription());
            callMetaData.put("country", "DK");
            callMetaData.put("zip", organization.getContactAddress().getZipCode());
            callMetaData.put("city", organization.getContactAddress().getCity());
            callMetaData.put("street", organization.getContactAddress().getStreet1());
            callMetaData.put("hnr", organization.getContactAddress().getHouseNumber());
            callMetaData.put(PRIORITY, 3);
            //FOR MVP All Partial Absence Task link to one at FLS
            callMetaData.put("taskTypeID", "16");
            Map<String, Object> timeFrameInfo = new HashMap<>();
            timeFrameInfo.put("dateFrom", task.getStartDate());
            timeFrameInfo.put("dateTo", task.getEndDate());
            DateTime startDateTime = new DateTime(task.getStartDate()).toDateTime(DateTimeZone.UTC);
            List<Map> tasks = taskService.findByStaffIdAndStartDate(staff.getId(), startDateTime);
            DateTime endDateTime = new DateTime(task.getEndDate()).toDateTime(DateTimeZone.UTC);
            if (tasks.size() != 0) {
                Task firstTaskOfDay = (Task) taskService.findOne(String.valueOf(tasks.get(0).get("_id")));
                Task lastTaskOfDay = (Task) taskService.findOne(String.valueOf(tasks.get(tasks.size() - 1).get("_id")));
                Date reducedEndTime = DateUtils.getDeductionInTimeDuration(firstTaskOfDay.getStartDate(), lastTaskOfDay.getEndDate(), organization.getDayShiftTimeDeduction(), organization.getNightShiftTimeDeduction());
                DateTime staffLeavingTime = new DateTime(lastTaskOfDay.getEndDate()).toDateTime(DateTimeZone.UTC);
                if (staffLeavingTime.hourOfDay().get() == endDateTime.hourOfDay().get() && staffLeavingTime.minuteOfHour().get() == endDateTime.minuteOfHour().get()) {
                    endDateTime = new DateTime(reducedEndTime).toDateTime(DateTimeZone.UTC);
                }
            }
            Duration dur = new Duration(startDateTime, endDateTime);
            callMetaData.put(DURATION, Integer.valueOf(String.valueOf(dur.getStandardMinutes())));
            timeFrameInfo.put("timeFrom", startDateTime);
            timeFrameInfo.put("timeTo", endDateTime);
            timeFrameInfo.put("fixedDate", startDateTime);

            /*vtID = scheduler.fixedAppointment(callMetaData, timeFrameInfo, flsCredentials);
            if (vtID > 0) {
                logger.info("Call Created Successfully " + vtID);
                fixedCallMetaData.put("vtid", vtID);
                fixedCallMetaData.put("fixCalls", true);
                fixedCallMetaData.put("fmextID", staff.getId());
                //FixScheduleResponse fixScheduleResponse = scheduler.getSchedule(fixedCallMetaData, null, flsCredentials);
                //logger.info("fixScheduleResponse----of call id--> " + vtID + " is--> " + fixScheduleResponse.getFixScheduleResult().getFixedCall());
                task1.setVisitourId(vtID);
                task1.setAbsencePlanningStatus(AbsencePlanningStatus.SYNCHRONISED);
                taskService.save(task1);

            } else {
                exceptionService.internalError("error.visitour.task.create");
            }*/
        }
    }

    /**
     * This method is used to check and send Full day Presences of given staff to FLS.
     *
     * @param startDate
     * @param staffId
     * @param flsCredentials
     * @param organization
     * @return
     */
    public int checkFullDayPresence(Date startDate, Long staffId, Map<String, String> flsCredentials, OrganizationDTO organization) {
        int workScheduleResult;
        Map<String, Object> workScheduleMetaData = new HashMap<>();
        workScheduleMetaData.put("fmextID", staffId);
        workScheduleMetaData.put("type", -1);
        workScheduleMetaData.put("startLocation", -1); //0=Start at home address (default), -1=Start at office address
        workScheduleMetaData.put("endLocation", -1);
        Map<String, Object> dateTimeInfo = new HashMap<>();
        DateTime shiftStartDate = new DateTime(startDate).toDateTime(DateTimeZone.UTC);
        List<Map> tasks = taskService.findByStaffIdAndStartDate(staffId, shiftStartDate);
        if (tasks.size() != 0) {
            Task firstTaskOfDay = (Task) taskService.findOne(String.valueOf(tasks.get(0).get("_id")));
            Task lastTaskOfDay = (Task) taskService.findOne(String.valueOf(tasks.get(tasks.size() - 1).get("_id")));
            DateTime startDateTime = new DateTime(firstTaskOfDay.getStartDate()).toDateTime(DateTimeZone.UTC);
            Date reducedEndTime = DateUtils.getDeductionInTimeDuration(firstTaskOfDay.getStartDate(), lastTaskOfDay.getEndDate(), organization.getDayShiftTimeDeduction(), organization.getNightShiftTimeDeduction());
            DateTime endDateTime = new DateTime(reducedEndTime).toDateTime(DateTimeZone.UTC);
            dateTimeInfo.put(START_DATE, startDate); //Assigning Absence starting from tomorrow
            dateTimeInfo.put(END_DATE, lastTaskOfDay.getEndDate()); //till day after tomorrow
            dateTimeInfo.put("startTime", startDateTime.hourOfDay().get()); //Assigning Absence starting from tomorrow
            dateTimeInfo.put("endTime", endDateTime.hourOfDay().get()); //till day after tomorrow
            dateTimeInfo.put("startTimeMinute", startDateTime.minuteOfHour().get()); //Assigning Absence starting from tomorrow
            dateTimeInfo.put("endTimeMinute", endDateTime.minuteOfHour().get()); //till day after tomorrow
            //workScheduleResult = scheduler.assignAbsencesToFLS(workScheduleMetaData, dateTimeInfo, flsCredentials);

            //return workScheduleResult;
        }
        return -1;
    }

    public TaskDTO copyProperties(Task task, TaskDTO taskDTO) {

        taskDTO.setId(task.getId().toString());
        taskDTO.setResource(task.getStaffId());
        taskDTO.setTaskTypeId(task.getTaskTypeId().toString());
        taskDTO.setStartDate(task.getStartDate());
        taskDTO.setEndDate(task.getEndDate());
        return taskDTO;
    }

    public void createAndSyncSubTask(Task task, OrganizationDTO organization) {
        try {
            Task subTask = null;
            TaskDTO taskDTO = new TaskDTO();
            List<BigInteger> subTaskIds = task.getSubTaskIds();
            if (task.getStartAddress().toString().equals(task.getEndAddress().toString()) == false) {

                if (subTaskIds.size() == 0) subTask = new Task();
                else subTask = taskMongoRepository.findOne(subTaskIds.get(0));
                subTask = Task.copyProperties(task, Task.getInstance());
                subTask.setActive(true);
                subTask.setDeleted(true);
                Calendar c = Calendar.getInstance();
                c.setTime(task.getEndDate());
                c.add(Calendar.MINUTE, -1);
                subTask.setStartDate(c.getTime());
                subTask.setStartAddress(task.getEndAddress());
                taskService.save(subTask);

                copyProperties(subTask, taskDTO);
                syncPartialAbsencesTask(taskDTO, organization);

            } else if (subTaskIds.size() > 0) {
                subTask = taskMongoRepository.findOne(subTaskIds.get(0));
                subTask.setActive(false);
                subTask.setDeleted(true);
                taskService.save(subTask);
                copyProperties(subTask, taskDTO);
                syncPartialAbsencesTask(taskDTO, organization);

            }
        } catch (Exception exception) {

        }

    }
}
