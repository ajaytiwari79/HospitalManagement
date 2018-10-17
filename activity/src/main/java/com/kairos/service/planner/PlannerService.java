package com.kairos.service.planner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kairos.dto.user.organization.skill.OrganizationClientWrapper;
import com.kairos.config.env.EnvConfig;
import com.kairos.dto.activity.task.*;
import com.kairos.enums.CitizenHealthStatus;
import com.kairos.enums.Day;
import com.kairos.enums.task_type.TaskTypeEnum;
import com.kairos.persistence.enums.task_type.DelayPenalty;
import com.kairos.persistence.model.CustomTimeScale;
import com.kairos.persistence.model.client_aggregator.ClientAggregator;
import com.kairos.persistence.model.client_exception.ClientException;
import com.kairos.persistence.model.client_exception.ClientExceptionType;
import com.kairos.persistence.model.task.SkillExpertise;
import com.kairos.persistence.model.task.Task;
import com.kairos.persistence.model.task.TaskAddress;
import com.kairos.persistence.model.task.TaskStatus;
import com.kairos.persistence.model.task_demand.TaskDemand;
import com.kairos.persistence.model.task_demand.TaskDemandVisit;
import com.kairos.persistence.model.task_type.TaskType;
import com.kairos.persistence.model.task_type.TaskTypeSkill;
import com.kairos.persistence.model.task_type.TaskTypeSlaConfig;
import com.kairos.persistence.repository.CustomTimeScaleRepository;
import com.kairos.persistence.repository.client_exception.ClientExceptionMongoRepository;
import com.kairos.persistence.repository.client_exception.ClientExceptionTypeMongoRepository;
import com.kairos.persistence.repository.common.CustomAggregationOperation;
import com.kairos.persistence.repository.repository_impl.TaskMongoRepositoryImpl;
import com.kairos.persistence.repository.task_type.TaskDemandMongoRepository;
import com.kairos.persistence.repository.task_type.TaskMongoRepository;
import com.kairos.persistence.repository.task_type.TaskTypeMongoRepository;
import com.kairos.persistence.repository.task_type.TaskTypeSlaConfigMongoRepository;
import com.kairos.rest_client.*;
import com.kairos.service.CustomTimeScaleService;
import com.kairos.service.MongoBaseService;
import com.kairos.service.client_exception.ClientExceptionService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.fls_visitour.schedule.Scheduler;
import com.kairos.service.fls_visitour.schedule.TaskConverterService;
import com.kairos.service.task_type.TaskDemandService;
import com.kairos.service.task_type.TaskDynamicReportService;
import com.kairos.service.task_type.TaskService;
import com.kairos.rule_validator.task.TaskDaySpecification;
import com.kairos.rule_validator.TaskSpecification;
import com.kairos.dto.user.client.Client;
import com.kairos.dto.user.country.basic_details.CountryHolidayCalender;
import com.kairos.dto.user.country.day_type.DayType;
import com.kairos.dto.user.organization.OrganizationDTO;
import com.kairos.dto.user.organization.skill.Skill;
import com.kairos.commons.utils.DateUtils;
import com.kairos.utils.user_context.UserContext;
import com.kairos.wrapper.TaskCountWithAssignedUnit;
import com.kairos.wrapper.task.TaskGanttDTO;
import com.kairos.wrapper.task.TaskUpdateDTO;
import com.kairos.wrapper.task_demand.TaskDemandRequestWrapper;
import com.kairos.wrapper.task_demand.TaskDemandVisitWrapper;
import de.tourenserver.ArrayOfFixedCall;
import de.tourenserver.CallInfoRec;
import de.tourenserver.FixScheduleResponse;
import de.tourenserver.FixedCall;
import org.apache.commons.collections.map.HashedMap;
import org.bson.Document;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import javax.inject.Inject;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.kairos.constants.AppConstants.FORWARD_SLASH;
import static com.kairos.constants.AppConstants.MERGED_TASK_NAME;
import static com.kairos.persistence.model.constants.ClientExceptionConstant.SICK;
import static com.kairos.persistence.model.constants.TaskConstants.*;
import static com.kairos.persistence.model.task.TaskStatus.CANCELLED;
import static com.kairos.commons.utils.DateUtils.*;
import static java.time.ZoneId.systemDefault;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;


/**
 * Created by oodles on 17/1/17.
 */
@Service
@Transactional
public class PlannerService extends MongoBaseService {

    private static final Logger logger = LoggerFactory.getLogger(PlannerService.class);
    @Inject
    private RandomDateGeneratorService randomDateGeneratorService;
    @Inject
    private TasksMergingService tasksMergingService;
    @Inject
    private TaskExceptionService taskExceptionService;
    @Inject
    private TaskDynamicReportService taskDynamicReportService;
    @Inject
    private ClientExceptionMongoRepository clientExceptionMongoRepository;
    @Inject
    private TaskTypeSlaConfigMongoRepository taskTypeSlaConfigMongoRepository;
    @Inject
    private TaskMongoRepositoryImpl customTaskMongoRepository;

    @Inject
    TaskService taskService;
    @Inject
    private Scheduler scheduler;

    @Inject
    TaskDemandMongoRepository taskDemandMongoRepository;
    @Inject
    TaskTypeMongoRepository taskTypeMongoRepository;
    @Inject
    TaskMongoRepository taskMongoRepository;
    @Inject
    EnvConfig envConfig;
    @Inject
    TaskConverterService taskConverterService;

    @Inject
    CustomTimeScaleService customTimeScaleService;
    @Inject
    private MongoTemplate mongoTemplate;
    @Inject
    private ExceptionService exceptionService;

    @Autowired
    CustomTimeScaleRepository customTimeScaleRepository;

    @Autowired
    ClientExceptionService clientExceptionService;
    @Autowired TaskDemandService taskDemandService;
    @Autowired
    ClientExceptionTypeMongoRepository clientExceptionTypeMongoRepository;
    @Autowired
    OrganizationRestClient organizationRestClient;
    @Autowired
    TimeSlotRestClient timeSlotRestClient;

    @Autowired
    ClientRestClient clientRestClient;
    @Autowired
    GenericIntegrationService genericIntegrationService;


    private int getWeekFrequencyAsInt(String frequency) {
        int weekFrequency = 0;
        switch (frequency) {
            case "ONE_WEEK": {
                weekFrequency = 1;
                break;
            }
            case "TWO_WEEK": {
                weekFrequency = 2;
                break;
            }
            case "THREE_WEEK": {
                weekFrequency = 3;
                break;
            }
            case "FOUR_WEEK": {
                weekFrequency = 4;
                break;
            }
        }
        return weekFrequency;
    }


    private Map<String, Object> getTaskDemandVisitMap(TaskDemand taskDemand, TaskDemandVisit taskDemandVisit, boolean isWeekend, boolean isPlanned) {

        Map<String, Object> taskDemandVisitMap = new HashMap();
        taskDemandVisitMap.put("taskDemandVisitId", taskDemandVisit.getId());
        taskDemandVisitMap.put("visitCount", taskDemandVisit.getVisitCount());

        Map<String, Object> timeSlotMap = new HashMap<>();
        timeSlotMap.put("id", taskDemandVisit.getTimeSlotId());
        timeSlotMap.put("name", taskDemandVisit.getTimeSlotName());

        taskDemandVisitMap.put("timeSlot", timeSlotMap);
        taskDemandVisitMap.put("isWeekend", isWeekend);
        taskDemandVisitMap.put("duration", TimeUnit.MINUTES.toSeconds(taskDemandVisit.getVisitDuration()));
        if (taskDemand.getRecurrencePattern().equals(TaskDemand.RecurrencePattern.WEEKLY)) {
            taskDemandVisitMap.put("weekFrequency", (isWeekend) ? getWeekFrequencyAsInt(taskDemand.getWeekendFrequency().toString()) : getWeekFrequencyAsInt(taskDemand.getWeekdayFrequency().toString()));
        }
        taskDemandVisitMap.put("priority", taskDemand.getPriority());
        taskDemandVisitMap.put("isPlanned", isPlanned);
        taskDemandVisitMap.put("taskTypeId", taskDemand.getTaskTypeId());

        return taskDemandVisitMap;
    }

    public Map<String, Object> getCitizenPlanning(long unitId, long citizenId, boolean isActualPlanningScreen, String startDate) throws ParseException {
        long startTime = System.currentTimeMillis();

        Map<String, Object> citizenPlanningMap = new HashMap<>();
        //anil m2
        Map<String, Object> clientAddressInfo=genericIntegrationService.getClientAddressInfo(citizenId);
        LocalDate upcomingMonday = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY));
        LocalDate fourWeekLater = upcomingMonday.plusDays(28);
        Date fromDate = Date.from(upcomingMonday.atStartOfDay(systemDefault()).toInstant());
        Date toDate = Date.from(fourWeekLater.atStartOfDay(systemDefault()).toInstant());
        //Get list of task demands for this citizen, unit, not deleted and demands end date greater then upcoming monday or end date is null
        List<TaskDemand> citizenTaskDemandList = taskDemandMongoRepository.getByCitizenIdAndUnitIdAndStatusNotAndIsDeletedAndEndDateGreaterOrNull(citizenId, unitId, TaskDemand.Status.PLANNED, false, fromDate);

        List<Map<String, Object>> demandList = new ArrayList<>(citizenTaskDemandList.size());

        List<Map<String, String>> taskTypeList = new ArrayList<>();
        Set<String> taskTypeIdsList = new HashSet<String>();

        Map<String, Object> taskDemandMap;

        for (TaskDemand taskDemand : citizenTaskDemandList) {

            List<Map> taskDemandVisits = new ArrayList<>();

            Set<Long> allTimeSlotsIds = new HashSet<>();

            if (taskDemand.getWeekdayVisits() != null) {
                for (TaskDemandVisit taskDemandVisit : taskDemand.getWeekdayVisits()) {
                    taskDemandVisits.add(getTaskDemandVisitMap(taskDemand, taskDemandVisit, false, taskDemandVisit.isPlanned()));

                    allTimeSlotsIds.add(taskDemandVisit.getTimeSlotId());
                }
            }

            if (taskDemand.getWeekendVisits() != null) {
                for (TaskDemandVisit taskDemandVisit : taskDemand.getWeekendVisits()) {
                    taskDemandVisits.add(getTaskDemandVisitMap(taskDemand, taskDemandVisit, true, taskDemandVisit.isPlanned()));

                    allTimeSlotsIds.add(taskDemandVisit.getTimeSlotId());
                }
            }
            taskDemandMap = new HashMap<>();
            taskDemandMap.put("id", taskDemand.getId());
            TaskType taskType = taskTypeMongoRepository.findOne(taskDemand.getTaskTypeId());
            taskDemandMap.put("name", taskType.getTitle());
            taskDemandMap.put("taskTypeColor", taskType.getColorForGantt());
            taskDemandMap.put("taskTypeId", taskType.getId());
            taskDemandMap.put("priority", taskDemand.getPriority());
            taskDemandMap.put("taskDemandVisits", taskDemandVisits);
            taskDemandMap.put("status", taskDemand.getStatus());
            taskDemandMap.put("noOfTaskStatus", taskService.countGeneratedPlannedTasks(taskDemand.getId() + ""));
            taskDemandMap.put("remarks", taskDemand.getRemarks());
            taskDemandMap.put("taskDemandTimeSlots", allTimeSlotsIds);

            int weekdayFrequencyAsInt = taskDemand.getWeekdayFrequency() != null ? getWeekFrequencyAsInt(taskDemand.getWeekdayFrequency().toString()) : 0;
            int weekendFrequencyAsInt = taskDemand.getWeekendFrequency() != null ? getWeekFrequencyAsInt(taskDemand.getWeekendFrequency().toString()) : 0;
            int maxFrequency;
            if ((weekdayFrequencyAsInt == weekendFrequencyAsInt)) {
                maxFrequency = weekdayFrequencyAsInt;
            } else if (weekdayFrequencyAsInt > weekendFrequencyAsInt) {
                maxFrequency = weekdayFrequencyAsInt;
            } else {
                maxFrequency = weekendFrequencyAsInt;
            }
            taskDemandMap.put("maxWeekFrequency", maxFrequency);

            demandList.add(taskDemandMap);

            taskTypeIdsList.add(taskType.getId() + "");
        }

        List<Task> taskList;
        if (isActualPlanningScreen) {
            DateFormat dateISOFormat = new SimpleDateFormat(ONLY_DATE);
            fromDate = dateISOFormat.parse(startDate);
            LocalDate fromDateInLocalFormat = fromDate.toInstant().atZone(systemDefault()).toLocalDate();

            fromDateInLocalFormat = (fromDateInLocalFormat.getDayOfWeek().name().equals("MONDAY")) ? fromDateInLocalFormat : DateUtils.getDateForPreviousDay(fromDateInLocalFormat, DayOfWeek.MONDAY);
            fromDate = Date.from(fromDateInLocalFormat.atStartOfDay().atZone(systemDefault()).toInstant());

            LocalDate toDateInLocalFormat = fromDateInLocalFormat.plusDays(28);
            toDate = Date.from(toDateInLocalFormat.atStartOfDay().atZone(systemDefault()).toInstant());
            taskList = customTaskMongoRepository.getActualPlanningTask(citizenId, fromDate, toDate);

            long loggedinUserId = UserContext.getUserDetails().getId();
            CustomTimeScale customTimeScale = customTimeScaleRepository.findByStaffIdAndCitizenIdAndUnitId(loggedinUserId, citizenId, unitId);
            Map<String, Object> plannerSettings = null;
            if (customTimeScale != null) {
                plannerSettings = new HashedMap();
                plannerSettings.put("showExceptionDeleteConfirmation", customTimeScale.isShowExceptionDeleteConfirmation());
            }
            citizenPlanningMap.put("plannerSettings", plannerSettings);
            toDate.setHours(23);
            toDate.setMinutes(59);
            toDate.setSeconds(0);

            List<ClientException> clientExceptions = clientExceptionMongoRepository.getExceptionOfCitizenBetweenDates(citizenId, fromDate, toDate,unitId);
            citizenPlanningMap.put("clientExceptions",clientExceptions);
        } else {
            taskList = taskMongoRepository.findAllBetweenDates(citizenId, fromDate, toDate);
        }
        for (Task task : taskList) {
            if (task.getTaskTypeId() != null) {
                taskTypeIdsList.add(task.getTaskTypeId().toString());
            }
        }
        List<TaskGanttDTO> customizedTaskList = taskService.customizeTaskData(taskList);

        List<TaskType> allTaskTypes = taskTypeMongoRepository.findAllByIdInAndIsEnabled(taskTypeIdsList, true);

        for (TaskType taskType : allTaskTypes) {
            Map<String, String> taskTypeMap = new HashMap<>();
            taskTypeMap.put("id", taskType.getId() + "");
            taskTypeMap.put("name", taskType.getTitle());
            taskTypeMap.put("color", taskType.getColorForGantt());
            if (!taskTypeList.contains(taskTypeMap))
                taskTypeList.add(taskTypeMap);
        }

        citizenPlanningMap.put("taskList", customizedTaskList);
        citizenPlanningMap.put("demandList", demandList);
        citizenPlanningMap.put("taskTypeList", taskTypeList);

        /* List<Map<String, Object>> temporaryAddressList = clientGraphRepository.getClientTemporaryAddressById(citizenId);
         citizenPlanningMap.put("temporaryAddressList", !temporaryAddressList.isEmpty() ? FormatUtil.formatNeoResponse(temporaryAddressList) : Collections.EMPTY_LIST);
         ContactAddressDTO address = citizen.getContactAddress();
         citizenPlanningMap.put("latitude", address.getLatitude());
         citizenPlanningMap.put("longitude", address.getLongitude());

*/
        citizenPlanningMap.putAll(clientAddressInfo);
        logger.info("Execution Time :(PlannerService:getCitizenPlanning) " + (System.currentTimeMillis() - startTime) + " ms");
        return citizenPlanningMap;
    }


    private List<Task> getTasksFromDemandVisits(TaskDemandVisit taskDemandVisit, TaskDemand taskDemand, boolean isWeekend,
                                                Long  citizenId, TaskType taskType, Date taskStartTime) throws ParseException, CloneNotSupportedException {

        LocalDate upcomingMonday = calcNextMonday(LocalDate.now());
        LocalDate fourWeekLater = upcomingMonday.plusDays(28);
        LocalDate taskDemandStartDate = taskDemand.getStartDate().toInstant().atZone(systemDefault()).toLocalDate();

        LocalDate createTaskFrom;
        createTaskFrom = taskDemandStartDate.isBefore(upcomingMonday) ? upcomingMonday : taskDemandStartDate;

        Date taskDemandEndDate;
        if(taskDemand.getEndDate() == null){
            taskDemandEndDate = Date.from(upcomingMonday.plusYears(1).atStartOfDay(systemDefault()).toInstant());
        }else {
            LocalDate taskDemandEndLocalDate = taskDemand.getEndDate().toInstant().atZone(systemDefault()).toLocalDate();
            LocalDate twoYearsFromNow = LocalDate.now().plusYears(2);
            if(taskDemandEndLocalDate.isAfter(twoYearsFromNow)){
                taskDemandEndDate = Date.from(upcomingMonday.plusYears(1).atStartOfDay(systemDefault()).toInstant());
            }else{
                taskDemandEndDate = taskDemand.getEndDate();
            }
        }

        logger.info("Citizen is --->" + citizenId);
        //anil m2 implements rest client
        TaskDemandVisitWrapper taskDemandInfo=genericIntegrationService.
                getClientDetailsForTaskDemandVisit(new TaskDemandRequestWrapper(citizenId,taskDemand.getUnitId(),
                        taskDemandVisit.getTimeSlotId(),taskDemand.getStartDate(),taskDemandEndDate));
        Client citizen=taskDemandInfo.getCitizen();
        List<Long> forbiddenStaff =taskDemandInfo.getForbiddenStaff();
        List<Long> preferredStaff = taskDemandInfo.getPreferredStaff();

        List<Task> tasksToReturn = new ArrayList<>();
        List<Task> tasksToSave = new ArrayList<>();

        TaskAddress taskAddress =taskDemandInfo.getTaskAddress();

        logger.info("taskDemandVisit getId " + taskDemandVisit.getId());

        Map<String, Object> timeSlotMap =taskDemandInfo.getTimeSlotMap();

        List<Map<String, LocalDate>> randomDates = Collections.EMPTY_LIST;
        List<Long> publicHolidayList = Collections.EMPTY_LIST;
        if (taskDemand.getRecurrencePattern().equals(TaskDemand.RecurrencePattern.WEEKLY)) {
            Long countryId = taskDemandInfo.getCountryId();
            publicHolidayList = taskDemandInfo.getPublicHolidayList();
            boolean  skipTaskOnPublicHoliday = skipTaskOnPublicHoliday(taskDemand,taskDemandVisit);
            if (isWeekend) {
                randomDates = randomDateGeneratorService.getRandomDates(getWeekFrequencyAsInt(taskDemand.getWeekendFrequency().toString()), taskDemandVisit.getVisitCount(), createTaskFrom, isWeekend, taskDemandEndDate, publicHolidayList,skipTaskOnPublicHoliday);
            } else {
                int intervalWeeks = (int) ChronoUnit.WEEKS.between(createTaskFrom.minusDays(1), taskDemandEndDate.toInstant().atZone(systemDefault()).toLocalDate());
                int numOfWeeks = getWeekFrequencyAsInt(taskDemand.getWeekdayFrequency().toString());
                int visitCount = taskDemandVisit.getVisitCount();
                if (intervalWeeks > 0){
                    if (intervalWeeks < numOfWeeks) {
                        numOfWeeks = intervalWeeks;
                        if (intervalWeeks == 1 && taskDemandVisit.getVisitCount() > 5) {
                            visitCount = 5;
                        } else if (intervalWeeks == 2 && taskDemandVisit.getVisitCount() > 10) {
                            visitCount = 10;
                        } else if (intervalWeeks == 3 && taskDemandVisit.getVisitCount() > 15) {
                            visitCount = 15;
                        } else if (intervalWeeks == 4 && taskDemandVisit.getVisitCount() > 20) {
                            visitCount = 20;
                        }
                    }
                } else {
                    logger.info("intervalWeeks " + intervalWeeks);
                    exceptionService.internalError("error.task.demand.date.startandend");
                }
                randomDates = randomDateGeneratorService.getRandomDates(numOfWeeks, visitCount, createTaskFrom, isWeekend, taskDemandEndDate, publicHolidayList,skipTaskOnPublicHoliday);
            }
        } else if (taskDemand.getRecurrencePattern().equals(TaskDemand.RecurrencePattern.DAILY)) {

            randomDates = randomDateGeneratorService.getRandomDatesForDailyPattern(taskDemand);
        } else if (taskDemand.getRecurrencePattern().equals(TaskDemand.RecurrencePattern.MONTHLY)) {

            randomDates = randomDateGeneratorService.getRandomDatesForMonthlyPattern(taskDemand);
        }

        Map<String, Object> tasksData = createTasksAndDefinitions(citizen,
                randomDates, taskType, taskDemand, taskDemandVisit, preferredStaff,
                forbiddenStaff, timeSlotMap, fourWeekLater, taskStartTime, taskAddress,taskDemandInfo);
        tasksToSave.addAll((List<Task>) tasksData.get("tasksToSave"));
        tasksToReturn.addAll((List<Task>) tasksData.get("tasksToReturn"));
        taskDemandVisit.setPlanned(true);
        taskDemandService.save(taskDemand);

        if(!tasksToSave.isEmpty()){
            taskService.save(tasksToSave);
            if (taskDemand.getStaffCount() > 1) {
                createMultipleTask(tasksToSave, taskDemand.getStaffCount());
            }
        }

        logger.debug("Total tasksToSave   :: " + tasksToSave.size());
        logger.debug("Total tasksToReturn :: " + tasksToReturn.size());
        return tasksToReturn;
    }



    private void createMultipleTask(List<Task> multiMenTasks, int staffCount) throws CloneNotSupportedException {
        logger.debug("creating multimen task now ");
        List<Task> multiMenRelatedTasks = new ArrayList<>();
        Task relatedTask;
        for (Task multiMenTask : multiMenTasks) {
            for (int i = 0; i < (staffCount - 1); i++) {
                relatedTask = Task.copyProperties(multiMenTask, Task.getInstance());
                relatedTask.setId(null);
                relatedTask.setRelatedTaskId(multiMenTask.getId().toString());
                multiMenRelatedTasks.add(relatedTask);
            }
            multiMenTask.setMultiStaffTask(true);
        }

        taskService.save(multiMenTasks);
        logger.info("total multimen task created = " + multiMenRelatedTasks.size());
        taskService.save(multiMenRelatedTasks);

    }

    private Map<String, Object> createTasksAndDefinitions(Client citizen, List<Map<String, LocalDate>> randomDates, TaskType taskType,
                                                          TaskDemand taskDemand, TaskDemandVisit taskDemandVisit, List<Long> preferredStaff, List<Long> forbiddenStaff, Map<String, Object> timeSlotMap,
                                                          LocalDate fourWeekLater, Date taskStartTime, TaskAddress taskAddress,TaskDemandVisitWrapper taskDemandInfo) {
        Task task;
        List<Task> tasksToSave = new ArrayList<>();
        List<Task> tasksToReturn = new ArrayList<>();
        Map<String, Object> tasksData = new HashMap<>();


        TaskTypeSlaConfig taskTypeSlaConfig = taskTypeSlaConfigMongoRepository.findByUnitIdAndTaskTypeIdAndTimeSlotId(taskDemand.getUnitId(),taskType.getId(),taskDemandVisit.getTimeSlotId());
        Map<String, Integer> slaPerDayInfo = (taskTypeSlaConfig != null)?  taskTypeSlaConfig.getSlaConfig() : null;


        //Long countryId = countryGraphRepository.getCountryOfUnit(taskDemand.getUnitId());
        Long countryId=taskDemandInfo.getCountryId();
        List<Long> publicHolidayList=taskDemandInfo.getPublicHolidayList();
        //List<Long> publicHolidayList = countryGraphRepository.getAllCountryHolidaysBetweenDates(countryId, taskDemand.getStartDate().getTime(), taskDemand.getEndDate().getTime());

        for (Map<String, LocalDate> dateMap : randomDates) {

            LocalDate randomDate = dateMap.get("randomDate");

            LocalDate taskStartBoundary = dateMap.get("taskStartBoundary");
            LocalDate taskEndBoundary = dateMap.get("taskEndBoundary");

            Date date = Date.from(randomDate.atStartOfDay().atZone(systemDefault()).toInstant());

            int slaStartDuration = 0;
            if(slaPerDayInfo!=null && slaPerDayInfo.get(randomDate.getDayOfWeek().toString())!=null){
                slaStartDuration = slaPerDayInfo.get(randomDate.getDayOfWeek().toString());
            }

            if (taskDemand.getRecurrencePattern().equals(TaskDemand.RecurrencePattern.WEEKLY)) {
                Long todaysDate = date.getTime();
                if (publicHolidayList.contains(todaysDate)) {
                    boolean activeTask = true;
                    List<CountryHolidayCalender> countryHolidayCalenders = taskDemandInfo.getCountryHolidayCalenderList();
                    if(!countryHolidayCalenders.isEmpty()){
                        Optional<CountryHolidayCalender> countryHoliday = countryHolidayCalenders.stream().filter(countryHolidayCalender -> countryHolidayCalender.getHolidayDate().equals(todaysDate)).findFirst();
                        Long dayTypeId = countryHoliday.get().getDayType().getId();
                        if(taskType.getForbiddenDayTypeIds()!=null && taskType.getForbiddenDayTypeIds().contains(dayTypeId)){
                            activeTask = false;
                        }
                    }

                    if(slaPerDayInfo!=null && slaPerDayInfo.get(TaskTypeEnum.TaskTypeSlaDay.PUBLIC_HOLIDAY.toString())!=null){
                        slaStartDuration = slaPerDayInfo.get(TaskTypeEnum.TaskTypeSlaDay.PUBLIC_HOLIDAY.toString());
                    }
                        /*for (TaskDemandVisit weekendTaskDemandVisit : taskDemand.getWeekendVisits()) {
                            Map<String, Object> differentTimeSlotMap = timeSlotMap;
                            if(taskDemandVisit.getTimeSlotId() != weekendTaskDemandVisit.getTimeSlotId()){
                                differentTimeSlotMap = timeSlotRestClient.getTimeSlotByUnitIdAndTimeSlotId(taskDemand.getUnitId(),weekendTaskDemandVisit.getTimeSlotId());
                                taskTypeSlaConfig = taskTypeSlaConfigMongoRepository.findByUnitIdAndTaskTypeIdAndTimeSlotId(taskDemand.getUnitId(),taskType.getId(),weekendTaskDemandVisit.getTimeSlotId());
                                slaPerDayInfo = (taskTypeSlaConfig != null)?  taskTypeSlaConfig.getSlaConfig() : null;
                                if(slaPerDayInfo!=null && slaPerDayInfo.get(TaskTypeEnum.TaskTypeSlaDay.PUBLIC_HOLIDAY.toString())!=null){
                                    slaStartDuration = slaPerDayInfo.get(TaskTypeEnum.TaskTypeSlaDay.PUBLIC_HOLIDAY.toString());
                                }
                                logger.info("slaStartDuration >>>>> "+slaStartDuration);
                            }
                            task = createTask(citizen, randomDate, taskStartBoundary, taskEndBoundary, taskType, taskDemand, weekendTaskDemandVisit, preferredStaff, forbiddenStaff, differentTimeSlotMap, null, taskAddress, slaStartDuration);
                            tasksToSave.add(task);
                            if (randomDate.isBefore(fourWeekLater) || randomDate.isEqual(fourWeekLater)) { //Send only 4 weeks tasks to front end.
                                tasksToReturn.add(task);
                            }
                        }*/
                    task = createTask(citizen, randomDate, taskStartBoundary, taskEndBoundary, taskType, taskDemand, taskDemandVisit, preferredStaff, forbiddenStaff, timeSlotMap, taskStartTime, taskAddress, slaStartDuration);
                    if(Optional.ofNullable(task).isPresent()){
                        task.setActive(activeTask);
                        tasksToSave.add(task);
                        if (randomDate.isBefore(fourWeekLater) || randomDate.isEqual(fourWeekLater)) { //Send only 4 weeks tasks to front end.
                            tasksToReturn.add(task);
                        }
                    }
                } else {
                    task = createTask(citizen, randomDate, taskStartBoundary, taskEndBoundary, taskType, taskDemand, taskDemandVisit, preferredStaff, forbiddenStaff, timeSlotMap, taskStartTime, taskAddress, slaStartDuration);
                    if(Optional.ofNullable(task).isPresent()){
                        tasksToSave.add(task);
                        if (randomDate.isBefore(fourWeekLater) || randomDate.isEqual(fourWeekLater)) { //Send only 4 weeks tasks to front end.
                            tasksToReturn.add(task);
                        }
                    }
                }
            } else if (taskDemand.getRecurrencePattern().equals(TaskDemand.RecurrencePattern.DAILY) || taskDemand.getRecurrencePattern().equals(TaskDemand.RecurrencePattern.MONTHLY)) {
                task = createTask(citizen, randomDate, taskStartBoundary, taskEndBoundary, taskType, taskDemand, taskDemandVisit, preferredStaff, forbiddenStaff, timeSlotMap, taskStartTime, taskAddress, slaStartDuration);
                if(Optional.ofNullable(task).isPresent()){
                    tasksToSave.add(task);
                    if (randomDate.isBefore(fourWeekLater) || randomDate.isEqual(fourWeekLater)) { //Send only 4 weeks tasks to front end.
                        tasksToReturn.add(task);
                    }
                }
            }

        }
        tasksData.put("tasksToSave", tasksToSave);
        tasksData.put("tasksToReturn", tasksToReturn);
        return tasksData;
    }

    private Task createTask(Client citizen, LocalDate randomDate, LocalDate taskStartBoundary,
                            LocalDate taskEndBoundary, TaskType taskType,
                            TaskDemand taskDemand, TaskDemandVisit taskDemandVisit,
                            List<Long> preferredStaff, List<Long> forbiddenStaff, Map<String, Object> timeSlotMap, Date taskStartTime, TaskAddress taskAddress, int slaStartDuration) {

        Date date = Date.from(randomDate.atStartOfDay().atZone(systemDefault()).toInstant());
        Task task = new Task();
        task.setTaskOriginator(TaskTypeEnum.TaskOriginator.PRE_PLANNING);

        task.setJoinEventId(randomDate.getDayOfWeek().name() + "_" + taskDemandVisit.getId());

        task.setName(taskType.getTitle());
        task.setColorForGantt(taskType.getColorForGantt());

        task.setCitizenId(taskDemand.getCitizenId());

        task.setPriority(taskDemand.getPriority());
        task.setSetupDuration(taskDemand.getSetupDuration());

        task.setTaskTypeId(taskType.getId());

        task.setTaskDemandId(taskDemand.getId());
        task.setTaskStatus(TaskStatus.GENERATED);
        task.setNumberOfStaffRequired(taskDemand.getStaffCount());


        task.setPrefferedStaffIdsList(preferredStaff);
        task.setForbiddenStaffIdsList(forbiddenStaff);

        task.setForbiddenStaffIdsList(forbiddenStaff);

        task.setVisitourTaskTypeID(taskType.getVisitourId());
        task.setPreProcessingDuration(taskType.getPreProcessingDuration());
        task.setPostProcessingDuration(taskType.getPostProcessingDuration());


        task.setTeamId(citizen.getVisitourTeamId());

        task.setDateFrom(date);
        task.setDateTo(date);

        task.setTaskStartBoundary(Date.from(taskStartBoundary.atStartOfDay().atZone(systemDefault()).toInstant()));
        task.setTaskEndBoundary(Date.from(taskEndBoundary.atStartOfDay().atZone(systemDefault()).toInstant()));

        Date timeFrom = date;
        if (taskStartTime != null) {
            //If Task's Start Time received while dropping task
            timeFrom.setHours(taskStartTime.getHours());
            timeFrom.setMinutes(taskStartTime.getMinutes());
        } else if (taskDemandVisit.getPreferredHour() != null && !taskDemandVisit.getPreferredHour().equals("") && taskDemandVisit.getPreferredMinute() != null && !taskDemandVisit.getPreferredMinute().equals("")) {
            timeFrom.setHours(Integer.parseInt(taskDemandVisit.getPreferredHour()));
            timeFrom.setMinutes(Integer.parseInt(taskDemandVisit.getPreferredMinute()));
            timeFrom.setSeconds(0);
            task.setTimeFrom(timeFrom);
        } else {
            //If Task's Start Time and PreferredTime is not present, then use timeslot's start and end
            timeFrom.setHours(((Integer) timeSlotMap.get("startHour")).intValue());
            timeFrom.setMinutes(((Integer) timeSlotMap.get("startMinute")).intValue());
            timeFrom.setSeconds(0);
            task.setTimeFrom(timeFrom);
        }

        timeFrom.setSeconds(0);
        task.setTimeFrom(timeFrom);

        Date timeTo = DateUtils.addMinutes(timeFrom, taskDemandVisit.getVisitDuration());
        task.setTimeTo(timeTo);

        task.setTimeSlotId(Long.valueOf(timeSlotMap.get("id") + ""));
        task.setTaskDemandVisitId(taskDemandVisit.getId());

        task.setDuration(taskDemandVisit.getVisitDuration());

        task.setAddress(taskAddress);

        task.setUnitId(taskDemand.getUnitId());

        task.setSlaStartDuration(slaStartDuration);
        //task.setSlaEndDuration(taskType.getSlaEndDuration());


        List<TaskTypeSkill>  taskTypeSkills = taskType.getTaskTypeSkills();
        if(taskTypeSkills!=null && !taskTypeSkills.isEmpty()) {
            List<SkillExpertise> skillExpertiseList = new ArrayList<>(taskTypeSkills.size());
            for (TaskTypeSkill taskTypeSkill : taskTypeSkills) {
                SkillExpertise skillExpertise = new SkillExpertise();
                skillExpertise.setSkillVisitourId(taskTypeSkill.getVisitourId());
                skillExpertise.setSkillLevel(getSkillLevelAsInt(taskTypeSkill.getSkillLevel()));
                skillExpertise.setSkillName(taskTypeSkill.getName());
                skillExpertiseList.add(skillExpertise);
            }
            task.setSkillExpertiseList(skillExpertiseList);
        }
        return validateDaySpecification(taskType,task)?task:null;
    }

    long getSkillLevelAsInt(Skill.SkillLevel skillLevel) {
        long skillLevelAsInt;
        switch (skillLevel) {
            case BASIC: {
                skillLevelAsInt = 1;
                break;
            }
            case ADVANCE: {
                skillLevelAsInt = 2;
                break;
            }
            case EXPERT: {
                skillLevelAsInt = 3;
                break;
            }
            default:{
                skillLevelAsInt = 2;
            }
        }
        return skillLevelAsInt;
    }

    public Map createTaskFromDemandTimeSlot(long unitId, long citizenId, Map<String, Object> requestPayload) throws ParseException, CloneNotSupportedException {

        logger.info("createTaskFromDemandTimeSlot" + requestPayload);
        long methodExecutionStartTime = System.currentTimeMillis();

        DateFormat dateISOFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        String taskDemandId = requestPayload.get("taskDemandId").toString();
        String taskDemandVisitId = requestPayload.get("taskDemandVisitId").toString();

        TaskDemand taskDemand = taskDemandMongoRepository.findByTaskDemandIdAndUnitIdAndIsDeleted(taskDemandId,unitId,false);
        TaskType taskType = taskTypeMongoRepository.findOne(taskDemand.getTaskTypeId());

        TaskDemandVisit droppedTaskDemandVisit = null;
        if (requestPayload.get("isWeekend") != null) {
            if ((boolean) requestPayload.get("isWeekend")) {
                for (TaskDemandVisit taskDemandVisit : taskDemand.getWeekendVisits()) {
                    if (taskDemandVisit.getId().toString().equals(taskDemandVisitId)) {
                        droppedTaskDemandVisit = taskDemandVisit;
                    }
                }
            } else {
                for (TaskDemandVisit taskDemandVisit : taskDemand.getWeekdayVisits()) {
                    if (taskDemandVisit.getId().toString().equals(taskDemandVisitId)) {
                        droppedTaskDemandVisit = taskDemandVisit;
                    }
                }
            }
        }
        Date taskStartTime = dateISOFormat.parse(requestPayload.get("startTime").toString());
        List<Task> taskList = getTasksFromDemandVisits(droppedTaskDemandVisit, taskDemand, (boolean) requestPayload.get("isWeekend"), citizenId, taskType, taskStartTime);

        Map<String, String> flsCredentials = genericIntegrationService.getFLS_Credentials(unitId);
        taskConverterService.createFlsCallFromTasks(taskList, flsCredentials);
        //Set Demand Status to Generated, once demand drag&drop in Gantt View.
        if (taskDemand.getStatus() == TaskDemand.Status.VISITATED) {
            taskDemand.setStatus(TaskDemand.Status.GENERATED);
            taskDemandService.save(taskDemand);
        }
        List<TaskGanttDTO> responseList = taskService.customizeTaskData(taskList);
        logger.info("Execution Time :(Planner Service: createTaskFromDemandTimeSlot ) " + (System.currentTimeMillis() - methodExecutionStartTime) + " ms");
        Map returnData = new HashMap();
        returnData.put("taskList", responseList);
        returnData.put("taskDemandList", getCitizenPlanning(unitId, citizenId, false, null));
        return returnData;

    }

    public List<TaskGanttDTO> generateIndividualTask(long unitId, long citizenId, List<List<Map<String, Object>>> taskData) throws ParseException, CloneNotSupportedException {

        //logger.info("taskData " + taskData);

        TaskDemandVisitWrapper taskDemandInfo=genericIntegrationService.
                getPrerequisitesForTaskCreation(citizenId,unitId);
        Map<String, String> flsCredentials=taskDemandInfo.getFlsCredentials();
        TaskAddress taskAddress = taskDemandInfo.getTaskAddress();
        Long loggedInUser =taskDemandInfo.getStaffId();
        List<Long> preferredStaffIds = taskDemandInfo.getPreferredStaff();
        List<Long> forbiddenStaffIds = taskDemandInfo.getForbiddenStaff();

        List<Task> tasksToReturn = new ArrayList<>();
        List<String> taskIdsToMerge = null;

        for (List<Map<String, Object>> taskList : taskData) {
            if (taskList.size() > 1) { //we need to merge tasks,if multiple tasks found in list
                //logger.info("taskList.size() " + taskList.size());
                taskIdsToMerge = new ArrayList<>();
            }
            for (Map<String, Object> taskMap : taskList) {
                //logger.info("taskMap " + taskMap);
                TaskType taskType = taskTypeMongoRepository.findOne(new BigInteger((String)taskMap.get("taskTypeId")));

                SimpleDateFormat executionDateFormat = new SimpleDateFormat(ONLY_DATE);
                DateFormat dateISOFormat = new SimpleDateFormat(ISO_FORMAT);

                Task task = new Task();
                task.setSingleTask(true);
                task.setUnitId(unitId);
                task.setName(taskType.getTitle());
                task.setCitizenId(citizenId);
                task.setTaskTypeId(taskType.getId());
                task.setVisitourTaskTypeID(taskType.getVisitourId());

                task.setTaskStatus(TaskStatus.GENERATED);
                task.setColorForGantt(taskType.getColorForGantt());

                int duration = (int) taskMap.get("duration");
                task.setDuration((int) TimeUnit.SECONDS.toMinutes(duration));
                task.setPriority((int) taskMap.get("priority"));

                task.setPrefferedStaffIdsList(preferredStaffIds);
                task.setForbiddenStaffIdsList(forbiddenStaffIds);

                task.setVisitourTaskTypeID(taskType.getVisitourId());
                Date startDate = executionDateFormat.parse(taskMap.get("resource").toString());
                task.setDateFrom(startDate);
                task.setDateTo(startDate);

                LocalDateTime timeFrom = LocalDateTime.ofInstant(dateISOFormat.parse(taskMap.get("start").toString()).toInstant(), systemDefault());
                LocalDateTime timeTo = LocalDateTime.ofInstant(dateISOFormat.parse(taskMap.get("end").toString()).toInstant(), ZoneId.systemDefault());

                Date updatedTimeFrom = (Date) startDate.clone();
                updatedTimeFrom.setHours(timeFrom.getHour());
                updatedTimeFrom.setMinutes(timeFrom.getMinute());
                updatedTimeFrom.setSeconds(0);
                task.setTimeFrom(updatedTimeFrom);

                Date updatedTimeTo = (Date) startDate.clone();
                updatedTimeTo.setHours(timeTo.getHour());
                updatedTimeTo.setMinutes(timeTo.getMinute());
                updatedTimeTo.setSeconds(0);
                task.setTimeTo(updatedTimeTo);

                task.setAddress(taskAddress);
                task.setSlaStartDuration(taskType.getSlaStartDuration());
                task.setSlaEndDuration(taskType.getSlaEndDuration());
                clientExceptionService.updateTaskException(citizenId, task);

                if(!validateDaySpecification(taskType,task)){
                    exceptionService.internalError("error.task.day.create");
                }

                taskService.save(task);

                if (taskList.size() > 1) {
                    taskIdsToMerge.add(task.getId().toString());
                } else {
                    //taskConverterService.generateTask(task, 0, null, flsCredentials);
                    tasksToReturn.add(task);
                }
                taskService.save(task);
            }
            if (taskIdsToMerge != null && taskIdsToMerge.size() > 1) {
                String mainTaskName = MERGED_TASK_NAME;
                String uniqueID = UUID.randomUUID().toString();
                uniqueID = uniqueID.substring(0, uniqueID.indexOf("-"));
                boolean isActualPlanningScreen = true;
                Task mergedTask = tasksMergingService.mergeTasksWithIds(taskIdsToMerge, unitId, citizenId, mainTaskName, isActualPlanningScreen, uniqueID, taskAddress, loggedInUser, preferredStaffIds, forbiddenStaffIds, flsCredentials);
                //taskConverterService.generateTask(mergedTask, 0, null, flsCredentials);
                tasksToReturn.add(mergedTask);
            }
        }


        if(!tasksToReturn.isEmpty()){
            ClientAggregator clientAggregator = taskExceptionService.updateTaskCountInAggregator(tasksToReturn,unitId,tasksToReturn.get(0).getCitizenId(),false);
            sendAggregateDataToClient(clientAggregator,unitId);
        }

        List<TaskGanttDTO> responseList = customizeTaskData(tasksToReturn);
        return responseList;
    }

    private boolean validateDaySpecification(TaskType taskType,Task task){
        List<DayType> dayTypes = genericIntegrationService.getDayTypes(taskType.getForbiddenDayTypeIds());

        Set<Day> days = new HashSet<>();
        for(DayType dayType : dayTypes){
            days.addAll(dayType.getValidDays());
        }
        TaskSpecification<Task> taskDaySpecification =  new TaskDaySpecification(days);
        return taskDaySpecification.isSatisfied(task);
    }

    public List<TaskGanttDTO> actualPlanningTaskUpdate(long unitId, List<TaskUpdateDTO> customTaskList) throws CloneNotSupportedException, JsonProcessingException, ParseException {
        Map<String, String> flsCredentials = genericIntegrationService.getFLS_Credentials(unitId);
        List<BigInteger> taskIds = new ArrayList<>();
        customTaskList.forEach(customTask -> taskIds.add(new BigInteger(customTask.getId())));

        List<Task> taskList = taskMongoRepository.findByIdIn(taskIds, new Sort(Sort.Direction.ASC, "timeFrom"));
        long citizenId = -1;
        for (Task task : taskList) {
            if (!task.isSingleTask() && task.getActualPlanningTask() == null) {
                taskService.savePreplanningStateOfTask(task);
            }
            Optional<TaskUpdateDTO> taskData = customTaskList.stream().filter(customTask -> new BigInteger(customTask.getId()).equals(task.getId())).findFirst();

            SimpleDateFormat executionDateFormat = new SimpleDateFormat(ONLY_DATE);
            Date updatedDate = executionDateFormat.parse(taskData.get().getResource());

            TaskType taskType = taskTypeMongoRepository.findOne(task.getTaskTypeId());
            Task proxyTask = new Task();
            proxyTask.setDateFrom(updatedDate);
            if(taskType != null && !validateDaySpecification(taskType,proxyTask)){
                exceptionService.internalError("error.task.day.move");
            }
            Date currentDate = DateUtils.getDate(task.getDateFrom().getTime());
            currentDate.setHours(0);
            currentDate.setMinutes(0);
            currentDate.setSeconds(0);
            long daysDifference = 0;
            if (updatedDate != null && !currentDate.equals(updatedDate)) {
                daysDifference = TimeUnit.DAYS.convert(updatedDate.getTime() - currentDate.getTime(), TimeUnit.MILLISECONDS);
            }
            updateTaskInfo(task, taskData.get(), daysDifference);
            clientExceptionService.updateTaskException(task.getCitizenId(), task);
            citizenId = task.getCitizenId();
        }
        taskConverterService.createFlsCallFromTasks(taskList, flsCredentials);

        if(citizenId != -1){
            ClientAggregator clientAggregator = taskExceptionService.updateTaskCountInAggregator(taskList,unitId,citizenId,false);
            sendAggregateDataToClient(clientAggregator,unitId);
        }

        return taskService.customizeTaskData(taskList);
    }


    public List<TaskGanttDTO> prePlanningTaskUpdate(long unitId, TaskUpdateDTO taskData) throws CloneNotSupportedException, JsonProcessingException, ParseException {
        long startTime = System.currentTimeMillis();
        LocalDate upcomingMonday = DateUtils.calcNextMonday(LocalDate.now());
        LocalDate fourWeekLater = upcomingMonday.plusDays(28);

        Map<String, String> flsCredentials = genericIntegrationService.getFLS_Credentials(unitId);
        List<Task> taskList = new ArrayList<>();
        List<Task> taskRepetitionsList=new ArrayList<>();
        List<Task> tasksToReturn = new ArrayList<>();
        List<Task> nonEditableTasks = new ArrayList<>();
        SimpleDateFormat executionDateFormat = new SimpleDateFormat(ONLY_DATE);
        Date updatedDate = null;
        try {
            updatedDate = executionDateFormat.parse(taskData.getResource());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Task task = taskMongoRepository.findOne(new BigInteger(taskData.getId()));
        TaskType taskType = taskTypeMongoRepository.findOne(task.getTaskTypeId());
        Task proxyTask = new Task();
        proxyTask.setDateFrom(updatedDate);
        if(taskType != null && !validateDaySpecification(taskType,proxyTask)){
            exceptionService.internalError("error.task.day.move");
        }
        if (task.isSingleTask() ) {
            return actualPlanningTaskUpdate(unitId,Arrays.asList(taskData));
        }
        if (!task.isSingleTask() && task.getActualPlanningTask() != null) {
            exceptionService.dataNotModifiedException("message.task.update");
        }
        Date currentDate = DateUtils.getDate(task.getDateFrom().getTime());
        currentDate.setHours(0);
        currentDate.setMinutes(0);
        currentDate.setSeconds(0);
        Long daysDifference = null;
        if (updatedDate != null && !currentDate.equals(updatedDate)) {
            daysDifference = TimeUnit.DAYS.convert(updatedDate.getTime() - currentDate.getTime(), TimeUnit.MILLISECONDS);
        }
        if (taskData.getMainTask() != null && taskData.getMainTask() == true && taskData.getUpdateAllByDemand() != null && taskData.getUpdateAllByDemand() == true) {
            exceptionService.dataNotFoundByIdException("error.task.main.update",task.getName());

        } else {
            if (taskData.getUpdateAllByDemand() != null && taskData.getUpdateAllByDemand() == true) {
                taskRepetitionsList = taskService.getTasksByDemandId(task.getTaskDemandId() + "");
            } else {
                taskRepetitionsList = taskMongoRepository.getTaskRepetitionsByEventIdAndAndStartDate(taskData.getJointEvents(), task.getDateFrom());
            }
            List<Task> multiStaffTasks = new ArrayList<>();
            for (Task repeatedtask : taskRepetitionsList) {
                if (!repeatedtask.isSingleTask() && repeatedtask.getActualPlanningTask() == null) {

                    Task existingTask = null;
                    if (daysDifference != null) {
                        Date dateFrom = DateUtils.addDays(repeatedtask.getDateFrom(), daysDifference.intValue());
                        Date dateTo = DateUtils.addDays(repeatedtask.getDateFrom(), daysDifference.intValue());
                        dateFrom.setHours(DAY_START_HOUR);
                        dateFrom.setMinutes(DAY_START_MINUTE);
                        dateTo.setHours(DAY_END_HOUR);
                        dateTo.setMinutes(DAY_END_MINUTE);
                        existingTask = taskMongoRepository.getTaskByDemandIdAndVisitIdAndBetweenDates(task.getTaskDemandId().toString(), task.getTaskDemandVisitId(), dateFrom, dateTo);
                    }
                    if (existingTask == null) {

                        updateTaskInfo(repeatedtask, taskData, daysDifference);
                        taskList.add(repeatedtask);
                        //Update related tasks (if it's multi staff task)
                        if (task.getMultiStaffTask() != null && task.getMultiStaffTask() == true) {
                            List<Task> relatedTaskList = taskMongoRepository.getRelatedMultiStaffTasks(task.getId().toString());
                            for (Task relatedTask : relatedTaskList) {
                                BigInteger taskId = relatedTask.getId();
                                relatedTask = Task.copyProperties(task, Task.getInstance()); //Cloning updated task into it's related tasks.
                                relatedTask.setId(taskId);
                                multiStaffTasks.add(relatedTask);
                            }
                        }
                        LocalDate dateFromInLocalFormat = repeatedtask.getDateFrom().toInstant().atZone(systemDefault()).toLocalDate();
                        if ((dateFromInLocalFormat.isAfter(upcomingMonday) || dateFromInLocalFormat.isEqual(upcomingMonday)) && (dateFromInLocalFormat.isBefore(fourWeekLater) || dateFromInLocalFormat.isEqual(fourWeekLater))) { //Send only 4 weeks tasks to front end.
                            tasksToReturn.add(repeatedtask);
                        }
                        if (repeatedtask.getSubTaskIds() != null && repeatedtask.getSubTaskIds().size() > 0) {

                            List<String> subTaskIds = new ArrayList<>();
                            repeatedtask.getSubTaskIds().forEach(taskId -> subTaskIds.add(taskId.toString()));

                            List<Task> subTaskList = taskMongoRepository.getAllTasksByIdsIn(subTaskIds);
                            for (Task subTask : subTaskList) {
                                subTask.setDateTo(repeatedtask.getDateTo());
                                subTask.setDateFrom(repeatedtask.getDateFrom());
                            }
                            taskService.save(subTaskList);
                        }
                    }
                } else {
                    nonEditableTasks.add(repeatedtask);
                }
            }
            if (!multiStaffTasks.isEmpty()) {
                taskService.save(multiStaffTasks);
            }
        }
        /*if (task.isHasActualTask()) {
            Task actualPlanningTask = taskMongoRepository.findByParentTaskId(task.getId());
            if (actualPlanningTask.getVisitourId() != null && actualPlanningTask.getVisitourId() > 0) {
                taskConverterService.updateAndConfirmTask(actualPlanningTask, flsCredentials);
            }
        } else {
            if (task.getVisitourId() != null && task.getVisitourId() > 0) {
                taskConverterService.updateAndConfirmTask(task, flsCredentials);
            }
        }*/
        if (!taskRepetitionsList.isEmpty()) {
            taskService.save(taskRepetitionsList);
        }

        taskConverterService.createFlsCallFromTasks(tasksToReturn, flsCredentials);
        tasksToReturn.addAll(nonEditableTasks);
        List<TaskGanttDTO> responseList = taskService.customizeTaskData(tasksToReturn);

        logger.info("Execution Time :(PlannerService:prePlanningTaskUpdate) " + (System.currentTimeMillis() - startTime) + "  ms.");
        return responseList;
    }



    private Task updateTaskInfo(Task task, TaskUpdateDTO taskData, Long daysDifference) {

        SimpleDateFormat executionDateFormat = new SimpleDateFormat(ONLY_DATE);
        DateFormat dateISOFormat = new SimpleDateFormat(ISO_FORMAT);

        if(Optional.ofNullable(taskData.getPriority()).isPresent()){
            task.setPriority(taskData.getPriority());
        }

        if( Optional.ofNullable(taskData.getSkillsList()).isPresent()){
            List<String> skillsList = taskData.getSkillsList();
            task.setSkills(String.join(",", skillsList));
        }

        if (Optional.ofNullable(taskData.getTeam()).isPresent()) {
            task.setTeamId(taskData.getTeam());
        }

        if(Optional.ofNullable(taskData.getPrefferedStaff()).isPresent()){
            task.setPrefferedStaffIdsList(taskData.getPrefferedStaff());
        }

        if(Optional.ofNullable(taskData.getForbiddenStaff()).isPresent()){
            task.setForbiddenStaffIdsList(taskData.getForbiddenStaff());
        }

        if(Optional.ofNullable(taskData.getSkillExpertiseList()).isPresent()){
            task.setSkillExpertiseList(taskData.getSkillExpertiseList());
        }

        if (Optional.ofNullable(taskData.getUpdateAllByDemand()).isPresent() == false) {
            try {
                Date dateFrom;
                if (daysDifference != null) {
                    dateFrom = DateUtils.addDays(task.getDateFrom(), daysDifference.intValue());
                } else {
                    dateFrom = task.getDateFrom(); //executionDateFormat.parse(taskData.getResource());
                }
                LocalDateTime timeFrom = LocalDateTime.ofInstant(dateISOFormat.parse(taskData.getStart()).toInstant(), systemDefault());
                LocalDateTime timeTo = LocalDateTime.ofInstant(dateISOFormat.parse(taskData.getEnd()).toInstant(), ZoneId.systemDefault());

                dateFrom.setHours(timeFrom.getHour());
                dateFrom.setMinutes(timeFrom.getMinute());

                task.setDateFrom(dateFrom);
                task.setDateTo(dateFrom);

                Date updatedTimeFrom = (Date) dateFrom.clone();
                //updatedTimeFrom.setDate(dateFrom.getDate());
                updatedTimeFrom.setHours(timeFrom.getHour());
                updatedTimeFrom.setMinutes(timeFrom.getMinute());
                updatedTimeFrom.setSeconds(0);

                task.setTimeFrom(updatedTimeFrom);

                Date updatedTimeTo = (Date) dateFrom.clone();
                //updatedTimeTo.setDate(dateFrom.getDate());
                updatedTimeTo.setHours(timeTo.getHour());
                updatedTimeTo.setMinutes(timeTo.getMinute());
                updatedTimeTo.setSeconds(0);

                task.setTimeTo(updatedTimeTo);

                //long duration = task.getTimeTo().getTime() - task.getTimeFrom().getTime();
                //long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(duration);
                task.setDuration(taskData.getDuration());



                String jointEventId = task.getJoinEventId();
                if(jointEventId!=null && !jointEventId.isEmpty()){
                    LocalDate localDate = dateFrom.toInstant().atZone(systemDefault()).toLocalDate();
                    task.setJoinEventId(jointEventId.replace(jointEventId.substring(0,jointEventId.indexOf('_')),localDate.getDayOfWeek().toString()));
                }


            } catch (ParseException e) {
                e.printStackTrace();
            }
            if(Optional.ofNullable(taskData.getTimeWindow()).isPresent()){
                Map<String, Object> timeWindow = taskData.getTimeWindow();
                int slaStartDuration = (int) TimeUnit.SECONDS.toMinutes((int) timeWindow.get(DURATION));
                if (slaStartDuration > 0 && task.getSlaStartDuration() != slaStartDuration) {
                    task.setSlaStartDuration(slaStartDuration);
                }
            }
        } else if (taskData.getUpdateAllByDemand() == false) {
            task.setInfo1(taskData.getInfo1());
            task.setInfo2(taskData.getInfo2());
        }
        return task;
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

        if(!task.isSingleTask() && task.getActualPlanningTask() != null){
            taskGanttDTO.setEditable(false);
        }

        taskGanttDTO.setResource(resourceDate);
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

    private LocalDate calcNextMonday(LocalDate today) {
        return today.with(TemporalAdjusters.next(DayOfWeek.MONDAY));
    }

    /*
    This method generates initial random dates, which later used to creating repetitions for each date.
     */
    private List<LocalDate> generateInitialRandomDates(LocalDate createTaskFrom, LocalDate dateAfterWeeks, int visitCount, boolean isWeekEnd, LocalDate taskDemandEndDate) {
        List<LocalDate> randomDateList = new ArrayList<>();
        Random random = new Random();
        int minDay = (int) createTaskFrom.toEpochDay();
        int maxDay = (int) dateAfterWeeks.toEpochDay();
        while (randomDateList.size() < visitCount) {
            long randomDay = minDay + random.nextInt(maxDay - minDay);
            LocalDate randomDate = LocalDate.ofEpochDay(randomDay);
            if ((randomDate.isBefore(taskDemandEndDate) || randomDate.isEqual(taskDemandEndDate)) && (!randomDateList.contains(randomDate))) {
                if (isWeekEnd != true) {
                    if (!randomDate.getDayOfWeek().name().equals("SATURDAY") && !randomDate.getDayOfWeek().name().equals("SUNDAY")) {
                        randomDateList.add(randomDate);
                    }
                } else {
                    if (randomDate.getDayOfWeek().name().equals("SATURDAY") || randomDate.getDayOfWeek().name().equals("SUNDAY")) {
                        randomDateList.add(randomDate);
                    }
                }
            }

        }
        return randomDateList;
    }

    /*
    This method receives initial set of random dates. Adds Number of weeks to each date for generating repetition till demand's end date.
     */
    private List<LocalDate> addRepetitionsToRandomDates(List<LocalDate> randomDates, int numberOfWeeks, LocalDate taskDemandEndDate) {
        List<LocalDate> repetitions = new ArrayList<>();
        for (LocalDate randomDate : randomDates) {
            LocalDate nextDate = randomDate.plusWeeks(numberOfWeeks);
            if (nextDate.isBefore(taskDemandEndDate) || nextDate.isEqual(taskDemandEndDate)) {
                repetitions.add(nextDate);
            }
        }
        return repetitions;
    }

    /*
    Add StartBoundaryDate and EndBoundaryDate  to a RandomDate.
     */
    private List<Map<String, LocalDate>> addBoundaryDates(List<LocalDate> initialRandomDates, LocalDate boundaryStartDate, LocalDate boundaryEndDate) {
        List<Map<String, LocalDate>> randomDatesList = new ArrayList<>();
        for (LocalDate randomDate : initialRandomDates) {
            Map<String, LocalDate> map = new HashMap<>();
            map.put("randomDate", randomDate);
            map.put("taskStartBoundary", boundaryStartDate);
            map.put("taskEndBoundary", boundaryEndDate);
            randomDatesList.add(map);
        }
        return randomDatesList;
    }


    public Map synchronizeTaskInVisitour(long unitId, Map<String, Object> synchronizeTaskPayload, long citizenId, boolean isActualPlanningScreen, String startDate) throws ParseException {

        logger.info("synchronizeTaskPayload " + synchronizeTaskPayload.toString());
        LocalDate upcomingMonday = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY));
        LocalDate fourWeekLater = upcomingMonday.plusDays(28);
        Date dateFrom = Date.from(upcomingMonday.atStartOfDay().atZone(systemDefault()).toInstant());
        Date dateTo = Date.from(fourWeekLater.atStartOfDay().atZone(systemDefault()).toInstant());
        List<String> taskIds = (List<String>) synchronizeTaskPayload.get("tasks");
        logger.info(" Task data " + synchronizeTaskPayload);
        List<Task> taskList = Collections.EMPTY_LIST;
        if (taskIds.size() > 0) {
            taskList = taskMongoRepository.getAllTasksByIdsInAndDateRange(taskIds, dateFrom, dateTo);
            logger.info("taskList >>>>>>>  " + taskList);
        }
        String action = synchronizeTaskPayload.get("action").toString();
        //Map<String, String> flsCredentials = integrationService.getFLS_Credentials(unitId);
        Map<String, String> flsCredentials = genericIntegrationService.getFLS_Credentials(unitId);
        if (action.equals("sync")) {
            if (taskList.size() > 0) {
                taskConverterService.createFlsCallFromTasks(taskList, flsCredentials);
            } else {
                logger.info("NO Tasks Available");
            }
        } else if (action.equals("optimize")) {

            Map<String, Object> datePayload = new HashMap<>();
            datePayload.put("startDate", dateFrom);
            datePayload.put("endDate", dateTo);
            Map<String, Object> openCall = new HashMap<>();
            openCall.put("openCallsMode", "2");
            scheduler.optmizeSchedule(openCall, datePayload, flsCredentials);

        } else if (action.equals("fixed")) {
            logger.debug("Fixed preference called :::::::::::::::::::::::::");

            if (taskIds.size() > 0) {
                for (String taskId : taskIds) {

                    Map<String, Object> datePayload = new HashMap<>();
                    //datePayload.put("startDate", dateFrom);
                    //datePayload.put("endDate", dateTo);
                    Map<String, Object> openCall = new HashMap<>();
                    openCall.put("extID", taskId);
                    openCall.put("confirmCalls", "true");
                    //openCall.put("fixCalls", "true");
                    FixScheduleResponse fixScheduleResponse = scheduler.getSchedule(openCall, datePayload, flsCredentials);
                    ArrayOfFixedCall arrayOfFixedCall = fixScheduleResponse.getFixScheduleResult();
                    List<FixedCall> fixedCallList = arrayOfFixedCall.getFixedCall();
                    logger.debug("fixedCallList size " + fixedCallList.size());
                    for (FixedCall fixedCall : fixedCallList) {
                        logger.debug("fixedCall ExtId " + fixedCall.getExtID());
                        logger.debug("fixedCall FMExtID " + fixedCall.getFMExtID());
                        logger.debug("fixedCall date " + fixedCall.getDate());
                        logger.debug("fixedCall state " + fixedCall.getState());
                        logger.debug("fixedCall Arrival " + fixedCall.getArrival());
                        logger.debug("fixedCall Distance " + fixedCall.getDistance());
                        Task task = taskMongoRepository.findOne(new BigInteger(fixedCall.getExtID() + ""));
                        logger.info(" Task found " + task);
                        if (task != null) {
                            if (fixedCall.getState() == 3) {
                                task.setExecutionDate(fixedCall.getArrival().toGregorianCalendar().getTime());
                                task.setTaskStatus(TaskStatus.PLANNED);
                            } else if (fixedCall.getState() == 2) {
                                task.setTaskStatus(TaskStatus.CONFIRMED);
                                task.setStaffId(Long.parseLong(fixedCall.getFMExtID()));
                            } else {
                                task.setTaskStatus(TaskStatus.GENERATED);
                            }
                            if (fixedCall.getFMExtID() != null && fixedCall.getFMExtID() != "") {
                                List<Long> assingedStaffIds = Stream.of(fixedCall.getFMExtID().split(",")).map(Long::parseLong).collect(Collectors.toList());
                                task.setAssignedStaffIds(assingedStaffIds);
                            } else {
                                task.setAssignedStaffIds(Collections.EMPTY_LIST);
                            }
                            taskService.save(task);
                        }
                    }
                }
            }
        }
        if (isActualPlanningScreen) {
            DateFormat dateISOFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date fromDate = dateISOFormat.parse(startDate);
            LocalDate fromDateInLocalFormat = fromDate.toInstant().atZone(systemDefault()).toLocalDate();
            LocalDate toDateInLocalFormat = fromDateInLocalFormat.plusDays(28);
            Date toDate = Date.from(toDateInLocalFormat.atStartOfDay().atZone(systemDefault()).toInstant());
            taskList = taskMongoRepository.getActualPlanningTask(citizenId, fromDate, toDate);
        } else {
            taskList = taskMongoRepository.findAllBetweenDates(citizenId, dateFrom, dateTo);
        }

        if (action.equals("optimize")) {
            if (taskList.size() > 0) {
                for (Task task : taskList) {
                    if (task.getVisitourId() != null && task.getVisitourId() > 0) { // Get CallInfo of only those tasks, where visitour id is available.
                        Map<String, Object> callInfoMetaData = new HashMap<>();
                        callInfoMetaData.put("extID", task.getId());
                        callInfoMetaData.put("vtid", task.getVisitourId());
                        CallInfoRec callInfoRec = scheduler.getCallInfo(callInfoMetaData, flsCredentials);
                        logger.debug(" Data received >>>>>>> " + callInfoRec.getState());
                        if (callInfoRec.getState() == 3) {
                            task.setExecutionDate(callInfoRec.getArrival().toGregorianCalendar().getTime());
                            task.setTaskStatus(TaskStatus.PLANNED);
                        } else if (callInfoRec.getState() == 2) {
                            task.setTaskStatus(TaskStatus.CONFIRMED);
                            task.setStaffId(Long.parseLong(callInfoRec.getFMExtID()));
                        } else {
                            task.setTaskStatus(TaskStatus.GENERATED);
                        }
                        if (callInfoRec.getExtID() != null && callInfoRec.getExtID() != "") {
                            List<Long> assingedStaffIds = Stream.of(callInfoRec.getFMExtID().split(",")).map(Long::parseLong).collect(Collectors.toList());
                            task.setAssignedStaffIds(assingedStaffIds);
                        } else {
                            task.setAssignedStaffIds(Collections.EMPTY_LIST);
                        }
                    }
                }
                taskService.save(taskList);
            }
        }

        List<TaskGanttDTO> responseList = customizeTaskData(taskList);
        logger.info("responseList " + responseList);

        Map returnData = new HashMap();
        returnData.put("taskList", responseList);
        returnData.put("taskDemandList", getCitizenPlanning(unitId, citizenId, false, null));
        return returnData;

    }

    public boolean deleteTasksFromDBAndVisitour(List<Task> taskList, long unitId)  {
        if (!taskList.isEmpty()) {
            // Map<String, String> flsCredentials = integrationService.getFLS_Credentials(unitId);
            //Map<String, String> flsCredentials = integrationServiceRestClient.getFLS_Credentials(unitId);
            for (Task task : taskList) {
                /*Map<String, Object> callMetaData = new HashMap<>();
                callMetaData.put("functionCode", 4);
                callMetaData.put("extID", task.getId());
                callMetaData.put("vtid", task.getVisitourId());


                boolean deleteTask = true;
                if (task.getVisitourId() != null && task.getVisitourId() > 0) { //If VisitourId found then Delete task from Visitour, else delete from kairos only.
                    int returnedValue = scheduler.deleteCall(callMetaData, flsCredentials);
                    if (returnedValue == 0) {
                        deleteTask = false;
                    }
                } else {
                    logger.debug("No Visitour Id: task.getId " + task.getId() + " vtid " + task.getVisitourId());
                }
                if (deleteTask) {
                    task.setTaskStatus(CANCELLED);
                    task.setDeleted(true);
                }*/
                task.setTaskStatus(CANCELLED);
                task.setDeleted(true);
            }
            taskService.save(taskList);
            return true;
        } else {
            logger.info("No Tasks to Delete " + taskList);
            return false;
        }
    }




    public List<Task> mergeRepetitions(List<String> jointEventsIds, Date dateFrom,Long citizenId, long unitId, String mainTaskName, boolean isActualPlanningScreen) throws CloneNotSupportedException {

        //Map<String, String> flsCredentials = integrationService.getFLS_Credentials(unitId);
        TaskDemandVisitWrapper taskDemandVisitWrapper=genericIntegrationService.
                getPrerequisitesForTaskCreation(citizenId,unitId);

        Map<String, String> flsCredentials = taskDemandVisitWrapper.getFlsCredentials();


        Criteria criteria = Criteria.where("joinEventId").in(jointEventsIds).and("dateFrom").gt(dateFrom).and("isDeleted").is(false);

        String projection = "{   $project : { date : {$substr: ['$dateFrom', 0, 10] }}}";

        String group = "{'$group':{'_id':'$date', 'taskIds':{'$push':'$_id'}}}";
        Document groupObject =Document.parse(group);
        Document projectionObject = Document.parse(projection);

        // Aggregate from DbObjects
        Aggregation aggregation = newAggregation(
                match(criteria),
                new CustomAggregationOperation(projectionObject),
                new CustomAggregationOperation(groupObject),
                sort(Sort.Direction.DESC, "dateFrom")
        );
        logger.debug("Merge Repetitions Query: " + aggregation.toString());

        // Result
        AggregationResults<Map> finalResult = mongoTemplate.aggregate(aggregation, Task.class, Map.class);

        List<Map> taskIdsGroupByDate = finalResult.getMappedResults();
        logger.debug("taskIdsGroupByDate: " + taskIdsGroupByDate);

        String uniqueID = UUID.randomUUID().toString();
        uniqueID = uniqueID.substring(0, uniqueID.indexOf("-"));


        TaskAddress taskAddress=taskDemandVisitWrapper.getTaskAddress();
        Long loggedInUser= taskDemandVisitWrapper.getStaffId();
        List<Long> preferredStaffIds=taskDemandVisitWrapper.getPreferredStaff();
        List<Long> forbiddenStaffIds=taskDemandVisitWrapper.getForbiddenStaff();

        List<Task> taskList = new ArrayList<>();
        for (Map map : taskIdsGroupByDate) {

            List<String> taskIds = (List<String>) map.get("taskIds");
            logger.debug("taskIds: " + taskIds);

            Task mergedTask = mergeTasksWithIds(taskIds, unitId,citizenId, mainTaskName,
                    isActualPlanningScreen, uniqueID, taskAddress, loggedInUser, preferredStaffIds, forbiddenStaffIds, flsCredentials);
            taskList.add(mergedTask);
        }
        return taskList;
    }





    public List<TaskGanttDTO> mergeMultipleTasks(String authToken, long unitId, long citizenId, Map<String, Object> tasksData, boolean isActualPlanningScreen) throws CloneNotSupportedException {
        long startTime = System.currentTimeMillis();


        List<String> jointEventsIds = new ArrayList<>();

        logger.debug("tasksData payload <><><><><><><><>" + tasksData);

        List<String> taskIds = new ArrayList<>();
        SimpleDateFormat executionDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date startDate = null;
        for (Map<String, Object> taskData : (List<Map<String, Object>>) tasksData.get("events")) {
            taskIds.add(taskData.get("id").toString());
            jointEventsIds.add(taskData.get("jointEvents").toString());
            try {
                startDate = executionDateFormat.parse(taskData.get("resource").toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        logger.debug("taskIds <><><><><><><><>" + taskIds);

        List<Task> taskList = new ArrayList<>();

        if (isActualPlanningScreen == false) {
            taskList.addAll(mergeRepetitions(jointEventsIds, startDate, citizenId, unitId, tasksData.get("mainTaskName").toString(), isActualPlanningScreen));
        }

        List<TaskGanttDTO> responseList = customizeTaskData(taskList);

        logger.debug("Execution Time :(PlannerService:mergeMultipleTasks) " + (System.currentTimeMillis() - startTime) + " ms");
        logger.debug("responseList " + responseList);
        return responseList;
    }

    private Task mergeTasksWithIds(List<String> taskIds, long unitId,Long citizenId, String mainTaskName, boolean isActualPlanningScreen, String uniqueID, TaskAddress taskAddress,
                                   Long loggedInUser, List<Long> preferredStaffIds, List<Long> forbiddenStaffIds, Map<String, String> flsCredentials) throws CloneNotSupportedException {

        Date mainTaskStartTime = null;
        Date mainTaskEndTime = null;
        Date startDate = null;
        Task firstTask = null;
        Date startBoundaryDate = null;
        Date endBoundaryDate = null;


        int mainTaskDuration = 0;

        Task mainTask = new Task();

        List<BigInteger> subTaskIdsList = new ArrayList<>();
        List<BigInteger> responseSubTaskIdsList = new ArrayList<>();

        List<Task> tasksToDelete = new ArrayList<>();

        Set<String> skillIds = new HashSet<>();

        List<String> jointEventsIds = new ArrayList<>();

        List<Task> tasksToMergeList = taskMongoRepository.getAllTasksByIdsIn(taskIds);
        for (Task task : tasksToMergeList) {

            jointEventsIds.add(task.getJoinEventId());

            if (isActualPlanningScreen) {
                if (TaskTypeEnum.TaskOriginator.PRE_PLANNING.equals(task.getTaskOriginator()) && !task.isHasActualTask()) {
                    Task actualPlanningTask = new Task();
                    BeanUtils.copyProperties(task, actualPlanningTask);
                    actualPlanningTask.setId(null);
                    actualPlanningTask.setParentTaskId(task.getId());
                    actualPlanningTask.setTaskOriginator(TaskTypeEnum.TaskOriginator.ACTUAL_PLANNING);
                    actualPlanningTask.setSubTask(true);
                    taskService.save(actualPlanningTask);
                    task.setHasActualTask(true);
                    taskService.save(task);
                    Task cloneObject = Task.copyProperties(actualPlanningTask, Task.getInstance());
                    cloneObject.setId(task.getId());
                    responseSubTaskIdsList.add(cloneObject.getId());
                    subTaskIdsList.add(actualPlanningTask.getId());
                    task = cloneObject;
                } else if (TaskTypeEnum.TaskOriginator.PRE_PLANNING.equals(task.getTaskOriginator()) && task.isHasActualTask()) {
                    Task actualPlanningTask = taskMongoRepository.findByParentTaskId(task.getId());
                    actualPlanningTask.setSubTask(true);
                    taskService.save(actualPlanningTask);
                    Task cloneObject = Task.copyProperties(actualPlanningTask, Task.getInstance());
                    cloneObject.setId(task.getId());
                    responseSubTaskIdsList.add(cloneObject.getId());
                    subTaskIdsList.add(actualPlanningTask.getId());
                    task = cloneObject;
                } else {
                    task.setSubTask(true);
                    subTaskIdsList.add(task.getId());
                    responseSubTaskIdsList.add(task.getId());
                    taskService.save(task);
                }
            } //else {
            task.setSubTask(true);
            subTaskIdsList.add(task.getId());
            responseSubTaskIdsList.add(task.getId());

            if (task.getVisitourId() != null && task.getVisitourId() > 0) {
                tasksToDelete.add(task);
            }

            taskService.save(task);
            //}

            mainTaskDuration = mainTaskDuration + task.getDuration();

            startDate = task.getDateFrom();
            startBoundaryDate = task.getTaskStartBoundary();
            endBoundaryDate = task.getTaskEndBoundary();


            if (mainTaskStartTime == null && mainTaskEndTime == null) {
                mainTaskStartTime = task.getTimeFrom();
                mainTaskEndTime = task.getTimeTo();
                firstTask = task;
            } else {
                if (task.getTimeFrom().before(mainTaskStartTime)) {
                    mainTaskStartTime = task.getTimeFrom();
                    firstTask = task;
                }
                if (task.getTimeTo().after(mainTaskEndTime)) {
                    mainTaskEndTime = task.getTimeTo();
                }
            }

            if (task.getSkills() != null && !task.getSkills().isEmpty())
                skillIds.addAll(Arrays.asList(task.getSkills().split(",")));
        }

        mainTask.setJoinEventId(uniqueID);

        mainTask.setTaskOriginator(isActualPlanningScreen ? TaskTypeEnum.TaskOriginator.ACTUAL_PLANNING : TaskTypeEnum.TaskOriginator.PRE_PLANNING);

        mainTask.setSkills(String.join(",", skillIds));

        mainTaskEndTime = DateUtils.getDate(mainTaskStartTime.getTime() + (mainTaskDuration * 60000));

        mainTask.setSubTaskIds(subTaskIdsList);

        mainTask.setDateFrom(startDate); //Main task's start date and end date is same. (as all subtasks will be of same day)
        mainTask.setDateTo(startDate); //Main task's start date and end date is same. (as all subtasks will be of same day)
        mainTask.setTaskStartBoundary(startBoundaryDate);
        mainTask.setTaskEndBoundary(endBoundaryDate);

        mainTask.setTimeFrom(mainTaskStartTime);
        mainTask.setTimeTo(mainTaskEndTime);

        //mainTask.setDuration((int) TimeUnit.MILLISECONDS.toMinutes(mainTaskEndTime.getTime() - mainTaskStartTime.getTime()));
        mainTask.setDuration(mainTaskDuration);

        mainTask.setSlaStartDuration(firstTask.getSlaStartDuration() > 0 ? firstTask.getSlaStartDuration() : 0);

        mainTask.setUnitId(unitId);
        mainTask.setCitizenId(citizenId);
        mainTask.setStaffCount(1); //Setting Staff count to 1, as main task has be to delivered by individual.
        mainTask.setPriority(2); //Setting Priority to 2, as it's default priority is fls visitour.
        mainTask.setVisitourTaskTypeID("37"); //Setting TaskType id 37 for Merged Tasks.
        mainTask.setName(mainTaskName);

        mainTask.setPrefferedStaffIdsList(preferredStaffIds);
        mainTask.setForbiddenStaffIdsList(forbiddenStaffIds);
        //mainTask.setPrefferedStaffIdsList(preferredStaffIds.stream().map(Long::intValue).collect(Collectors.toList()));
        //mainTask.setForbiddenStaffIdsList(forbiddenStaffIds.stream().map(Long::intValue).collect(Collectors.toList()));

        mainTask.setTaskStatus(TaskStatus.GENERATED);

        mainTask.setAddress(taskAddress);

        mainTask.setCreatedByStaffId(loggedInUser);

        mainTask.setSubTask(false);
        taskService.save(mainTask);

        /*for (Task task : tasksToDelete) {
            Map<String, Object> callMetaData = new HashMap<>();
            callMetaData.put("functionCode", 4);
            callMetaData.put("extID", task.getId());
            callMetaData.put("vtid", task.getVisitourId());
            scheduler.deleteCall(callMetaData, flsCredentials);
        }*/

        Task responseMainTask = Task.copyProperties(mainTask, Task.getInstance());
        responseMainTask.setSubTaskIds(responseSubTaskIdsList);
        return responseMainTask;
    }

    private Map<String, Object> unMergeTasks(Task mainTask, List<Task> unMergeTasksList, boolean isActualPlanningScreen) throws CloneNotSupportedException {

        int mainTaskDuration = 0;

        Map<String, Object> returnedData = new HashMap<>();

        List<Task> taskList = new ArrayList<>();
        List<Task> tasksToCreate = new ArrayList<>();
        List<Task> tasksToDelete = new ArrayList<>();

        List<BigInteger> subTaskIdsList = new ArrayList<>();
        for (Task taskToUnmerge : unMergeTasksList) {

            Task task = taskToUnmerge;  //taskMongoRepository.findOne(new BigInteger(taskData.get("id").toString()));

            boolean isSubTask = true;

            if (isActualPlanningScreen) {
                if (TaskTypeEnum.TaskOriginator.PRE_PLANNING.equals(task.getTaskOriginator()) && !task.isHasActualTask()) {
                    Task actualPlanningTask = Task.copyProperties(task, Task.getInstance());
                    actualPlanningTask.setId(null);
                    actualPlanningTask.setDateFrom(mainTask.getDateFrom());
                    actualPlanningTask.setDateTo(mainTask.getDateTo());
                    actualPlanningTask.setParentTaskId(task.getId());
                    actualPlanningTask.setTaskOriginator(TaskTypeEnum.TaskOriginator.ACTUAL_PLANNING);
                    task.setHasActualTask(true);

                    //boolean isSubTask = (boolean) taskData.get("isSelected");
                    Task cloneObject;
                    if (isSubTask) {
                        actualPlanningTask.setSubTask(false);
                        cloneObject = Task.copyProperties(actualPlanningTask, Task.getInstance());
                        cloneObject.setId(task.getId());
                        taskList.add(cloneObject);
                    } else {
                        cloneObject = Task.copyProperties(actualPlanningTask, Task.getInstance());
                        cloneObject.setId(task.getId());
                        subTaskIdsList.add(actualPlanningTask.getId());
                        int duration = actualPlanningTask.getDuration();
                        mainTaskDuration = mainTaskDuration + duration;
                    }
                    taskService.save(actualPlanningTask);
                } else if (TaskTypeEnum.TaskOriginator.PRE_PLANNING.equals(task.getTaskOriginator()) && task.isHasActualTask()) {
                    Task actualPlanningTask = taskMongoRepository.findByParentTaskId(task.getId());
                    actualPlanningTask.setDateFrom(mainTask.getDateFrom());
                    actualPlanningTask.setDateTo(mainTask.getDateTo());

                    //boolean isSubTask = (boolean) taskData.get("isSelected");

                    Task cloneObject;
                    if (isSubTask) {
                        actualPlanningTask.setSubTask(false);
                        cloneObject = Task.copyProperties(actualPlanningTask, Task.getInstance());
                        cloneObject.setId(task.getId());
                        taskList.add(cloneObject);
                    } else {
                        subTaskIdsList.add(actualPlanningTask.getId());
                        int duration = task.getDuration();
                        mainTaskDuration = mainTaskDuration + duration;
                    }
                    taskService.save(actualPlanningTask);
                } else {
                    task.setDateFrom(mainTask.getDateFrom());
                    task.setDateTo(mainTask.getDateTo());

                    //boolean isSubTask = (boolean) taskData.get("isSelected");
                    if (isSubTask) {
                        task.setSubTask(false);
                        taskList.add(task);
                    } else {
                        subTaskIdsList.add(task.getId());
                        int duration = task.getDuration();
                        mainTaskDuration = mainTaskDuration + duration;
                    }
                    taskService.save(task);
                }
            } else {
                task.setDateFrom(mainTask.getDateFrom());
                task.setDateTo(mainTask.getDateTo());

                //boolean isSubTask = (boolean) taskData.get("isSelected");
                //if (isSubTask) {
                task.setSubTask(false);
                mainTask.getSubTaskIds().remove(task.getId());
                int duration = task.getDuration();
                mainTaskDuration = mainTaskDuration + duration;

                if (task.getVisitourId() != null && task.getVisitourId() > 0) {
                    tasksToCreate.add(task);
                }

                //}
                taskService.save(task);
                taskList.add(task);
            }
        }

        if (isActualPlanningScreen && TaskTypeEnum.TaskOriginator.PRE_PLANNING.equals(mainTask.getTaskOriginator())) {
            mainTask.setHasActualTask(true);
            taskService.save(mainTask);
            Task copyForActualPlanning = Task.copyProperties(mainTask, Task.getInstance());
            copyForActualPlanning.setTaskOriginator(TaskTypeEnum.TaskOriginator.ACTUAL_PLANNING);
            copyForActualPlanning.setId(null);
            copyForActualPlanning.setHasActualTask(false);
            copyForActualPlanning.setParentTaskId(mainTask.getId());
            mainTask = copyForActualPlanning;
        }
        //mainTask.setSubTaskIds(subTaskIdsList);

        if (mainTask.getSubTaskIds().size() > 0) {

            mainTaskDuration = mainTask.getDuration() - mainTaskDuration;

            mainTask.setTimeTo(DateUtils.getDate(mainTask.getTimeFrom().getTime() + (mainTaskDuration * 60000)));

            long duration = mainTask.getTimeTo().getTime() - mainTask.getTimeFrom().getTime();

            long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(duration);
            mainTask.setDuration((int) diffInMinutes);

            taskService.save(mainTask);
            taskList.add(mainTask);

        } else {
            mainTask.setDeleted(true);
            tasksToDelete.add(mainTask);
        }
        taskService.save(mainTask);

        returnedData.put("taskList", taskList);
        returnedData.put("tasksToCreate", tasksToCreate);
        returnedData.put("tasksToDelete", tasksToDelete);
        return returnedData;
    }



    public List<TaskGanttDTO> unMergeMultipleTasks(String authToken, long unitId, long citizenId, Map<String, Object> tasksData, boolean isActualPlanningScreen) throws CloneNotSupportedException {
        long startTime = System.currentTimeMillis();

        Map<String, Object> returnedData;

        List<Task> tasksToReturn = new ArrayList<>();
        List<Task> tasksToCreate = new ArrayList<>();
        List<Task> tasksToDelete = new ArrayList<>();
        logger.debug("tasksData payload <><><><><><><><>" + tasksData);
        String mainTaskId = tasksData.get("mainTaskId").toString();
        Task mainTask = taskMongoRepository.findOne(new BigInteger(mainTaskId));

        List<String> jointEventsIds = new ArrayList<>();
        jointEventsIds.add(mainTask.getJoinEventId());

        List<String> unMergeTaskIds = new ArrayList<>();
        for (Map<String, Object> taskData : (List<Map<String, Object>>) tasksData.get("tasksToUnmerge")) {
            if ((boolean) taskData.get("isSelected") == true) {
                jointEventsIds.add(taskData.get("jointEvents").toString());
                unMergeTaskIds.add(taskData.get("id").toString());
            }

        }

        List<Task> unMergeTasksList = taskMongoRepository.getAllTasksByIdsIn(unMergeTaskIds);
        returnedData = unMergeTasks(mainTask, unMergeTasksList, isActualPlanningScreen);
        tasksToReturn.addAll((List<Task>) returnedData.get("taskList"));
        tasksToCreate.addAll((List<Task>) returnedData.get("tasksToCreate"));
        tasksToDelete.addAll((List<Task>) returnedData.get("tasksToDelete"));


        Criteria criteria = Criteria.where("joinEventId").in(jointEventsIds).and("dateFrom").gt(mainTask.getDateFrom()).and("isDeleted").is(false);

        String projection = "{   $project : { date : {$substr: ['$dateFrom', 0, 10] }}}";

        String group = "{'$group':{'_id':'$date', 'taskIds':{'$push':'$_id'}}}";
        Document groupObject = Document.parse(group);
        Document projectionObject = Document.parse(projection);


        // Aggregate from DbObjects
        Aggregation aggregation = newAggregation(
                match(criteria),
                new CustomAggregationOperation(projectionObject),
                new CustomAggregationOperation(groupObject),
                sort(Sort.Direction.DESC, "dateFrom")
        );

        // Result
        AggregationResults<Map> finalResult = mongoTemplate.aggregate(aggregation, Task.class, Map.class);

        List<Map> taskIdsListGroupByDate = finalResult.getMappedResults();

        for (Map map : taskIdsListGroupByDate) {

            List<String> taskIds = (List<String>) map.get("taskIds");

            List<Task> taskList = taskMongoRepository.getAllTasksByIdsIn(taskIds);

            Task mainTaskNew = null;
            List<Task> mergedTaskList = new ArrayList<>();
            for (Task task : taskList) {
                if (task.getSubTaskIds() != null && task.getSubTaskIds().size() > 1) {
                    mainTaskNew = task;
                } else {
                    mergedTaskList.add(task);
                }
            }
            if (mainTaskNew != null) {
                returnedData = unMergeTasks(mainTaskNew, mergedTaskList, isActualPlanningScreen);
                tasksToReturn.addAll((List<Task>) returnedData.get("taskList"));
                tasksToCreate.addAll((List<Task>) returnedData.get("tasksToCreate"));
                tasksToDelete.addAll((List<Task>) returnedData.get("tasksToDelete"));
            }
        }

        //Map<String, String> flsCredentials = integrationService.getFLS_Credentials(unitId);
        Map<String, String> flsCredentials=genericIntegrationService.getFLS_Credentials(unitId);
        if (tasksToCreate.size() > 0) {
            taskConverterService.createFlsCallFromTasks(tasksToCreate, flsCredentials);
        } else {
            logger.info("NO Tasks Available");
        }

        /*for (Task task : tasksToDelete) {
            Map<String, Object> callMetaData = new HashMap<>();
            callMetaData.put("functionCode", 4);
            callMetaData.put("extID", task.getId());
            callMetaData.put("vtid", task.getVisitourId());
            scheduler.deleteCall(callMetaData, flsCredentials);
        }*/

        List<TaskGanttDTO> responseList = customizeTaskData(tasksToReturn);
        logger.info("Execution Time :(PlannerService:Un-mergeMultipleTasks) " + (System.currentTimeMillis() - startTime) + " ms");
        return responseList;
    }

    public List<TaskGanttDTO> revertActualPlanningTask(List<String> taskIds, long unitId) {
        List<Task> tasksToRevert = taskMongoRepository.getAllTasksByIdsIn(taskIds);
        if (tasksToRevert.isEmpty()) {
            return Collections.emptyList();
        }
        ObjectMapper objectMapper = new ObjectMapper();
        List<Task> revertTaskState = new ArrayList<>();
        tasksToRevert.forEach(prePlanningTask -> {
            if (!prePlanningTask.isSingleTask() && prePlanningTask.getActualPlanningTask() != null) {
                Task actualTask = objectMapper.convertValue(prePlanningTask.getActualPlanningTask(), Task.class);
                actualTask.setId(prePlanningTask.getId());
                actualTask.setActualPlanningTask(null);
                clientExceptionService.updateTaskException(actualTask.getCitizenId(), actualTask);
                revertTaskState.add(actualTask);
            }
        });
        // Map<String, String> flsCredentials = integrationService.getFLS_Credentials(unitId);
        Map<String, String> flsCredentials=genericIntegrationService.getFLS_Credentials(unitId);
        taskConverterService.createFlsCallFromTasks(revertTaskState, flsCredentials);
        return customizeTaskData(revertTaskState);
    }



    /**
     *
     * @param timeSlotIds
     * @param unitId

     * @return
     */
    public Map<String, Object> getCitizenListByTimeSlotIds(List<Long> timeSlotIds, Long unitId) {
        if (timeSlotIds == null || timeSlotIds.size() == 0)
            return getOrganizationClientsWithPlanning(unitId);
        List<Long> citizenIds = taskDemandService.getListOfClientByTimeSlotId(timeSlotIds, unitId);
        if (citizenIds == null || citizenIds.size() == 0) {
            Map<String, Object> returnBlankData = new HashMap<>();
            returnBlankData.put("clientList", Collections.EMPTY_LIST);
            return returnBlankData;
        }
        return getClintsWithPlanningByClintIds(citizenIds, unitId);
    }


    /**
     *
     * @auther  Anil maurya
     *
     * @param organizationId
     * @return
     */
    private  Map<String, Object> getOrganizationClientsWithPlanning(Long organizationId) {
        Map<String, Object> response = new HashMap<>();
        List<Object> clientList = new ArrayList<>();

        logger.debug("Finding citizen with Id: " + organizationId);
        OrganizationClientWrapper organizationClientWrapper= genericIntegrationService.getOrganizationClients(organizationId);
        //List<Map<String, Object>> mapList = organizationGraphRepository.getClientsOfOrganizationExcludeDead(organizationId,envConfig.getServerHost() + FORWARD_SLASH);
        List<Map<String, Object>> mapList=organizationClientWrapper.getClientList();
        logger.debug("CitizenList Size: " + mapList.size());
        Long staffId=organizationClientWrapper.getStaffId();

        Map<String, Object> responseFromTask=taskDemandService.getOrganizationClientsWithPlanning(organizationId,staffId,mapList);
        response.putAll(responseFromTask);

        //Map<String, Object> timeSlotData = timeSlotService.getTimeSlots(organizationId);
        Map<String, Object> timeSlotData=organizationClientWrapper.getTimeSlotData();

        if (timeSlotData != null) {
            response.put("timeSlotList", timeSlotData);
        }

        return response;
    }

    /**
     * @auther anil maurya
     * @param citizenIds
     * @param unitId
     * @return
     */
    private  Map<String, Object> getClintsWithPlanningByClintIds(List<Long> citizenIds, Long unitId) {
        Map<String, Object> response = new HashMap<>();
        List<Object> clientList = new ArrayList<>();
        OrganizationClientWrapper organizationClientWrapper=genericIntegrationService.
                getClientsByIds(citizenIds);
        logger.info("Finding citizen with Id: " + citizenIds);
        //List<Map<String, Object>> mapList = organizationGraphRepository.getClientsByClintIdList(citizenId);
        List<Map<String, Object>> mapList=organizationClientWrapper.getClientList();
        logger.info("CitizenList Size: " + mapList.size());

        if (mapList != null) {
            logger.info("Adding Citizen ");
            for (Map<String, Object> map : mapList) {
                clientList.add(map.get("Client"));
            }
            response.put("clientList", clientList);
        }

        List<ClientExceptionType> exceptionTypeData = clientExceptionTypeMongoRepository.findAll();

        if (exceptionTypeData != null) {
            response.put("exceptionTypes", exceptionTypeData);
        }

        //Map<String, Object> timeSlotData = timeSlotService.getTimeSlots(unitId);
        Map<String, Object> timeSlotData=organizationClientWrapper.getTimeSlotData();

        if (timeSlotData != null) {
            response.put("timeSlotList", timeSlotData);
        }
        return response;
    }





    public List<TaskGanttDTO> autoGenerateTasks(long unitId, long citizenId) throws ParseException, CloneNotSupportedException {

        long startTime = System.currentTimeMillis();

        LocalDate upcomingMonday = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY));
        LocalDate fourWeekLater = upcomingMonday.plusDays(7);
        Date dateTo = Date.from(fourWeekLater.atStartOfDay().atZone(systemDefault()).toInstant());
        //Organization organization = organizationGraphRepository.findOne(unitId);
        logger.debug("DATE FROM -->" + dateTo);

        List<TaskDemand> taskDemands = taskDemandMongoRepository.getByUnitIdAndStatusBetweenDates(unitId, TaskDemand.Status.VISITATED, dateTo);
        logger.debug("Count of demands  " + taskDemands.size());

        Client client;
         TaskType taskType;
        List<Task> tasksToReturn = new ArrayList<>();
        List<Task> weekDayTask;
        List<Task> weekEndTask;
        boolean isTaskGenerated = false;
        for (TaskDemand taskDemand : taskDemands) {

            taskType = taskTypeMongoRepository.findOne(taskDemand.getTaskTypeId());
            List<TaskDemandVisit> weekendVisits = taskDemand.getWeekendVisits();
            if (taskType!=null) {
                for (TaskDemandVisit weekendVisit : weekendVisits) {
                    if (weekendVisit.getId() != null) {
                        weekEndTask = getTasksFromDemandVisits(weekendVisit, taskDemand, true,taskDemand.getCitizenId(), taskType, null);
                        if (!weekEndTask.isEmpty()) {
                            isTaskGenerated = true;
                        }
                        if (taskDemand.getCitizenId() == citizenId) {
                            tasksToReturn.addAll(weekEndTask);
                        }
                    }

                }

                List<TaskDemandVisit> weekdayVisits = taskDemand.getWeekdayVisits();

                for (TaskDemandVisit weekdayVisit : weekdayVisits) {
                    weekDayTask = getTasksFromDemandVisits(weekdayVisit, taskDemand, false,citizenId, taskType, null);
                    if (!weekDayTask.isEmpty()) {
                        isTaskGenerated = true;
                    }
                    if (taskDemand.getCitizenId() == citizenId) {
                        tasksToReturn.addAll(weekDayTask);
                    }
                }
                if (isTaskGenerated)
                    taskDemand.setStatus(TaskDemand.Status.GENERATED);
            }


        }

        // to update status of task demands
        if (!taskDemands.isEmpty()) taskDemandService.save(taskDemands);
        logger.info("total time of PlannerService:autoGenerateTasks()" + (System.currentTimeMillis() - startTime) + " ms");
        //organization.setAutoGeneratedPerformed(true);
        //organizationGraphRepository.save(organization);
        organizationRestClient.updateAutoGenerateTaskSettings(unitId);
        return customizeTaskData(tasksToReturn);
    }





    public boolean updatePreferredTimeOfDemands(long unitId) {
        LocalDate upcomingMonday = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY));
        LocalDate fourWeekLater = upcomingMonday.plusDays(28);
        Date dateFrom = Date.from(upcomingMonday.atStartOfDay().atZone(systemDefault()).toInstant());
        Date dateTo = Date.from(fourWeekLater.atStartOfDay().atZone(systemDefault()).toInstant());
        List<TaskDemand> taskDemands = taskDemandMongoRepository.getByUnitIdAndStatusBetweenDates(unitId, TaskDemand.Status.VISITATED, dateFrom);
        List<TaskDemand> demandsToUpdate = new ArrayList<>();
        for (TaskDemand taskDemand : taskDemands) {

            if (taskDemand.getWeekendVisits() != null) {
                for (TaskDemandVisit weekendVisit : taskDemand.getWeekendVisits()) {

                    if ("Day".equals(weekendVisit.getTimeSlotName())) {
                        weekendVisit.setPreferredTime("07:33");
                        weekendVisit.setPreferredHour("7");
                        weekendVisit.setPreferredMinute("33");
                    } else if ("Evening".equals(weekendVisit.getTimeSlotName())) {
                        weekendVisit.setPreferredTime("19:33");
                        weekendVisit.setPreferredHour("19");
                        weekendVisit.setPreferredMinute("33");
                    } else {
                        weekendVisit.setPreferredTime("01:10");
                        weekendVisit.setPreferredHour("1");
                        weekendVisit.setPreferredMinute("10");
                    }
                }
            }

            if (taskDemand.getWeekdayVisits() != null) {
                for (TaskDemandVisit weekdayVisit : taskDemand.getWeekdayVisits()) {

                    if ("Day".equals(weekdayVisit.getTimeSlotName())) {
                        weekdayVisit.setPreferredTime("07:33");
                        weekdayVisit.setPreferredHour("7");
                        weekdayVisit.setPreferredMinute("33");
                    } else if ("Evening".equals(weekdayVisit.getTimeSlotName())) {
                        weekdayVisit.setPreferredTime("19:33");
                        weekdayVisit.setPreferredHour("19");
                        weekdayVisit.setPreferredMinute("33");
                    } else if ("Night".equals(weekdayVisit.getTimeSlotName())) {
                        weekdayVisit.setPreferredTime("01:10");
                        weekdayVisit.setPreferredHour("1");
                        weekdayVisit.setPreferredMinute("10");
                    }
                }
            }

            demandsToUpdate.add(taskDemand);
        }

        taskDemandService.save(demandsToUpdate);
        return true;

    }

    /**
     * @param unitId
     * @return
     */
    public boolean oneTimeSync(long unitId) {
        long startTime = System.currentTimeMillis();
        OrganizationDTO unit =organizationRestClient.getOrganization(unitId);
        if (unit == null) {
            exceptionService.dataNotFoundByIdException("message.unit.id");
        }

        if (unit.isOneTimeSyncPerformed()) {
            logger.debug("One time sync already performed on this organization");
            return false;
        }

        LocalDate upcomingMonday = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY));
        LocalDate fourWeekLater = upcomingMonday.plusDays(28);
        Date dateFrom = Date.from(upcomingMonday.atStartOfDay().atZone(systemDefault()).toInstant());
        Date dateTo = Date.from(fourWeekLater.atStartOfDay().atZone(systemDefault()).toInstant());

        logger.debug("DATE FROM -->" + dateFrom);

        List<Task> tasks = taskMongoRepository.getTaskBetweenDatesForUnit(unitId, dateFrom, dateTo);
        logger.info("No of tasks to sync " + tasks.size());
        Map<String, String> flsCredentials = genericIntegrationService.getFLS_Credentials(unitId);
        if (tasks.size() > 0) {
            taskConverterService.createFlsCallFromTasks(tasks, flsCredentials);
        } else {
            logger.debug("NO Tasks Available");
        }
        organizationRestClient.setOneTimeSyncPerformed(unitId);
        logger.debug("Total time taken by one time sync::" + (System.currentTimeMillis() - startTime) + "   ms");
        return true;
    }


    public List<TaskGanttDTO> updateUnhandledActualPlanningTasks(long unitId, TaskDTO taskDTO) {
        List<Task> tasks = taskMongoRepository.findByIdIn(taskDTO.getTaskIds(), new Sort(Sort.Direction.ASC, "timeFrom"));
        tasks.forEach(task -> {
            if (!task.isSingleTask() && task.getActualPlanningTask() == null) {
                taskService.savePreplanningStateOfTask(task);
            }
            updateUnhandledTaskInfo(task, taskDTO);
        });

        // Map<String, String> flsCredentials = integrationService.getFLS_Credentials(unitId);
        Map<String, String> flsCredentials = genericIntegrationService.getFLS_Credentials(unitId);
        taskConverterService.createFlsCallFromTasks(tasks, flsCredentials);
        return customizeTaskData(tasks);
    }

    private void updateUnhandledTaskInfo(Task task, TaskDTO taskDTO) {
        task.setForbiddenStaffIdsList(taskDTO.getForbiddenStaff() == null || taskDTO.getForbiddenStaff().isEmpty() ? task.getForbiddenStaffIdsList() : taskDTO.getForbiddenStaff());
        task.setPrefferedStaffIdsList(taskDTO.getForbiddenStaff() == null || taskDTO.getForbiddenStaff().isEmpty() ? task.getForbiddenStaffIdsList() : taskDTO.getForbiddenStaff());
        task.setInfo1(taskDTO.getInfo1() == null ? task.getInfo1() : task.getInfo1() + taskDTO.getInfo1());
        task.setInfo2(taskDTO.getInfo2() == null ? task.getInfo2() : task.getInfo2() + taskDTO.getInfo2());
        task.setSkills(taskDTO.getSkillsList() == null || taskDTO.getSkillsList().isEmpty() ? task.getSkills() : String.join(",", taskDTO.getSkillsList()));
        task.setTeamId(taskDTO.getTeam() == null ? task.getTeamId() : taskDTO.getTeam());
        task.setDuration(taskDTO.getDuration() == null ? task.getDuration() : (int) TimeUnit.SECONDS.toMinutes(taskDTO.getDuration()));
        task.setPriority(taskDTO.getPriority() == null ? task.getPriority() : taskDTO.getPriority());
        Iterator<Task.ClientException> clientExceptionIterator = task.getClientExceptions().iterator();
        while (clientExceptionIterator.hasNext()) {
            Task.ClientException clientException = clientExceptionIterator.next();
            if (SICK.equals(clientException.getValue())) {
                clientExceptionIterator.remove();
                break;
            }
        }
    }

    public List<TaskGanttDTO> copySingleTask(BigInteger taskId, List<Map<String, Object>> taskData) throws ParseException {
        {

            Task task = taskMongoRepository.findOne(taskId);
            if (task!=null) {
                exceptionService.dataNotFoundByIdException("message.task.id");
            }
            if (!task.isSingleTask()) {
                exceptionService.internalError("error.task.single");
            }
            ArrayList<Task> tasksToReturn = new ArrayList<>(taskData.size());
            SimpleDateFormat executionDateFormat = new SimpleDateFormat(ONLY_DATE);
            DateFormat dateISOFormat = new SimpleDateFormat(ISO_FORMAT);

            for(Map<String, Object> taskDataMap : taskData){
                Task cloneTask = Task.getInstance();
                Task.copyProperties(task, cloneTask);
                cloneTask.setId(null);
                cloneTask.setVisitourId(null);
                cloneTask.setMultiStaffTask(false);

                Date startDate = executionDateFormat.parse(taskDataMap.get("resource").toString());
                cloneTask.setDateFrom(startDate);
                cloneTask.setDateTo(startDate);

                LocalDateTime timeFrom = LocalDateTime.ofInstant(dateISOFormat.parse(taskDataMap.get("start").toString()).toInstant(), systemDefault());
                LocalDateTime timeTo = LocalDateTime.ofInstant(dateISOFormat.parse(taskDataMap.get("end").toString()).toInstant(), ZoneId.systemDefault());

                Date updatedTimeFrom = (Date) startDate.clone();
                updatedTimeFrom.setHours(timeFrom.getHour());
                updatedTimeFrom.setMinutes(timeFrom.getMinute());
                updatedTimeFrom.setMinutes(0);
                cloneTask.setTimeFrom(updatedTimeFrom);

                Date updatedTimeTo = (Date) startDate.clone();
                updatedTimeTo.setHours(timeTo.getHour());
                updatedTimeTo.setMinutes(timeTo.getMinute());
                updatedTimeTo.setMinutes(0);
                cloneTask.setTimeTo(updatedTimeTo);

                clientExceptionService.updateTaskException(cloneTask.getCitizenId(), cloneTask);
                //save(cloneTask);
                tasksToReturn.add(cloneTask);

            }

        /*Date updatedDate = executionDateFormat.parse(taskData.get("resource").toString());
        Date currentDate = DateUtils.getDate(cloneTask.getDateFrom().getTime());
        currentDate.setHours(0);
        currentDate.setMinutes(0);
        currentDate.setSeconds(0);
        long daysDifference = 0;
        if (updatedDate != null && !currentDate.equals(updatedDate)) {
            daysDifference = TimeUnit.DAYS.convert(updatedDate.getTime() - currentDate.getTime(), TimeUnit.MILLISECONDS);
        }
        updateTaskInfo(cloneTask, taskData, daysDifference);*/
            taskService.save(tasksToReturn);
            Map<String, String> flsCredentials = genericIntegrationService.getFLS_Credentials(task.getUnitId());
            taskConverterService.createFlsCallFromTasks(tasksToReturn, flsCredentials);
            return customizeTaskData(tasksToReturn);
        }
    }

    /**
     * to save settings of planner of actual planner, like {confirmation on delete exception} etc
     *
     * @param unitId
     * @param plannerSettings
     * @return
     */
    public void saveSettingsOfPlanner(long unitId, Map<String, Object> plannerSettings) {

        long loggedinUserId = UserContext.getUserDetails().getId();
        long citizenId;

        if (plannerSettings.get("citizenId") instanceof Integer) {
            citizenId = (int) plannerSettings.get("citizenId");
        } else {
            citizenId = (long) plannerSettings.get("citizenId");
        }

        CustomTimeScale customTimeScale = customTimeScaleRepository.findByStaffIdAndCitizenIdAndUnitId(loggedinUserId, citizenId, unitId);
        if (customTimeScale == null) {
            customTimeScale = new CustomTimeScale(loggedinUserId, citizenId, unitId, 0);
        }
        customTimeScale.setShowExceptionDeleteConfirmation((boolean) plannerSettings.get("showExceptionDeleteConfirmation"));
        save(customTimeScale);
    }

    public List<TaskGanttDTO> updateBulkTask(long unitId, BulkUpdateTaskDTO bulkUpdateTaskDTO) {

        List<Task> tasks = taskMongoRepository.findByIdIn(bulkUpdateTaskDTO.getTaskIds(), new Sort(Sort.Direction.ASC, "timeFrom"));
        if (tasks.isEmpty()) {
            return Collections.emptyList();
        }
        tasks.forEach(task -> {
            if (!task.isSingleTask() && task.getActualPlanningTask() == null) {
                taskService.savePreplanningStateOfTask(task);
            }
            updateTaskInfo(task, bulkUpdateTaskDTO);
        });
        //Map<String, String> flsCredentials = integrationService.getFLS_Credentials(unitId);
        Map<String, String> flsCredentials = genericIntegrationService.getFLS_Credentials(unitId);
        taskConverterService.createFlsCallFromTasks(tasks, flsCredentials);
        return customizeTaskData(tasks);
    }

    private void updateTaskInfo(Task task, BulkUpdateTaskDTO bulkUpdateTaskDTO) {


        if (bulkUpdateTaskDTO.getTeam() != null) {
            task.setTeamId(bulkUpdateTaskDTO.getTeam() == null ? task.getTeamId() : bulkUpdateTaskDTO.getTeam());
        }
        if (bulkUpdateTaskDTO.getRemoveTeam() != null && bulkUpdateTaskDTO.getRemoveTeam()) {
            task.setTeamId("");
            task.setVisitourTeamId(null);
        }
        if (bulkUpdateTaskDTO.getSkillsList() != null) {
            task.setSkills(String.join(",", bulkUpdateTaskDTO.getSkillsList()));
        }
        if (bulkUpdateTaskDTO.getRemoveSkills() != null && bulkUpdateTaskDTO.getRemoveSkills()) {
            task.setSkills("");
        }
        if (bulkUpdateTaskDTO.getForbiddenStaff() != null) {
            task.setForbiddenStaffIdsList(bulkUpdateTaskDTO.getForbiddenStaff());
        }
        if (bulkUpdateTaskDTO.getRemoveNotAllowedStaff() != null && bulkUpdateTaskDTO.getRemoveNotAllowedStaff()) {
            task.setForbiddenStaffIdsList(null);
        }
        if (bulkUpdateTaskDTO.getPrefferedStaff() != null) {
            task.setPrefferedStaffIdsList(bulkUpdateTaskDTO.getPrefferedStaff());
        }
        if (bulkUpdateTaskDTO.getRemoveAllowedStaff() != null && bulkUpdateTaskDTO.getRemoveAllowedStaff()) {
            task.setPrefferedStaffIdsList(null);
        }
        if(bulkUpdateTaskDTO.getInfo1() != null){
            task.setInfo1(bulkUpdateTaskDTO.getInfo1());
        }
        if(bulkUpdateTaskDTO.getInfo2() != null){
            task.setInfo2(bulkUpdateTaskDTO.getInfo2());
        }

        if (bulkUpdateTaskDTO.getSlaDuration() != null) {
            task.setSlaStartDuration(bulkUpdateTaskDTO.getSlaDuration());
        }
        task.setPriority(bulkUpdateTaskDTO.getPriority() == null ? task.getPriority() : bulkUpdateTaskDTO.getPriority());

        if (bulkUpdateTaskDTO.getPercentageDuration() != null && bulkUpdateTaskDTO.getPercentageDuration() > 1) {
            updateTaskDuration(task,bulkUpdateTaskDTO.isReduced(),bulkUpdateTaskDTO.getPercentageDuration());
        }
    }

    private void updateTaskDuration(Task task,boolean reduction,Integer percentageDuration){
        LocalDateTime timeTo = LocalDateTime.ofInstant(task.getTimeTo().toInstant(), ZoneId.systemDefault());
        int minutes = task.getDuration() * percentageDuration / 100;
        logger.info("percentage of duration :: " + minutes + "   bulkUpdateTaskDTO.isReduced()  " + reduction);
        if (reduction) {
            if (task.getDuration() - minutes <= 0) {
                exceptionService.internalError("error.task.duration");
                //throw new InternalError("Task duration cannot be less then 1");
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


    public List<TaskGanttDTO> makeTasksActiveInactive(TaskActiveUpdationDTO taskActiveUpdationDTO, long unitId) {

        List<Task> tasksToUpdate = taskMongoRepository.findByIdIn(taskActiveUpdationDTO.getTaskIds(), new Sort(Sort.Direction.ASC, "timeFrom"));
        if (tasksToUpdate.isEmpty()) {
            return Collections.emptyList();
        }
        List<Task> tasksToReturn = new ArrayList<>(tasksToUpdate.size());

        // Map<String, String> flsCredentials = integrationService.getFLS_Credentials(unitId);
        Map<String, String> flsCredentials = genericIntegrationService.getFLS_Credentials(unitId);
        tasksToUpdate.forEach(task -> {
            if (!task.isSingleTask() && task.getActualPlanningTask() == null) {
                taskService.savePreplanningStateOfTask(task);
            }
            task.setActive(taskActiveUpdationDTO.isMakeActive());
            if (taskActiveUpdationDTO.isMakeActive()) {
                clientExceptionService.updateTaskException(task.getCitizenId(), task);
                //taskConverterService.createFlsCallFromTask(task, flsCredentials);
            } else {
                deleteTaskFromVisitour(task, flsCredentials);
                task.setVisitourId(null);
                save(task);
            }
            tasksToReturn.add(task);
        });
        return customizeTaskData(tasksToReturn);
    }

    private void deleteTaskFromVisitour(Task taskToDelete, Map<String, String> flsCredentials) {
        /*if (taskToDelete.getVisitourId() != null && taskToDelete.getVisitourId() > 0) {
            Map<String, Object> callMetaData = new HashMap<>();
            callMetaData.put("functionCode", 4);
            callMetaData.put("extID", taskToDelete.getId());
            callMetaData.put("vtid", taskToDelete.getVisitourId());
            scheduler.deleteCall(callMetaData, flsCredentials);
        }*/
    }

    public List<TaskGanttDTO> removeRestrictionsOfCitizens(long unitId,long citizenId,List<TaskRestrictionDto> taskRestrictionDtos){
        DayOfWeek dayOfWeek = LocalDate.now().getDayOfWeek();
        LocalDateTime dateFrom = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime dateTo = LocalDateTime.now().withHour(23).withMinute(59).withSecond(0);
        if (DayOfWeek.FRIDAY.equals(dayOfWeek)) {
            dateTo = dateTo.plusDays(2);
        } else if (DayOfWeek.SATURDAY.equals(dayOfWeek)) {
            dateTo = dateTo.plusDays(1);
        }
        Date dateFromAsDate = Date.from(dateFrom.atZone(ZoneId.systemDefault()).toInstant());
        Date dateToAsDate = Date.from(dateTo.atZone(ZoneId.systemDefault()).toInstant());

        List<Long> citizenIds = taskRestrictionDtos.stream().map(taskRestrictionDto -> taskRestrictionDto.getCitizenId()).collect(Collectors.toList());

        List<Task> tasks = taskMongoRepository.getPrePlanningTaskBetweenExceptionDates(unitId, citizenIds, dateFromAsDate, dateToAsDate);

        // Map<String,String> flsCredentails = integrationService.getFLS_Credentials(unitId);
        Map<String,String> flsCredentails = genericIntegrationService.getFLS_Credentials(unitId);
        taskRestrictionDtos.forEach(taskRestrictionDto -> {
            List<Task> filtertedTasks = tasks.stream().filter(task -> task.getCitizenId() == taskRestrictionDto.getCitizenId()).collect(Collectors.toList());
            removeRestrictionFromTask(filtertedTasks,taskRestrictionDto,flsCredentails);
        });
        taskConverterService.createFlsCallFromTasks(tasks,flsCredentails);
        List<Task> selectedCitizenTasks = tasks.stream().filter(task -> task.getCitizenId() == citizenId).collect(Collectors.toList());
        return customizeTaskData(selectedCitizenTasks);
    }



    private void removeRestrictionFromTask(List<Task> tasks, TaskRestrictionDto taskRestrictionDto, Map<String,String> flsCredentails){

        tasks.forEach(task -> {
            if(!task.isSingleTask() && task.getActualPlanningTask() == null){
                taskService.savePreplanningStateOfTask(task);
            }
            if(taskRestrictionDto.isAllowedEngineers()){
                task.setPrefferedStaffIdsList(null);
            }
            if(taskRestrictionDto.isNotAllowedEngineers()){
                task.setForbiddenStaffIdsList(null);
            }
            if(taskRestrictionDto.isTeam()){
                task.setTeamId("");
            }
            if(taskRestrictionDto.isSkills()){
                task.setSkills("");
            }
            if(taskRestrictionDto.getPriority() != null){
                task.setPriority(taskRestrictionDto.getPriority());
            }

            if (taskRestrictionDto.getPercentageDuration() != null && taskRestrictionDto.getPercentageDuration() > 1) {
                updateTaskDuration(task,taskRestrictionDto.isReduction(),taskRestrictionDto.getPercentageDuration());
            }

            if(taskRestrictionDto.getExtraPenalty() != null){
                task.setExtraPenalty(taskRestrictionDto.getExtraPenalty());
            }

            if(taskRestrictionDto.getDelayPenalty() != null && !taskRestrictionDto.getDelayPenalty().isEmpty()){
                task.setDelayPenalty(DelayPenalty.valueOf(taskRestrictionDto.getDelayPenalty()));
            }

            if(taskRestrictionDto.isRemoveFix()){
                taskConverterService.removeEngineer(task,flsCredentails);
            }

            if(taskRestrictionDto.getSlaTime() != null){
                task.setSlaStartDuration(taskRestrictionDto.getSlaTime());
            }
        });
    }


    public Map<String,Object> askAppointmentSuggestionsFromVisitour(long unitId,long taskId){

        Task task =  taskService.findOne(String.valueOf(taskId));

        Map<String, Object> returnData = new HashMap<>();

        // Map<String, String> flsCredentials = integrationService.getFLS_Credentials(unitId);
        Map<String, String> flsCredentials=genericIntegrationService.getFLS_Credentials(unitId);

        Map <String,Object> callMetaData = new HashMap<>();
        callMetaData.put("functionCode",1);

        /*callMetaData.put("extID","88068"); // for single suggestion
        callMetaData.put("vtid",88068);*/

        /*callMetaData.put("extID","1656293"); // for two suggestions
        callMetaData.put("vtid",51120);*/

        /*callMetaData.put("extID","1581097"); // no suggestion
        callMetaData.put("vtid",93359);*/

        callMetaData.put("extID", task.getId());
        callMetaData.put("vtid", task.getVisitourId());

        List<TaskAppointmentSuggestionDTO> taskAppointmentSuggestionDTOList = scheduler.getAppointmentSuggestions(callMetaData,flsCredentials);
        logger.debug("taskAppointmentSuggestionDTOList >>>>>>  "+taskAppointmentSuggestionDTOList);

        returnData.put("taskId",taskId);
        returnData.put("taskAppointmentSuggestionList",taskAppointmentSuggestionDTOList);

        return returnData;

    }

    public List<TaskGanttDTO> confirmAppointmentSuggestion(long unitId, long taskId, @RequestBody Map<String, Object> payload) throws ParseException {

        logger.debug("payload >>>>>>  "+payload);

        Task task =  taskService.findOne(String.valueOf(taskId));

        //Map<String, String> flsCredentials = integrationService.getFLS_Credentials(unitId);
        Map<String, String> flsCredentials=genericIntegrationService.getFLS_Credentials(unitId);

        Map<String,Object> confirmMetaData = new HashMap<>();
        confirmMetaData.put("functionCode",2);
        confirmMetaData.put("extID",task.getId());
        confirmMetaData.put("vtid",task.getVisitourId());

        Date fixedDate = DateUtils.getDate(Long.parseLong(payload.get("fixedDate").toString()));
        Map <String,Object> timeFrameInfo = new HashMap<>();
        timeFrameInfo.put("fixedDate",fixedDate);

        //scheduler.confirmAppointmentSuggestion(confirmMetaData,timeFrameInfo,flsCredentials );


        Map<String,Object> callInfoMetaData = new HashMap<>();
        callInfoMetaData.put("extID",task.getId());
        callInfoMetaData.put("vtid",task.getVisitourId());
        CallInfoRec callInfoRec = scheduler.getCallInfo(callInfoMetaData, flsCredentials);
        if(callInfoRec.getState() == 3) {
            task.setExecutionDate(callInfoRec.getArrival().toGregorianCalendar().getTime());
            task.setTaskStatus(TaskStatus.PLANNED);
        }if(callInfoRec.getState() == 2){
            task.setTaskStatus(TaskStatus.CONFIRMED);
        } else {
            task.setTaskStatus(TaskStatus.GENERATED);
        }
        task.setDateFrom(fixedDate);
        task.setDateTo(fixedDate);
        task.setTimeFrom(fixedDate);
        task.setTimeTo(fixedDate);
        taskService.save(task);

        List<Task> taskToReturn = new ArrayList<>();
        taskToReturn.add(task);
        return customizeTaskData(taskToReturn);


    }


    public boolean deleteTasks(List<BigInteger> tasksIdsToDelete,long unitId){

        List<Task> tasks = taskMongoRepository.findByIdIn(tasksIdsToDelete, new Sort(Sort.Direction.ASC, "timeFrom"));
        return deleteTasksFromDBAndVisitour(tasks,unitId);
    }

    public void totalUnhandledTasks(){


    }


    /**
     * @auther anil maurya
     *
     * @param citizenId
     * @return
     */
    public void deleteTasksForCitizen(long citizenId, CitizenHealthStatus citizenHealthStatus,String deathDate) throws ParseException {

        Date deathDateInDateFormat = DateUtils.convertToOnlyDate(deathDate, MONGODB_QUERY_DATE_FORMAT);
        TaskCountWithAssignedUnit taskCountWithAssignedUnit = taskMongoRepository.countOfTasksAfterDateAndAssignedUnits(citizenId,deathDateInDateFormat);
        if(taskCountWithAssignedUnit == null){
            return;
        }
        switch (citizenHealthStatus){
            case DECEASED:
                taskMongoRepository.inactiveTasksAfterDate(citizenId,deathDateInDateFormat);
                break;
            case TERMINATED:
                taskMongoRepository.deleteTasksAfterDate(citizenId,deathDateInDateFormat);
                break;
            default:
                logger.error("Invalid health status");
                return;
        }
        //TODO FLS service disabled
        /*int startPosition = 0;
        Sort sort = new Sort(Sort.DEFAULT_DIRECTION,"unitId");
        ConcurrentMap<Long,ConcurrentMap<String,String>> flsCredentailsForUnits =
                integrationServiceRestClient.getFLSCredentials(taskCountWithAssignedUnit.getUnitId());
        Long totalTasksToDelete = taskCountWithAssignedUnit.getTotalTasks();
        do {
            List<Task> tasks = taskMongoRepository.getCitizenTasksGroupByUnitIds(citizenId,deathDateInDateFormat,
                    new PageRequest(startPosition,MONOGDB_QUERY_RECORD_LIMIT,sort));
            logger.info("Number of tasks to delete " + tasks.size());
            startPosition += MONOGDB_QUERY_RECORD_LIMIT;
            deleteTasksFromVisitour(tasks,flsCredentailsForUnits);
        } while (startPosition<totalTasksToDelete);*/
    }

    private void deleteTasksFromVisitour(List<Task> tasks,ConcurrentMap<Long,ConcurrentMap<String,String>> flsCredentailsByUnitId){
        tasks.parallelStream().forEach(taskToDelete -> {
            ConcurrentMap<String, String> flsCredentials = flsCredentailsByUnitId.get(taskToDelete.getUnitId());
            Map<String, Object> callMetaData = new HashMap<>();
            callMetaData.put("functionCode", 4);
            callMetaData.put("extID", taskToDelete.getId());
            callMetaData.put("vtid", taskToDelete.getVisitourId());
            if(Optional.ofNullable(taskToDelete.getVisitourId()).isPresent() && taskToDelete.getVisitourId() > 0){
                scheduler.deleteCall(callMetaData, flsCredentials);
            }
        });
    }

    public boolean deleteTasksFromDBAndVisitour(List<Task> taskList, Long unitId)  {
        if (!taskList.isEmpty()) {
            //Map<String, String> flsCredentials = integrationServiceRestClient.getFLS_Credentials(unitId);
            for (Task task : taskList) {
                /*Map<String, Object> callMetaData = new HashMap<>();
                callMetaData.put("functionCode", 4);
                callMetaData.put("extID", task.getId());
                callMetaData.put("vtid", task.getVisitourId());


                boolean deleteTask = true;
                if (task.getVisitourId() != null && task.getVisitourId() > 0) { //If VisitourId found then Delete task from Visitour, else delete from kairos only.
                    int returnedValue = scheduler.deleteCall(callMetaData, flsCredentials);
                    if (returnedValue == 0) {
                        deleteTask = false;
                    }
                } else {
                    logger.debug("No Visitour Id: task.getId " + task.getId() + " vtid " + task.getVisitourId());
                }
                if (deleteTask) {
                    task.setTaskStatus(CANCELLED);
                    task.setDeleted(true);
                }*/
                task.setTaskStatus(CANCELLED);
                task.setDeleted(true);
            }
            taskService.save(taskList);
            return true;
        } else {
            logger.info("No Tasks to Delete " + taskList);
            return false;
        }
    }

    public void sendAggregateDataToClient(ClientAggregator clientAggregator, long unitId){
        ObjectMapper objectMapper = new ObjectMapper();
        String s= "";
        try {
            s = objectMapper.writeValueAsString(clientAggregator);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("citizenDataList", Arrays.asList(s));
        taskDynamicReportService.sendCitizenDynamicReports(unitId, jsonObject);
    }

    private boolean skipTaskOnPublicHoliday(TaskDemand taskDemand, TaskDemandVisit taskDemandVisit){
        return false;
        /*int weekdayFrequency = getWeekFrequencyAsInt(taskDemand.getWeekdayFrequency().toString());

        int visitCount = taskDemandVisit.getVisitCount();

        if(weekdayFrequency == 1 && visitCount==5){
            return false;
        }else if(weekdayFrequency == 2 && visitCount==10){
            return false;
        }else if(weekdayFrequency == 3 && visitCount==15){
            return false;
        }else if(weekdayFrequency == 4 && visitCount==20){
            return false;
        }
        return true;*/
    }

}
