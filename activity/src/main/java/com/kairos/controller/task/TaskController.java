package com.kairos.controller.task;

import com.kairos.dto.activity.task.BulDeleteTaskDTO;
import com.kairos.dto.activity.task.TaskActiveUpdationDTO;
import com.kairos.dto.user.staff.ImportShiftDTO;
import com.kairos.constants.ApiConstants;
import com.kairos.service.external_service.AuthService;
import com.kairos.service.planner.PlannerService;
import com.kairos.service.planner.TaskExceptionService;
import com.kairos.service.task_type.TaskService;
import com.kairos.utils.response.ResponseHandler;
import com.kairos.dto.planner.vrp.task.VRPTaskDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.kairos.commons.utils.DateUtils.ONLY_DATE;


/**
 * Created by prabjot on 14/10/16.
 */
@RestController
@RequestMapping(ApiConstants.API_ORGANIZATION_UNIT_URL)
@Api(value = ApiConstants.API_ORGANIZATION_UNIT_URL)
public class TaskController {

    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);

    @Inject
    TaskService taskService;


    @Inject
    private PlannerService plannerService;
    @Autowired
    TaskExceptionService taskExceptionService;
    @Autowired
    AuthService authService;

    @RequestMapping(value = "/task/", method = RequestMethod.GET)
    @ApiOperation("get All task ")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateStatus() {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, taskService.getAllTasks());
    }

    @RequestMapping(value = "/staff/{staffId}/fetchTasks", method = RequestMethod.GET)
    @ApiOperation("Fetch Staff Tasks")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    ResponseEntity<Map<String, Object>> getStaffTasks(@PathVariable Long staffId, @PathVariable Long unitId) {
        List tasks = taskService.getStaffTasks(staffId, unitId);
        return  ResponseHandler.generateResponse(HttpStatus.OK, true,
                tasks);

    }

    /**
     * This job exceutes every Sunday @ 10AM to sync tasks (Not Synced with Visitour) for upcoming fourth week.
     */
    @Scheduled(cron="0 0 10 ? * SUN")
    public void syncFourthWeekTasks() {
        LocalDateTime localDateTime =  LocalDateTime.of(LocalDateTime.now().getYear(), LocalDateTime.now().getMonth(), LocalDateTime.now().getDayOfMonth(), 0, 0).plusDays(1);
        taskService.syncFourthWeekTasks(localDateTime);
    }


    @RequestMapping(value = "/task/syncTasks", method = RequestMethod.POST)
    @ApiOperation("Fetch Staff Tasks")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public void manualSyncTasks(@RequestParam(value = "startDate") String startDateString) {
        LocalDateTime localDateTime = null;
        if(startDateString!=null && startDateString!=""){
            DateFormat dateFormat = new SimpleDateFormat(ONLY_DATE);
            try {
                Date fromDate = dateFormat.parse(startDateString);
                localDateTime = LocalDateTime.ofInstant(fromDate.toInstant(), ZoneId.systemDefault());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        taskService.syncFourthWeekTasks(localDateTime);
    }


    @RequestMapping(value = "/tasks/updateActiveStatus",method = RequestMethod.POST)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String,Object>> activeInactiveTasks(@Validated @RequestBody TaskActiveUpdationDTO taskActiveUpdationDTO, @PathVariable long unitId) throws CloneNotSupportedException {
        return ResponseHandler.generateResponse(HttpStatus.OK,true,taskExceptionService.makeTasksActiveInactive(taskActiveUpdationDTO, unitId));
    }

    @RequestMapping(value = "/tasks/delete",method = RequestMethod.POST)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String,Object>> deleteTasks(@PathVariable long unitId,@RequestBody BulDeleteTaskDTO bulDeleteTaskDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK,true,plannerService.deleteTasks(bulDeleteTaskDTO.getTaskIds(),unitId));
    }

    @RequestMapping(value = "/getStaffNotAssignedTasks",method = RequestMethod.GET)
    public ResponseEntity<Map<String,Object>> getStaffNotAssignedTasksByUnitId(@PathVariable long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK,true,taskService.getStaffNotAssignedTasksByUnitId(unitId));
    }


    /**
     * @atuher anil maurya
     *
     * move this endpoint from client controller to task controller
     *
     * @param clientId
     * @param serviceId
     * @param unitId
     * @return
     */
    @RequestMapping(value = "/client/{clientId}/service/{serviceId}/task")
    @ApiOperation("Get citizen services")
    private ResponseEntity<Map<String, Object>> getClientTaskServiceTasks(@PathVariable long clientId, @PathVariable long serviceId, @PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, taskService.getTaskByServiceId(clientId, serviceId, unitId));
    }



    /**
     *  @auther anil maurya
     *   This method is called from rest template of user micro service
     * @param clientId
     * @return
     */
    @RequestMapping(value = "/task/{clientId}/organization/{organizationId1}/service")
    @ApiOperation("Get clientTaskService services")
    private ResponseEntity<Map<String, Object>> getClientTaskServices(@PathVariable long clientId,@PathVariable Long organizationId1 ) {
        logger.info("call from user micro service clientId={}",clientId);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, taskService.getClientTaskServices(clientId, organizationId1));
    }


    /**
     *
     * @param staffId
     * @param anonymousStaffId
     * @return
     */
    @RequestMapping(value = "/task/staff/{staffId}/{anonymousStaffId}")
    @ApiOperation("updateTaskForStaff")
    public ResponseEntity<Map<String, Object>> updateTaskForStaff(@PathVariable Long staffId,@PathVariable  Long anonymousStaffId){
        logger.info("call from user micro service update task for staff={}",staffId);
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                taskService.updateTaskForStaff(staffId,anonymousStaffId));

    }

    /**
     * @auther anil maurya
     * @param staffId
     * @return
     */
    @RequestMapping(value = "/{staffId}/taskTypes", method = RequestMethod.GET)
    @ApiOperation("Get All Task types of a Staff")
    public ResponseEntity<Map<String, Object>> getStaffTaskTypes(@PathVariable long staffId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,taskService
                .getStaffTasks(staffId));
    }


    /**
     * TODO new endpoint
     * @auther anil maurya
     * this endpoint in call from citizen serviceRestClient in user micro service.
     * @param staffId
     * @param shift
     * @param unitId
     * @return
     */
    @RequestMapping(value = "/createTask/{staffId}", method = RequestMethod.POST)
    @ApiOperation("createTaskFromKMD")
    public ResponseEntity<Map<String, Object>> createTaskFromKMD(@PathVariable Long staffId, @RequestBody ImportShiftDTO shift, @PathVariable Long unitId) {
        taskService.createTaskFromKMD(staffId,shift,unitId);
        return ResponseHandler.generateResponse(HttpStatus.OK, true,true);
    }


    /**
     * @auther anil maurya
     * this endpoint  move from
     * @param filterId
     * @param unitId
     * @return
     */
    @RequestMapping(value = "/unit/{unitId}/getTasks/{filterId}", method = RequestMethod.GET)
    public String getTasks(@PathVariable Long filterId, @PathVariable Long unitId){
        authService.kmdAuth();
       taskService.getTasks(filterId,unitId);
        return "Citizen Relative Data Sync";
    }

    /**
     * @auther anil maurya
     * this endpoint will be called from staff service
     * @param unitId
     * @return
     */
    @RequestMapping(value = "/task/staff/{staffId}/assigned_tasks", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> getTasks(@PathVariable Long unitId, @PathVariable Long staffId,@RequestParam("date") String date){
        return ResponseHandler.generateResponse(HttpStatus.OK, true,taskService.getAssignedTasksOfStaff(date,staffId,unitId));
    }


    /**
     * this method will return list of unhandled tasks for current week
     * @return
     */
    @RequestMapping(value = "/client/{clientId}/current_week/unhandled_tasks", method = RequestMethod.GET)
    public ResponseEntity<Map<String,Object>> getUnhandledTasks(@PathVariable long unitId,@PathVariable long clientId){
        return ResponseHandler.generateResponse(HttpStatus.OK, true,taskService.getUnhandledTasksOfCurrentWeek(clientId,unitId));
    }


    /**
     * this method will assign logged-in user to given task
     * @return
     */
    @RequestMapping(value = "/task/{taskId}/pick_task", method = RequestMethod.GET)
    public ResponseEntity<Map<String,Object>> assignGivenTaskToUser(@PathVariable BigInteger taskId){
        return ResponseHandler.generateResponse(HttpStatus.OK, true,taskService.assignGivenTaskToUser(taskId));
    }

    @ApiOperation(value = "import Unit Task Excel File")
    @PostMapping(value = "/importTask")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> importVrpTask(@PathVariable Long unitId, @RequestBody List<VRPTaskDTO> taskDTOS) {
        return ResponseHandler.generateResponse(HttpStatus.CREATED, true,
                taskService.importTask(unitId,taskDTOS));
    }

    @ApiOperation(value = "get All VRPTask by Organization")
    @GetMapping(value = "/vrpTasks")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getTasks(@PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                taskService.getAllTask(unitId));
    }

    @ApiOperation(value = "get VRPTask by Id")
    @GetMapping(value = "/vrpTask/{taskId}")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getTask(@PathVariable Long unitId,@PathVariable BigInteger taskId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                taskService.getTask(taskId));
    }





}
