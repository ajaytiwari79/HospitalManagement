package com.kairos.service.fls_visitour.schedule;

import com.kairos.rest_client.GenericIntegrationService;
import com.kairos.dto.user.client.Client;
import com.kairos.persistence.model.task.SkillExpertise;
import com.kairos.persistence.model.task.Task;
import com.kairos.persistence.model.task.TaskStatus;
import com.kairos.persistence.repository.task_type.TaskMongoRepository;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.task_type.TaskService;
import com.kairos.commons.utils.DateUtils;
import de.tourenserver.ArrayOfFixedCall;
import de.tourenserver.FixScheduleResponse;
import de.tourenserver.FixedCall;
import io.jsonwebtoken.lang.Assert;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by oodles on 16/11/16.
 */

@Service
public class TaskConverterService {


    @Inject
    private Scheduler scheduler;
    @Inject
    TaskMongoRepository taskMongoRepository;
    @Inject
    TaskService taskService;
    @Inject
    ExceptionService exceptionService;

    @Inject
    GenericIntegrationService genericIntegrationService;

    private static final Logger logger = LoggerFactory.getLogger(TaskConverterService.class);


    /**
     * @auther maurya
     *
     * @param task
     * @param functionCode
     * @param relationExtID
     * @param flsCredentials
     */
    public void generateTask(Task task, int functionCode, String relationExtID, Map<String, String> flsCredentials) {

        /*Map<String,Object> citizenDetails=taskServiceRestClient.getCitizenDetails(task.getCitizenId());*/
       // Client client = clientGraphRepository.findById(task.getCitizenId());
        Client client = genericIntegrationService.getClient(task.getCitizenId());


        Map<String, Object> callMetaData = new HashMap<>();
        callMetaData.put("functionCode", functionCode);
        callMetaData.put("extID", task.getId());
        callMetaData.put("vtid", -1);
        callMetaData.put("customerExtID", client.getId());

        callMetaData.put("name", client.getFirstName() + " " + client.getLastName());
        callMetaData.put("name2", client.getLastName());
        if (client.getGender() != null) {
            callMetaData.put("title", (client.getGender().toString().equals("MALE") ? "Mr." : "Ms."));
        }

        callMetaData.put("phone1", client.getContactDetail() != null ? client.getContactDetail().getPrivatePhone() : "");
        callMetaData.put("callInfo1", task.getInfo1());
        callMetaData.put("callInfo2", task.getInfo2());
        callMetaData.put("teamID", task.getTeamId());


        callMetaData.put("country", task.getAddress().getCountry());
        callMetaData.put("zip", task.getAddress().getZip().toString());
        callMetaData.put("city", task.getAddress().getCity());
        callMetaData.put("street", task.getAddress().getStreet());
        callMetaData.put("hnr", task.getAddress().getHouseNumber());

        callMetaData.put("duration", task.getDuration());
        callMetaData.put("priority", task.getPriority());

        if(task.getSkillExpertiseList()!=null && !task.getSkillExpertiseList().isEmpty()){
            String skills = "";
            for(SkillExpertise skillExpertise : task.getSkillExpertiseList()){
                skills = skills + skillExpertise.getSkillVisitourId()+"("+skillExpertise.getSkillLevel()+"),";
            }
            skills = skills.substring(0,skills.length()-1);
            callMetaData.put("skills", skills);
        }else if(task.getSkills()!=null && !task.getSkills().isEmpty()){
            String skills = "";
            List<String> skillList = Arrays.asList(task.getSkills().split(","));
            for(String skill: skillList){
                skills = skills + skill+"("+2+"),";
            }
            skills = skills.substring(0,skills.length()-1);
            callMetaData.put("skills", skills);
        }

        // callMetaData.put("preferredFieldmanagerID",task.getPreferredFieldmanagerID());
        // callMetaData.put("preferredFieldmanagerID2",task.getPreferredFieldmanagerID2());
        // callMetaData.put("forbiddenFieldManagerID",task.getForbiddenFieldManagerID());
        callMetaData.put("taskTypeID", task.getVisitourTaskTypeID());
        callMetaData.put("teamID", task.getTeamId());

        if(task.getDelayPenalty() != null){
            callMetaData.put("delayPenalty", task.getDelayPenalty().value);
        }

        if(task.getExtraPenalty() != null){
            callMetaData.put("extraPenalty", task.getExtraPenalty());
        }

        if(task.getPreProcessingDuration() > 0){
            callMetaData.put("preparationtime", task.getPreProcessingDuration());
        }
        if(task.getPostProcessingDuration() > 0){
            callMetaData.put("postProcessing", task.getPostProcessingDuration());
        }
        if(task.getSetupDuration() > 0){
            callMetaData.put("setupTime", task.getSetupDuration());
        }



        String allowedFieldmanagerIDs = "";
        if (task.getPrefferedStaffIdsList() != null || task.getForbiddenStaffIdsList() != null) {

            String prefferedStaffIdsList = StringUtils.join(task.getPrefferedStaffIdsList(), ',');
            String forbiddenStaffIdsList = StringUtils.join(task.getForbiddenStaffIdsList(), ",-");


            /*String prefferedStaffIdsList = task.getPrefferedStaffIdsList().stream().map(Object::toString)
                    .collect(Collectors.joining(","));

            String forbiddenStaffIdsList = task.getForbiddenStaffIdsList().stream().map(Object::toString)
                    .collect(Collectors.joining(",-"));*/

            allowedFieldmanagerIDs = prefferedStaffIdsList;
            if (forbiddenStaffIdsList != null && !forbiddenStaffIdsList.isEmpty()) {
                allowedFieldmanagerIDs = allowedFieldmanagerIDs + ",-" + forbiddenStaffIdsList;
            }
        }
        callMetaData.put("allowedFieldmanagerIDs", allowedFieldmanagerIDs);

        if (functionCode == 0 && relationExtID != null) {
            callMetaData.put("relationExtID", relationExtID);
            callMetaData.put("relationType", 1);
        }

        Map<String, Object> timeFrameInfo = new HashMap<>();
        timeFrameInfo.put("dateFrom", (task.getDateFrom()));
        timeFrameInfo.put("dateTo", (task.getDateTo()));
        if (task.getSlaStartDuration() > 0)
            timeFrameInfo.put("timeFrom", DateUtils.getDate(task.getTimeFrom().getTime() - (task.getSlaStartDuration() * 60000)));
        else
            timeFrameInfo.put("timeFrom", task.getTimeFrom());
        timeFrameInfo.put("timeTo", task.getTimeTo());

        int vtID = 0;
        if (functionCode == 2) {
            vtID = scheduler.confirmAppointment(callMetaData, timeFrameInfo, flsCredentials);
        } else {
            vtID = scheduler.createCall(callMetaData, timeFrameInfo, flsCredentials);
        }

        if (vtID > 0) {
            logger.info("Call Created Successfully " + vtID);
            task.setVisitourId(vtID);
            task.setTaskStatus(TaskStatus.GENERATED);

            /*if(functionCode == 2){
                Map<String,Object> callInfoMetaData = new HashMap<>();
                callInfoMetaData.put("extID",task.getId());
                CallInfoRec callInfoRec = scheduler.getCallInfo(callInfoMetaData);
                if(callInfoRec.getState() == 2) {
                    task.setExecutionDate(callInfoRec.getArrival().toGregorianCalendar().getTime());
                    task.setTaskStatus(TaskStatus.CONFIRMED);
                }
            }else {
                task.setTaskStatus(TaskStatus.NOT_CONFIRMED);
                }
            taskService.save(task);
            }*/
            //taskService.save(task);

        } else {
            exceptionService.internalError("error.task.visitour.create");
        }

    }

    /**
     * @auther anil maurya
     *
     * @param tasks
     * @param flsCredentials
     */
    public void createFlsCallFromTasks(List<Task> tasks, Map<String, String> flsCredentials) {
           /*
             By Yasir
             Commented below method as we are no longer using FLS Visitour
          */

        /*if (tasks != null && tasks.size() > 0) {
            if (flsCredentials == null) {
                flsCredentials=integrationServiceRestClient.getFLS_Credentials(tasks.get(0).getUnitId());
                //flsCredentials = getFlsCredentials(tasks.get(0).getUnitId());
            }
            for (Task task : tasks) {
                generateTask(task, 0, null, flsCredentials);
                syncMultiStaffTaskInVisitour(task, flsCredentials);
            }
            taskService.save(tasks);
        }*/
    }

    private void syncMultiStaffTaskInVisitour(Task task, Map<String, String> flsCredentials) {

        if (task.getMultiStaffTask() != null && task.getMultiStaffTask() == true) {

            List<Task> relatedTaskList = taskMongoRepository.getRelatedMultiStaffTasks(task.getId().toString());

            for (Task relatedTask : relatedTaskList) {
                //logger.debug("relatedTask Id <><><>  " +relatedTask.getId());
                generateTask(relatedTask, 0, relatedTask.getRelatedTaskId(), flsCredentials);
            }
            taskService.save(relatedTaskList);
        }
    }

    public void createFlsCallFromTask(Task task, Map<String, String> flsCredentials) {
        Assert.notNull(task);
        generateTask(task, 0, null, flsCredentials);
        syncMultiStaffTaskInVisitour(task, flsCredentials);
        taskService.save(task);
    }

/*
    private Map<String, String> getFlsCredentials(long unitId) {

        HashMap<String, String> flsCredentials = new HashMap<>();
        Visitour visitour = visitourGraphRepository.findByOrganizationId(unitId);
        String url = (visitour != null) ? visitour.getServerName() : "";
        String userPass = (visitour != null) ? visitour.getUsername() + ":" + visitour.getPassword() : ":";
        flsCredentials.put("flsDefaultUrl", url);
        flsCredentials.put("userpassword", userPass);
        return flsCredentials;
    }*/

    public void updateAndConfirmTask(Task task, Map<String, String> flsCredentials) {

        logger.info("task.getId  " + task.getId());

        Map<String, Object> callMetaData = new HashMap<>();
        callMetaData.put("functionCode", 0);
        callMetaData.put("extID", task.getId());
        callMetaData.put("vtid", task.getVisitourId());
        callMetaData.put("duration", task.getDuration());
        callMetaData.put("priority", task.getPriority());
        callMetaData.put("callInfo1", task.getInfo1());
        callMetaData.put("callInfo2", task.getInfo2());

        callMetaData.put("skills", task.getSkills());
        //callMetaData.put("preferredFieldmanagerID",task.getPreferredFieldmanagerID());
        //callMetaData.put("forbiddenFieldManagerID",task.getForbiddenFieldManagerID());
        callMetaData.put("taskTypeID", task.getVisitourTaskTypeID());
        callMetaData.put("teamID", task.getTeamId());

        if (task.getPrefferedStaffIdsList() != null || task.getForbiddenStaffIdsList() != null) {

            String prefferedStaffIdsList = task.getPrefferedStaffIdsList().stream().map(Object::toString)
                    .collect(Collectors.joining(","));

            String forbiddenStaffIdsList = task.getForbiddenStaffIdsList().stream().map(Object::toString)
                    .collect(Collectors.joining(",-"));

            String allowedFieldmanagerIDs = prefferedStaffIdsList;
            if (!forbiddenStaffIdsList.isEmpty()) {
                allowedFieldmanagerIDs = allowedFieldmanagerIDs + ",-" + forbiddenStaffIdsList;
            }
            callMetaData.put("allowedFieldmanagerIDs", allowedFieldmanagerIDs);
        }


        Map<String, Object> timeFrameInfo = new HashMap<>();
        timeFrameInfo.put("dateFrom", (task.getDateFrom()));
        timeFrameInfo.put("dateTo", (task.getDateTo()));
        if (task.getSlaStartDuration() > 0)
            timeFrameInfo.put("timeFrom", DateUtils.getDate(task.getTimeFrom().getTime() - (task.getSlaStartDuration() * 60000)));
        else
            timeFrameInfo.put("timeFrom", task.getTimeFrom());
        timeFrameInfo.put("timeTo", task.getTimeTo());

        int vtID = scheduler.updateCall(callMetaData, timeFrameInfo, flsCredentials);
        if (vtID > 0) {
            logger.info("Call Updated Successfully " + vtID);
        } else {
            exceptionService.internalError("error.task.visitour.update");
        }

        //Below code is commented by Yasir, as we are not confirming calls in Visitour just after updating.
        /*Map<String,Object> confirmMetaData = new HashMap<>();
        confirmMetaData.put("functionCode",2);
        confirmMetaData.put("extID",task.getId());
        confirmMetaData.put("vtid",task.getVisitourId());
        vtID = scheduler.confirmAppointment(confirmMetaData,timeFrameInfo, flsCredentials);
        if (vtID > 0) {
            logger.info("Call Confirmed Successfully " + vtID);

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
            taskService.save(task);
        }else{
            throw new InternalError("Error while Confirming Call in Visitour");
        }*/

    }

    public void removeEngineer(Task task,Map<String,String> flsCredentials){
        Map<String, Object> datePayload = new HashMap<>();
        //datePayload.put("startDate", dateFrom);
        //datePayload.put("endDate", dateTo);
        Map<String, Object> openCall = new HashMap<>();
        openCall.put("extID", task.getId());
        openCall.put("confirmCalls", "false");
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
                if (fixedCall.getFMExtID() != null && !fixedCall.getFMExtID().trim().isEmpty()) {
                    List<Long> assingedStaffIds = Stream.of(fixedCall.getFMExtID().split(",")).map(Long::parseLong).collect(Collectors.toList());
                    task.setAssignedStaffIds(assingedStaffIds);
                } else {
                    task.setAssignedStaffIds(Collections.EMPTY_LIST);
                }
            }
        }
    }


}
