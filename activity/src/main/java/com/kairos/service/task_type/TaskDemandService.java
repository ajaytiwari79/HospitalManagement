package com.kairos.service.task_type;

import com.kairos.persistence.model.client_exception.ClientException;
import com.kairos.persistence.model.client_exception.ClientExceptionType;
import com.kairos.persistence.model.task_demand.MonthlyFrequency;
import com.kairos.persistence.model.task_demand.TaskDemand;
import com.kairos.persistence.model.task_demand.TaskDemandVisit;
import com.kairos.persistence.model.task_type.TaskType;
import com.kairos.persistence.repository.client_aggregator.ClientAggregatorMongoRepository;
import com.kairos.persistence.repository.client_exception.ClientExceptionTypeMongoRepository;
import com.kairos.persistence.repository.common.CustomAggregationOperation;
import com.kairos.persistence.repository.common.MongoSequenceRepository;
import com.kairos.persistence.repository.repository_impl.CustomTaskTypeRepositoryImpl;
import com.kairos.persistence.repository.task_type.TaskDemandMongoRepository;
import com.kairos.persistence.repository.task_type.TaskTypeMongoRepository;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.service.CustomTimeScaleService;
import com.kairos.service.MongoBaseService;
import com.kairos.dto.user.organization.Shifts;
import com.kairos.dto.user.staff.client.ClientFilterDTO;
import com.kairos.dto.user.visitation.RepetitionType;
import com.kairos.utils.JsonUtils;
import com.kairos.wrapper.OrgTaskTypeAggregateResult;
import com.kairos.wrapper.TaskTypeAggregateResult;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.*;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;

/**
 * Created by prabjot on 2/11/16.
 */
@Service
public class TaskDemandService extends MongoBaseService {


    @Inject
    TaskTypeMongoRepository taskTypeMongoRepository;
    @Inject
    TaskDemandMongoRepository taskDemandMongoRepository;

    @Inject
    MongoTemplate mongoTemplate;
//    @Inject
//    com.kairos.user.service.services.organization.OrganizationService organizationService;

    @Inject
    TaskService taskService;

    @Inject
    private CustomTimeScaleService customTimeScaleService;
    @Inject
    private ClientExceptionTypeMongoRepository clientExceptionTypeMongoRepository;
    @Inject
    private ClientAggregatorMongoRepository clientAggregatorMongoRepository;

    @Inject
    private MongoSequenceRepository mongoSequenceRepository;

    @Autowired
     TaskTypeService taskTypeService;

    @Autowired
    UserIntegrationService userIntegrationService;

    @Autowired
    CustomTaskTypeRepositoryImpl customTaskTypeRepository;


    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public TaskDemand createTaskDemand(Map<String, Object> reqData) throws ParseException {
        TaskType taskType = taskTypeMongoRepository.findById((BigInteger) reqData.get("taskTypeId")).get();
        if (taskType == null) {
            return null;
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date startDate = simpleDateFormat.parse((String) reqData.get("startDate"));
        Date endDate = simpleDateFormat.parse((String) reqData.get("endDate"));

        //TaskDemand.TaskFrequency taskFrequency = TaskDemand.TaskFrequency.valueOf((String) reqData.get("taskFrequency"));

        /*if (taskFrequency == null) {
            return null;
        }*/

        /*TaskDemand taskDemand = new TaskDemand(taskType.getId(),
                startDate,
                endDate,
          //      taskFrequency,
                Long.parseLong((String) reqData.get("timeSlotId")),
                Integer.parseInt((String) reqData.get("dailyVisit")),
                Integer.parseInt((String) reqData.get("dailyVisitDuration")),
                Integer.parseInt((String) reqData.get("weekendVisit")),
                Integer.parseInt((String) reqData.get("weekendVisitDuration")),
                (String) reqData.get("supplier"),
                Long.parseLong((String) reqData.get("organizationId")),
                Long.parseLong((String) reqData.get("citizenId")),
                Long.parseLong((String) reqData.get("staffId")),
                (String) reqData.get("taskDescription"),
                Integer.parseInt((String) reqData.get("priority")),
                (String) reqData.get("remark"));
        save(taskDemand);
        taskService.createTask(taskDemand);*/
        TaskDemand taskDemand = new TaskDemand();
        taskDemand.setStartDate(startDate);
        taskDemand.setEndDate(endDate);
        taskDemand.setPriority(Integer.parseInt((String) reqData.get("priority")));
        taskDemand.setCitizenId(Long.parseLong((String) reqData.get("citizenId")));
        //taskDemand.setOrganizationId(Long.parseLong((String) reqData.get("organizationId")));
        taskDemand.setTaskTypeId(new BigInteger(reqData.get("taskTypeId")+""));



        save(taskDemand);
        return taskDemand;
    }

    public List<TaskDemand> getbyTaskTypeId(String id) {
        return taskDemandMongoRepository.findByTaskTypeId(new ObjectId(id));
    }

    public List<TaskDemand> getByTaskTypeIds(List<String> taskTypeIdList) {
        return taskDemandMongoRepository.findByTaskTypeIdIn(taskTypeIdList);
    }


    public List<Long> getListOfClientByTimeSlotId(List<Long> timeSlotIds, Long unitId){
        String unitMatch = "{'$match': {'unitId': "+unitId+"}}";
        String unwindWeekdayVisits = "{'$unwind': {'path': '$weekdayVisits','preserveNullAndEmptyArrays': true}}";
        String unwindWeekendVisits = "{'$unwind': {'path': '$weekendVisits','preserveNullAndEmptyArrays': true}}";
        String matchTimeSlot = "{'$match':{'$or':[{'weekdayVisits.timeSlotId':{'$in':"+timeSlotIds+"}}, {'weekendVisits.timeSlotId':{'$in':"+timeSlotIds+"}}]}}";
        String groupCitizenId = "{'$group':{'_id':null, 'citizenList':{'$addToSet':'$citizenId'}}}";
        Document matchUnitObj =Document.parse(unitMatch);
        Document unwindWeekdayVisitsObj = Document.parse(unwindWeekdayVisits);
        Document unwindWeekendVisitsObj = Document.parse(unwindWeekendVisits);
        Document matchTimeSlotObj = Document.parse(matchTimeSlot);
        Document groupCitizenIdObj = Document.parse(groupCitizenId);

        Aggregation agg = Aggregation.newAggregation(

                new CustomAggregationOperation(matchUnitObj),
                new CustomAggregationOperation(unwindWeekdayVisitsObj),
                new CustomAggregationOperation(unwindWeekendVisitsObj),
                new CustomAggregationOperation(matchTimeSlotObj),
                new CustomAggregationOperation(groupCitizenIdObj)
        );
        logger.info("Citizen by TimeSlot query :: "+agg.toString());
        AggregationResults<Map> result =
                mongoTemplate.aggregate(agg, TaskDemand.class, Map.class);
        List<Map> data = result.getMappedResults();
        if(data == null || data.size() == 0)
            return Collections.EMPTY_LIST;
        return (List<Long>) data.get(0).get("citizenList");
    }

    /*
    * db.task_demands.aggregate([{$match:{'unitId': 136, citizenId:5551}},{$project:{allValues: { $setUnion: [ "$weekendVisits", "$weekdayVisits" ] }}},{$unwind:{path:"$allValues", preserveNullAndEmptyArrays:true}}, {$group:{_id:null, allTimeSlots:{$addToSet:"$allValues.timeSlotId"}}}]);
{ "_id" : null, "allTimeSlots" : [ NumberLong(51), NumberLong(50) ] }*/
    public List<Long> getListOfTimeSlotIdByCitizenAndUnit(Long citizen, Long unitId){
        String unitMatch = "{'$match': {'unitId': "+unitId+", 'citizenId':"+citizen+"}}";
        String projectQuery = "{'$project':{'allValues': { '$setUnion': [ '$weekendVisits', '$weekdayVisits' ] }}}";
        String unwindAllValue = "{'$unwind': {'path': '$allValues','preserveNullAndEmptyArrays': true}}";
        String groupCitizenId = "{'$group':{'_id':null, 'allTimeSlots':{'$addToSet':'$allValues.timeSlotId'}}}";
        Document matchUnitObj = Document.parse(unitMatch);
        Document unwindAllValueObj = Document.parse(unwindAllValue);
        Document projectObj = Document.parse(projectQuery);
        Document groupCitizenIdObj =Document.parse(groupCitizenId);

        Aggregation agg = Aggregation.newAggregation(

                new CustomAggregationOperation(matchUnitObj),
                new CustomAggregationOperation(projectObj),
                new CustomAggregationOperation(unwindAllValueObj),
                new CustomAggregationOperation(groupCitizenIdObj)
        );
        //logger.info("TimeSlot  list query query :: "+agg.toString());
        AggregationResults<Map> result =
                mongoTemplate.aggregate(agg, TaskDemand.class, Map.class);
        List<Map> data = result.getMappedResults();
        if(data == null || data.size() == 0)
            return Collections.EMPTY_LIST;
        return (List<Long>) data.get(0).get("allTimeSlots");
    }

    TaskDemand findByKmdExternalId(String kmdExternalId){
        return taskDemandMongoRepository.findByKmdExternalId(kmdExternalId);
    }

    public Boolean hasTaskGenerated(Long clientId, Long unitId){
        return !taskDemandMongoRepository.getByCitizenIdAndStatusAndUnitId(clientId, TaskDemand.Status.VISITATED, unitId).isEmpty();

    }

    public Map<String, String> countCitizenTaskDemandsHoursAndTasks(Long clientId, Long unitId){
        double hours = 0;
        float tasks = 0;
        Map<String, String> citizenDemandData = new HashMap<String, String>();
        List<TaskDemand> taskDemands = taskDemandMongoRepository.findAllByCitizenIdAndUnitIdAndRecurrencePattern(clientId,unitId, TaskDemand.RecurrencePattern.WEEKLY);
        for(TaskDemand taskDemand : taskDemands){
            int weekDayFrequency = 1;
            int weekEndFrequency = 1;
            if (taskDemand.getWeekdayFrequency() != null) {
                switch (taskDemand.getWeekdayFrequency()) {
                    case ONE_WEEK:
                        weekDayFrequency = 1;
                        break;
                    case TWO_WEEK:
                        weekDayFrequency = 2;
                        break;
                    case THREE_WEEK:
                        weekDayFrequency = 3;
                        break;
                    case FOUR_WEEK:
                        weekDayFrequency = 4;
                        break;
                }
                for (TaskDemandVisit taskDemandVisit : taskDemand.getWeekdayVisits()) {
                    hours += taskDemandVisit.getVisitCount() * taskDemandVisit.getVisitDuration();
                    tasks += taskDemandVisit.getVisitCount();
                }
                if(hours != 0)  hours = hours / weekDayFrequency;
                if(tasks != 0) tasks = tasks / weekDayFrequency;
            }
            if (taskDemand.getWeekendFrequency() != null){
                switch (taskDemand.getWeekendFrequency()){
                    case ONE_WEEK: weekEndFrequency = 1;
                        break;
                    case TWO_WEEK: weekEndFrequency = 2;
                        break;
                    case THREE_WEEK: weekEndFrequency = 3;
                        break;
                    case FOUR_WEEK: weekEndFrequency = 4;
                        break;
                }
                for(TaskDemandVisit  taskDemandVisit : taskDemand.getWeekendVisits()){
                    hours += taskDemandVisit.getVisitCount()*taskDemandVisit.getVisitDuration();
                    tasks += taskDemandVisit.getVisitCount() ;
                }
                if(hours != 0)  hours = hours/weekEndFrequency;
                if(tasks != 0) tasks = tasks/weekEndFrequency;

            }


        }
        DecimalFormat df = new DecimalFormat("###.##");
        citizenDemandData.put("hours", df.format(hours/60));
        citizenDemandData.put("tasks", df.format(tasks));
        return citizenDemandData;
    }

    /**
     * @atuher anil maurya
     * @param citizenIds
     * @return List<TaskTypeAggregateResult>
     *  @see List<TaskTypeAggregateResult>
     */
    public List<TaskTypeAggregateResult> getTaskTypesOfCitizens(List<Long> citizenIds )
    {
        return customTaskTypeRepository.getTaskTypesOfCitizens(citizenIds);
    }

    /**
     *  @auther anil maurya
     * @param unitId
     * @return
     */
    public List<OrgTaskTypeAggregateResult> getTaskTypesOfUnit(Long unitId){
       return  customTaskTypeRepository.getTaskTypesOfUnit(unitId);
    }



    /**
     *  @auther anil maurya
     * Worrying method have 4  call in loop need to change implementation or write store proccesure
     * @param organizationId
     * @param staffId
     * @param mapList
     * @return Map<String, Object>
     */
    public Map<String, Object> getOrganizationClientsWithPlanning(Long organizationId,Long staffId,List<Map<String, Object>> mapList) {
        Map<String, Object> response = new HashMap<>();
        List<Object> clientList = new ArrayList<>();
        List<Long> clientIds = new ArrayList<>();
        if (mapList != null) {

            for (Map<String, Object> map : mapList) {
              //  Map<String, Object> clientMap = (Map<String, Object>) map.get("Client");
                Long clientId = Long.parseLong(map.get("id")+"");;
                List<Long> timeSlotIdSets =getListOfTimeSlotIdByCitizenAndUnit(clientId, organizationId);
                //logger.debug("TimeSlotId   " + timeSlotIdSets);
                Map<String, Object> clientUpdatedMap = new HashMap<>();
                map.forEach((String, Object) -> clientUpdatedMap.put(String, Object));
                clientUpdatedMap.put("timeSlots", timeSlotIdSets);

                List<Long> citizens = new ArrayList<>();
                citizens.add(clientId);
                clientIds.add(clientId);
                List<TaskTypeAggregateResult> taskTypeAggregateResultList = customTaskTypeRepository.getTaskTypesOfCitizens(citizens);
                if(taskTypeAggregateResultList.size() > 0){
                    List<String> taskTypeIds = new ArrayList<>();
                    TaskTypeAggregateResult taskTypeAggregateResult = taskTypeAggregateResultList.get(0);
                    taskTypeIds.addAll(taskTypeAggregateResult.getTaskTypeIds());
                    clientUpdatedMap.put("taskTypeIds", taskTypeIds);
                }

                clientUpdatedMap.put("services", getClientServicesIds(clientId, organizationId));
                //    Map<String , String> clientDemandsHoursTasksData = taskDemandService.countCitizenTaskDemandsHoursAndTasks(clientId, organizationId);
                clientUpdatedMap.put("hasDemandGenerated", hasTaskGenerated(clientId, organizationId));

                clientUpdatedMap.put("numberOfAdditionalScales", customTimeScaleService.getNumberOfAdditionalScales(staffId, clientId, organizationId));
                clientUpdatedMap.put("noOfExceptions", countExceptions( clientId));
                clientUpdatedMap.put("sumOfVisitationHoursAndTasks", clientAggregatorMongoRepository.findVisitationHoursAndTasksByCitizenIdIn( clientId, organizationId));
                //  clientUpdatedMap.put("noOfUnHandlesExceptions", countUnhandledExceptions(organizationId, clientId));
                //logger.debug(" ClientMap " + clientMap);

                clientList.add(clientUpdatedMap);
            }
            response.put("clientList", clientList);
          //  response.put("citizenPlanningProblemsData", clientAggregatorMongoRepository.findByUnitIdAndCitizenIdIn(organizationId, clientIds));
        }

    /*    List<ClientExceptionType> exceptionTypeData = clientExceptionTypeMongoRepository.findAll();


        response.put("exceptionTypes", exceptionTypeData);*/
        return response;
    }



    private List<Map> countExceptions( Long citizenId){
        LocalDateTime starDate=LocalDateTime.of(LocalDate.now(), LocalTime.of(0,0,0));
        LocalDateTime endDate=LocalDateTime.of(LocalDate.now(),LocalTime.MAX);
        Criteria criteria = Criteria.where("clientId").is(citizenId).and("isDeleted").is(false).and("fromTime").gte(Date.from(starDate.atZone(ZoneId.systemDefault()).toInstant())).and("toTime").lte(Date.from(endDate.atZone(ZoneId.systemDefault()).toInstant()));
        Document dbObject = new Document("$group", new Document("_id", "$value").append("count", new Document("$sum", 1)));
        Aggregation aggregation = Aggregation.newAggregation(
                match(
                        criteria

                ),
                new CustomAggregationOperation(dbObject)
        );
        AggregationResults<Map> finalResult =
                mongoTemplate.aggregate(aggregation, ClientException.class, Map.class);
        return finalResult.getMappedResults();

    }


    private List<Long> getClientServicesIds(Long clientId, long orgId) {
        logger.debug("Getting Demands  ClientId:" + clientId + " UnitId: " + orgId);
        List<Long> serviceList = new ArrayList<>();
        List<Long> serviceIdList = taskService.getClientTaskServices(clientId, orgId);
        return serviceList;
    }


    /**
     * @auther anil maurya
     *
     * @param organizationId
     * @param mapList
     * @return
     */
    public Map<String, Object> getOrganizationClientsInfo(Long organizationId, List<Map<String, Object>> mapList) {

        Map<String, Object> clientData = new HashMap<String, Object>();

        List<TaskType> taskTypes = taskTypeMongoRepository.findByOrganizationIdAndIsEnabled(organizationId,true);
          clientData.put("taskTypes", taskTypes);
          clientData.put("clientList", retreiveClients(mapList, organizationId));
        return clientData;
    }


    /**
     *  @auther anil maurya
     *  call from rest template user module
     * @param mapList
     * @param organizationId
     * @return
     */
    public List<Object> retreiveClients(List<Map<String, Object>> mapList, Long organizationId) {

        List<Object> clientList = new ArrayList<>();
        for (Map<String, Object> map : mapList) {
            Map<String, Object> newClientMap = new HashMap<String, Object>();
            Map<String, Object> clientMap = (Map<String, Object>) map.get("Client");
            Long clientId = Long.parseLong(clientMap.get("id")+"");
            List<Long> timeSlotIdSets =getListOfTimeSlotIdByCitizenAndUnit(clientId, organizationId);
            newClientMap.putAll(clientMap);
            newClientMap.put("timeSlots", timeSlotIdSets);
            List<Long> citizens = new ArrayList<>();
            citizens.add(Long.parseLong(clientMap.get("id")+""));
            List<TaskTypeAggregateResult> taskTypeAggregateResults = customTaskTypeRepository.getTaskTypesOfCitizens(citizens);
            if(taskTypeAggregateResults.size() > 0){
                TaskTypeAggregateResult taskTypeAggregateResult = taskTypeAggregateResults.get(0);
                List<String> taskTypeIds = new ArrayList<>();
                taskTypeIds.addAll(taskTypeAggregateResult.getTaskTypeIds());
                newClientMap.put("taskTypeIds", taskTypeIds);

            }else{
                newClientMap.put("taskTypeIds", Collections.emptyList());
            }
            newClientMap.put("noOfExceptions", countExceptions( clientId));
            newClientMap.put("services", getClientServicesIds(clientId, organizationId));
            newClientMap.put("sumOfVisitationHoursAndTasks", clientAggregatorMongoRepository.findVisitationHoursAndTasksByCitizenIdIn( clientId, organizationId));

            clientList.add(newClientMap);
        }
        return clientList;
    }






   public TaskDemand createGrants(Map<String, Object> grantObject,Long subServiceId) throws CloneNotSupportedException {
       JSONObject grantJson = new JSONObject(grantObject);
       TaskType taskType = null;
        Integer weekDayCount=0;
        Integer weekEndCount=0;
        String visitDuration = grantObject.get("visitatedDuration").toString();;
        String pattern = (String) grantObject.get("grantPattern");
        String grantName = (String) grantObject.get("grantName");
        Integer grantId = Integer.valueOf(grantObject.get("grantId").toString());
        Long clientId = null;
        Optional clientOptional = Optional.ofNullable(grantObject.get("clientId"));
        if(clientOptional.isPresent()) clientId = Long.valueOf(grantObject.get("clientId").toString());
        Long organizationId = null;
        Optional organizationOptional = Optional.ofNullable(grantObject.get("organizationId"));
        if(organizationOptional.isPresent()) organizationId = Long.valueOf(grantObject.get("organizationId").toString());
        Long supplierId = null;
        Optional supplierOptional = Optional.ofNullable(grantObject.get("supplierId"));
       if(supplierOptional.isPresent()) supplierId = Long.valueOf(grantObject.get("supplierId").toString());

        RepetitionType shiftRepetition = JsonUtils.toObject(grantJson.get("weekDayShifts").toString(), RepetitionType.class);
        List<TaskDemandVisit> weekDayVisits = new ArrayList<TaskDemandVisit>();
        List<TaskDemandVisit> weekEndVisits = new ArrayList<TaskDemandVisit>();
        if (weekDayCount > 0 || pattern.equals("DAY")) {
            weekDayVisits = getTaskDemandVisits(shiftRepetition, organizationId, weekDayCount, visitDuration);
        }

        if (weekEndCount > 0) {
            weekEndVisits = getTaskDemandVisits(shiftRepetition, organizationId, weekEndCount, visitDuration);
        }

       //   if(grantObject.has("description") == true) taskType.setDescription(grantObject.get("description").toString());


        taskType = taskTypeMongoRepository.findByTitleAndSubServiceId(grantName, subServiceId);
        if (taskType == null) {
            taskType = new TaskType();
            taskType.setTitle(grantName);
            taskType.setSubServiceId(subServiceId);
        }
        save(taskType);

           if (taskTypeMongoRepository.findBySubServiceIdAndOrganizationIdAndIsEnabled(subServiceId,organizationId, true).isEmpty()) {
                taskTypeService.linkTaskTypesWithOrg(taskType.getId().toString(), organizationId, subServiceId);
            }

        TaskDemand taskDemand = createDemandsFromKMD(grantId, weekDayVisits, weekEndVisits, taskType, clientId, grantJson, organizationId, supplierId, pattern);
         return taskDemand;

    }

    /**
     * This method is used to create weekday and weekend visits for the demands imported from KMD Nexus
     * @param shiftRepetition
     * @param organizationId
     * @param weekDayCount
     * @param visitDuration
     * @return
     */
    List<TaskDemandVisit> getTaskDemandVisits(RepetitionType shiftRepetition, Long organizationId, Integer weekDayCount, String visitDuration) {
        List<TaskDemandVisit> visits = new ArrayList<>();
        for (Shifts shifts : shiftRepetition.getShifts()) {
            Map<String, Object> timeSlot= userIntegrationService.getTimeSlotByUnitIdAndTimeSlotName(organizationId,Long.valueOf(shifts.getId()));
            //Map<String, Object> timeSlot = timeSlotGraphRepository.getTimeSlotByUnitIdAndTimeSlotName(organizationId, shifts.getTitle());
            TaskDemandVisit weekDayVisit = new TaskDemandVisit();
            weekDayVisit.setTimeSlotId(Long.valueOf(timeSlot.get("id").toString()));
            weekDayVisit.setTimeSlotName(timeSlot.get("name").toString());
            weekDayVisit.setVisitDuration(Integer.valueOf(visitDuration));
            weekDayVisit.setVisitCount(weekDayCount);
            visits.add(weekDayVisit);
        }
        return visits;

    }
    /**
     * This method is used to create demands which imported from KMD Nexus
     * @param grantId
     * @param weekDayVisits
     * @param weekEndVisits
     * @param taskType
     * @param clientId
     * @param grantObject
     * @param organizationId
     * @param supplierId
     * @return
     */
    public TaskDemand createDemandsFromKMD(int grantId, List<TaskDemandVisit> weekDayVisits, List<TaskDemandVisit> weekEndVisits, TaskType taskType, Long clientId, JSONObject grantObject, Long organizationId, Long supplierId, String pattern) {
        Integer weekDayCount = Integer.valueOf(grantObject.get("grantWeekDays").toString());
        Integer weekEndCount = Integer.valueOf(grantObject.get("grantWeekEnds").toString());
        Integer staffCount = Integer.valueOf(grantObject.get("resourceCount").toString());
        Integer priority = Integer.valueOf(grantObject.get("priority").toString());
        Integer count = Integer.valueOf(grantObject.get("grantCount").toString());
        String description = "";
        if(grantObject.has("description") ) description = grantObject.get("description").toString();
        TaskDemand taskDemand = taskDemandMongoRepository.findByKmdExternalId(String.valueOf(grantId));
        if (taskDemand == null) taskDemand = new TaskDemand();


        taskDemand.setTaskTypeId(taskType.getId());
        taskDemand.setCitizenId(clientId);

        LocalDateTime date =  Instant.ofEpochMilli(Long.valueOf(grantObject.get("date").toString())).atZone(ZoneId.of("UTC")).toLocalDateTime();
        Date startDate = Date.from(date.atZone(ZoneId.of("UTC")).toInstant());
        taskDemand.setStartDate(startDate);
        Calendar c = Calendar.getInstance();
        c.setTime(startDate);
        c.add(Calendar.YEAR, 5);
        taskDemand.setEndDate(c.getTime());
        taskDemand.setUnitId(organizationId);
        taskDemand.setStaffCount(staffCount);
        taskDemand.setPriority(priority);
        taskDemand.setWeekdaySupplierId(supplierId);
        taskDemand.setWeekendSupplierId(supplierId);
        taskDemand.setWeekdayVisits(weekDayVisits);
        taskDemand.setRemarks(description);
        switch(pattern){
            case "WEEK":
                taskDemand.setRecurrencePattern(TaskDemand.RecurrencePattern.WEEKLY);
                taskDemand.setWeekendVisits(weekEndVisits);
                if (weekDayCount > 0) {
                    switch (count) {
                        case 1:
                            taskDemand.setWeekdayFrequency(TaskDemand.WeekFrequency.ONE_WEEK);
                            break;
                        case 2:
                            taskDemand.setWeekdayFrequency(TaskDemand.WeekFrequency.TWO_WEEK);
                            break;
                        case 3:
                            taskDemand.setWeekdayFrequency(TaskDemand.WeekFrequency.THREE_WEEK);
                            break;
                        case 4:
                            taskDemand.setWeekdayFrequency(TaskDemand.WeekFrequency.FOUR_WEEK);
                            break;
                    }

                }

                if (weekEndCount > 0) {
                    switch (count) {
                        case 1:
                            taskDemand.setWeekendFrequency(TaskDemand.WeekFrequency.ONE_WEEK);
                            break;
                        case 2:
                            taskDemand.setWeekendFrequency(TaskDemand.WeekFrequency.TWO_WEEK);
                            break;
                        case 3:
                            taskDemand.setWeekendFrequency(TaskDemand.WeekFrequency.THREE_WEEK);
                            break;
                        case 4:
                            taskDemand.setWeekendFrequency(TaskDemand.WeekFrequency.FOUR_WEEK);
                            break;
                    }

                }
                break;
            case "DAY":
                taskDemand.setRecurrencePattern(TaskDemand.RecurrencePattern.DAILY);
                taskDemand.setDailyFrequency(Long.valueOf(count));
                taskDemand.setMonthlyFrequency(null);
                break;
            case "MONTH":
                taskDemand.setDailyFrequency(Long.valueOf(null));
                MonthlyFrequency monthlyFrequency = taskDemand.getMonthlyFrequency();
                if(monthlyFrequency == null) monthlyFrequency = new MonthlyFrequency();
                monthlyFrequency.setMonthFrequency(count);
                monthlyFrequency.setWeekdayCount(weekDayCount);
                taskDemand.setMonthlyFrequency(monthlyFrequency);
                taskDemand.setRecurrencePattern(TaskDemand.RecurrencePattern.MONTHLY);
                break;
        }

        taskDemand.setKmdExternalId(String.valueOf(grantId));


        if (!taskDemand.getWeekendVisits().isEmpty() && taskDemand.getWeekendFrequency() != null) {

            logger.info("taskDemand.getWeekendVisits()  " + taskDemand.getWeekendVisits());
            for (TaskDemandVisit taskDemandVisit : taskDemand.getWeekendVisits()) {
                taskDemandVisit.setId(mongoSequenceRepository.nextSequence(TaskDemand.class.getSimpleName()));
            }
        }
        if (!taskDemand.getWeekdayVisits().isEmpty() && taskDemand.getWeekdayFrequency() != null) {
            logger.info("taskDemand.getWeekdayVisits()  " + taskDemand.getWeekdayVisits());
            for (TaskDemandVisit taskDemandVisit : taskDemand.getWeekdayVisits()) {
                taskDemandVisit.setId(mongoSequenceRepository.nextSequence(TaskDemand.class.getSimpleName()));
            }
        }
        save(taskDemand);
        return  taskDemand;
    }

    public List<TaskTypeAggregateResult> getCitizensWithFilters(Long unitId, ClientFilterDTO clientFilterDTO){

        List<String> taskTypeIdsByServiceIds = new ArrayList<String>();
        if(!clientFilterDTO.getServicesTypes().isEmpty()){
            taskTypeIdsByServiceIds = taskTypeService.getTaskTypeIdsByServiceIds(clientFilterDTO.getServicesTypes(),unitId);
        }
        return customTaskTypeRepository.getCitizenTaskTypesOfUnit(unitId,clientFilterDTO, taskTypeIdsByServiceIds);

    }

    public List<ClientExceptionType>  getCitizensExceptionTypes(){

       return clientExceptionTypeMongoRepository.findAll();

    }

}
