package com.kairos.service.task_type;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kairos.activity.cta.CTAResponseDTO;
import com.kairos.activity.shift.StaffUnitPositionDetails;
import com.kairos.activity.task.AbsencePlanningStatus;
import com.kairos.activity.task.TaskDTO;
import com.kairos.config.env.EnvConfig;
import com.kairos.constants.AppConstants;
import com.kairos.enums.Day;
import com.kairos.enums.task_type.TaskTypeEnum;
import com.kairos.messaging.ReceivedTask;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.activity.Shift;
import com.kairos.persistence.model.client_exception.ClientException;
import com.kairos.persistence.model.task.Task;
import com.kairos.persistence.model.task.TaskAddress;
import com.kairos.persistence.model.task.TaskStatus;
import com.kairos.persistence.model.task_demand.TaskDemand;
import com.kairos.persistence.model.task_type.AddressCode;
import com.kairos.persistence.model.task_type.TaskType;
import com.kairos.persistence.model.task_type.TaskTypeDefination;
import com.kairos.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.persistence.repository.cta.CostTimeAgreementRepository;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.persistence.repository.client_exception.ClientExceptionMongoRepository;
import com.kairos.persistence.repository.client_exception.ClientExceptionMongoRepositoryImpl;
import com.kairos.persistence.repository.common.CustomAggregationOperation;
import com.kairos.persistence.repository.common.CustomAggregationQuery;
import com.kairos.persistence.repository.common.MongoSequenceRepository;
import com.kairos.persistence.repository.repository_impl.TaskMongoRepositoryImpl;
import com.kairos.persistence.repository.task_type.TaskDemandMongoRepository;
import com.kairos.persistence.repository.task_type.TaskMongoRepository;
import com.kairos.persistence.repository.task_type.TaskTypeMongoRepository;
import com.kairos.rest_client.*;
import com.kairos.serializers.MongoDateMapper;
import com.kairos.service.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.fls_visitour.schedule.Scheduler;
import com.kairos.service.fls_visitour.schedule.TaskConverterService;
import com.kairos.service.pay_out.PayOutCalculationService;
import com.kairos.service.pay_out.PayOutService;
import com.kairos.service.planner.TasksMergingService;
import com.kairos.service.time_bank.TimeBankService;
import com.kairos.rule_validator.task.MergeTaskSpecification;
import com.kairos.rule_validator.task.TaskDaySpecification;
import com.kairos.rule_validator.TaskSpecification;
import com.kairos.rule_validator.task.TaskStaffTypeSpecification;
import com.kairos.user.client.Client;
import com.kairos.user.country.day_type.DayType;
import com.kairos.user.organization.OrganizationDTO;
import com.kairos.user.patient.PatientResourceList;
import com.kairos.user.staff.*;
import com.kairos.user.user.staff.StaffAdditionalInfoDTO;
import com.kairos.util.*;
import com.kairos.util.external_plateform_shift.GetWorkShiftsFromWorkPlaceByIdResponse;
import com.kairos.util.external_plateform_shift.GetWorkShiftsFromWorkPlaceByIdResult;
import com.kairos.util.time_bank.TimeBankCalculationService;
import com.kairos.util.user_context.UserContext;
import com.kairos.vrp.task.VRPTaskDTO;
import com.kairos.wrapper.EscalatedTasksWrapper;
import com.kairos.wrapper.TaskWrapper;
import com.kairos.wrapper.task.StaffAssignedTasksWrapper;
import com.kairos.wrapper.task.TaskClientExceptionWrapper;
import com.kairos.wrapper.task.TaskGanttDTO;
import org.apache.poi.ss.usermodel.Cell;
import org.bson.Document;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.inject.Inject;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.kairos.constants.AppConstants.CITIZEN_ID;
import static com.kairos.constants.AppConstants.FORWARD_SLASH;
import static com.kairos.enums.task_type.TaskTypeEnum.TaskTypeStaff.EXCLUDED_EMPLOYEES;
import static com.kairos.enums.task_type.TaskTypeEnum.TaskTypeStaff.PREFERRED_EMPLOYEES;
import static com.kairos.persistence.model.constants.TaskConstants.*;
import static java.time.ZoneId.systemDefault;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

/**
 * Created by prabjot on 17/10/16.
 */
@Transactional
@Service
public class TaskService extends MongoBaseService {

    private static final Logger logger = LoggerFactory.getLogger(TaskService.class);

    @Inject
    private TaskMongoRepository taskMongoRepository;
    @Inject
    private TaskConverterService taskConverterService;
    @Inject
    private TaskTypeMongoRepository taskTypeMongoRepository;
    @Inject
    private TaskDemandMongoRepository taskDemandMongoRepository;
    @Inject
    private MongoTemplate mongoTemplate;

    @Inject
    private Scheduler scheduler;
    @Inject
    private TaskTypeService taskTypeService;

    @Inject
    private MongoSequenceRepository mongoSequenceRepository;
    @Inject
    private TaskMongoRepositoryImpl taskMongoRepositoryImpl;
    @Inject
    private ClientExceptionMongoRepositoryImpl clientExceptionRepositoryImpl;
    @Inject
    private CountryRestClient countryRestClient;
    @Inject
    private IntegrationRestClient integrationServiceRestClient;
    @Inject
    private EnvConfig envConfig;
    @Inject
    private TasksMergingService tasksMergingService;

    @Inject
    private ControlPanelRestClient controlPanelRestClient;
    @Inject
    private OrganizationRestClient organizationRestClient;
    @Inject
    private TimeCareRestClient timeCareRestClient;
    @Inject
    private ClientExceptionMongoRepository clientExceptionMongoRepository;
    @Inject
    private StaffRestClient staffRestClient;
    @Inject
    private ShiftMongoRepository shiftMongoRepository;
    @Inject
    private ActivityMongoRepository activityMongoRepository;
    @Inject private TimeBankService timeBankService;
    @Inject
    private TimeBankCalculationService timeBankCalculationService;
    @Inject
    private PayOutService payOutService;
    @Inject
    private PayOutCalculationService payOutCalculationService;
    @Inject
    private ExceptionService exceptionService;
    @Inject private CostTimeAgreementRepository costTimeAgreementRepository;

    public List<Long> getClientTaskServices(Long clientId, long orgId) {
        logger.info("Fetching tasks for ClientId: " + clientId);
        List response = new ArrayList();


//        String query  = "db.tasks.aggregate([{$match:{'citizenId':3439 , 'unitId': 136}},{$lookup:{from:'task_types',localField:'taskTypeId', foreignField:'_id', as:'taskTypes'}},{$unwind:'$taskTypes'},{$group:{_id:null, taskTypesList:{$addToSet:'$taskTypes.subServiceId'}}}]).pretty()\n";

        String matchStaffId = " {'$match':{'citizenId' :" + clientId + " , 'unitId': " + orgId + " }  }";
        String lookup = "{'$lookup':{'from':'task_types','localField':'taskTypeId','foreignField':'_id','as':'taskTypes'}}";
        String unwind = "{ '$unwind': '$taskTypes'  }";
        String group = "{'$group':{'_id':null, 'taskTypesList':{'$addToSet':'$taskTypes.subServiceId'}}}";


        Document groupObject = Document.parse(group);
        Document matchServiceObj = Document.parse(matchStaffId);
        Document unwindObj = Document.parse(unwind);
        Document lookObj = Document.parse(lookup);

        // Aggregate from DbObjects
        Aggregation aggregation = newAggregation(
                new CustomAggregationOperation(matchServiceObj),
                new CustomAggregationOperation(lookObj),
                new CustomAggregationOperation(unwindObj),
                new CustomAggregationOperation(groupObject)
        );
        logger.info("Query: " + aggregation.toString());

        // Result
        AggregationResults<Map> finalResult = mongoTemplate.aggregate(aggregation, TaskDemand.class, Map.class);

        // Mapped Result
        List<Map> mappedResult = finalResult.getMappedResults();
        if (mappedResult == null) {
            return null;
        }
        logger.debug("mappedResult: " + mappedResult.size());
        for (Map<String, Object> map : mappedResult) {
            logger.info("Data: " + map);
            response = (List) map.get("taskTypesList");
        }
        return Optional.ofNullable(response).orElse(Collections.emptyList());
    }

    public List<Object> getTaskByServiceId(Long clientId, Long serviceId, Long unitId) {
        logger.info("Fetching tasks for Service: " + serviceId);
        List<Long> serviceIds = new ArrayList<>();
        List<Object> response = new ArrayList<>();
        Map<String, String> taskMap = null;
        serviceIds.add(serviceId);
        LocalDate upcomingMonday = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY));
        LocalDate fourWeekLater = upcomingMonday.plusDays(28);
        Date dateFrom = Date.from(upcomingMonday.atStartOfDay().atZone(systemDefault()).toInstant());
        Date dateTo = Date.from(fourWeekLater.atStartOfDay().atZone(systemDefault()).toInstant());

        String matchServiceId = " {$match:{'subServiceId': {'$in':" + serviceIds + "} } }";
        String group = "{'$group':{'_id':null, 'taskl':{'$push':'$task_list'}}}";
        String unwind = "{ '$unwind': {  'path': '$task_list', 'preserveNullAndEmptyArrays':true } }";
        String lookup = "{'$lookup':{'from':'tasks','localField':'_id','foreignField':'taskTypeId','as':'task_list'}}";
        String matchClient = "{$match:{task_list.citizenId:" + clientId + "}} ";
        String matchUnit = "{$match:{task_list.unitId:" + unitId + "}} ";
        String sort = "{$sort: {task_list.citizenId: -1}}";

        Criteria criteria = Criteria.where("task_list.unitId").is(unitId).and("task_list.citizenId").is(clientId).and("task_list.dateFrom").gt(dateFrom).and("task_list.dateTo").lte(dateTo);

        Document groupObject = Document.parse(group);
        Document matchServiceObj = Document.parse(matchServiceId);
        Document unwindObj = Document.parse(unwind);
        Document lookObj = Document.parse(lookup);
        Document matchClientObj = Document.parse(matchClient);
        Document matchUnitObj = Document.parse(matchUnit);
        Document sortObj = Document.parse(sort);

        logger.debug("Match Client: " + matchClientObj.toString());
        logger.debug("Match Unit: " + matchUnitObj.toString());

        // Aggregate from DbObjects
        Aggregation aggregation = newAggregation(

                new CustomAggregationOperation(matchServiceObj),
                new CustomAggregationOperation(lookObj),
                new CustomAggregationOperation(unwindObj),
                match(criteria),
                new CustomAggregationOperation(sortObj),
                limit(5 * 10),
                new CustomAggregationOperation(groupObject)

        );
        logger.info("Query: " + aggregation.toString());

        // Result
        mongoTemplate.indexOps(Task.class).
                ensureIndex(new Index().on("taskTypeId", Sort.Direction.ASC));
        AggregationResults<Map> finalResult = mongoTemplate.aggregate(aggregation, TaskType.class, Map.class);

        // Mapped Result
        List<Map> mappedResult = finalResult.getMappedResults();


        logger.info("Preparing response..");
        for (Map data : mappedResult) {
            //logger.debug("Data: " + data);

            List<Task> taskList = (List<Task>) data.get("taskl");
            if (taskList != null) {
                response.addAll(taskList);
            }
        }

/*        logger.info("Preparing response..");
        Date startDate = null;
        Date endDate = null;
        for (Map data: mappedResult) {
            logger.debug("Data: "+data);
            List<Task> taskList = (List<Task>) data.get("taskl");
            if (taskList!=null){
                for (Task task:taskList) {
                    taskMap = new HashMap<>();
                    // name
                    // description
                    // Delivered by
                    // TaskType
                    if(task!=null){
                        taskMap.put( "name", task.getName());
                        taskMap.put( "status", task.getStatus().value);
                        taskMap.put( "priority", String.valueOf(task.getPriority()));
                        if(task.getDateFrom() != null && task.getTimeFrom() != null){
                            startDate = task.getDateFrom();
                            startDate.setHours(task.getTimeFrom().getHours());
                            startDate.setMinutes(task.getTimeFrom().getMinutes());
                            startDate.setSeconds(task.getTimeFrom().getSeconds());
                        }
                        if(task.getDateTo() != null && task.getTimeTo() != null){
                            endDate = task.getDateTo();
                            endDate.setHours(task.getTimeTo().getHours());
                            endDate.setMinutes(task.getTimeTo().getMinutes());
                            endDate.setSeconds(task.getTimeTo().getSeconds());
                        }
                        taskMap.put( "startDate",startDate!=null ? startDate.toString() : null);
                        taskMap.put( "endDate",endDate!=null ? endDate.toString(): null);
                        taskMap.put( "supplier", organization.getName());
                        taskMap.put( "info1", task.getInfo1());
                        taskMap.put( "info2", task.getInfo2());
                        response.add(taskMap);
                    }
                }
            }
        }*/
        return response;
    }

    public List<Task> getTaskByDates(long clientId, DateTime date, Long unitId) {
        logger.info("Fetching tasks for Date: " + date);

        Date dateStart = DateUtils.getStartOfDay(date.toDateTime(DateTimeZone.UTC).toDate());
        Date dateEnd = DateUtils.getEndOfDay(date.toDateTime(DateTimeZone.UTC).toDate());

        String dateS = DateUtils.getISODateString(dateStart);
        String dateE = DateUtils.getISODateString(dateEnd);


        List<Task> response = new ArrayList<>();
        Map<String, String> taskMap = null;

        logger.debug("Fetching tasks for StartDate: " + dateS);
        logger.debug("Fetching tasks for StartEnd: " + dateE);

        String matchClientAndUnitAndDate = "{ '$match' : {'citizenId':" + clientId + "," + " 'unitId':" + unitId + "," + " 'dateFrom' : {  $gte:{ '$date' : '" + dateS + "'}  }   , 'dateTo':   {   $lt: {'$date' :'" + dateE + "'}   }  }    }";
        //// TODO: 16/3/17 Apply isEnabled Check


        Document matchObject = Document.parse(matchClientAndUnitAndDate);

        // Aggregate from DbObjects
        Aggregation aggregation = newAggregation(
                new CustomAggregationOperation(matchObject)
        );
        logger.info("Query: " + aggregation.toString());


        // Result
        AggregationResults<Task> finalResult = mongoTemplate.aggregate(aggregation, Task.class, Task.class);

        // Mapped Result
        List<Task> mappedResult = finalResult.getMappedResults();

        logger.info("Preparing response..");
        for (Task data : mappedResult) {

            logger.info("Data: " + "id:" + data.getId() + " " + data.getInfo1());
        }
        return mappedResult;
    }


    public List<Task> getTasksByDemandId(String taskDemandId) {
        return taskMongoRepository.findAllByTaskDemandIdAndIsDeleted(taskDemandId, false, new Sort(Sort.Direction.ASC, "dateFrom"));
    }

    public List<Task> getAllTasks() {
        return taskMongoRepository.findAll();
    }

    public Task findBySubTaskIds(String subTaskId) {
        return taskMongoRepository.findBySubTaskIdsAndIsDeleted(subTaskId, false);
    }


    /**
     * This method is used to create task coming from TimeCare and according to data coming from TimeCare.
     *
     * @param data
     * @return
     * @throws ParseException
     */
    public Task createTaskFromTimeCare(Map<String, Object> data, String requestFrom) throws ParseException {
        if (data != null) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
            Date startDate = simpleDateFormat.parse(data.get("startDate").toString());
            Date endDate = simpleDateFormat.parse(String.valueOf(data.get("endDate")));
            Date fixedDate = simpleDateFormat.parse(String.valueOf(data.get("startDate")));
            Date updateDate = simpleDateFormat.parse(String.valueOf(data.get("updateDate")));
            String externalId = String.valueOf(data.get("externalId"));
            String kmdExternalId = String.valueOf(data.get("kmdExternalId"));
            String lastSyncJobId = String.valueOf(data.get("lastSyncJobId"));
            AbsencePlanningStatus absencePlanningStatus = (AbsencePlanningStatus) data.get("absencePlanningStatus");

            Boolean isTaskTypeAnonymous = Boolean.valueOf(String.valueOf(data.get("isTaskTypeAnonymous")));
            Task task;

            switch (requestFrom) {
                case AppConstants.REQUEST_FROM_KMD:
                    task = taskMongoRepository.findByKmdExternalId(kmdExternalId);
                    break;
                case AppConstants.REQUEST_FROM_TIME_CARE:
                    task = taskMongoRepository.findByExternalId(externalId);
                    break;
                default:
                    task = taskMongoRepository.findByExternalId(externalId);
                    break;
            }
            if (task != null) {
                task.setDeleted(false);
                task.setLastSyncJobId(lastSyncJobId);
                save(task);// saving task to update updatedAt date on every sync
            }
            //if task in kairos has recent changes than the shift coming from time care in that case we will not update the task.
            if (task != null && task.getUpdatedAt().compareTo(updateDate) == 1) {
                return task;
            }

            if (task == null) task = new Task();
            if (isTaskTypeAnonymous == false) {
                task.setTaskTypeId(new BigInteger(data.get("taskTypeId") + ""));
                logger.debug("taskTypeId--object Id---> " + data.get("taskTypeId"));
            } else {
                Long taskTypeAnonymousId = Long.valueOf(String.valueOf(data.get("taskTypeAnonymousId")));
                task.setTaskTypeAnonymousId(taskTypeAnonymousId);
            }
            if (data.get("startAddress") != null && data.get("endAddress") != null) {
                String startAddress = data.get("startAddress").toString();
                String endAddress = data.get("endAddress").toString();
                task.setStartAddress(AddressCode.getKey(startAddress));
                task.setEndAddress(AddressCode.getKey(endAddress));
            }
            Long orgnaizationId = Long.valueOf(String.valueOf(data.get("organizationId")));
            Boolean isStaffAnonymous = Boolean.valueOf(String.valueOf(data.get("isStaffAnonymous")));
            Long staffId = null;
            if (data.get("staffId") != null) {
                staffId = Long.valueOf(String.valueOf(data.get("staffId")));
            }
            Long duration = Long.valueOf(String.valueOf(data.get("duration")));
            task.setTaskStatus(TaskStatus.GENERATED);
            task.setPriority(3);
            task.setFixed(true);
            task.setDeleted(false);
            task.setAbsencePlanningStatus(absencePlanningStatus);
            task.setFixedDate(fixedDate);
            if (isStaffAnonymous == false) task.setStaffId(staffId);
            else {
                Long staffAnonymousId = Long.valueOf(String.valueOf(data.get("staffAnonymousId")));
                task.setStaffAnonymousId(staffAnonymousId);
            }
            task.setUnitId(orgnaizationId);
            task.setExternalId(externalId);
            task.setDuration(Integer.valueOf(String.valueOf(TimeUnit.HOURS.toMinutes(duration))));
            task.setStartDate(startDate);
            task.setEndDate(endDate);
            task.setActive(true);
            task.setTimeCareExternalId(externalId);
            task.setStaffAnonymous(isStaffAnonymous);
            task.setTaskTypeAnonymous(isTaskTypeAnonymous);
            if (kmdExternalId != null) task.setKmdExternalId(kmdExternalId);

            save(task);

            //logger.info("Task----- created---> "+task);

            return task;

        }
        return null;
    }

    public List<Map<String, Object>> getStaffTasks(Long staffId, Long organizationId) {
        List<Task> tasks = taskMongoRepository.findByStaffIdAndUnitId(staffId, organizationId);
        //logger.info("Task----- created---> "+tasks);
        List<Map<String, Object>> mapList = new ArrayList<>();
        for (Task task : tasks) {
            Map<String, Object> map = new HashMap<>();
            // logger.info("Task---Type id-- ---> "+id);
            map.put("id", task.getId());
            map.put("name", taskTypeMongoRepository.findOne(task.getTaskTypeId()).getTitle());
            map.put("shifts", 1);
            map.put("start", task.getStartDate());
            map.put("taskTypeId", task.getTaskTypeId());
            map.put("end", task.getEndDate());
            map.put("citizenId", task.getCitizenId());
            map.put("organizationId", task.getUnitId());
            map.put("status", task.getTaskStatus());
            mapList.add(map);
        }
        return mapList;
    }

    public List<Map> findByStaffIdAndStartDate(Long staffId, DateTime startDate) {
        Document dbGroupObj = Document.parse(CustomAggregationQuery.sortingTaskByDate());
        DateTime startDateFrom = startDate.withTime(00, 00, 00, 00);
        DateTime startDateTo = startDate.withTime(23, 59, 59, 00);
        Aggregation aggregation = Aggregation.newAggregation(
                match(
                        Criteria.where("startDate").gte(startDateFrom).lte(startDateTo).and("staffId").is(staffId)
                ),
                new CustomAggregationOperation(dbGroupObj)
        );
        AggregationResults<Map> finalResult =
                mongoTemplate.aggregate(aggregation, Task.class, Map.class);
        return finalResult.getMappedResults();
        //  return taskMongoRepository.findByStaffIdAndStartDate(staffId, startDateFrom, startDateTo, new Sort(Sort.Direction.ASC, "startDate"));
    }


    public List<Task> findByCitizenId(Long clientId) {
        return taskMongoRepository.findByCitizenId(clientId);
    }

    public List<Task> getTaskByClientIdAndDate(DateTime date, Long id) {

        return null;
    }

    public List<Map> tasksNotUpdatedInLastSync(String jobId) {
        DateTime startDateFrom = new DateTime(DateUtils.getDate(), DateTimeZone.forTimeZone(TimeZone.getTimeZone("Denmark")));
        Aggregation aggregation = Aggregation.newAggregation(
                match(
                        Criteria.where("lastSyncJobId").ne(jobId).and("isDeleted").is(false).and("timeCareExternalId").exists(true).and("startDate").gte(startDateFrom)
                )
        );
        AggregationResults<Map> finalResult =
                mongoTemplate.aggregate(aggregation, Task.class, Map.class);
        return finalResult.getMappedResults();
    }

    public void deleteTimeCareTask(Task task) {
        task.setDeleted(true);
        save(task);
    }

    public int syncDeleteTask(Date startDate, Date endDate, String fmextID, Map<String, String> flsCredentials) {
        int workScheduleResult;
        Boolean sendToFls = false; //Add task to
        Map<String, Object> workScheduleMetaData = new HashMap<>();
        workScheduleMetaData.put("fmextID", fmextID);
        workScheduleMetaData.put("startLocation", 0); //0=Start at home address (default), -1=Start at office address
        workScheduleMetaData.put("endLocation", 0);
        workScheduleMetaData.put("type", -1);
        Map<String, Object> dateTimeInfo = new HashMap<>();
        dateTimeInfo.put("startDate", startDate); //Assigning Absence starting from tomorrow
        dateTimeInfo.put("endDate", endDate); //till day after tomorrow
        dateTimeInfo.put("startTime", 0); //Assigning Absence starting from tomorrow
        dateTimeInfo.put("endTime", 0); //till day after tomorrow
        dateTimeInfo.put("startTimeMinute", 0); //Assigning Absence starting from tomorrow
        dateTimeInfo.put("endTimeMinute", 0); //till day after tomorrow
        workScheduleResult = scheduler.assignAbsencesToFLS(workScheduleMetaData, dateTimeInfo, flsCredentials);
        return workScheduleResult;

    }

    public Task findByExternalId(String externalId) {
        return taskMongoRepository.findByExternalId(externalId);
    }

    public Task findOne(String id) {
        return taskMongoRepository.findOne(new BigInteger(id));
    }

    public Map countGeneratedPlannedTasks(String taskDemandId) {
        LocalDate upcomingMonday = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY));
        LocalDate fourWeekLater = upcomingMonday.plusDays(28);
        Date dateFrom = Date.from(upcomingMonday.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        Date dateTo = Date.from(fourWeekLater.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        String groupByTaskStatus = "{'$group':{'_id':'$taskStatus', 'count':{'$sum':1}}}";
        Document group = Document.parse(groupByTaskStatus);
        Aggregation agg = Aggregation.newAggregation(
                match(Criteria.where("taskDemandId").is(taskDemandId).and("dateFrom").gte(dateFrom).and("dateTo").lte(dateTo).and("taskOriginator").ne("ACTUAL_PLANNING").and("relatedTaskId").exists(false)),
                new CustomAggregationOperation(group)
        );
        AggregationResults<Map> result =
                mongoTemplate.aggregate(agg, Task.class, Map.class);
        List<Map> listData = result.getMappedResults();
        Map returnData = new HashMap();
        int counter = 0; // Initialize counter
        for (Map countWithStatus : listData) {
            counter = counter + Integer.parseInt(countWithStatus.get("count") + ""); // This counter counts total tasks
            if (countWithStatus.get("_id") != null) // Ignore null
                returnData.put(countWithStatus.get("_id"), countWithStatus.get("count")); // Put number of count with task status
        }
        returnData.put("TOTAL", counter); // Set total task counter in response
        return returnData;
    }

    public List getStaffTaskTypes(Object staffId) {
        logger.info("Fetching tasks for staffId: " + staffId);

        List response = null;

//      String query = "db.tasks.aggregate([{$match:{staffId:6091}},{$lookup:{from:'task_types',localField:'taskTypeId', foreignField:'_id', as:'taskTypes'}},{$unwind:'$taskTypes'},{$group:{_id:null, taskTypesList:{$addToSet:'$taskTypes.title'}}}]).pretty()";

        String matchStaffId = " {'$match':{'staffId' :" + staffId + "}  }";
        String lookup = "{'$lookup':{'from':'task_types','localField':'taskTypeId','foreignField':'_id','as':'taskTypes'}}";
        String unwind = "{ '$unwind': '$taskTypes'  }";
        String group = "{'$group':{'_id':null, 'taskTypesList':{'$addToSet':{'title':'$taskTypes.title','id':'$taskTypes.id', 'color':'$taskTypes.colorForGantt' }}}}";
        Criteria criteria = Criteria.where("staffId").is(staffId).and("citizenId").ne(0);


        Document groupObject = Document.parse(group);
        Document matchServiceObj = Document.parse(matchStaffId);
        Document unwindObj = Document.parse(unwind);
        Document lookObj = Document.parse(lookup);

        // Aggregate from DbObjects
        Aggregation aggregation = newAggregation(
                // new CustomAggregationOperation(matchServiceObj),
                match(criteria),
                new CustomAggregationOperation(lookObj),
                new CustomAggregationOperation(unwindObj),
                new CustomAggregationOperation(groupObject)
        );
        logger.debug("Query: " + aggregation.toString());

        // Result
        AggregationResults<Map> finalResult = mongoTemplate.aggregate(aggregation, Task.class, Map.class);

        // Mapped Result
        List<Map> mappedResult = finalResult.getMappedResults();
        if (mappedResult == null) {
            return null;
        }
        for (Map<String, Object> map : mappedResult) {
            logger.debug("Data: " + map);
            response = (List) map.get("taskTypesList");
        }
        return response;
    }

    public List<String> importShiftsFromTimeCare(GetWorkShiftsFromWorkPlaceByIdResponse timeCareShifts) {
        List<String> skippedShiftsWhileSave = new ArrayList<>();
        Optional<Long> workPlaceId = timeCareShifts.getGetWorkShiftsFromWorkPlaceByIdResult().stream().map(getWorkShiftsFromWorkPlaceByIdResult ->
                getWorkShiftsFromWorkPlaceByIdResult.getWorkPlaceId()).findFirst();
        Optional<String> personExternalId = timeCareShifts.getGetWorkShiftsFromWorkPlaceByIdResult().stream().map(
                getWorkShiftsFromWorkPlaceByIdResult ->
                        getWorkShiftsFromWorkPlaceByIdResult.getStaffId()).findFirst();

        Optional<String> personExternalEmploymentId = timeCareShifts.getGetWorkShiftsFromWorkPlaceByIdResult().stream().map(
                getWorkShiftsFromWorkPlaceByIdResult ->
                        getWorkShiftsFromWorkPlaceByIdResult.getEmploymentId()).findFirst();

        if (!workPlaceId.isPresent() || !personExternalId.isPresent() || !personExternalEmploymentId.isPresent()) {
            exceptionService.internalError("error.timecare.workplaceid.personid.person-external-employment-id");
        }
        OrganizationStaffWrapper organizationStaffWrapper = organizationRestClient.getOrganizationAndStaffByExternalId(String.valueOf(workPlaceId.get()), personExternalId.get(), personExternalEmploymentId.get());
        StaffDTO staffDTO = organizationStaffWrapper.getStaff();
        OrganizationDTO organizationDTO = organizationStaffWrapper.getOrganization();

        if (organizationDTO == null) {
            exceptionService.dataNotFoundByIdException("message.organization.externalid",workPlaceId.get());
        }
        if (!Optional.ofNullable(staffDTO).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.organization.external-staffid",personExternalId.get());
        }
        if (!Optional.ofNullable(organizationStaffWrapper.getUnitPosition()).isPresent()) {
            exceptionService.internalError("error.kairos.unitposition",personExternalId.get());
        }
        List<GetWorkShiftsFromWorkPlaceByIdResult> shiftsFromTimeCare = timeCareShifts.getGetWorkShiftsFromWorkPlaceByIdResult();
        int sizeOfTimeCareShifts = shiftsFromTimeCare.size();
        int skip = 0;
        if (sizeOfTimeCareShifts > MONOGDB_QUERY_RECORD_LIMIT) {
            do {
                saveShifts(skip, shiftsFromTimeCare, organizationDTO.getId(), staffDTO.getId(), organizationStaffWrapper.getUnitPosition(), skippedShiftsWhileSave);
                skip += MONOGDB_QUERY_RECORD_LIMIT;
            } while (skip <= sizeOfTimeCareShifts);
        } else {
            saveShifts(skip, shiftsFromTimeCare, organizationDTO.getId(), staffDTO.getId(), organizationStaffWrapper.getUnitPosition(), skippedShiftsWhileSave);
        }
        return skippedShiftsWhileSave;
    }

    private Shift mapTimeCareShiftDataToKairos(GetWorkShiftsFromWorkPlaceByIdResult timeCareShift, Long workPlaceId) {

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
        Shift shift = objectMapper.convertValue(timeCareShift, Shift.class);
        shift.setId(null);
        shift.setExternalId(timeCareShift.getId());
        shift.setUnitId(workPlaceId);
        return shift;
    }

    private void saveShifts(int skip, List<GetWorkShiftsFromWorkPlaceByIdResult> shiftsFromTimeCare, Long workPlaceId, Long staffId,UnitPositionDTO unitPositionDTO , List<String> skippedShiftsWhileSave) {
        List<String> externalIdsOfShifts = shiftsFromTimeCare.stream().skip(skip).limit(MONOGDB_QUERY_RECORD_LIMIT).map(timeCareShift -> timeCareShift.getId()).
                collect(Collectors.toList());
        List<String> externalIdsOfActivities = shiftsFromTimeCare.stream().skip(skip).limit(MONOGDB_QUERY_RECORD_LIMIT).map(timeCareShift -> timeCareShift.getActivityId()).
                collect(Collectors.toList());
        List<Shift> shiftsInKairos = shiftMongoRepository.findByExternalIdIn(externalIdsOfShifts);
        List<Activity> activities = activityMongoRepository.findByUnitIdAndExternalIdInAndDeletedFalse(workPlaceId, externalIdsOfActivities);
        List<GetWorkShiftsFromWorkPlaceByIdResult> timeCareShiftsByPagination = shiftsFromTimeCare.stream().skip(skip).limit(MONOGDB_QUERY_RECORD_LIMIT).collect(Collectors.toList());
        List<Shift> shiftsToCreate = new ArrayList<>();
        StaffUnitPositionDetails staffUnitPositionDetails = new StaffUnitPositionDetails(unitPositionDTO.getWorkingDaysInWeek(),unitPositionDTO.getTotalWeeklyMinutes());
        StaffAdditionalInfoDTO staffAdditionalInfoDTO = staffRestClient.verifyUnitEmploymentOfStaff(staffId, AppConstants.ORGANIZATION, unitPositionDTO.getId());
        CTAResponseDTO ctaResponseDTO = costTimeAgreementRepository.getCTAByUnitPositionId(staffAdditionalInfoDTO.getUnitPosition().getId(),new Date());
        staffAdditionalInfoDTO.getUnitPosition().setCtaRuleTemplates(ctaResponseDTO.getRuleTemplates());
        staffUnitPositionDetails.setFullTimeWeeklyMinutes(unitPositionDTO.getFullTimeWeeklyMinutes());
        Map<String,Activity> activityMap = activities.stream().collect(Collectors.toMap(k->k.getExternalId(),v->v));
        for (GetWorkShiftsFromWorkPlaceByIdResult timeCareShift : timeCareShiftsByPagination) {
            Shift shift = shiftsInKairos.stream().filter(shiftInKairos -> shiftInKairos.getExternalId().equals(timeCareShift.getId())).findAny().orElse(mapTimeCareShiftDataToKairos
                    (timeCareShift, workPlaceId));
            Activity activity = activityMap.get(timeCareShift.getActivityId());
            if (!Optional.ofNullable(activity).isPresent()) {
                skippedShiftsWhileSave.add(timeCareShift.getId());
            } else {
                shift.setName(activity.getName());
                shift.setActivityId(activity.getId());
                shift.setStaffId(staffId);
                shift.setUnitPositionId(unitPositionDTO.getId());
                List<Integer> activityDayTypes = new ArrayList<>();
                if (staffAdditionalInfoDTO.getDayTypes() != null && !staffAdditionalInfoDTO.getDayTypes().isEmpty()) {
                    activityDayTypes = WTARuleTemplateValidatorUtility.getValidDays(staffAdditionalInfoDTO.getDayTypes(), activity.getTimeCalculationActivityTab().getDayTypes());
                }
                if (activityDayTypes.contains(new DateTime(shift.getStartDate()).getDayOfWeek())) {
                    timeBankCalculationService.calculateScheduleAndDurationHour(shift, activity, staffAdditionalInfoDTO.getUnitPosition());
                }
                shiftsToCreate.add(shift);
            }

        }
        if (!shiftsToCreate.isEmpty()) {
            save(shiftsToCreate);
            timeBankService.saveTimeBanks(staffAdditionalInfoDTO, shiftsToCreate);
            payOutService.savePayOuts(staffAdditionalInfoDTO, shiftsToCreate,activities);
        }
    }


    /*public boolean createTasksFromTimeCare(GetWorkShiftsFromWorkPlaceByIdResponse shifts,Long controlPanelId){

        //moved code into control panel controller
         *//*String jobId = controlPanelService.getRecentJobId(controlPanelId);
         Long unitId = controlPanelService.getControlPanelUnitId(controlPanelId);
         Map<String, String> flsCredentials = integrationService.getFLS_Credentials(unitId);*//*

        ControlPanelDTO controlPanelDTO = controlPanelRestClient.getRequiredControlPanelData(controlPanelId);
        return   createTasksFromTimeCare(shifts, controlPanelDTO.getJobId(), controlPanelDTO.getFlsCredentails());

    }*/

    /*public Boolean createTasksFromTimeCare(GetWorkShiftsFromWorkPlaceByIdResponse shifts, String jobId, Map<String, String> flsCredentials){


        try {
            logger.info("flsCredentials-----------> " + flsCredentials);
            for (GetWorkShiftsFromWorkPlaceByIdResult shift : shifts.getGetWorkShiftsFromWorkPlaceByIdResult()) {
                logger.info("Shift ID getting from Time care is----> " + shift.getId());
                OrganizationDTO parentOrganization = organizationRestClient.getOrganizationAndStaffByExternalId(shift.getPerson().getParentWorkPlaceId().toString());

                if (parentOrganization != null && shift.getPerson().getParentWorkPlaceId().toString().equals("14") == true) {

                    TimeCareOrganizationDTO timeCareOrganizationDTO = timeCareRestClient.getPrerequisitesForTimeCareTask(shift);

                    OrganizationContactAddress organizationContactData = timeCareOrganizationDTO.getOrganizationContactAddress();
                    ContactAddressDTO officeAddress = organizationContactData.getContactAddress();
                    if (officeAddress == null) {
                        throw new InternalError("organization address is null");
                    }
                    ZipCode officeZipCode = organizationContactData.getZipCode();

                    if (officeZipCode == null) {
                        throw new InternalError("office zip code can not null");
                    }

                    Boolean isTaskTypeAnonymous = false;
                    TaskType taskType = taskTypeService.findByExternalId(shift.getActivity().getId());
                    //logger.info("taskType---------> " + taskType);
                    if (taskType == null) isTaskTypeAnonymous = true;
                    // else logger.info("taskType exist---> " + taskType.getId());

                    StaffDTO staff = timeCareOrganizationDTO.getStaff(); // need to change externalId
                    Boolean isStaffAnonymous = false;
                    AddressCode startAddress = null;
                    AddressCode endAddress = null;

                    AbsencePlanningStatus absencePlanningStatus = AbsencePlanningStatus.NOT_SYNCHRONISED;


                    if (staff != null && Long.valueOf(staff.getVisitourId()) != null) {
                        //we should uncomment when Absence Planning functionality completed
                        int returnedValue = -1;
                        //Add Engineer to Fls
                        Map<String, Object> engineerMetaData = new HashMap<>();

                        //General Info
                        engineerMetaData.put("fmvtid", staff.getVisitourId());    //If the engineer is not found in the VISITOUR database, he will be created, otherwise he will be updated.
                        engineerMetaData.put("fmextID", staff.getId());   //If the engineer is not found in the VISITOUR database, he will be created, otherwise he will be updated.
                        engineerMetaData.put("prename", staff.getFirstName());
                        engineerMetaData.put("name", staff.getLastName());
                        //Address1 (Home)
                        //  engineerMetaData.put("scountry", staff.getContactAddress().getCountryId());
                        engineerMetaData.put("scountry", "DK");
                        engineerMetaData.put("szip", officeZipCode.getZipCode());
                        engineerMetaData.put("scity", officeAddress.getCity());
                        engineerMetaData.put("sstreet", officeAddress.getStreet() + " " + officeAddress.getHouseNumber());

                        //Address2 (Office)
                        engineerMetaData.put("ecountry", "DK");
                        engineerMetaData.put("ezip", officeZipCode.getZipCode());
                        engineerMetaData.put("ecity", officeAddress.getCity());
                        engineerMetaData.put("estreet", officeAddress.getStreet() + " " + officeAddress.getHouseNumber());

                        //Contact Data
                        engineerMetaData.put("email", staff.getEmail());


                        returnedValue = scheduler.createEngineer(engineerMetaData, flsCredentials);
                    } else {
                        isStaffAnonymous = true;

                    }

                    int workScheduleResult;
                    Boolean sendToFls = false; //Add task to
                    Map<String, Object> workScheduleMetaData = new HashMap<>();
                    Map<String, Object> dateTimeInfo = new HashMap<>();
                    Date reducedEndTime = DateUtils.getDeductionInTimeDuration(shift.getStartDateTime(), shift.getEndDateTime(), parentOrganization.getDayShiftTimeDeduction(), parentOrganization.getNightShiftTimeDeduction());
                    dateTimeInfo.put("startDate", shift.getStartDateTime()); //Assigning Absence starting from tomorrow
                    dateTimeInfo.put("endDate", reducedEndTime); //till day after tomorrow

                    if (staff != null) workScheduleMetaData.put("fmextID", staff.getId());

                    Boolean fullDayAbsence = false;


                    Map<String, Object> taskMetaData = new HashMap<>();
                    taskMetaData.put("startDate", shift.getStartDateTime());
                    taskMetaData.put("endDate", shift.getEndDateTime());
                    taskMetaData.put("externalId", shift.getId());
                    taskMetaData.put("organizationId", timeCareOrganizationDTO.getOrganization().getId());
                    if (isStaffAnonymous == false) {
                        taskMetaData.put("staffId", staff.getId());

                    } else {
                        taskMetaData.put("staffAnonymousId", shift.getPersonId());
                        taskMetaData.put("staffAnonymousGender", shift.getPerson().getGender());
                        absencePlanningStatus = AbsencePlanningStatus.ERROR;
                    }
                    if (isTaskTypeAnonymous == false) {
                        taskMetaData.put("taskTypeId", taskType.getId());
                    } else {
                        taskMetaData.put("taskTypeAnonymousId", shift.getActivityId());
                        absencePlanningStatus = AbsencePlanningStatus.ERROR;
                    }
                    taskMetaData.put("duration", Math.round(shift.getLength()));
                    taskMetaData.put("isStaffAnonymous", isStaffAnonymous);
                    taskMetaData.put("isTaskTypeAnonymous", isTaskTypeAnonymous);
                    taskMetaData.put("absencePlanningStatus", absencePlanningStatus);
                    taskMetaData.put("updateDate", shift.getUpdateDate());
                    taskMetaData.put("lastSyncJobId", jobId);
                    if (startAddress != null && endAddress != null) {
                        taskMetaData.put("startAddress", startAddress);
                        taskMetaData.put("endAddress", endAddress);
                    }
                    Task task = createTaskFromTimeCare(taskMetaData, AppConstants.REQUEST_FROM_TIME_CARE);


                    if (task != null && isStaffAnonymous) {
                        //        notificationService.addStaffMissingNotification(task, STAFF_MISSING_MESSAGE, STAFF_MISSING_STATUS);
                    }
                    if (task != null && isTaskTypeAnonymous) {
                        //       notificationService.addStaffMissingNotification(task, TASK_TYPE_MISSING_MESSAGE, TASK_TYPE_MISSING_STATUS);
                    }
                    if (task != null && task.getAddress() == null) {
                        //       notificationService.addStaffMissingNotification(task, LOCATION_MISSING_MESSAGE, LOCATION_MISSING_STATUS);
                    }

                    //When shift is of type Full day Absence or Presence we need to update Engineer's Working hours
                    if (taskType != null && isStaffAnonymous == false) {

                        //CASE1 FULL DAY ABSENCE TASKS
                        if (taskType.getTaskTypeSchedule().equals(AppConstants.FULL_DAY) && taskType.getTaskTypeVisibility().equals(AppConstants.ABSENT)) {
                            //   logger.info(" Activity is of type Full Day Absence");
                            AbsenceTypes absenceTypes = timeCareOrganizationDTO.getAbsenceTypes();
                            //    logger.info("Absence Types------------> " + absenceTypes.getATVTID());
                            workScheduleMetaData.put("type", absenceTypes.getATVTID());
                            //  else workScheduleMetaData.put("type", absenceTypes.getATVTID());b   .;l;4

                            //Ranging 1-40 : Whole day absence according to the VISITOUR master data, eg. holiday, illness.
                            workScheduleMetaData.put("info", "Testing Absence");
                            workScheduleMetaData.put("startLocation", 0); //0=Start at home address (default), -1=Start at office address
                            workScheduleMetaData.put("endLocation", 0);
                            startAddress = AddressCode.HOME;
                            endAddress = AddressCode.HOME;
                            sendToFls = true;
                            fullDayAbsence = true;
                        }

                        //CASE2 PARTIAL DAY ABSENCE
                        if (taskType.getTaskTypeSchedule().equals(AppConstants.PARTIALLY) && taskType.getTaskTypeVisibility().equals(AppConstants.ABSENT)) {
                            //   logger.info("Partially absent case--------!!! ");
                            sendToFls = true;
                        }

                        //CASE3 Presence
                        if (taskType.getTaskTypeVisibility().equals(AppConstants.PRESENT)) {
                            //   logger.info(" Activity is of type Presence");
                            workScheduleMetaData.put("type", -1);
                            //   workScheduleMetaData.put("type", 0); //Ranging 1-40 : Whole day absence according to the VISITOUR master data, eg. holiday, illness.
                            workScheduleMetaData.put("info", "Testing Available");
                            sendToFls = true;
                            workScheduleMetaData.put("startLocation", -1); //0=Start at home address (default), -1=Start at office address
                            workScheduleMetaData.put("endLocation", -1);
                            DateTime startDateTime = new DateTime(shift.getStartDateTime()).toDateTime(DateTimeZone.UTC);
                            DateTime endDateTime = new DateTime(reducedEndTime).toDateTime(DateTimeZone.UTC);
                            dateTimeInfo.put("startTime", startDateTime.hourOfDay().get()); //Assigning Absence starting from tomorrow
                            dateTimeInfo.put("endTime", endDateTime.hourOfDay().get()); //till day after tomorrow
                            dateTimeInfo.put("startTimeMinute", startDateTime.minuteOfHour().get()); //Assigning Absence starting from tomorrow
                            dateTimeInfo.put("endTimeMinute", endDateTime.minuteOfHour().get()); //till day after tomorrow
                            startAddress = AddressCode.UNIT;
                            endAddress = AddressCode.UNIT;

                        }


                        if (sendToFls) {
                            //     logger.info("fullDayAbsence-------------> " + fullDayAbsence);
                            if (!fullDayAbsence) {
                                DateTime shiftStartDate = new DateTime(shift.getStartDateTime()).toDateTime(DateTimeZone.UTC);
                                List<Map> tasks = findByStaffIdAndStartDate(staff.getId(), shiftStartDate);
                                logger.info("Tasks of this date " + shiftStartDate + " of Staff name--> " + staff.getFirstName() + " " + staff.getLastName() + " is " + tasks.size());
                                if (tasks.size() != 0) {
                                    Task firstTaskOfDay = (Task) findOne(String.valueOf(tasks.get(0).get("_id")));
                                    Task lastTaskOfDay = findOne(String.valueOf(tasks.get(tasks.size() - 1).get("_id")));
                                    DateTime startDateTime = new DateTime(firstTaskOfDay.getStartDate()).toDateTime(DateTimeZone.UTC);
                                    logger.info("startDateTime-------------> " + startDateTime);
                                    reducedEndTime = DateUtils.getDeductionInTimeDuration(firstTaskOfDay.getStartDate(), lastTaskOfDay.getEndDate(), parentOrganization.getDayShiftTimeDeduction(), parentOrganization.getNightShiftTimeDeduction());

                                    DateTime endDateTime = new DateTime(reducedEndTime).toDateTime(DateTimeZone.UTC);
                                    logger.info("reducedEndTime--------------> " + endDateTime);
                                    logger.info("endDateTime--------------> " + new DateTime(lastTaskOfDay.getEndDate()).toDateTime(DateTimeZone.UTC));
                                    dateTimeInfo.put("startTime", startDateTime.hourOfDay().get());
                                    dateTimeInfo.put("startTimeMinute", startDateTime.minuteOfHour().get());
                                    if (startDateTime.getDayOfMonth() != endDateTime.getDayOfMonth() && endDateTime.hourOfDay().get() == 0 && endDateTime.minuteOfHour().get() == 0) {
                                        dateTimeInfo.put("endDate", shift.getEndDate());
                                        dateTimeInfo.put("endTime", 23);
                                        dateTimeInfo.put("endTimeMinute", 59);
                                        dateTimeInfo.put("startTimeSeconds", 0);
                                        dateTimeInfo.put("endTimeSeconds", 59);
                                    } else {
                                        dateTimeInfo.put("endTime", endDateTime.hourOfDay().get());
                                        dateTimeInfo.put("endTimeMinute", endDateTime.minuteOfHour().get());
                                    }
                                }
                            }
                            //     logger.info("workScheduleMetaData-------------> " + workScheduleMetaData);
                            //    logger.info("dateTimeInfo--------------> " + dateTimeInfo);

                            //   workScheduleResult = scheduler.assignAbsencesToFLS(workScheduleMetaData, dateTimeInfo);
                            //    logger.info("workScheduleResult------------------> " + workScheduleResult);


                            workScheduleResult = scheduler.assignAbsencesToFLS(workScheduleMetaData, dateTimeInfo, flsCredentials);

                            //     logger.info("workScheduleResult------------------> " + workScheduleResult);

                            //if tasks get deleted from FLS
       *//*     Thread.sleep(2000);
            List<Map> nonSyncTasks = taskService.tasksNotUpdatedInLastSync(jobId.toString());
            logger.info("nonSyncTasks------in last job---> "+nonSyncTasks.size());
            if(nonSyncTasks != null) {
                for (Map task : nonSyncTasks) {
                    Task nonSyncTask = (Task) taskService.findByExternalId(String.valueOf(task.get("externalId")));
                    String staffId = String.valueOf(nonSyncTask.getStaff());
                    DateTime nonSyncTaskDate = new DateTime(nonSyncTask.getStartDate());
                    Date startDate = nonSyncTask.getStartDate();
                    Date endDate = nonSyncTask.getEndDate();
                    taskService.deleteTimeCareTask(nonSyncTask);
                    List<Map> tasks = taskService.findByStaffIdAndStartDate(Long.valueOf(staffId), nonSyncTaskDate);
                    if(tasks.size() == 0)  taskService.syncDeleteTask(startDate, endDate, staffId, null);

                }
            }*//*

                        }
                    } else {
                        logger.info("TaskType " + shift.getActivity().getName() + " not found for this shift " + shift.getId() + " , please add this taskType in Kairos!! ");
                    }
                }
            }
            return true;
        } catch (ParseException parseException) {
            return false;
        }
    }*/

    public void syncFourthWeekTasks(LocalDateTime startDate) {

        logger.info("SyncFourthWeekTasks Job starting at" + LocalDateTime.now());

            /*LocalDate today = LocalDate.now();
            LocalDate fourthWeekStartDate = today.plusDays(22);
            LocalDate fourthWeekEndDate = today.plusDays(28);*/

        /*LocalDate fourthWeekStartDate = LocalDate.of(2017, 07, 10);
        logger.info("fourthWeekStartDate " + fourthWeekStartDate);
        LocalDate fourthWeekEndDate = LocalDate.of(2017, 07, 10);
        logger.info("fourthWeekEndDate " + fourthWeekEndDate);*/

        /*Date fromDate = Date.from(fourthWeekStartDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date toDate = Date.from(fourthWeekEndDate.atStartOfDay(ZoneId.systemDefault()).toInstant());*/

        LocalDateTime localDateFrom = startDate;
        LocalDateTime localDateTo = localDateFrom.plusDays(1);


        for (int i = 1; i <= 7; i++) {

            logger.debug("LocalDateFrom " + localDateFrom);
            logger.debug("LocalDateTo   " + localDateTo);

            Date fromDate = Date.from(localDateFrom.atZone(ZoneId.systemDefault()).toInstant());
            Date toDate = Date.from(localDateTo.atZone(ZoneId.systemDefault()).toInstant());


            Criteria criteria = Criteria.where("visitourId").exists(false).orOperator(Criteria.where("visitourId").is(null));
            criteria.and("dateFrom").gte(fromDate).and("dateTo").lt(toDate).and("isSubTask").is(false).and("isDeleted").is(false);
            criteria.and("taskOriginator").is(TaskTypeEnum.TaskOriginator.PRE_PLANNING).and("taskStatus").ne(TaskStatus.CANCELLED);

            String lookup = "{'$lookup':{'from':'task_types','localField':'taskTypeId','foreignField':'_id','as':'taskTypeList'}}";
            String unwind = "{ '$unwind': '$taskTypeList'  }";
            String group = "{'$group':{'_id':'$taskTypeList.organizationId', 'taskList':{'$push':'$$ROOT'}}}";

            Document groupObject = Document.parse(group);
            Document unwindObj = Document.parse(unwind);
            Document lookObj = Document.parse(lookup);


            // Aggregate from DbObjects
            Aggregation aggregation = newAggregation(
                    match(criteria),
                    new CustomAggregationOperation(lookObj),
                    new CustomAggregationOperation(unwindObj),
                    new CustomAggregationOperation(groupObject)
            );
            logger.debug("SyncFourthWeekTasks Job  Query: " + aggregation.toString());

            // Result
            AggregationResults<Map> finalResult = mongoTemplate.aggregate(aggregation, Task.class, Map.class);

            List<Map> taskListWithUnitIdGroup = finalResult.getMappedResults();

            for (Map<String, Object> map : taskListWithUnitIdGroup) {

                Map<String, String> flsCredentials = getFLS_Credentials((long) map.get("_id"));

                if (flsCredentials.get("flsDefaultUrl") != "" && flsCredentials.get("userpassword") != "") {

                    //List tasks = new ArrayList();
                    List tasks = (List) map.get("taskList");
                    //tasks.addAll(existingList);

                    List<Task> tasksToSync = new ArrayList<>();
                    for (Object object : tasks) {
                        ObjectMapper mapper = new ObjectMapper();
                        Task task = mapper.convertValue(object, Task.class);
                        tasksToSync.add(task);
                    }

                    logger.debug("tasksToSync: size " + tasksToSync.size());

                    if (tasksToSync.size() > 0) {
                        taskConverterService.createFlsCallFromTasks(tasksToSync, flsCredentials);

                        Map<String, Object> datePayload = new HashMap<>();
                        datePayload.put("startDate", fromDate);
                        datePayload.put("endDate", toDate);
                        Map<String, Object> openCall = new HashMap<>();
                        openCall.put("openCallsMode", "2");
                        scheduler.optmizeSchedule(openCall, datePayload, flsCredentials);
                    }
                } else {
                    logger.info("FLS Credentials Missing for Unit Id " + map.get("_id"));
                }
            }

            localDateFrom = localDateFrom.plusDays(1);
            localDateTo = localDateTo.plusDays(1);
        }

    }

    /**
     * @param organizationId
     * @return
     * @auther anil maurya
     */
    private Map<String, String> getFLS_Credentials(long organizationId) {
        Map<String, String> flsCredential = integrationServiceRestClient.getFLS_Credentials(organizationId);
       /* Visitour visitour = visitourGraphRepository.findByOrganizationId(organizationId);
        Map<String, String> credentials = new HashMap<>();
        String url = (visitour != null) ? visitour.getServerName() : "";
        String userPass = (visitour != null) ? visitour.getUsername() + ":" + visitour.getPassword() : "";
        credentials.put("flsDefaultUrl", url);
        credentials.put("userpassword", userPass);*/
        return flsCredential;
    }

    public Task updateTaskStatus(ReceivedTask receivedTask) {
        Task task = taskMongoRepository.findOne(new BigInteger(receivedTask.getExtid()));
        if (task != null) {
            task.setExternalId(receivedTask.getVtid());
            switch (receivedTask.getStatus()) {
                case 1:
                    task.setTaskStatus(TaskStatus.GENERATED);
                    break;
                case 2:
                    task.setTaskStatus(TaskStatus.CONFIRMED);
                    break;
                case 3:
                    task.setTaskStatus(TaskStatus.FIXED);
                    break;
                case 4:
                    task.setTaskStatus(TaskStatus.DRIVING);
                    break;
                case 5:
                    task.setTaskStatus(TaskStatus.ARRIVED);
                    break;
                case 6:
                    task.setTaskStatus(TaskStatus.FINISHED);
                    break;
                default:
                    task.setStatus(task.getStatus());

            }
            // task.setStaff(Long.parseLong(receivedTask.getFMExtID() != null ? receivedTask.getFMExtID() : "-1"));
            Optional<String> receivedTaskOptional = Optional.ofNullable(receivedTask.getFMExtID());

            if (receivedTaskOptional.isPresent()) {
                List<Long> assingedStaffIds = Stream.of(receivedTaskOptional.get().split(",")).map(Long::parseLong).collect(Collectors.toList());
                task.setAssignedStaffIds(assingedStaffIds);
            } else {
                task.setAssignedStaffIds(Collections.EMPTY_LIST);
            }
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            logger.info("privious start date" + task.getStartDate());
            task.setDateFrom(receivedTask.getEarliestStartTime());
            task.setExecutionDate(receivedTask.getLatestStartTime());
            logger.info("new start date" + task.getStartDate());
            Calendar cal = Calendar.getInstance();
            cal.setTime(receivedTask.getLatestStartTime());
            cal.add(Calendar.MINUTE, receivedTask.getPlannedDuration());
            task.setDateTo(cal.getTime());
            task.setInfo1(receivedTask.getInfoText());
            task.setUpdatedAt(DateUtils.getDate());
            save(task);
            return task;
        } else {
            return null;
        }
    }

    public List<Map<String, Object>> getStaffTasks(long staffId) {
        // TODO : We need to find Tasks assigned to this Staff. NOT TASK DEMAND.
        List<TaskDemand> taskDemands = taskDemandMongoRepository.findByCreatedByStaffId(staffId);
        List<Map<String, Object>> mapList = new ArrayList<>();
        for (TaskDemand demand : taskDemands) {
            List<Task> tasks = getTasksByDemandId(demand.getId() + "");
            logger.info("Number of Tasks in this demand " + tasks.size());
            for (Task task : tasks) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", task.getId());
                map.put("name", taskTypeMongoRepository.findOne(demand.getTaskTypeId()).getTitle());
                // map.put("shifts", task.getTaskFrequency());
                map.put("start", task.getStartDate());
                map.put("taskTypeId", demand.getTaskTypeId());
                map.put("end", task.getEndDate());
                map.put("citizenId", task.getCitizenId());
                //map.put("organizationId", task.getUnitID());
                mapList.add(map);
            }
        }
        return mapList;
    }

    public void savePreplanningStateOfTask(Task task) {
        ObjectMapper objectMapperWithSerializer = MongoDateMapper.objectMapper();
        Task preplanningState = Task.getInstance();
        Task.copyProperties(task, preplanningState);
        task.setActualPlanningTask(objectMapperWithSerializer.convertValue(preplanningState, Map.class));
    }

    private TaskGanttDTO customTask(Task task) {


        ObjectMapper objectMapper = new ObjectMapper();
        TaskGanttDTO taskGanttDTO = objectMapper.convertValue(task, TaskGanttDTO.class);
        taskGanttDTO.setTaskTypeIconUrl(envConfig.getServerHost() + FORWARD_SLASH + "cleaningIcon.png");
        Date resourceDate;
        if (task.getTaskStatus() == TaskStatus.PLANNED) {
            resourceDate = task.getExecutionDate();

        } else {
            resourceDate = task.getDateFrom();
            resourceDate.setHours(task.getTimeFrom().getHours());
            resourceDate.setMinutes(task.getTimeFrom().getMinutes());
            resourceDate.setSeconds(0);
        }

        if (!task.isSingleTask() && task.getActualPlanningTask() != null) {
            taskGanttDTO.setEditable(false);
        }

        taskGanttDTO.setResource(resourceDate);
        taskGanttDTO.setLocationChanged(task.isLocationChanged());
        if (Optional.ofNullable(task.getTaskTypeId()).isPresent()) {
            TaskType taskType = taskTypeService.getTaskTypeById(String.valueOf(task.getTaskTypeId()));
            if (Optional.ofNullable(taskType).isPresent()) {
                TaskTypeDefination taskTypeDefination = taskType.getDefinations();
                if (Optional.ofNullable(taskTypeDefination).isPresent())
                    taskGanttDTO.setChangeLocationNotAllowed(taskTypeDefination.isHasPrimaryAddress());
            }
        }


        taskGanttDTO.setStartHour(resourceDate.getHours());
        taskGanttDTO.setStartMinute(resourceDate.getMinutes());

        resourceDate = DateUtils.addMinutes(resourceDate, task.getDuration());
        taskGanttDTO.setEndHour(resourceDate.getHours());
        taskGanttDTO.setEndMinute(resourceDate.getMinutes());
        Map<String, Object> timeWindow = new HashMap<>();
        timeWindow.put("duration", TimeUnit.MINUTES.toSeconds(task.getSlaStartDuration()));
        taskGanttDTO.setTimeWindow(timeWindow);
        return taskGanttDTO;
    }

    public List<TaskGanttDTO> customizeTaskData(List<Task> taskList) {
        List<TaskGanttDTO> responseList = new ArrayList<>();
        for (Task task : taskList) {

            TaskGanttDTO response = customTask(task);

            List<TaskGanttDTO> customSubTaskList = new ArrayList<>();
            if (task.getSubTaskIds() != null && task.getSubTaskIds().size() > 0) {
                List<Task> subTasks = taskMongoRepository.findByIdIn(task.getSubTaskIds(), new Sort(Sort.Direction.ASC, "timeFrom"));

                for (Task subTask : subTasks) {
                    TaskGanttDTO subTaskResponse = customTask(subTask);
                    customSubTaskList.add(subTaskResponse);
                }
            }

            response.setSubTasks(customSubTaskList);

            responseList.add(response);
        }
        return responseList;
    }

    public void updateTaskDuration(Task task, boolean reduction, Integer percentageDuration) {
        LocalDateTime timeTo = LocalDateTime.ofInstant(task.getTimeTo().toInstant(), ZoneId.systemDefault());
        int minutes = task.getDuration() * percentageDuration / 100;
        logger.info("percentage of duration :: " + minutes + "   bulkUpdateTaskDTO.isReduced()  " + reduction);
        if (reduction) {
            if (task.getDuration() - minutes <= 0) {
                exceptionService.internalError("error.task.duration");
            }
            timeTo = timeTo.minusMinutes(minutes);
            task.setTimeTo(Date.from(timeTo.atZone(ZoneId.systemDefault()).toInstant()));
            task.setDuration(task.getDuration() - minutes);
        } else {
            timeTo = timeTo.plusMinutes(percentageDuration);
            task.setTimeTo(Date.from(timeTo.atZone(ZoneId.systemDefault()).toInstant()));
            task.setDuration(task.getDuration() + minutes);
        }
    }


    public Task findByKmdTaskExternalId(String kmdTaskExternalId) {
        return taskMongoRepository.findByKmdExternalId(kmdTaskExternalId);
    }

    public Task saveTask(Task task) {
        return save(task);
    }


    public List<StaffAssignedTasksWrapper> getAssignedTasksOfStaff(String date, long staffId, long unitId) {
        LocalDate localDate = LocalDate.parse(date);
        LocalDateTime startOfDay = localDate.atStartOfDay();
        LocalDateTime endOfDay = startOfDay.withHour(DAY_END_HOUR).withMinute(DAY_END_MINUTE).withSecond(DAY_END_SECOND).withNano(DAY_END_NANO);
        Date startOfDayAsDate = Date.from(startOfDay.atZone(ZoneId.systemDefault()).toInstant());
        Date endOfDayAsDate = Date.from(endOfDay.atZone(ZoneId.systemDefault()).toInstant());
        List<StaffAssignedTasksWrapper> tasks = taskMongoRepository.getStaffAssignedTasks(unitId, staffId, startOfDayAsDate, endOfDayAsDate);
        return Optional.ofNullable(tasks).orElse(new ArrayList<>());
    }

    /**
     * @param unitId
     * @return
     * @auther anil maurya
     */
    public List<EscalatedTasksWrapper> getStaffNotAssignedTasksByUnitId(Long unitId) {
        List<EscalatedTasksWrapper> escalatedTasksWrappers = taskMongoRepository.getStaffNotAssignedTasksGroupByCitizen(unitId);
        return escalatedTasksWrappers;

    }

    @Scheduled(cron = "0 15 10 ? * *")
    public void generateTaskFromScheduler() {
        List<TaskDemand> taskDemands = taskDemandMongoRepository.getTaskDemandWhichTaskCreatedTillDateNotNull();
        LocalDate now = LocalDate.now();
        Date toDate = Date.from(now.plusMonths(1).atStartOfDay(systemDefault()).toInstant());
        Date fromDate = Date.from(now.minusMonths(11).atStartOfDay(systemDefault()).toInstant());
        /* while(dbCursor.hasNext()){
             DBObject dbObject = dbCursor.next();
             ObjectMapper objectMapper = new ObjectMapper();
             TaskDemand taskDemand = objectMapper.convertValue(dbObject, TaskDemand.class);
             createNewTasksOfGivenTaskDemandsAndBetweenDates( taskDemand, fromDate, toDate);
         }*/
        for (TaskDemand taskDemand : taskDemands) {
            createNewTasksOfGivenTaskDemandsAndBetweenDates(taskDemand, fromDate, toDate);
        }

    }

    private void createNewTasksOfGivenTaskDemandsAndBetweenDates(TaskDemand taskDemand, Date fromDate, Date toDate) {
        List<Task> tasks = taskMongoRepository.findAllByTaskDemandAndBetweenDates(taskDemand.getId().longValue(), fromDate, toDate);
        List<Task> updatedTasks = new ArrayList<Task>();
        tasks.parallelStream().forEach(task -> {
            updatedTasks.add(TaskUtil.copyPropertiesOfTask(task));
        });
        if (!updatedTasks.isEmpty()) save(updatedTasks);
        updatedTasks.clear();


    }


    /**
     * @param staffId
     * @param anonymousStaffId
     * @return
     * @auther anil maurya
     * this method is called from user micro service
     */
    public boolean updateTaskForStaff(Long staffId, Long anonymousStaffId) {

        List<Task> tasks = taskMongoRepository.findByStaffAnonymousId(anonymousStaffId);
        if (tasks != null) {
            for (Task task : tasks) {
                task.setStaffId(staffId);
                task.setStaffAnonymous(false);
                task.setStaffAnonymousId(null);
                save(task);
            }
        }
        return true;

    }

    /**
     * @param staffId
     * @return
     * @auther anil maurya
     */
    public Map<String, Object> getStaffTasks(Long staffId) {

        List taskTypeData = getStaffTaskTypes(staffId);
        if (taskTypeData != null) {
            Map<String, Object> completeData = new HashMap<>();
            completeData.put("taskTypes", taskTypeData);
            logger.info("Complete data: " + completeData);
            return completeData;
        }
        return null;
    }


    /**
     * @param staffId
     * @param shift
     * @param unitId
     * @auther anil maurya
     * This method is use in citizen controller
     */

    public void createTaskFromKMD(Long staffId, ImportShiftDTO shift, Long unitId) {
        try {
            TaskType taskType = taskTypeService.findByExternalId("6123");
            Map<String, Object> taskMetaData = new HashMap<>();
            taskMetaData.put("startDate", shift.getStartTime());
            taskMetaData.put("endDate", shift.getEndTime());
            taskMetaData.put("updateDate", DateUtils.getDate());
            taskMetaData.put("kmdExternalId", shift.getId());
            taskMetaData.put("externalId", shift.getId());
            taskMetaData.put("organizationId", unitId);
            taskMetaData.put("staffId", staffId);
            taskMetaData.put("taskTypeId", taskType.getId());
            taskMetaData.put("duration", DateUtils.getTimeDuration(shift.getStartTime(), shift.getEndTime()));
            taskMetaData.put("startAddress", -1);
            taskMetaData.put("endAddress", -1);
            createTaskFromTimeCare(taskMetaData, AppConstants.REQUEST_FROM_KMD);
        } catch (ParseException exception) {
            logger.warn("Exception Occur while saving shifts from KMD----> " + exception.getMessage());

        } catch (Exception e) {
            logger.warn("Exception Occur while saving shifts from KMD----> " + e.getMessage());
        }
    }


    /**
     * TODO use rest template for getting staff and client
     *
     * @param filterId
     * @param unitId
     * @auther anil maurya
     * this method come from citizen service bcs its performing action on task
     */

    public void getTasks(Long filterId, Long unitId) {
        RestTemplate loginTemplate = new RestTemplate();
        HttpMessageConverter formHttpMessageConverter = new FormHttpMessageConverter();
        HttpMessageConverter stringHttpMessageConverterNew = new StringHttpMessageConverter();
        loginTemplate.getMessageConverters().add(formHttpMessageConverter);
        loginTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        loginTemplate.getMessageConverters().add(stringHttpMessageConverterNew);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + AppConstants.KMD_NEXUS_ACCESS_TOKEN);
        //   headers.add("Content-Type" , "application/json");
        logger.info("Auth token--------> " + AppConstants.KMD_NEXUS_ACCESS_TOKEN);
        Map map = new HashMap<String, String>();
        map.put("Content-Type", "application/json");
        //   headers.setAll(map);
        HttpEntity<String> headersElements = new HttpEntity<String>(headers);
        logger.info("headers------headersElements-----> " + headersElements);
        ResponseEntity<String> responseEntity = loginTemplate.exchange(String.format(AppConstants.KMD_NEXUS_CALENDAR_STAFFS_SHIFT_FILTER, filterId), HttpMethod.POST, headersElements, String.class);
        JSONObject jsonObject = new JSONObject(responseEntity.getBody());
        JSONArray jsonArray = jsonObject.getJSONArray("eventResources");

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject objects = jsonArray.getJSONObject(i);
            ImportTaskDTO kmdTask = JsonUtils.toObject(objects.toString(), ImportTaskDTO.class);
            Integer grantCount = 0;
            TaskDemand taskDemand = null;
            Client citizen = null;
            // Staff staff = null;
            List<Long> staffIds = new ArrayList<>();
            TaskAddress taskAddress = createTaskAddress(kmdTask);
            List<String> taskIds = new ArrayList<>();
            if (kmdTask.getPatientResourceList() != Collections.EMPTY_LIST) {
                for (PatientResourceList patientResourceList : kmdTask.getPatientResourceList()) {
                    for (Grants grants : patientResourceList.getGrants()) {
                        String grantId = grants.getId();
                        taskDemand = taskDemandMongoRepository.findByKmdExternalId(String.valueOf(grantId));
                        //citizen = clientGraphRepository.findOne(taskDemand.getCitizenId());
                        if (citizen == null) return;
                        TaskType taskType = taskTypeMongoRepository.findOne(taskDemand.getTaskTypeId());
                        if (taskDemand == null) return;
                        grantCount += 1;
                        String staffExternalId = kmdTask.getResourceId();
                        staffExternalId = staffExternalId.substring(staffExternalId.indexOf("PROFESSIONAL:") + 13);
                        //anil me will use rest template here
                        //staff = staffGraphRepository.findByKmdExternalId(Long.valueOf(staffExternalId));
                        // staffIds.add(staff.getId());
                        Task task = createKMDPlannedTask(kmdTask, taskType, taskDemand, taskAddress, staffIds);
                        taskIds.add(task.getId().toString());
                    }

                }
                if (grantCount > 1) {
                    String uniqueID = UUID.randomUUID().toString();
                    uniqueID = uniqueID.substring(0, uniqueID.indexOf("-"));
                    try {
                        Task task = tasksMergingService.mergeTasksWithIds(taskIds, taskDemand.getUnitId(), taskDemand.getCitizenId(), AppConstants.MERGED_TASK_NAME, false, uniqueID, taskAddress, null, Collections.EMPTY_LIST, Collections.EMPTY_LIST, null);
                        task.setAssignedStaffIds(staffIds);
                    } catch (CloneNotSupportedException exception) {
                        logger.warn("Exception occurs while merging Imported KMD Tasks----> " + exception.getMessage());
                    }
                }

            }

        }

    }


    public Task createKMDPlannedTask(ImportTaskDTO kmdTask, TaskType taskType, TaskDemand taskDemand,
                                     TaskAddress taskAddress, List<Long> staffIds) {
        //Single task

        Task task = findByKmdTaskExternalId(kmdTask.getId());
        if (task == null) task = new Task();
        task.setTaskOriginator(TaskTypeEnum.TaskOriginator.PRE_PLANNING);
        task.setName(taskType.getTitle());
        task.setColorForGantt(taskType.getColorForGantt());
        task.setCitizenId(taskDemand.getCitizenId());
        task.setPriority(taskDemand.getPriority());
        task.setSetupDuration(taskDemand.getSetupDuration());
        task.setTaskTypeId(taskType.getId());
        task.setTaskDemandId(taskDemand.getId());
        task.setTaskStatus(TaskStatus.GENERATED);
        task.setNumberOfStaffRequired(taskDemand.getStaffCount());
        task.setVisitourTaskTypeID(taskType.getVisitourId());
        task.setPreProcessingDuration(taskType.getPreProcessingDuration());
        task.setPostProcessingDuration(taskType.getPostProcessingDuration());
        //discuss with Jasgeet not required, as we are not going to sync these tasks with Visitour
        // task.setTeamId(citizen.getVisitourTeamId());
        task.setDateFrom(kmdTask.getStart());
        task.setDateTo(kmdTask.getEnd());
        task.setKmdTaskExternalId(kmdTask.getId());
        task.setTimeFrom(kmdTask.getStart());
        task.setTimeTo(kmdTask.getEnd());
        task.setAddress(taskAddress);
        task.setUnitId(taskDemand.getUnitId());
        task.setSlaStartDuration(taskType.getSlaStartDuration());
        task.setSlaEndDuration(taskType.getSlaEndDuration());
        task.setAssignedStaffIds(staffIds);
        return saveTask(task);

    }

    private TaskAddress createTaskAddress(ImportTaskDTO kmdTask) {
        TaskAddress taskAddress = new TaskAddress();
        CurrentAddress currentAddress = kmdTask.getPatientResourceList().get(0).getCurrentAddress();
        String addressLine1 = currentAddress.getAddressLine1();
        String street = addressLine1.substring(0, addressLine1.indexOf(" "));
        String hnr = addressLine1.substring(addressLine1.indexOf(" "));
        taskAddress.setCountry("DK");
        taskAddress.setZip(currentAddress.getPostalCode().intValue());
        taskAddress.setCity(currentAddress.getPostalDistrict());
        taskAddress.setStreet(street);
        taskAddress.setHouseNumber(hnr);
        return taskAddress;

    }

    public TaskClientExceptionWrapper getUnhandledTasksOfCurrentWeek(long citizendId, long unitId) {
        LocalDate startOfCurrentWeek = (LocalDate.now().getDayOfWeek().equals(DayOfWeek.MONDAY)) ? LocalDate.now() : DateUtils.getDateForPreviousDay(LocalDate.now(), DayOfWeek.MONDAY);
        LocalDate endOfCurrentWeek = startOfCurrentWeek.plusDays(6);
        Date startOfCurrentWeekAsDate = Date.from(startOfCurrentWeek.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endOfCurrentWeekAsDate = Date.from(endOfCurrentWeek.atStartOfDay(ZoneId.systemDefault()).toInstant());

        List<TaskWrapper> tasks = taskMongoRepository.getUnhandledTaskForMobileView(citizendId, unitId, startOfCurrentWeekAsDate,
                endOfCurrentWeekAsDate, new Sort(Sort.DEFAULT_DIRECTION, CITIZEN_ID));
        List<ClientException> clientExceptions = clientExceptionMongoRepository.getExceptionOfCitizenBetweenDates(citizendId, startOfCurrentWeekAsDate, endOfCurrentWeekAsDate, unitId);
        TaskClientExceptionWrapper taskClientExceptionWrapper = new TaskClientExceptionWrapper();
        taskClientExceptionWrapper.setClientExceptions(clientExceptions);
        taskClientExceptionWrapper.setTasks(tasks);
        return taskClientExceptionWrapper;

    }

    public Task assignGivenTaskToUser(BigInteger taskId) {
        Task pickTask = taskMongoRepository.findOne(taskId);
        Long userId = UserContext.getUserDetails().getId();
        StaffDTO staffDTO = staffRestClient.getStaffByUser(userId);
        List<Long> assignedStaffIds = pickTask.getAssignedStaffIds();
        if (!assignedStaffIds.contains(staffDTO.getId())) assignedStaffIds.add(staffDTO.getId());
        pickTask.setAssignedStaffIds(assignedStaffIds);
        save(pickTask);
        return pickTask;
    }

    public void validateTask(Task task) {
        TaskType taskType = taskTypeMongoRepository.findOne(task.getTaskTypeId());
        TaskSpecification<Task> mergeTaskSpecification = new MergeTaskSpecification(taskType.isMainTask());
        boolean excludeEmployees = taskType.getEmployees().contains(EXCLUDED_EMPLOYEES);
        boolean preferredEmployees = taskType.getEmployees().contains(PREFERRED_EMPLOYEES);
        TaskSpecification<Task> taskStaffSpecification = new TaskStaffTypeSpecification(excludeEmployees, preferredEmployees);

        List<DayType> dayTypes = countryRestClient.getDayTypes(taskType.getForbiddenDayTypeIds());

        Set<Day> days = new HashSet<>();
        for (DayType dayType : dayTypes) {
            days.addAll(dayType.getValidDays());
        }
        TaskSpecification<Task> taskDaySpecification = new TaskDaySpecification(days);
        TaskSpecification<Task> taskSpecification = taskDaySpecification.and(taskStaffSpecification).or(mergeTaskSpecification);
    }


    private Double getValue(Cell cell){
        Double value = null;
        if(cell.getCellType()==Cell.CELL_TYPE_NUMERIC){
            value =  cell.getNumericCellValue();
        }else {
            value = new Double(cell.getStringCellValue().replaceAll(",","."));

        }
        return value;
    }

    /*private List<Task> getVrpTasksByRows(List<Row> rows, Long unitId){
        List<VRPClientDTO> vrpClientDTOS = vrpRestClient.getAllVRPClient();
        Map<Integer,Long> installationAndCitizenId = vrpClientDTOS.stream().collect(Collectors.toMap(c->c.getInstallationNumber(),c->c.getId()));
        List<Task> tasks = new ArrayList<>();
        for (int i = 2;i<rows.size();i++){
            Row row = rows.get(i);
            Task task = new Task();
            TaskAddress taskAddress = new TaskAddress();
            taskAddress.setLatitude(getConstraintValue(row.getCell(14)).toString());
            taskAddress.setLongitude(getConstraintValue(row.getCell(14)).toString());
            taskAddress.setCity(row.getCell(13).getStringCellValue());
            taskAddress.setFloorNumber((int) row.getCell(10).getNumericCellValue());
            taskAddress.setHouseNumber( ""+row.getCell(8).getNumericCellValue());
            taskAddress.setZip(new Integer(row.getCell(12).getStringCellValue()));
            taskAddress.setStreet(row.getCell(7).getStringCellValue());
            taskAddress.setBlock(row.getCell(9).getStringCellValue());
            task.setAddress(taskAddress);
            task.setInstallationNumber(getConstraintValue(row.getCell(5)).intValue());
            task.setDuration((int) row.getCell(0).getNumericCellValue());
            task.setCitizenId(installationAndCitizenId.get(task.getInstallationNumber()));
            task.setUnitId(unitId);
            tasks.add(task);
        }
        return tasks;
    }*/

    public Boolean importTask(Long unitId, List<VRPTaskDTO> taskDTOS){
        // List<Row> rows = excelService.getRowsByXLSXFile(multipartFile,0);
        Set<String> skills = taskDTOS.stream().map(t->t.getSkill()).collect(Collectors.toSet());
        List<TaskType> taskTypes = taskTypeMongoRepository.findByName(unitId,new ArrayList<>(skills));
        Map<String,BigInteger> taskTypeIds = taskTypes.stream().collect(Collectors.toMap(t->t.getTitle(),t->t.getId()));
        Map<Long,BigInteger> installationNoAndTaskTypeId = taskMongoRepository.getAllTasksInstallationNoAndTaskTypeId(unitId);
        List<VRPTaskDTO> newTasks = new ArrayList<>();
        for (VRPTaskDTO task : taskDTOS) {
            if(!taskTypeIds.containsKey(task.getSkill())){
                exceptionService.dataNotFoundException("message.taskType.notExists",task.getSkill());
            }
            if(!installationNoAndTaskTypeId.containsKey(new Long(task.getInstallationNumber()+""+taskTypeIds.get(task.getSkill())))) {
                task.setUnitId(unitId);
                task.setTaskTypeId(taskTypeIds.get(task.getSkill()));
                newTasks.add(task);
            }
        }
        List<Task> tasks = ObjectMapperUtils.copyPropertiesOfListByMapper(newTasks,Task.class);//getVrpTasksByRows(rows,unitId);
        save(tasks);
        return true;
    }

    public List<VRPTaskDTO> getAllTask(Long unitId){
        List<VRPTaskDTO> tasks = taskMongoRepository.getAllTasksByUnitId(unitId);
        //return ObjectMapperUtils.copyPropertiesOfListByMapper(tasks,TaskDTO.class);
        return tasks;//new ArrayList<>();

    }

    public TaskDTO getTask(BigInteger taskId){
        Task task = taskMongoRepository.findOne(taskId);
        return ObjectMapperUtils.copyPropertiesByMapper(task,TaskDTO.class);
    }


}
