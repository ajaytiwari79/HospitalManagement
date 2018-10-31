package com.kairos.service.planner;

import com.kairos.rest_client.GenericIntegrationService;
import com.kairos.enums.task_type.TaskTypeEnum;
import com.kairos.persistence.model.task.Task;
import com.kairos.persistence.model.task.TaskAddress;
import com.kairos.persistence.model.task.TaskStatus;
import com.kairos.persistence.repository.common.CustomAggregationOperation;
import com.kairos.persistence.repository.task_type.TaskMongoRepository;
import com.kairos.service.MongoBaseService;
import com.kairos.service.fls_visitour.schedule.Scheduler;
import com.kairos.service.fls_visitour.schedule.TaskConverterService;
import com.kairos.service.task_type.TaskService;
import com.kairos.commons.utils.DateUtils;
import com.kairos.wrapper.task_demand.TaskDemandVisitWrapper;
import com.kairos.wrapper.task.TaskGanttDTO;
import org.bson.Document;
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

import javax.inject.Inject;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.kairos.commons.utils.DateUtils.ISO_FORMAT;
import static java.time.ZoneId.systemDefault;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

/**
 * Created by oodles on 19/7/17.
 */
@Service
@Transactional
public class TasksMergingService extends MongoBaseService {
    @Inject
    TaskMongoRepository taskMongoRepository;

    @Inject
    TaskService taskService;
    @Inject
    MongoTemplate mongoTemplate;
    @Inject
    Scheduler scheduler;
    @Inject
    TaskConverterService taskConverterService;
    @Autowired
    GenericIntegrationService genericIntegrationService;


    private static final Logger logger = LoggerFactory.getLogger(TasksMergingService.class);


    public List<TaskGanttDTO> mergeMultipleTasks(long unitId, long citizenId, Map<String, Object> tasksData, boolean isActualPlanningScreen) throws CloneNotSupportedException {
        long startTime = System.currentTimeMillis();

        //Client citizen = clientGraphRepository.findById(Long.valueOf(citizenId), 0);

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
        List<Task> taskList = mergeRepetitions(taskIds, jointEventsIds, startDate, citizenId, unitId, tasksData.get("mainTaskName").toString(), isActualPlanningScreen);
        List<TaskGanttDTO> responseList = taskService.customizeTaskData(taskList);

        logger.debug("Execution Time :(PlannerService:mergeMultipleTasks) " + (System.currentTimeMillis() - startTime) + " ms");
        logger.debug("responseList " + responseList);
        return responseList;
    }

    public List<Task> mergeRepetitions(List<String> taskIds, List<String> jointEventsIds, Date dateFrom,Long  citizenId, long unitId, String mainTaskName, boolean isActualPlanningScreen) throws CloneNotSupportedException {

        List<Task> taskList = new ArrayList<>();
        //Map<String, String> flsCredentials = integrationService.getFLS_Credentials(unitId);
        //Map<String, String> flsCredentials = integrationService.getFLS_Credentials(unitId);
        TaskDemandVisitWrapper taskDemandVisitWrapper=genericIntegrationService.
                getPrerequisitesForTaskCreation(citizenId,unitId);

        Map<String, String> flsCredentials = taskDemandVisitWrapper.getFlsCredentials();

        String uniqueID = UUID.randomUUID().toString();
        uniqueID = uniqueID.substring(0, uniqueID.indexOf("-"));


        TaskAddress taskAddress=taskDemandVisitWrapper.getTaskAddress();
        Long loggedInUser= taskDemandVisitWrapper.getStaffId();
        List<Long> preferredStaffIds=taskDemandVisitWrapper.getPreferredStaff();
        List<Long> forbiddenStaffIds=taskDemandVisitWrapper.getForbiddenStaff();



        if(!isActualPlanningScreen) {

            Criteria criteria = Criteria.where("joinEventId").in(jointEventsIds).and("citizenId").is(citizenId).and("unitId").is(unitId).and("dateFrom").gt(dateFrom).and("isDeleted").is(false);

            String projection = "{   $project : { date : {$substr: ['$dateFrom', 0, 10] }}}";

            String group = "{'$group':{'_id':'$date', 'taskIds':{'$push':'$_id'}}}";
            Document groupObject = Document.parse(group);
            Document projectionObject =Document.parse(projection);

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

            for (Map map : taskIdsGroupByDate) {

                taskIds = (List<String>) map.get("taskIds");
                logger.debug("taskIds: " + taskIds);

                Task mergedTask = mergeTasksWithIds(taskIds, unitId, citizenId, mainTaskName, isActualPlanningScreen, uniqueID, taskAddress, loggedInUser, preferredStaffIds, forbiddenStaffIds, flsCredentials);
                taskList.add(mergedTask);
            }
        } else {
            Task mergedTask = mergeTasksWithIds(taskIds, unitId, citizenId, mainTaskName, isActualPlanningScreen, uniqueID, taskAddress, loggedInUser, preferredStaffIds, forbiddenStaffIds, flsCredentials);
            taskList.add(mergedTask);
        }
        taskConverterService.createFlsCallFromTasks(taskList, flsCredentials);
        return taskList;
    }


    public Task mergeTasksWithIds(List<String> taskIds, long unitId, long citizenId, String mainTaskName, boolean isActualPlanningScreen, String uniqueID, TaskAddress taskAddress, Long loggedInUserId, List<Long> preferredStaffIds, List<Long> forbiddenStaffIds, Map<String, String> flsCredentials) throws CloneNotSupportedException {

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
            logger.info("isActualPlanningScreen " +isActualPlanningScreen);
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
                }
            }
            task.setSubTask(true);
            subTaskIdsList.add(task.getId());
            responseSubTaskIdsList.add(task.getId());

            if (task.getVisitourId() != null && task.getVisitourId() > 0) {
                tasksToDelete.add(task);
            }

            taskService.save(task);

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

        LocalDate localDate = startDate.toInstant().atZone(systemDefault()).toLocalDate();
        mainTask.setJoinEventId(localDate.getDayOfWeek().name() + "_" + uniqueID);

        mainTask.setUnitId(unitId);
        mainTask.setCitizenId(citizenId);
        mainTask.setStaffCount(1); //Setting Staff count to 1, as main task has be to delivered by individual.
        mainTask.setPriority(2); //Setting Priority to 2, as it's default priority is fls visitour.
        mainTask.setVisitourTaskTypeID("37"); //Setting TaskType id 37 for Merged Tasks.
        mainTask.setName(mainTaskName);

        mainTask.setPrefferedStaffIdsList(preferredStaffIds);
        mainTask.setForbiddenStaffIdsList(forbiddenStaffIds);

        mainTask.setTaskStatus(TaskStatus.GENERATED);

        mainTask.setAddress(taskAddress);

        mainTask.setCreatedByStaffId(loggedInUserId);

        mainTask.setSubTask(false);
        if (isActualPlanningScreen) {
            mainTask.setSingleTask(true);
        }
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


    public List<TaskGanttDTO> unMergeMultipleTasks( long unitId, long citizenId, Map<String, Object> tasksData, boolean isActualPlanningScreen) throws CloneNotSupportedException, ParseException {
        long startTime = System.currentTimeMillis();

        Map<String, Object> returnedData;

        List<Task> tasksToReturn = new ArrayList<>();
        List<Task> tasksToCreate = new ArrayList<>();
        List<Task> tasksToDelete = new ArrayList<>();
        logger.debug("tasksData payload <><><><><><><><>" + tasksData);
        String mainTaskId = tasksData.get("mainTaskId").toString();
        Task mainTask = taskMongoRepository.findById(new BigInteger(mainTaskId)).get();

        List<String> jointEventsIds = new ArrayList<>();
        jointEventsIds.add(mainTask.getJoinEventId());

        List<String> unMergeTaskIds = new ArrayList<>();
        for (Map<String, Object> taskData : (List<Map<String, Object>>) tasksData.get("tasksToUnmerge")) {
            if ((boolean) taskData.get("isSelected") == true) {
                unMergeTaskIds.add(taskData.get("id").toString());
                if( !mainTask.isSingleTask() ){ //If it's not single task then there's no repetitions for this task.
                    jointEventsIds.add(taskData.get("jointEvents").toString());
                }
            }

        }

        List<Task> unMergeTasksList = taskMongoRepository.getAllTasksByIdsIn(unMergeTaskIds);
        returnedData = unMergeTasks(mainTask, unMergeTasksList, isActualPlanningScreen);
        tasksToReturn.addAll((List<Task>) returnedData.get("taskList"));
        tasksToCreate.addAll((List<Task>) returnedData.get("tasksToCreate"));
        tasksToDelete.addAll((List<Task>) returnedData.get("tasksToDelete"));

        Criteria criteria;
        if( !mainTask.isSingleTask() ) {
            if(isActualPlanningScreen){
                Date unmergeTillDate = DateUtils.convertToOnlyDate(tasksData.get("unmergeTillDate").toString(), ISO_FORMAT);
                criteria = Criteria.where("joinEventId").in(jointEventsIds).and("citizenId").is(citizenId).and("unitId").is(unitId).and("dateFrom").gt(mainTask.getDateFrom()).and("dateTo").lte(unmergeTillDate).and("isDeleted").is(false);
            }else{
                criteria = Criteria.where("joinEventId").in(jointEventsIds).and("citizenId").is(citizenId).and("unitId").is(unitId).and("dateFrom").gt(mainTask.getDateFrom()).and("isDeleted").is(false);
            }

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
        }

        Map<String, String> flsCredentials = genericIntegrationService.getFLS_Credentials(unitId);
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

        List<TaskGanttDTO> responseList = taskService.customizeTaskData(tasksToReturn);
        logger.info("Execution Time :(PlannerService:Un-mergeMultipleTasks) " + (System.currentTimeMillis() - startTime) + " ms");
        return responseList;
    }


    private Map<String, Object> unMergeTasks(Task mainTask, List<Task> unMergeTasksList, boolean isActualPlanningScreen) throws CloneNotSupportedException {

        int mainTaskDuration = 0;

        Map<String, Object> returnedData = new HashMap<>();

        List<Task> taskList = new ArrayList<>();
        List<Task> tasksToCreate = new ArrayList<>();
        List<Task> tasksToDelete = new ArrayList<>();

        Set<String> mainTaskSkillIds = new HashSet<>();
        mainTaskSkillIds.addAll(Arrays.asList(mainTask.getSkills().split(",")));

        for (Task taskToUnmerge : unMergeTasksList) {

            taskToUnmerge.setDateFrom(mainTask.getDateFrom());
            taskToUnmerge.setDateTo(mainTask.getDateTo());
            taskToUnmerge.setSubTask(false);
                mainTask.getSubTaskIds().remove(taskToUnmerge.getId());
                if(taskToUnmerge.getSkills()!=null && taskToUnmerge.getSkills()!=""){
                    mainTaskSkillIds.removeAll(Arrays.asList(taskToUnmerge.getSkills().split(",")));
                }
                int duration = taskToUnmerge.getDuration();
                mainTaskDuration = mainTaskDuration + duration;

                //if (taskToUnmerge.getVisitourId() != null && taskToUnmerge.getVisitourId() > 0) {
                    tasksToCreate.add(taskToUnmerge);
                //}
                taskService.save(taskToUnmerge);
                taskList.add(taskToUnmerge);
        }

        if (mainTask.getSubTaskIds().size() > 0) {

            mainTask.setSkills(String.join(",", mainTaskSkillIds));

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

}
