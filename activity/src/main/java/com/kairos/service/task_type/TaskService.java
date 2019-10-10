package com.kairos.service.task_type;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.config.env.EnvConfig;
import com.kairos.constants.AppConstants;
import com.kairos.dto.activity.cta.CTAResponseDTO;
import com.kairos.dto.activity.shift.StaffEmploymentDetails;
import com.kairos.dto.activity.task.AbsencePlanningStatus;
import com.kairos.dto.activity.task.TaskDTO;
import com.kairos.dto.planner.vrp.task.VRPTaskDTO;
import com.kairos.dto.user.client.Client;
import com.kairos.dto.user.organization.OrganizationDTO;
import com.kairos.dto.user.patient.PatientResourceList;
import com.kairos.dto.user.staff.*;
import com.kairos.dto.user.user.staff.StaffAdditionalInfoDTO;
import com.kairos.enums.task_type.TaskTypeEnum;
import com.kairos.messaging.ReceivedTask;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.client_exception.ClientException;
import com.kairos.persistence.model.phase.Phase;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.model.task.Task;
import com.kairos.persistence.model.task.TaskAddress;
import com.kairos.persistence.model.task.TaskStatus;
import com.kairos.persistence.model.task_demand.TaskDemand;
import com.kairos.persistence.model.task_type.AddressCode;
import com.kairos.persistence.model.task_type.TaskType;
import com.kairos.persistence.model.task_type.TaskTypeDefination;
import com.kairos.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.persistence.repository.client_exception.ClientExceptionMongoRepository;
import com.kairos.persistence.repository.common.CustomAggregationOperation;
import com.kairos.persistence.repository.common.CustomAggregationQuery;
import com.kairos.persistence.repository.cta.CostTimeAgreementRepository;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.persistence.repository.task_type.TaskDemandMongoRepository;
import com.kairos.persistence.repository.task_type.TaskMongoRepository;
import com.kairos.persistence.repository.task_type.TaskTypeMongoRepository;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.serializers.MongoDateMapper;
import com.kairos.service.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.pay_out.PayOutService;
import com.kairos.service.phase.PhaseService;
import com.kairos.service.planner.TasksMergingService;
import com.kairos.service.shift.ShiftService;
import com.kairos.service.time_bank.TimeBankService;
import com.kairos.utils.JsonUtils;
import com.kairos.utils.TaskUtil;
import com.kairos.utils.external_plateform_shift.GetWorkShiftsFromWorkPlaceByIdResponse;
import com.kairos.utils.external_plateform_shift.GetWorkShiftsFromWorkPlaceByIdResult;
import com.kairos.utils.user_context.UserContext;
import com.kairos.wrapper.EscalatedTasksWrapper;
import com.kairos.wrapper.TaskWrapper;
import com.kairos.wrapper.task.StaffAssignedTasksWrapper;
import com.kairos.wrapper.task.TaskClientExceptionWrapper;
import com.kairos.wrapper.task.TaskGanttDTO;
import org.bson.Document;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
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

import static com.kairos.constants.ActivityMessagesConstants.*;
import static com.kairos.constants.AppConstants.CITIZEN_ID;
import static com.kairos.constants.AppConstants.FORWARD_SLASH;
import static com.kairos.persistence.model.constants.TaskConstants.*;
import static java.time.ZoneId.systemDefault;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

/**
 * Created by prabjot on 17/10/16.
 */
@Transactional
@Service
public class TaskService extends MongoBaseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskService.class);
    public static final String TASK_TYPE_ID = "taskTypeId";
    public static final String DATE_FROM = "dateFrom";
    public static final String START_DATE = "startDate";
    public static final String END_DATE = "endDate";
    public static final String START_ADDRESS = "startAddress";
    public static final String END_ADDRESS = "endAddress";
    public static final String ORGANIZATION_ID = "organizationId";
    public static final String STAFF_ID = "staffId";
    public static final String DURATION = "duration";

    @Inject
    private TaskMongoRepository taskMongoRepository;
    @Inject
    private TaskTypeMongoRepository taskTypeMongoRepository;
    @Inject
    private TaskDemandMongoRepository taskDemandMongoRepository;
    @Inject
    private TaskTypeService taskTypeService;
    @Inject
    private UserIntegrationService userIntegrationService;
    @Inject
    private EnvConfig envConfig;
    @Inject
    private TasksMergingService tasksMergingService;
    @Inject
    private ClientExceptionMongoRepository clientExceptionMongoRepository;
    @Inject
    private ShiftMongoRepository shiftMongoRepository;
    @Inject
    private ActivityMongoRepository activityMongoRepository;
    @Inject private TimeBankService timeBankService;
    @Inject
    private PayOutService payOutService;
    @Inject
    private ExceptionService exceptionService;
    @Inject private CostTimeAgreementRepository costTimeAgreementRepository;
    @Inject private ShiftService shiftService;
    @Inject
    private PhaseService phaseService;

    public List<Long> getClientTaskServices(Long clientId, long orgId) {
        LOGGER.info("Fetching tasks for ClientId: " + clientId);
        List response = new ArrayList();

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
        LOGGER.info("Query: " + aggregation.toString());
        // Result
        AggregationResults<Map> finalResult = mongoTemplate.aggregate(aggregation, TaskDemand.class, Map.class);
        // Mapped Result
        List<Map> mappedResult = finalResult.getMappedResults();
        if (mappedResult == null) {
            return null;
        }
        LOGGER.debug("mappedResult: " + mappedResult.size());
        for (Map<String, Object> map : mappedResult) {
            LOGGER.info("Data: " + map);
            response = (List) map.get("taskTypesList");
        }
        return Optional.ofNullable(response).orElse(Collections.emptyList());
    }

    public List<Object> getTaskByServiceId(Long clientId, Long serviceId, Long unitId) {
        LOGGER.info("Fetching tasks for Service: " + serviceId);
        List<Long> serviceIds = new ArrayList<>();
        List<Object> response = new ArrayList<>();
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

        LOGGER.debug("Match Client: " + matchClientObj.toString());
        LOGGER.debug("Match Unit: " + matchUnitObj.toString());
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
        LOGGER.info("Query: " + aggregation.toString());
        // Result
        mongoTemplate.indexOps(Task.class).
                ensureIndex(new Index().on(TASK_TYPE_ID, Sort.Direction.ASC));
        AggregationResults<Map> finalResult = mongoTemplate.aggregate(aggregation, TaskType.class, Map.class);
        // Mapped Result
        List<Map> mappedResult = finalResult.getMappedResults();
        LOGGER.info("Preparing response..");
        for (Map data : mappedResult) {
            List<Task> taskList = (List<Task>) data.get("taskl");
            if (taskList != null) {
                response.addAll(taskList);
            }
        }
        return response;
    }

    public List<Task> getTasksByDemandId(String taskDemandId) {
        return taskMongoRepository.findAllByTaskDemandIdAndIsDeleted(taskDemandId, false, new Sort(Sort.Direction.ASC, DATE_FROM));
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
            Date startDate = simpleDateFormat.parse(data.get(START_DATE).toString());
            Date endDate = simpleDateFormat.parse(String.valueOf(data.get(END_DATE)));
            Date fixedDate = simpleDateFormat.parse(String.valueOf(data.get(START_DATE)));
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
                task.setTaskTypeId(new BigInteger(data.get(TASK_TYPE_ID) + ""));
                LOGGER.debug("taskTypeId--object Id---> " + data.get(TASK_TYPE_ID));
            } else {
                Long taskTypeAnonymousId = Long.valueOf(String.valueOf(data.get("taskTypeAnonymousId")));
                task.setTaskTypeAnonymousId(taskTypeAnonymousId);
            }
            if (data.get(START_ADDRESS) != null && data.get(END_ADDRESS) != null) {
                String startAddress = data.get(START_ADDRESS).toString();
                String endAddress = data.get(END_ADDRESS).toString();
                task.setStartAddress(AddressCode.getKey(startAddress));
                task.setEndAddress(AddressCode.getKey(endAddress));
            }
            Long orgnaizationId = Long.valueOf(String.valueOf(data.get(ORGANIZATION_ID)));
            Boolean isStaffAnonymous = Boolean.valueOf(String.valueOf(data.get("isStaffAnonymous")));
            Long staffId = null;
            if (data.get(STAFF_ID) != null) {
                staffId = Long.valueOf(String.valueOf(data.get(STAFF_ID)));
            }
            Long duration = Long.valueOf(String.valueOf(data.get(DURATION)));
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
            return task;
        }
        return null;
    }

    public List<Map<String, Object>> getStaffTasks(Long staffId, Long organizationId) {
        List<Task> tasks = taskMongoRepository.findByStaffIdAndUnitId(staffId, organizationId);
        List<Map<String, Object>> mapList = new ArrayList<>();
        for (Task task : tasks) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", task.getId());
            map.put("name", taskTypeMongoRepository.findOne(task.getTaskTypeId()).getTitle());
            map.put("shifts", 1);
            map.put("start", task.getStartDate());
            map.put(TASK_TYPE_ID, task.getTaskTypeId());
            map.put("end", task.getEndDate());
            map.put("citizenId", task.getCitizenId());
            map.put(ORGANIZATION_ID, task.getUnitId());
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
                        Criteria.where(START_DATE).gte(startDateFrom).lte(startDateTo).and(STAFF_ID).is(staffId)
                ),
                new CustomAggregationOperation(dbGroupObj)
        );
        AggregationResults<Map> finalResult =
                mongoTemplate.aggregate(aggregation, Task.class, Map.class);
        return finalResult.getMappedResults();
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
                match(Criteria.where("taskDemandId").is(taskDemandId).and(DATE_FROM).gte(dateFrom).and("dateTo").lte(dateTo).and("taskOriginator").ne("ACTUAL_PLANNING").and("relatedTaskId").exists(false)),
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
            exceptionService.internalError(ERROR_TIMECARE_WORKPLACEID_PERSONID_PERSON_EXTERNAL_POSITION_ID);
        }
        OrganizationStaffWrapper organizationStaffWrapper = userIntegrationService.getOrganizationAndStaffByExternalId(String.valueOf(workPlaceId.get()), personExternalId.get(), personExternalEmploymentId.get());
        StaffDTO staffDTO = organizationStaffWrapper.getStaff();
        OrganizationDTO organizationDTO = organizationStaffWrapper.getOrganization();

        if (organizationDTO == null) {
            exceptionService.dataNotFoundByIdException(MESSAGE_ORGANIZATION_EXTERNALID,workPlaceId.get());
        }
        if (!Optional.ofNullable(staffDTO).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.organization.external-staffid",personExternalId.get());
        }
        if (!Optional.ofNullable(organizationStaffWrapper.getEmployment()).isPresent()) {
            exceptionService.internalError(ERROR_KAIROS_EMPLOYMENT,personExternalId.get());
        }
        List<GetWorkShiftsFromWorkPlaceByIdResult> shiftsFromTimeCare = timeCareShifts.getGetWorkShiftsFromWorkPlaceByIdResult();
        int sizeOfTimeCareShifts = shiftsFromTimeCare.size();
        int skip = 0;
        if (sizeOfTimeCareShifts > MONOGDB_QUERY_RECORD_LIMIT) {
            do {
                saveShifts(skip, shiftsFromTimeCare, organizationDTO.getId(), staffDTO.getId(), organizationStaffWrapper.getEmployment(), skippedShiftsWhileSave);
                skip += MONOGDB_QUERY_RECORD_LIMIT;
            } while (skip <= sizeOfTimeCareShifts);
        } else {
            saveShifts(skip, shiftsFromTimeCare, organizationDTO.getId(), staffDTO.getId(), organizationStaffWrapper.getEmployment(), skippedShiftsWhileSave);
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

    private void saveShifts(int skip, List<GetWorkShiftsFromWorkPlaceByIdResult> shiftsFromTimeCare, Long workPlaceId, Long staffId, EmploymentDTO employmentDTO, List<String> skippedShiftsWhileSave) {
        List<String> externalIdsOfShifts = shiftsFromTimeCare.stream().skip(skip).limit(MONOGDB_QUERY_RECORD_LIMIT).map(timeCareShift -> timeCareShift.getId()).
                collect(Collectors.toList());
        List<String> externalIdsOfActivities = shiftsFromTimeCare.stream().skip(skip).limit(MONOGDB_QUERY_RECORD_LIMIT).map(timeCareShift -> timeCareShift.getActivityId()).
                collect(Collectors.toList());
        List<Shift> shiftsInKairos = shiftMongoRepository.findByExternalIdIn(externalIdsOfShifts);
        List<Activity> activities = activityMongoRepository.findByUnitIdAndExternalIdInAndDeletedFalse(workPlaceId, externalIdsOfActivities);
        List<GetWorkShiftsFromWorkPlaceByIdResult> timeCareShiftsByPagination = shiftsFromTimeCare.stream().skip(skip).limit(MONOGDB_QUERY_RECORD_LIMIT).collect(Collectors.toList());
        List<Shift> shiftsToCreate = new ArrayList<>();
        StaffEmploymentDetails staffEmploymentDetails = new StaffEmploymentDetails(employmentDTO.getWorkingDaysInWeek(), employmentDTO.getTotalWeeklyMinutes());
        StaffAdditionalInfoDTO staffAdditionalInfoDTO = userIntegrationService.verifyUnitEmploymentOfStaff(null,staffId, employmentDTO.getId(),Collections.emptySet());
        CTAResponseDTO ctaResponseDTO = costTimeAgreementRepository.getCTAByEmploymentIdAndDate(staffAdditionalInfoDTO.getEmployment().getId(),new Date());
        staffAdditionalInfoDTO.getEmployment().setCtaRuleTemplates(ctaResponseDTO.getRuleTemplates());
        staffEmploymentDetails.setFullTimeWeeklyMinutes(employmentDTO.getFullTimeWeeklyMinutes());
        Map<String,Activity> activityMap = activities.stream().collect(Collectors.toMap(k->k.getExternalId(),v->v));
        for (GetWorkShiftsFromWorkPlaceByIdResult timeCareShift : timeCareShiftsByPagination) {
            Shift shift = shiftsInKairos.stream().filter(shiftInKairos -> shiftInKairos.getExternalId().equals(timeCareShift.getId())).findAny().orElse(mapTimeCareShiftDataToKairos
                    (timeCareShift, workPlaceId));
            Activity activity = activityMap.get(timeCareShift.getActivityId());
            if (!Optional.ofNullable(activity).isPresent()) {
                skippedShiftsWhileSave.add(timeCareShift.getId());
            } else {
                shift.setStaffId(staffId);
                shift.setEmploymentId(employmentDTO.getId());
                shiftsToCreate.add(shift);
            }
        }
        if (!shiftsToCreate.isEmpty()) {
            Set<LocalDateTime> dates = shiftsToCreate.stream().map(s -> DateUtils.asLocalDateTime(s.getActivities().get(0).getStartDate())).collect(Collectors.toSet());
            Map<Date, Phase> phaseListByDate = phaseService.getPhasesByDates(shiftsToCreate.get(0).getUnitId(), dates);
            shiftService.saveShiftWithActivity(phaseListByDate,shiftsToCreate,staffAdditionalInfoDTO);
            shiftsToCreate.sort(Comparator.comparing(Shift::getStartDate));
            Date startDate = shiftsToCreate.get(0).getStartDate();
            Date endDate = shiftsToCreate.get(shiftsToCreate.size()-1).getEndDate();
            timeBankService.updateTimeBankForMultipleShifts(staffAdditionalInfoDTO, startDate,endDate);
            payOutService.savePayOuts(staffAdditionalInfoDTO.getEmployment(), shiftsToCreate,activities,null,staffAdditionalInfoDTO.getDayTypes());
        }
    }


    public void syncFourthWeekTasks(LocalDateTime startDate) {
        LOGGER.info("SyncFourthWeekTasks Job starting at" + LocalDateTime.now());
        LocalDateTime localDateFrom = startDate;
        LocalDateTime localDateTo = localDateFrom.plusDays(1);
        for (int i = 1; i <= 7; i++) {
            LOGGER.debug("LocalDateFrom " + localDateFrom);
            LOGGER.debug("LocalDateTo   " + localDateTo);
            Date fromDate = Date.from(localDateFrom.atZone(ZoneId.systemDefault()).toInstant());
            Date toDate = Date.from(localDateTo.atZone(ZoneId.systemDefault()).toInstant());
            Criteria criteria = Criteria.where("visitourId").exists(false).orOperator(Criteria.where("visitourId").is(null));
            criteria.and(DATE_FROM).gte(fromDate).and("dateTo").lt(toDate).and("isSubTask").is(false).and("isDeleted").is(false);
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
            LOGGER.debug("SyncFourthWeekTasks Job  Query: " + aggregation.toString());
            AggregationResults<Map> finalResult = mongoTemplate.aggregate(aggregation, Task.class, Map.class);
            List<Map> taskListWithUnitIdGroup = finalResult.getMappedResults();
            for (Map<String, Object> map : taskListWithUnitIdGroup) {
                Map<String, String> flsCredentials = getFLS_Credentials((long) map.get("_id"));
                if (flsCredentials.get("flsDefaultUrl") != "" && flsCredentials.get("userpassword") != "") {
                    List tasks = (List) map.get("taskList");
                    List<Task> tasksToSync = new ArrayList<>();
                    for (Object object : tasks) {
                        ObjectMapper mapper = new ObjectMapper();
                        Task task = mapper.convertValue(object, Task.class);
                        tasksToSync.add(task);
                    }
                    LOGGER.debug("tasksToSync: size " + tasksToSync.size());
                    if (tasksToSync.size() > 0) {
                        //taskConverterService.createFlsCallFromTasks(tasksToSync, flsCredentials);

                        Map<String, Object> datePayload = new HashMap<>();
                        datePayload.put(START_DATE, fromDate);
                        datePayload.put(END_DATE, toDate);
                        Map<String, Object> openCall = new HashMap<>();
                        openCall.put("openCallsMode", "2");
                        //scheduler.optmizeSchedule(openCall, datePayload, flsCredentials);
                    }
                } else {
                    LOGGER.info("FLS Credentials Missing for Unit Id " + map.get("_id"));
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
        Map<String, String> flsCredential = userIntegrationService.getFLS_Credentials(organizationId);
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
            Optional<String> receivedTaskOptional = Optional.ofNullable(receivedTask.getFMExtID());
            if (receivedTaskOptional.isPresent()) {
                List<Long> assingedStaffIds = Stream.of(receivedTaskOptional.get().split(",")).map(Long::parseLong).collect(Collectors.toList());
                task.setAssignedStaffIds(assingedStaffIds);
            } else {
                task.setAssignedStaffIds(Collections.EMPTY_LIST);
            }
            task.setDateFrom(receivedTask.getEarliestStartTime());
            task.setExecutionDate(receivedTask.getLatestStartTime());
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
            LOGGER.info("Number of Tasks in this demand " + tasks.size());
            for (Task task : tasks) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", task.getId());
                map.put("name", taskTypeMongoRepository.findOne(demand.getTaskTypeId()).getTitle());
                map.put("start", task.getStartDate());
                map.put(TASK_TYPE_ID, demand.getTaskTypeId());
                map.put("end", task.getEndDate());
                map.put("citizenId", task.getCitizenId());
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
        timeWindow.put(DURATION, TimeUnit.MINUTES.toSeconds(task.getSlaStartDuration()));
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
        LOGGER.info("percentage of duration :: " + minutes + "   bulkUpdateTaskDTO.isReduced()  " + reduction);
        if (reduction) {
            if (task.getDuration() - minutes <= 0) {
                exceptionService.internalError(ERROR_TASK_DURATION);
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
     * @param shift
     * @param unitId
     * @auther anil maurya
     * This method is use in citizen controller
     */
    public void createTaskFromKMD(Long staffId, ImportShiftDTO shift, Long unitId) {
        try {
            TaskType taskType = taskTypeService.findByExternalId("6123");
            Map<String, Object> taskMetaData = new HashMap<>();
            taskMetaData.put(START_DATE, shift.getStartTime());
            taskMetaData.put(END_DATE, shift.getEndTime());
            taskMetaData.put("updateDate", DateUtils.getDate());
            taskMetaData.put("kmdExternalId", shift.getId());
            taskMetaData.put("externalId", shift.getId());
            taskMetaData.put(ORGANIZATION_ID, unitId);
            taskMetaData.put(STAFF_ID, staffId);
            taskMetaData.put(TASK_TYPE_ID, taskType.getId());
            taskMetaData.put(DURATION, DateUtils.getTimeDuration(shift.getStartTime(), shift.getEndTime()));
            taskMetaData.put(START_ADDRESS, -1);
            taskMetaData.put(END_ADDRESS, -1);
            createTaskFromTimeCare(taskMetaData, AppConstants.REQUEST_FROM_KMD);
        } catch (ParseException exception) {
            LOGGER.warn("Exception Occur while saving shifts from KMD----> {}" , exception.getMessage());

        } catch (Exception e) {
            LOGGER.warn("Exception Occur while saving shifts from KMD----> {}" ,e.getMessage());
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
        LOGGER.info("Auth token--------> {}" , AppConstants.KMD_NEXUS_ACCESS_TOKEN);
        Map map = new HashMap<String, String>();
        map.put("Content-Type", "application/json");
        //   headers.setAll(map);
        HttpEntity<String> headersElements = new HttpEntity<String>(headers);
        LOGGER.info("headers------headersElements-----> {}" , headersElements);
        ResponseEntity<String> responseEntity = loginTemplate.exchange(String.format(AppConstants.KMD_NEXUS_CALENDAR_STAFFS_SHIFT_FILTER, filterId), HttpMethod.POST, headersElements, String.class);
        JSONObject jsonObject = new JSONObject(responseEntity.getBody());
        JSONArray jsonArray = jsonObject.getJSONArray("eventResources");

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject objects = jsonArray.getJSONObject(i);
            ImportTaskDTO kmdTask = JsonUtils.toObject(objects.toString(), ImportTaskDTO.class);
            Integer grantCount = 0;
            TaskDemand taskDemand = null;
            Client citizen = null;
            List<Long> staffIds = new ArrayList<>();
            TaskAddress taskAddress = createTaskAddress(kmdTask);
            List<String> taskIds = new ArrayList<>();
            if (kmdTask.getPatientResourceList() != Collections.EMPTY_LIST) {
                for (PatientResourceList patientResourceList : kmdTask.getPatientResourceList()) {
                    for (Grants grants : patientResourceList.getGrants()) {
                        String grantId = grants.getId();
                        taskDemand = taskDemandMongoRepository.findByKmdExternalId(String.valueOf(grantId));
                        if (citizen == null) return;
                        TaskType taskType = taskTypeMongoRepository.findOne(taskDemand.getTaskTypeId());
                        if (taskDemand == null) return;
                        grantCount += 1;
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
                        LOGGER.warn("Exception occurs while merging Imported KMD Tasks----> " + exception.getMessage());
                    }
                }
            }
        }
    }

    public Task createKMDPlannedTask(ImportTaskDTO kmdTask, TaskType taskType, TaskDemand taskDemand,
                                     TaskAddress taskAddress, List<Long> staffIds) {
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
        StaffDTO staffDTO = userIntegrationService.getStaffByUser(userId);
        List<Long> assignedStaffIds = pickTask.getAssignedStaffIds();
        if (!assignedStaffIds.contains(staffDTO.getId())) assignedStaffIds.add(staffDTO.getId());
        pickTask.setAssignedStaffIds(assignedStaffIds);
        save(pickTask);
        return pickTask;
    }


    public Boolean importTask(Long unitId, List<VRPTaskDTO> taskDTOS){
        Set<String> skills = taskDTOS.stream().map(t->t.getSkill()).collect(Collectors.toSet());
        List<TaskType> taskTypes = taskTypeMongoRepository.findByName(unitId,new ArrayList<>(skills));
        Map<String,BigInteger> taskTypeIds = taskTypes.stream().collect(Collectors.toMap(t->t.getTitle(),t->t.getId()));
        Map<Long,BigInteger> installationNoAndTaskTypeId = taskMongoRepository.getAllTasksInstallationNoAndTaskTypeId(unitId);
        List<VRPTaskDTO> newTasks = new ArrayList<>();
        for (VRPTaskDTO task : taskDTOS) {
            if(!taskTypeIds.containsKey(task.getSkill())){
                exceptionService.dataNotFoundException(MESSAGE_TASKTYPE_NOTEXISTS,task.getSkill());
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
        return tasks;//new ArrayList<>();
    }

    public TaskDTO getTask(BigInteger taskId){
        Task task = taskMongoRepository.findOne(taskId);
        return ObjectMapperUtils.copyPropertiesByMapper(task,TaskDTO.class);
    }

}
