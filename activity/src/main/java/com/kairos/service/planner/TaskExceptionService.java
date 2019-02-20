package com.kairos.service.planner;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kairos.dto.activity.task.BulkUpdateTaskDTO;
import com.kairos.dto.activity.task.TaskActiveUpdationDTO;
import com.kairos.dto.activity.task.TaskDTO;
import com.kairos.dto.activity.task.TaskRestrictionDto;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.persistence.enums.task_type.DelayPenalty;
import com.kairos.persistence.model.CustomTimeScale;
import com.kairos.persistence.model.client_aggregator.ClientAggregator;
import com.kairos.persistence.model.client_aggregator.FourWeekFrequency;
import com.kairos.persistence.model.restrcition_freuency.RestrictionFrequency;
import com.kairos.persistence.model.task.Task;
import com.kairos.persistence.model.task.TaskStatus;
import com.kairos.persistence.model.task.UnhandledTaskCount;
import com.kairos.persistence.model.task_type.TaskType;
import com.kairos.persistence.repository.CustomTimeScaleRepository;
import com.kairos.persistence.repository.client_aggregator.ClientAggregatorMongoRepository;
import com.kairos.persistence.repository.task_type.TaskMongoRepository;
import com.kairos.persistence.repository.task_type.TaskTypeMongoRepository;
import com.kairos.service.MongoBaseService;
import com.kairos.service.client_exception.ClientExceptionService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.fls_visitour.schedule.Scheduler;
import com.kairos.service.fls_visitour.schedule.TaskConverterService;
import com.kairos.service.restrcition_freuency.RestrictionFrequencyService;
import com.kairos.service.task_type.TaskService;
import com.kairos.rule_validator.TaskSpecification;
import com.kairos.rule_validator.task.TaskStaffTypeSpecification;
import com.kairos.commons.utils.DateUtils;
import com.kairos.utils.user_context.UserContext;
import com.kairos.wrapper.task.TaskGanttDTO;
import de.tourenserver.ArrayOfFixedCall;
import de.tourenserver.CallInfoRec;
import de.tourenserver.FixScheduleResponse;
import de.tourenserver.FixedCall;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
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
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.kairos.enums.task_type.TaskTypeEnum.TaskTypeStaff.EXCLUDED_EMPLOYEES;
import static com.kairos.enums.task_type.TaskTypeEnum.TaskTypeStaff.PREFERRED_EMPLOYEES;
import static com.kairos.persistence.model.constants.ClientExceptionConstant.SICK;
import static com.kairos.persistence.model.constants.TaskConstants.*;
import static com.kairos.commons.utils.DateUtils.ISO_FORMAT;
import static com.kairos.commons.utils.DateUtils.ONLY_DATE;
import static java.time.ZoneId.systemDefault;

/**
 * Created by oodles on 19/7/17.
 */

@Service
@Transactional
public class TaskExceptionService extends MongoBaseService {

    @Inject
    private TaskService taskService;
    @Inject
    private TaskMongoRepository taskMongoRepository;
    @Inject
    private TaskConverterService taskConverterService;
    @Inject
    private Scheduler scheduler;
    @Inject
    private ClientExceptionService clientExceptionService;
    @Inject
    private CustomTimeScaleRepository customTimeScaleRepository;
    @Inject
    private PlannerService plannerService;
    @Inject
    private ClientAggregatorMongoRepository clientAggregatorMongoRepository;
    @Inject
    private UserIntegrationService userIntegrationService;

    @Inject
    private RestrictionFrequencyService restrictionFrequencyService;
    @Inject
    private TaskTypeMongoRepository taskTypeMongoRepository;
    @Inject
    private ExceptionService exceptionService;


    private static final Logger logger = LoggerFactory.getLogger(TaskExceptionService.class);




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
                taskService.updateTaskDuration(task,taskRestrictionDto.isReduction(),taskRestrictionDto.getPercentageDuration());
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

    public List<TaskGanttDTO> removeRestrictionsOfCitizens(long unitId, long citizenId, List<TaskRestrictionDto> taskRestrictionDtos){
        DayOfWeek dayOfWeek = LocalDate.now().getDayOfWeek();
        LocalDateTime dateFrom = LocalDateTime.now().withHour(DAY_START_HOUR).withMinute(DAY_START_MINUTE).withSecond(DAY_START_SECOND);
        LocalDateTime dateTo = LocalDateTime.now().withHour(DAY_END_HOUR).withMinute(DAY_END_MINUTE).withSecond(DAY_END_SECOND);
        if (DayOfWeek.FRIDAY.equals(dayOfWeek)) {
            dateTo = dateTo.plusDays(2);
        } else if (DayOfWeek.SATURDAY.equals(dayOfWeek)) {
            dateTo = dateTo.plusDays(1);
        }
        Date dateFromAsDate = Date.from(dateFrom.atZone(ZoneId.systemDefault()).toInstant());
        Date dateToAsDate = Date.from(dateTo.atZone(ZoneId.systemDefault()).toInstant());

        List<Long> citizenIds = taskRestrictionDtos.stream().map(taskRestrictionDto -> taskRestrictionDto.getCitizenId()).collect(Collectors.toList());

        List<Task> tasks = taskMongoRepository.getPrePlanningTaskBetweenExceptionDates(unitId, citizenIds, dateFromAsDate, dateToAsDate);
        Map<String,String> flsCredentails = userIntegrationService.getFLS_Credentials(unitId);
        taskRestrictionDtos.forEach(taskRestrictionDto -> {
            List<Task> filtertedTasks = tasks.stream().filter(task -> task.getCitizenId() == taskRestrictionDto.getCitizenId()).collect(Collectors.toList());
            removeRestrictionFromTask(filtertedTasks,taskRestrictionDto,flsCredentails);
        });
        taskConverterService.createFlsCallFromTasks(tasks,flsCredentails);
        List<Task> selectedCitizenTasks = tasks.stream().filter(task -> task.getCitizenId() == citizenId).collect(Collectors.toList());
        return taskService.customizeTaskData(selectedCitizenTasks);
    }

    /**
     * @auther anil maurya
     * use rest template to get List of client from user micro service
     * @param unitId
     * @return
     */
    public boolean removeRestrictionsOfAllCitizens(long unitId,BigInteger restrictionFrequencyId) {

        RestrictionFrequency restrictionFrequency = restrictionFrequencyService.getRestrictionFrequency(restrictionFrequencyId);
        if(restrictionFrequency == null){
            logger.info("Incorrect id of restriction frequency id " + restrictionFrequencyId);
            exceptionService.dataNotFoundByIdException("message.restrictionfrequency.id",restrictionFrequencyId);
        }
        List<Long> citizenIds= userIntegrationService.getCitizenIds();
        //DayOfWeek dayOfWeek = LocalDate.now().getHeaderName();
        LocalDateTime dateFrom = LocalDateTime.now().withHour(DAY_START_HOUR).withMinute(DAY_START_MINUTE).withSecond(DAY_START_SECOND);
        LocalDateTime dateTo = LocalDateTime.now().withHour(DAY_END_HOUR).withMinute(DAY_END_MINUTE).withSecond(DAY_END_SECOND);
        dateTo = dateTo.plusDays(restrictionFrequency.getValue());
       /* if (DayOfWeek.FRIDAY.equals(dayOfWeek)) {
            dateTo  = dateTo.plusDays(2);
        } else if (DayOfWeek.SATURDAY.equals(dayOfWeek)) {
            dateTo = dateTo.plusDays(1);
        }*/
        Date dateFromAsDate = Date.from(dateFrom.atZone(ZoneId.systemDefault()).toInstant());
        Date dateToAsDate = Date.from(dateTo.atZone(ZoneId.systemDefault()).toInstant());
        List<Task> tasks = taskMongoRepository.getPrePlanningTaskBetweenExceptionDates(unitId, citizenIds, dateFromAsDate, dateToAsDate);
        logger.debug("Unhandled tasks  list size " + tasks.size());
        tasks.forEach(task -> {
            if (!task.isSingleTask() && task.getActualPlanningTask() == null) {
                taskService.savePreplanningStateOfTask(task);
            }
            removeRestrictionFromTask(task);
        });
        Map<String,String> flsCredentials = userIntegrationService.getFLS_Credentials(unitId);
        taskConverterService.createFlsCallFromTasks(tasks,flsCredentials);
        return true;
    }

    private void removeRestrictionFromTask(Task task) {

        task.setPrefferedStaffIdsList(null);
        task.setForbiddenStaffIdsList(null);
        task.setSkills("");
        task.setTeamId("");
    }

    public List<TaskGanttDTO> updateUnhandledActualPlanningTasks(long unitId, TaskDTO taskDTO) {
        List<Task> tasks = taskMongoRepository.findByIdIn(taskDTO.getTaskIds(), new Sort(Sort.Direction.ASC, "timeFrom"));
        tasks.forEach(task -> {
            if (!task.isSingleTask() && task.getActualPlanningTask() == null) {
                taskService.savePreplanningStateOfTask(task);
            }
            updateUnhandledTaskInfo(task, taskDTO);
        });
        Map<String, String> flsCredentials = userIntegrationService.getFLS_Credentials(unitId);
        taskConverterService.createFlsCallFromTasks(tasks, flsCredentials);

        if(!tasks.isEmpty()){
            ClientAggregator clientAggregator = updateTaskCountInAggregator(tasks,unitId,tasks.get(0).getCitizenId(),false);
            plannerService.sendAggregateDataToClient(clientAggregator,unitId);
        }
        return taskService.customizeTaskData(tasks);
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
        Map<String, String> flsCredentials = userIntegrationService.getFLS_Credentials(unitId);
        taskConverterService.createFlsCallFromTasks(tasks, flsCredentials);
        return taskService.customizeTaskData(tasks);
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
        if (bulkUpdateTaskDTO.getRemoveNotAllowedStaff() != null && bulkUpdateTaskDTO.getRemoveNotAllowedStaff()) {
            task.setForbiddenStaffIdsList(null);
        }

        /**
         * to validate task staff type
         */
        TaskType taskType = taskTypeMongoRepository.findOne(task.getTaskTypeId());
        boolean excludeEmployees = taskType.getEmployees().contains(EXCLUDED_EMPLOYEES);
        boolean preferredEmployees = taskType.getEmployees().contains(PREFERRED_EMPLOYEES);
        TaskSpecification<Task> taskStaffSpecification = new TaskStaffTypeSpecification(excludeEmployees,preferredEmployees);

        if(taskStaffSpecification.isSatisfied(task)){
            if (bulkUpdateTaskDTO.getPrefferedStaff() != null) {
                task.setPrefferedStaffIdsList(bulkUpdateTaskDTO.getPrefferedStaff());
            }
            if (bulkUpdateTaskDTO.getForbiddenStaff() != null) {
                task.setForbiddenStaffIdsList(bulkUpdateTaskDTO.getForbiddenStaff());
            }
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
            taskService.updateTaskDuration(task,bulkUpdateTaskDTO.isReduced(),bulkUpdateTaskDTO.getPercentageDuration());
        }
    }

    public Map<String,Object> askAppointmentSuggestionsFromVisitour(long unitId,long taskId){

        Task task =  taskService.findOne(String.valueOf(taskId));

        Map<String, Object> returnData = new HashMap<>();

        /*Map<String, String> flsCredentials = integrationServiceRestClient.getFLS_Credentials(unitId);

        Map <String,Object> callMetaData = new HashMap<>();
        callMetaData.put("functionCode",1);

        *//*callMetaData.put("extID","88068"); // for single suggestion
        callMetaData.put("vtid",88068);*//*

        *//*callMetaData.put("extID","1656293"); // for two suggestions
        callMetaData.put("vtid",51120);*//*

        *//*callMetaData.put("extID","1581097"); // no suggestion
        callMetaData.put("vtid",93359);*//*

        callMetaData.put("extID", task.getId());
        callMetaData.put("vtid", task.getVisitourId());

        List<TaskAppointmentSuggestionDTO> taskAppointmentSuggestionDTOList = scheduler.getAppointmentSuggestions(callMetaData,flsCredentials);
        logger.debug("taskAppointmentSuggestionDTOList >>>>>>  "+taskAppointmentSuggestionDTOList);

        returnData.put("taskId",taskId);
        returnData.put("taskAppointmentSuggestionList",taskAppointmentSuggestionDTOList);*/

        return returnData;

    }

    public List<TaskGanttDTO> confirmAppointmentSuggestion(long unitId,long taskId, @RequestBody Map<String, Object> payload) throws ParseException {

        logger.debug("payload >>>>>>  "+payload);

        Task task =  taskService.findOne(String.valueOf(taskId));

        Map<String, String> flsCredentials = userIntegrationService.getFLS_Credentials(unitId);

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
        return taskService.customizeTaskData(taskToReturn);


    }

    public List<TaskGanttDTO> makeTasksActiveInactive(TaskActiveUpdationDTO taskActiveUpdationDTO, long unitId) {

        List<Task> tasksToUpdate = taskMongoRepository.findByIdIn(taskActiveUpdationDTO.getTaskIds(), new Sort(Sort.Direction.ASC, "timeFrom"));
        if (tasksToUpdate.isEmpty()) {
            return Collections.emptyList();
        }
        List<Task> tasksToReturn = new ArrayList<>(tasksToUpdate.size());
        Map<String, String> flsCredentials = userIntegrationService.getFLS_Credentials(unitId);
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
                taskService.save(task);
            }
            tasksToReturn.add(task);
        });
        return taskService.customizeTaskData(tasksToReturn);
    }

    /*
        By Yasir
        Commented below method as we are no longer using FLS Visitour
    */
    private void deleteTaskFromVisitour(Task taskToDelete, Map<String, String> flsCredentials) {
        /*if (taskToDelete.getVisitourId() != null && taskToDelete.getVisitourId() > 0) {
            Map<String, Object> callMetaData = new HashMap<>();
            callMetaData.put("functionCode", 4);
            callMetaData.put("extID", taskToDelete.getId());
            callMetaData.put("vtid", taskToDelete.getVisitourId());
            scheduler.deleteCall(callMetaData, flsCredentials);
        }*/
    }

    public List<TaskGanttDTO> copySingleTask(BigInteger taskId, List<Map<String, Object>> taskData) throws ParseException {

        Optional<Task> taskOptional = taskMongoRepository.findById(taskId);
        if (!taskOptional.isPresent()) {
            exceptionService.dataNotFoundByIdException("message.task.id");
        }
        Task task=taskOptional.get();
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

        taskService.save(tasksToReturn);
        Map<String, String> flsCredentials = userIntegrationService.getFLS_Credentials(task.getUnitId());
        taskConverterService.createFlsCallFromTasks(tasksToReturn, flsCredentials);
        return taskService.customizeTaskData(tasksToReturn);
    }

    /**
     * to save settings of planner of actual planner, like {confirmation on delete exception} etc
     *
     * @param unitId
     * @param plannerSettings
     * @return
     */
    public void saveSettingsOfPlanner(long unitId, Map<String, Object> plannerSettings) {
        Long loggedInUserId = UserContext.getUserDetails().getId();
        long citizenId;

        if (plannerSettings.get("citizenId") instanceof Integer) {
            citizenId = (int) plannerSettings.get("citizenId");
        } else {
            citizenId = (long) plannerSettings.get("citizenId");
        }

        CustomTimeScale customTimeScale = customTimeScaleRepository.findByStaffIdAndCitizenIdAndUnitId(loggedInUserId, citizenId, unitId);
        if (customTimeScale == null) {
            customTimeScale = new CustomTimeScale(loggedInUserId, citizenId, unitId, 0);
        }
        customTimeScale.setShowExceptionDeleteConfirmation((boolean) plannerSettings.get("showExceptionDeleteConfirmation"));
        save(customTimeScale);
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
        Map<String, String> flsCredentials = userIntegrationService.getFLS_Credentials(unitId);
        taskConverterService.createFlsCallFromTasks(revertTaskState, flsCredentials);
        if(!revertTaskState.isEmpty()){
            ClientAggregator clientAggregator = updateTaskCountInAggregator(revertTaskState,unitId,revertTaskState.get(0).getCitizenId(),false);
            plannerService.sendAggregateDataToClient(clientAggregator,unitId);

        }
        return taskService.customizeTaskData(revertTaskState);
    }

    //TODO optimize this method
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
        Map<String, String> flsCredentials = userIntegrationService.getFLS_Credentials(unitId);
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
                        Optional<Task> taskOptional = taskMongoRepository.findById(new BigInteger(fixedCall.getExtID() + ""));
                        logger.info(" Task found " + taskOptional);
                        if (taskOptional.isPresent()) {
                           Task task=taskOptional.get();
                            if (fixedCall.getState() == 3) {
                                task.setExecutionDate(fixedCall.getArrival().toGregorianCalendar().getTime());
                                task.setTaskStatus(TaskStatus.PLANNED);
                            } else if (fixedCall.getState() == 2) {
                                task.setTaskStatus(TaskStatus.CONFIRMED);
                                task.setStaffId(Long.parseLong(fixedCall.getFMExtID()));
                            } else {
                                task.setTaskStatus(TaskStatus.GENERATED);
                            }
                            if (fixedCall.getFMExtID() != null && !fixedCall.getFMExtID().isEmpty()) {
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
                        if (callInfoRec.getFMExtID() != null && !callInfoRec.getFMExtID().isEmpty()) {
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

        List<TaskGanttDTO> responseList = taskService.customizeTaskData(taskList);
        logger.info("responseList " + responseList);

        Map returnData = new HashMap();
        returnData.put("taskList", responseList);
        returnData.put("taskDemandList", plannerService.getCitizenPlanning(unitId, citizenId, false, null));
        return returnData;

    }

    private Set<BigInteger> getUpdatedUnhandledTaskIds(Set<BigInteger> taskIds,Task task){

        if(task.getClientExceptions().isEmpty()){
            taskIds.remove(task.getId());
        } else {
            taskIds.add(task.getId());
        }
        return taskIds;
    }

    public ClientAggregator updateTaskCountInAggregator(List<Task> tasks,long unitId,long citizenId,boolean isDeleteOperation){

        long startTime = System.currentTimeMillis();

        ClientAggregator clientAggregator = clientAggregatorMongoRepository.findByUnitIdAndCitizenId(unitId,citizenId);
        if(clientAggregator == null && isDeleteOperation){
            return null;
        } else if(clientAggregator == null) {
            clientAggregator = new ClientAggregator(unitId,citizenId);
        }

        FourWeekFrequency fourWeekFrequency = FourWeekFrequency.getInstance();
        UnhandledTaskCount unhandledTaskCount = (clientAggregator.getUnhandledTaskCount() == null)?new UnhandledTaskCount() : clientAggregator.getUnhandledTaskCount();

        tasks.forEach(task -> {
            LocalDateTime taskStartTime = LocalDateTime.ofInstant(task.getTimeFrom().toInstant(), ZoneId.systemDefault());
            if(taskStartTime.isEqual(fourWeekFrequency.getStartOfDay()) || (taskStartTime.isAfter(fourWeekFrequency.getStartOfDay()) && taskStartTime.isBefore(fourWeekFrequency.getEndOfDay()))){

                unhandledTaskCount.setUnhandledTodayTasks(getUpdatedUnhandledTaskIds(unhandledTaskCount.getUnhandledTodayTasks(),task));
                unhandledTaskCount.setUnhandledTasksTodayCount(unhandledTaskCount.getUnhandledTodayTasks().size());
            }
            if(taskStartTime.isEqual(fourWeekFrequency.getStartOfTomorrow()) || (taskStartTime.isAfter(fourWeekFrequency.getStartOfTomorrow()) && taskStartTime.isBefore(fourWeekFrequency.getEndOfTomorrow()))){
                unhandledTaskCount.setUnhandledTomorrowTasks(getUpdatedUnhandledTaskIds(unhandledTaskCount.getUnhandledTomorrowTasks(),task));
                unhandledTaskCount.setUnhandledTasksTomorrowCount(unhandledTaskCount.getUnhandledTomorrowTasks().size());
            }
            if(taskStartTime.isEqual(fourWeekFrequency.getStartOfDayAfterTomorrow()) || (taskStartTime.isAfter(fourWeekFrequency.getStartOfDayAfterTomorrow()) && taskStartTime.isBefore(fourWeekFrequency.getEndOfDayAfterTomorrow()))){
                unhandledTaskCount.setUnhandledDayAfterTomorrowTasks(getUpdatedUnhandledTaskIds(unhandledTaskCount.getUnhandledDayAfterTomorrowTasks(),task));
                unhandledTaskCount.setUnhandledTasksDayAfterTomorrowCount(unhandledTaskCount.getUnhandledDayAfterTomorrowTasks().size());
            }
            if(taskStartTime.isAfter(fourWeekFrequency.getStartOfWeek()) && taskStartTime.isBefore(fourWeekFrequency.getEndOfWeek())){
                unhandledTaskCount.setUnhandledOneWeekTasks(getUpdatedUnhandledTaskIds(unhandledTaskCount.getUnhandledOneWeekTasks(),task));
                unhandledTaskCount.setUnhandledTasksOneWeekCount(unhandledTaskCount.getUnhandledOneWeekTasks().size());
            }
            if(taskStartTime.isAfter(fourWeekFrequency.getStartOfWeek()) && taskStartTime.isBefore(fourWeekFrequency.getEndOfSecondWeek())){
                unhandledTaskCount.setUnhandledTwoWeekTasks(getUpdatedUnhandledTaskIds(unhandledTaskCount.getUnhandledTwoWeekTasks(),task));
                unhandledTaskCount.setUnhandledTasksTwoWeekCount(unhandledTaskCount.getUnhandledTwoWeekTasks().size());
            }
            if(taskStartTime.isAfter(fourWeekFrequency.getStartOfWeek()) && taskStartTime.isBefore(fourWeekFrequency.getEndOfThirdWeek())){
                unhandledTaskCount.setUnhandledThreeWeekTasks(getUpdatedUnhandledTaskIds(unhandledTaskCount.getUnhandledThreeWeekTasks(),task));
                unhandledTaskCount.setUnhandledTasksThreeWeekCount(unhandledTaskCount.getUnhandledThreeWeekTasks().size());
            }
            if(taskStartTime.isAfter(fourWeekFrequency.getStartOfWeek()) && taskStartTime.isBefore(fourWeekFrequency.getEndOfFourWeek())){
                unhandledTaskCount.setUnhandledFourWeekTasks(getUpdatedUnhandledTaskIds(unhandledTaskCount.getUnhandledFourWeekTasks(),task));
                unhandledTaskCount.setUnhandledTasksFourWeekCount(unhandledTaskCount.getUnhandledFourWeekTasks().size());
            }
        });
        clientAggregator.setUnhandledTaskCount(unhandledTaskCount);
        save(clientAggregator);

        logger.debug("Unhandled task count:::" + unhandledTaskCount.toString());

        logger.debug("Total time taken by this method::  " + (System.currentTimeMillis()-startTime) + "  ms");

        return clientAggregator;
    }

}
