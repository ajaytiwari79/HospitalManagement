package com.kairos.service.visitator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kairos.dto.activity.task_type.TaskTypeDTO;
import com.kairos.rest_client.*;
import com.kairos.dto.user.staff.ClientStaffInfoDTO;
import com.kairos.dto.user.client.Client;
import com.kairos.persistence.model.task.Task;
import com.kairos.persistence.model.task.TaskPackage;
import com.kairos.persistence.model.task_demand.TaskDemand;
import com.kairos.persistence.model.task_demand.TaskDemandVisit;
import com.kairos.persistence.model.task_type.TaskType;
import com.kairos.persistence.repository.client_aggregator.ClientAggregatorMongoRepository;
import com.kairos.persistence.repository.common.MongoSequenceRepository;
import com.kairos.persistence.repository.task_type.TaskDemandMongoRepository;
import com.kairos.persistence.repository.task_type.TaskMongoRepository;
import com.kairos.persistence.repository.task_type.TaskPackageMongoRepository;
import com.kairos.persistence.repository.task_type.TaskTypeMongoRepository;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.planner.PlannerService;
import com.kairos.service.planner.TasksMergingService;
import com.kairos.service.task_type.TaskDemandService;
import com.kairos.service.task_type.TaskService;
import com.kairos.service.task_type.TaskTypeService;
import com.kairos.commons.utils.DateUtils;
import com.kairos.wrapper.task.TaskDemandDTO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigInteger;
import java.text.ParseException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;
import static com.kairos.constants.AppConstants.ORGANIZATION;
import static java.lang.Math.toIntExact;
import static java.time.ZoneId.systemDefault;
import static java.time.temporal.ChronoUnit.DAYS;

;

/**
 * Created by oodles on 15/11/16.
 */
@Service
@Transactional
public class VisitatorService{

    private static final Logger logger = LoggerFactory.getLogger(VisitatorService.class);

    @Inject
    private TaskTypeService taskTypeService;
    @Inject
    private TaskDemandService taskDemandService;
    @Inject
    private TaskService taskService;

    @Inject
    TaskPackageMongoRepository taskPackageMongoRepository;
    @Inject
    TaskDemandMongoRepository taskDemandMongoRepository;
    @Inject
    TaskTypeMongoRepository taskTypeMongoRepository;
    @Inject
    MongoSequenceRepository mongoSequenceRepository;
    @Inject
    TaskMongoRepository taskMongoRepository;
    @Inject
    PlannerService plannerService;
    @Autowired
    TasksMergingService tasksMergingService;


    @Inject
    GenericIntegrationService genericIntegrationService;

    @Inject
    private ClientAggregatorMongoRepository clientAggregatorMongoRepository;
    @Autowired
    private ExceptionService exceptionService;


    public Object getUnitProvidedServices(long unitId) {
        Map<String, Object> services = genericIntegrationService.getOrganizationServices(unitId, StringUtils.capitalize(ORGANIZATION));
        return services.get("selectedServices");
    }

    public List<Client> getClientAvailingOrganizationService(long serviceId) {
        List<String> taskTypeIdList = new ArrayList<>();
        List<TaskDemand> taskDemandIdList = new ArrayList<>();
        List<Client> clientList = new ArrayList<>();

        List<TaskTypeDTO> taskTypeDTOS = taskTypeService.getTaskTypes(serviceId);
        for (TaskTypeDTO taskTypeDTO : taskTypeDTOS) {
            taskTypeIdList.add(taskTypeDTO.getId().toString());
        }
        /*for (String id : taskTypeIdList) {
            taskDemandIdList = taskDemandService.getbyTaskTypeId(id);

        }*/

        taskDemandIdList = taskDemandService.getByTaskTypeIds(taskTypeIdList);

        /*for (TaskDemand demand : taskDemandIdList) {
            clientList.add(clientGraphRepository.findById(demand.getCitizenId()));
        }*/

        List<Long> citizenIds = taskDemandIdList.stream().map(demand -> demand.getCitizenId()).collect(Collectors.toList());
        clientList = genericIntegrationService.getCitizensByIdsInList(citizenIds);

        return clientList;
    }
//
//    public List<Map> getClientAssignedTask(long clientId){
//        organizationGraphRepository.findById(clientId);
//       return taskService.getTaskByServiceId(clientId, serviceID);
//
//    }

    private Map<String, Object> createCustomServiceMap(Object id, String type, String name, Integer setupDuration) {
        Map<String, Object> customMap = new HashMap<>();
        customMap.put("id", id);
        customMap.put("type", type);
        customMap.put("name", name);
        if(setupDuration != null ){
            customMap.put("setupDuration", setupDuration);
        }
        return customMap;
    }

    private List<Map<String, Object>> createCustomTaskTypeList(long unitId, long subServiceId) {
        List<TaskType> taskTypes = taskTypeMongoRepository.findBySubServiceIdAndOrganizationIdAndIsEnabled(subServiceId, unitId, true);

        List<Map<String, Object>> customTaskTypes = new ArrayList<>(taskTypes.size());
        for (TaskType taskType : taskTypes) {
            customTaskTypes.add(createCustomServiceMap(taskType.getId(), "taskType", taskType.getTitle(), taskType.getSetupDuration()));
        }
        return customTaskTypes;
    }

    private List<Map> getWeekFrequencyList() {
        List<Map> weekFrequencyList = new ArrayList<>();
        Map<String, Object> weekFrequencyMap;
        for (TaskDemand.WeekFrequency weekFrequency : TaskDemand.WeekFrequency.values()) {
            weekFrequencyMap = new HashMap();
            weekFrequencyMap.put("id", weekFrequency.name());
            weekFrequencyMap.put("name", weekFrequency.value);

            weekFrequencyList.add(weekFrequencyMap);
        }
        return weekFrequencyList;
    }

    private List<Map> getDemandStatusList() {
        List<Map> demandStatusList = new ArrayList<>();
        Map<String, Object> demadStatusMap;
        for (TaskDemand.Status demadStatus : TaskDemand.Status.values()) {
            demadStatusMap = new HashMap();
            demadStatusMap.put("id", demadStatus.name());
            demadStatusMap.put("name", demadStatus.value);

            demandStatusList.add(demadStatusMap);
        }
        return demandStatusList;
    }

    public Map<String, Object> getUnitVisitationInfo( long unitId) {
        long startTime = System.currentTimeMillis();

        Map<String, Object> organizationResult = new HashMap();

        Map<String, Object> response=genericIntegrationService.getUnitVisitationInfo();
        Map<String, Object> unitData =(Map<String, Object>)response.get("unitData");
        List<TaskPackage> taskPackageList = taskPackageMongoRepository.findAllByUnitIdAndIsDeleted(unitId,false);
        unitData.put("taskPackageList", CollectionUtils.isNotEmpty(taskPackageList) ? taskPackageList : Collections.EMPTY_LIST);

        unitData.put("weekFrequency", getWeekFrequencyList());
        unitData.put("demandStatus", getDemandStatusList());
        unitData.put("services", getServiceHierarchy(unitId));
        List<TaskType> taskTypes = taskTypeMongoRepository.findByOrganizationIdAndIsEnabled(unitId,true);
        unitData.put("taskTypes", taskTypes);

        List<Map<String, Object>> citizenListResponse=(List<Map<String, Object>>)response.get("citizenList");

        List<Object> citizenList =getOrganizationClientsExcludeDead(unitId,citizenListResponse);
        organizationResult.putAll(response);

        organizationResult.put("citizenList", CollectionUtils.isNotEmpty(citizenList) ? citizenList : Collections.EMPTY_LIST);

        logger.info("Execution Time :(VisitatorService:getUnitVisitationInfo) " + (System.currentTimeMillis() - startTime) + " ms");
        return organizationResult;
    }

    private List<Map<String, Object>> getServiceHierarchy(long unitId){
          Map<String, Object> organizationServices=genericIntegrationService.getOrganizationServices(unitId,StringUtils.capitalize(ORGANIZATION));
        List<Map<String, Object>> orgSelectedServices = (List<Map<String,Object>>) organizationServices.get("selectedServices");
        List<Map<String, Object>> mainServiceList = new ArrayList<>(orgSelectedServices.size());
        Map<String, Object> mainServiceMap;

        //Iterate all Services
        for (Map<String, Object> service : orgSelectedServices) {
            mainServiceMap = createCustomServiceMap(service.get("id"), "service", (String) service.get("customName"), null);

            if(service.get("children")!=null){
                List<Map<String, Object>> subServicesList = new ArrayList<>();
                List<Map<String, Object>> subServices = (List<Map<String, Object>>) service.get("children");

                //Iterate all Sub-Services
                for (Map<String, Object> subService : subServices){
                    Map<String, Object> subServiceMap;
                    subServiceMap = createCustomServiceMap(subService.get("id"), "subService", (String) subService.get("customName"), null);

                    //Iterate TaskTypes associated with Sub-Service
                    subServiceMap.put("children",createCustomTaskTypeList(unitId,  new Long((int)subService.get("id"))  ));
                    subServicesList.add(subServiceMap);
                }
                mainServiceMap.put("children",subServicesList);
            }
            mainServiceList.add(mainServiceMap);
        }
        return mainServiceList;
    }


    public Map<String, Object> createTaskDemand( long unitId, long clientId, TaskDemandDTO taskDemandDTO) {
        logger.info("taskDemand to create  " + taskDemandDTO);
        ClientStaffInfoDTO  clientStaffInfoDTO=genericIntegrationService.getClientStaffInfo(clientId);

        if (clientStaffInfoDTO.getClientId() == null) {
            logger.info("No Citizen Found with Id " + clientId);
            exceptionService.taskDemandException("message.citizen.id",clientId);
        } else if (  !DateUtils.isSameDay(taskDemandDTO.getStartDate(), DateUtils.getDate()) && taskDemandDTO.getStartDate().before(DateUtils.getDate())) {
            logger.info("Task Demand's cannot start on past date");
            exceptionService.taskDemandException("message.taskdemand.startdate");
        } else if (!taskDemandDTO.getDayName().toUpperCase().equals("MONDAY")) {
            logger.info("Task Demand can only start on Monday.");
            exceptionService.taskDemandException("message.taskdemand.startday");
        } else if (taskDemandDTO.getEndDate()!=null && (taskDemandDTO.getRecurrencePattern().equals(TaskDemand.RecurrencePattern.WEEKLY)||taskDemandDTO.getRecurrencePattern().equals(TaskDemand.RecurrencePattern.DAILY)) &&
                //ChronoUnit.WEEKS.between(taskDemandDTO.getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()), DateUtil.dateToLocalDateTime(taskDemandDTO.getEndDate()))<1) {
                DAYS.between(taskDemandDTO.getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), taskDemandDTO.getEndDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate())<6){
            logger.info("Task Demand should be of atleast one week");
            exceptionService.taskDemandException("message.taskdemand.week");
        } else if (taskDemandDTO.getEndDate()!=null &&  taskDemandDTO.getNextVisit()!=null &&  taskDemandDTO.getNextVisit().before(taskDemandDTO.getEndDate())) {
            logger.info("Task Demand's next visit date should be greater then end date");
            exceptionService.taskDemandException("message.taskdemand.visitdate");
        } else if (taskDemandDTO.getEndDate()!=null && taskDemandDTO.getRecurrencePattern().equals(TaskDemand.RecurrencePattern.MONTHLY) &&
                ChronoUnit.MONTHS.between(DateUtils.dateToLocalDateTime(taskDemandDTO.getStartDate()), DateUtils.dateToLocalDateTime(taskDemandDTO.getEndDate()))<1) {
            logger.info("Task Demand should be of atleast one month");
            exceptionService.taskDemandException("message.taskdemand.month");
        } else if ( !taskDemandDTO.getRecurrencePattern().equals(TaskDemand.RecurrencePattern.WEEKLY) &&
                (taskDemandDTO.getEndDate()==null && taskDemandDTO.getEndAfterOccurrence()==0)) {
            logger.info("Please enter either Task Demand end date or number of occurences");
            exceptionService.taskDemandException("message.taskdemandend-date.numberofoccurence");
                    } else {
            ObjectMapper mapper = new ObjectMapper();
            TaskDemand taskDemand = mapper.convertValue(taskDemandDTO, TaskDemand.class);
            taskDemand.setCitizenId(clientStaffInfoDTO.getClientId());
            taskDemand.setUnitId(unitId);
            if (taskDemand.getWeekendVisits() == null)
                taskDemand.setWeekendVisits(Collections.EMPTY_LIST);
            if (taskDemand.getWeekdayVisits() == null)
                taskDemand.setWeekdayVisits(Collections.EMPTY_LIST);
            if (!taskDemand.getWeekendVisits().isEmpty() && taskDemand.getWeekendFrequency() != null) {

                taskDemand.getWeekendVisits().forEach(taskDemandVisit -> taskDemandVisit.setId(mongoSequenceRepository.nextSequence(TaskDemand.class.getSimpleName())));

            }
            if ((!taskDemand.getWeekdayVisits().isEmpty() && taskDemand.getWeekdayFrequency() != null) || !taskDemand.getRecurrencePattern().equals(TaskDemand.RecurrencePattern.WEEKLY)) {

                taskDemand.getWeekdayVisits().forEach(taskDemandVisit -> taskDemandVisit.setId(mongoSequenceRepository.nextSequence(TaskDemand.class.getSimpleName())));

            }

            if (!taskDemand.getRecurrencePattern().equals(TaskDemand.RecurrencePattern.WEEKLY)) {
                taskDemand.setWeekendVisits(Collections.EMPTY_LIST);
            }
            // Staff staff = staffGraphRepository.getByUser(userGraphRepository.findByAccessToken(authToken).getId());
            taskDemand.setCreatedByStaffId(clientStaffInfoDTO.getStaffId());
            taskDemand.setLastModifiedByStaffId(clientStaffInfoDTO.getStaffId());
            TaskDemand createdTaskDemand = taskDemandService.save(taskDemand);
            if (createdTaskDemand != null) {
                return getTaskDemandMap(createdTaskDemand);
            }
        }
        return null;
    }

    public Map<String, Object> updateTaskDemand(long unitId, String taskDemandId, TaskDemandDTO taskDemandDTO) throws CloneNotSupportedException {

        if (taskDemandDTO.getDayName() == null || !taskDemandDTO.getDayName().toUpperCase().equals("MONDAY")) {
            logger.info("Task Demand can only start on Monday.");
            exceptionService.taskDemandException("message.taskdemand.startday");
        } else if (taskDemandDTO.getRecurrencePattern().equals(TaskDemand.RecurrencePattern.WEEKLY) &&
                DAYS.between(taskDemandDTO.getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), taskDemandDTO.getEndDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate())<6) {
            logger.info("Task Demand should be of atleast one week");
            exceptionService.taskDemandException("message.taskdemand.week");
        } else if ( !taskDemandDTO.getRecurrencePattern().equals(TaskDemand.RecurrencePattern.WEEKLY) &&
                (taskDemandDTO.getEndDate()==null && taskDemandDTO.getEndAfterOccurrence()==0)) {
            logger.info("Please enter either Task Demand end date or number of occurences");
            exceptionService.taskDemandException("message.taskdemandend-date.numberofoccurence");
        } else {

            TaskDemand existingTaskDemand = taskDemandMongoRepository.findByTaskDemandIdAndUnitIdAndIsDeleted(taskDemandId,unitId,false);

            ObjectMapper mapper = new ObjectMapper();
            TaskDemand updatedTaskDemand = mapper.convertValue(taskDemandDTO, TaskDemand.class);

            if (existingTaskDemand.getStatus() == TaskDemand.Status.VISITATED) {

                if (updatedTaskDemand.getWeekendVisits() == null)
                    updatedTaskDemand.setWeekendVisits(Collections.EMPTY_LIST);
                if (updatedTaskDemand.getWeekdayVisits() == null)
                    updatedTaskDemand.setWeekdayVisits(Collections.EMPTY_LIST);

                if (!updatedTaskDemand.getRecurrencePattern().equals(TaskDemand.RecurrencePattern.WEEKLY)) {
                    updatedTaskDemand.setWeekendVisits(Collections.EMPTY_LIST);
                }

                if (!updatedTaskDemand.getWeekendVisits().isEmpty() && updatedTaskDemand.getWeekendFrequency() != null) {
                    for (TaskDemandVisit taskDemandVisit : updatedTaskDemand.getWeekendVisits()) {
                        if (taskDemandVisit.getId() == null) {
                            taskDemandVisit.setId(mongoSequenceRepository.nextSequence(TaskDemand.class.getSimpleName()));
                        }
                    }
                }
                if (!updatedTaskDemand.getWeekdayVisits().isEmpty() && updatedTaskDemand.getWeekdayFrequency() != null) {
                    for (TaskDemandVisit taskDemandVisit : updatedTaskDemand.getWeekdayVisits()) {
                        if (taskDemandVisit.getId() == null) {
                            taskDemandVisit.setId(mongoSequenceRepository.nextSequence(TaskDemand.class.getSimpleName()));
                        }
                    }
                }
            } else if (existingTaskDemand.getStatus() == TaskDemand.Status.GENERATED || existingTaskDemand.getStatus() == TaskDemand.Status.UPDATED) {

                if (existingTaskDemand.getEndDate().equals(updatedTaskDemand.getEndDate())) {
                    logger.info("Only Task Demand EndDate can be changed, when demand status is " + existingTaskDemand.getStatus());
                    exceptionService.taskDemandException("message.taskdemand.demandstatus.update");
                    } else if (existingTaskDemand.getEndDate().before(updatedTaskDemand.getEndDate())) {
                    logger.info("Task Demand EndDate should be less than previous selected EndDate");
                    exceptionService.taskDemandException("message.taskdemand.end-date");
                } else {
                    logger.info("Task Demand EndDate updated, so delete tasks from kairos and visitour after this date");
                    List<Task> tasksToDelete = taskMongoRepository.getTasksByDemandIdAndDateTo(existingTaskDemand.getId().toString(), updatedTaskDemand.getEndDate());
                    plannerService.deleteTasksFromDBAndVisitour(tasksToDelete, unitId);

                    updatedTaskDemand.setStatus(TaskDemand.Status.UPDATED);
                    updatedTaskDemand.setWeekendVisits(Collections.EMPTY_LIST);
                }
            } else {
                logger.info("Sorry, Cannot update task demand with status " + existingTaskDemand.getStatus());
                exceptionService.taskDemandException("message.taskdemand.status");
            }

            //Staff staff = staffGraphRepository.getByUser(userGraphRepository.findByAccessToken(authToken).getId());
            ClientStaffInfoDTO clientStaffInfoDTO = genericIntegrationService.getStaffInfo();
            updatedTaskDemand.setCreatedAt(existingTaskDemand.getCreatedAt());
            updatedTaskDemand.setCitizenId(existingTaskDemand.getCitizenId());
            updatedTaskDemand.setUnitId(existingTaskDemand.getUnitId());
            updatedTaskDemand.setLastModifiedByStaffId(clientStaffInfoDTO.getStaffId());
            updatedTaskDemand.setId(existingTaskDemand.getId());
            return getTaskDemandMap(taskDemandService.save(updatedTaskDemand));
        }
       return null;
    }

    public boolean deleteTaskDemand(String taskDemandId, Long unitId) throws ParseException, CloneNotSupportedException {

        boolean isSuccess=false;

        TaskDemand taskDemand = taskDemandMongoRepository.findByTaskDemandIdAndUnitIdAndIsDeleted(taskDemandId,unitId,false);
        if (taskDemand != null) {
            if (taskDemand.getStatus() == TaskDemand.Status.GENERATED || taskDemand.getStatus() == TaskDemand.Status.UPDATED) {
                List<Task> tasksToDelete = taskService.getTasksByDemandId(taskDemandId);

                for (Task task : tasksToDelete) { // Unmerge tasks (if any task of deleted demand is merged)
                    if (task.getSubTask() == true) {
                        Task mainTask = taskService.findBySubTaskIds(task.getId().toString());
                        if (mainTask != null) {
                            Map unmergeMap = new HashMap();
                            unmergeMap.put("mainTaskId", mainTask.getId());
                            List<Map<String, Object>> taskDataList = new ArrayList<>();
                            Map taskToUnmerge;
                            if (mainTask.getSubTaskIds().size() == 2) {
                                for (BigInteger subTaskId : mainTask.getSubTaskIds()) {
                                    taskToUnmerge = new HashMap();
                                    if (subTaskId != task.getId()) {
                                        Task secondTask = taskService.findOne(subTaskId.toString());
                                        taskToUnmerge.put("isSelected", true);
                                        taskToUnmerge.put("jointEvents", secondTask.getJoinEventId());
                                        taskToUnmerge.put("id", secondTask.getId());
                                        taskDataList.add(taskToUnmerge);
                                    }
                                }
                            }
                            taskToUnmerge = new HashMap();
                            taskToUnmerge.put("isSelected", true);
                            taskToUnmerge.put("jointEvents", task.getJoinEventId());
                            taskToUnmerge.put("id", task.getId());
                            taskDataList.add(taskToUnmerge);

                            unmergeMap.put("tasksToUnmerge", taskDataList);
                            tasksMergingService.unMergeMultipleTasks( unitId, task.getCitizenId(), unmergeMap, false);
                        }
                    }
                }
                plannerService.deleteTasksFromDBAndVisitour(tasksToDelete, unitId);
            }
            taskDemand.setDeleted(true);
            taskDemandService.save(taskDemand);
            isSuccess = true;
        } else {
            logger.error("Task Demand not found with ID " + taskDemandId);
            exceptionService.taskDemandException("message.taskdemand.id");
        }
        return isSuccess;

    }

    public List<Map<String, Object>> fetchTaskDemand(long unitId, long citizenId, Map<String, String> requestParams) {

        Date startDate = (requestParams.get("startDate") != null) ? new DateTime(requestParams.get("startDate")).toDate() : null;

        Date endDate = (requestParams.get("endDate") != null) ? new DateTime(requestParams.get("endDate")).toDate() : null;

        List<TaskDemand> taskDemandList = Collections.EMPTY_LIST;
        if (startDate == null && endDate == null) {
            logger.error("Start Date and End Date are null");
            exceptionService.taskDemandException("message.taskdemand.startdate.enddate");
        } else if (startDate != null && endDate != null) {
            taskDemandList = taskDemandMongoRepository.findAllBetweenDates(unitId, citizenId, startDate, endDate);
        } else if (startDate != null && endDate == null) {
            taskDemandList = taskDemandMongoRepository.findAllAfterDate(unitId, citizenId, startDate);
        } else {
            taskDemandList = taskDemandMongoRepository.findAllBeforeDate(unitId, citizenId, endDate);
        }

        List<Map<String, Object>> demandList = new ArrayList<>(taskDemandList.size());
        for (TaskDemand taskDemand : taskDemandList) {
            Map<String, Object> taskDemandMap = getTaskDemandMap(taskDemand);
            demandList.add(taskDemandMap);
        }
        return demandList;
    }

    /**
     * @auther anil maurya
     * @param taskDemand
     * @return
     */
    public Map<String, Object> getTaskDemandMap(TaskDemand taskDemand) {
        Map<String, Object> taskDemandMap = new HashMap<>();
        taskDemandMap.put("id", taskDemand.getId());
        logger.info("taskDemand.getId() " + taskDemand.getId());
        TaskType taskType = taskTypeMongoRepository.findOne(taskDemand.getTaskTypeId());

        taskDemandMap.put("taskTypeId", taskType.getId());
        taskDemandMap.put("taskTypeDescription", taskType.getDescription());
        taskDemandMap.put("taskTypeName", taskType.getTitle());

        taskDemandMap.put("startDate", taskDemand.getStartDate());
        taskDemandMap.put("startDateFormatted", DateFormatUtils.format(taskDemand.getStartDate(), "MM-dd-yyyy"));
        taskDemandMap.put("endDate", taskDemand.getEndDate());
        taskDemandMap.put("endDateFormatted", taskDemand.getEndDate()!=null ?  DateFormatUtils.format(taskDemand.getEndDate(), "MM-dd-yyyy") : "");

        taskDemandMap.put("priority", taskDemand.getPriority());
        taskDemandMap.put("needRehabilitation", taskDemand.isNeedRehabilitation());
        taskDemandMap.put("staffCount", taskDemand.getStaffCount());
        taskDemandMap.put("remarks", taskDemand.getRemarks());
        taskDemandMap.put("status", taskDemand.getStatus().value);
       // taskDemandMap.put("lastModifiedBy", taskDemand.getLastModifiedByStaffId() > 0 ? staffGraphRepository.findById(taskDemand.getLastModifiedByStaffId()).getFirstName() : "");
        taskDemandMap.put("shift", taskDemand.isShift());
        taskDemandMap.put("demandImages", taskDemand.getDemandImages());
        taskDemandMap.put("setupDuration", taskDemand.getSetupDuration());

        if(taskDemand.getRecurrencePattern()== TaskDemand.RecurrencePattern.WEEKLY){
            if (taskDemand.getWeekdaySupplierId() > 0 && taskDemand.getWeekdayFrequency() != null && taskDemand.getWeekdayVisits() != null) {
                Map<String,Object> organizaionInfo=genericIntegrationService.getTaskDemandSupplierInfo();
                //anilm2 comments code
                //Organization weekdaySupplier = organizationGraphRepository.findById(taskDemand.getWeekdaySupplierId());
                //taskDemandMap.put("weekdaySupplier", weekdaySupplier.getName());
                // taskDemandMap.put("weekdaySupplierId", weekdaySupplier.getId());

                taskDemandMap.putAll(organizaionInfo);
                taskDemandMap.put("weekdayFrequency", taskDemand.getWeekdayFrequency());
                taskDemandMap.put("weekdayFrequencyValue", taskDemand.getWeekdayFrequency().value);
                taskDemandMap.put("weekdayVisits", taskDemand.getWeekdayVisits());
            } else {
                taskDemandMap.put("weekdaySupplier", "");
                taskDemandMap.put("weekdaySupplierId", "");
                taskDemandMap.put("weekdayFrequency", "");
                taskDemandMap.put("weekdayFrequencyValue", "");
                taskDemandMap.put("weekdayVisits", Collections.EMPTY_LIST);
            }

            if (taskDemand.getWeekendSupplierId() > 0 && taskDemand.getWeekendFrequency() != null && taskDemand.getWeekendVisits() != null) {
               /* Organization weekendSupplier = organizationGraphRepository.findById(taskDemand.getWeekendSupplierId());
                taskDemandMap.put("weekendSupplierId", weekendSupplier.getId());
                taskDemandMap.put("weekendSupplier", weekendSupplier.getName());*/
                Map<String,Object> organizaionInfo=genericIntegrationService.getTaskDemandSupplierInfo();


                taskDemandMap.putAll(organizaionInfo);
                taskDemandMap.put("weekendFrequency", taskDemand.getWeekendFrequency());
                taskDemandMap.put("weekendFrequencyValue", taskDemand.getWeekendFrequency().value);
                taskDemandMap.put("weekendVisits", taskDemand.getWeekendVisits());
            } else {
                taskDemandMap.put("weekendSupplierId", "");
                taskDemandMap.put("weekendSupplier", "");
                taskDemandMap.put("weekendFrequency", "");
                taskDemandMap.put("weekendFrequencyValue", "");
                taskDemandMap.put("weekendVisits", Collections.EMPTY_LIST);
            }
        } else if(taskDemand.getRecurrencePattern()== TaskDemand.RecurrencePattern.DAILY){
           /* Organization weekdaySupplier = organizationGraphRepository.findById(taskDemand.getWeekdaySupplierId());
            taskDemandMap.put("weekdaySupplier", weekdaySupplier.getName());
            taskDemandMap.put("weekdaySupplierId", weekdaySupplier.getId());*/
            Map<String,Object> organizaionInfo=genericIntegrationService.getTaskDemandSupplierInfo();
            taskDemandMap.putAll(organizaionInfo);
            List<TaskDemandVisit> taskDemandVisits = taskDemand.getWeekdayVisits();
            taskDemandVisits.forEach(taskDemandVisit -> taskDemandVisit.setVisitCount(toIntExact(taskDemand.getDailyFrequency())));
            //taskDemandVisits.get(0).setVisitCount(toIntExact(taskDemand.getDailyFrequency()));
            taskDemandMap.put("weekdayVisits", taskDemandVisits);

            taskDemandMap.put("dailyFrequency", taskDemand.getDailyFrequency());
            taskDemandMap.put("endAfterOccurrence", taskDemand.getEndAfterOccurrence());

            Map<String, Object> monthlyFrequency = new HashMap<>();
            monthlyFrequency.put("monthFrequency","");
            monthlyFrequency.put("dayOfWeek","");
            monthlyFrequency.put("weekOfMonth","");
            taskDemandMap.put("monthlyFrequency",monthlyFrequency);

        } else if(taskDemand.getRecurrencePattern() == TaskDemand.RecurrencePattern.MONTHLY){
           /* Organization weekdaySupplier = organizationGraphRepository.findById(taskDemand.getWeekdaySupplierId());
            taskDemandMap.put("weekdaySupplier", weekdaySupplier.getName());
            taskDemandMap.put("weekdaySupplierId", weekdaySupplier.getId());*/
            Map<String,Object> organizaionInfo=genericIntegrationService.getTaskDemandSupplierInfo();

            taskDemandMap.putAll(organizaionInfo);

            List<TaskDemandVisit> taskDemandVisits = taskDemand.getWeekdayVisits();
            int weekDayVisitCount;
            if (taskDemand.getMonthlyFrequency().getWeekdayCount() > 0){
                weekDayVisitCount = taskDemand.getMonthlyFrequency().getWeekdayCount();
            } else if (taskDemand.getMonthlyFrequency().getDayOfWeek() != null && taskDemand.getMonthlyFrequency().getWeekOfMonth() == null){
                weekDayVisitCount = 4;
            } else {
                weekDayVisitCount = 1;
            }
            taskDemandVisits.forEach(taskDemandVisit -> taskDemandVisit.setVisitCount(weekDayVisitCount));
            //taskDemandVisits.get(0).setVisitCount(weekDayVisitCount);
            taskDemandMap.put("weekdayVisits", taskDemandVisits);

            taskDemandMap.put("endAfterOccurrence", taskDemand.getEndAfterOccurrence());

            Map<String, Object> monthlyFrequency = new HashMap<>();
            monthlyFrequency.put("monthFrequency",taskDemand.getMonthlyFrequency().getMonthFrequency());
            monthlyFrequency.put("dayOfWeek",taskDemand.getMonthlyFrequency().getDayOfWeek() !=null ? taskDemand.getMonthlyFrequency().getDayOfWeek().value : "");
            monthlyFrequency.put("weekOfMonth",taskDemand.getMonthlyFrequency().getWeekOfMonth() != null ? taskDemand.getMonthlyFrequency().getWeekOfMonth().value : "");
            monthlyFrequency.put("weekdayCount",taskDemand.getMonthlyFrequency().getWeekdayCount());
            taskDemandMap.put("monthlyFrequency",monthlyFrequency);
        }

        taskDemandMap.put("nextVisit", (taskDemand.getNextVisit() != null) ? taskDemand.getNextVisit() : "");
        taskDemandMap.put("nextVisitFormatted", (taskDemand.getNextVisit() != null) ? DateFormatUtils.format(taskDemand.getNextVisit(), "MM-dd-yyyy") : "");
        taskDemandMap.put("daysToReview", (taskDemand.getNextVisit() != null) ? DAYS.between(LocalDate.now(), taskDemand.getNextVisit().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()) : 0);
        taskDemandMap.put("recurrencePattern", taskDemand.getRecurrencePattern());
        //taskDemandMap.put("citizenHouseholds", clientService.getPeopleInHousehold(taskDemand.getCitizenId()));
        if(Optional.ofNullable(taskDemand.getLastModifiedByStaffId()).isPresent() && taskDemand.getLastModifiedByStaffId() != 0){
            Map<String, Object> staffAndCitizenHouseholds = genericIntegrationService.getStaffAndCitizenHouseholds(taskDemand.getCitizenId(),taskDemand.getLastModifiedByStaffId());
            logger.info("staffAndCitizenHouseholds "+staffAndCitizenHouseholds);
            taskDemandMap.put("lastModifiedBy", staffAndCitizenHouseholds.get("lastModifiedBy"));
            taskDemandMap.put("citizenHouseholds", staffAndCitizenHouseholds.get("citizenHouseholds"));
        }


        return taskDemandMap;


    }

    public Map<String, Object> getCitizenVisitation(long unitId, long citizenId) {
        long startTime = System.currentTimeMillis();

        Map<String, Object> citizenVisitationInfo = new HashMap<>();


            List<TaskDemand> citizenTaskDemandList = taskDemandMongoRepository.findAllByCitizenIdAndUnitId(citizenId, unitId);
            List<Map<String, Object>> demandList = new ArrayList<>();
            for (TaskDemand taskDemand : citizenTaskDemandList) {
                Map<String, Object> taskDemandMap = getTaskDemandMap(taskDemand);
                demandList.add(taskDemandMap);
            }

        //used rest template to get citizen details from user micro service
        Map<String,Object> citizenDetails=genericIntegrationService.getClientDetails(citizenId);

       /* Client citizen = clientGraphRepository.findById(citizenId);

        Map<String, Object> citizenDetails = new HashMap<>();
        citizenDetails.put("id", citizen.getId());
        citizenDetails.put("name", citizen.getFirstName() + " " + citizen.getLastName());
        citizenDetails.put("age", citizen.getAge());
        citizenDetails.put("profilePic", citizen.getProfilePic()!=null? envConfig.getServerHost() + FORWARD_SLASH + citizen.getProfilePic() : "");
        citizenDetails.put("phone", citizen.getContactDetail() != null ? citizen.getContactDetail().retreiveContactNumbers() : "");
        citizenDetails.put("address", citizen.getContactAddress());
        citizenDetails.put("cprNumber", citizen.getCprNumber());
        citizenDetails.put("privateNumber",citizen.getContactDetail()!=null ? citizen.getContactDetail().getPrivatePhone() : "NA");
        citizenDetails.put("privateAddress",citizen.getContactAddress());
        citizenDetails.put("gender",citizen.getGender());
        citizenDetails.put("status",citizen.getCivilianStatus());*/
        citizenDetails.put("sumOfVisitationHoursAndTasks", clientAggregatorMongoRepository.findVisitationHoursAndTasksByCitizenIdIn( citizenId, unitId));

        citizenVisitationInfo.put("citizenDetails", citizenDetails);
        citizenVisitationInfo.put("demandList", demandList);

        logger.info("Execution Time :(VisitatorService:getCitizenVisitation) " + (System.currentTimeMillis() - startTime) + " ms");

        return citizenVisitationInfo;
    }

    public List<Map<String, Object>> getTaskDemandsByTaskPackageId(String taskPackageId) {

        TaskPackage taskPackage = taskPackageMongoRepository.findOne(new BigInteger(taskPackageId));
        if (taskPackage != null) {
            List<String> taskDemandIds = taskPackage.getTaskDemandIds();
            List<TaskDemand> taskDemands = taskDemandMongoRepository.findByIdIn(taskDemandIds);

            List<Map<String, Object>> demandList = new ArrayList<>(taskDemands.size());
            for (TaskDemand taskDemand : taskDemands) {
                Map<String, Object> taskDemandMap = getTaskDemandMap(taskDemand);
                demandList.add(taskDemandMap);
            }

        } else {
            logger.error("Task Package not found with ID " + taskPackageId);
            exceptionService.internalError("message.taskpackage.id");
        }
        return null;
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
     * @auther anil maurya
     * @param organizationId
     * @param mapList
     * @return
     */
    public List<Object> getOrganizationClientsExcludeDead(Long organizationId,List<Map<String, Object>> mapList) {
        //List<Map<String, Object>> mapList = organizationGraphRepository.getClientsOfOrganizationExcludeDead(organizationId,envConfig.getServerHost() + FORWARD_SLASH);
        if (!mapList.isEmpty()) {
            return taskDemandService.retreiveClients(mapList, organizationId);
        }
        return null;
    }

}
