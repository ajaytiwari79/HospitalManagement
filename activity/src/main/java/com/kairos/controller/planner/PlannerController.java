package com.kairos.controller.planner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.kairos.dto.activity.task.BulkUpdateTaskDTO;
import com.kairos.dto.activity.task.TaskDTO;
import com.kairos.dto.activity.task.TaskRestrictionDto;
import com.kairos.enums.CitizenHealthStatus;
import com.kairos.dto.planner.solverconfig.SolverConfigDTO;
import com.kairos.service.CustomTimeScaleService;
import com.kairos.service.planner.PlannerService;
import com.kairos.service.planner.TaskExceptionService;
import com.kairos.service.planner.TasksMergingService;
import com.kairos.service.planner.vrpPlanning.VRPPlanningService;
import com.kairos.service.task_type.TaskService;
import com.kairos.service.visitator.VisitatorService;
import com.kairos.utils.response.ResponseHandler;
import com.kairos.wrapper.task.TaskUpdateDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.math.BigInteger;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static com.kairos.constants.ApiConstants.API_ORGANIZATION_UNIT_URL;

/**
 * Created by oodles on 17/1/17.
 */
@RestController
@RequestMapping(API_ORGANIZATION_UNIT_URL + "/planner")
@Api(API_ORGANIZATION_UNIT_URL + "/planner")
public class PlannerController {

    private static final Logger logger = LoggerFactory.getLogger(PlannerController.class);

    @Inject
    PlannerService plannerService;

    @Inject
    CustomTimeScaleService customTimeScaleService;
    @Autowired
    TaskExceptionService taskExceptionService;
    @Autowired
    TasksMergingService tasksMergingService;
    @Autowired
    VisitatorService visitatorService;
    @Autowired
    TaskService taskService;
    @Inject private VRPPlanningService vrpPlanningService;

    @ApiOperation("Get Citizen planner data")
    @RequestMapping(value = "/citizen/{citizenId}", method = RequestMethod.GET)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getCitizenPlanning(@PathVariable long unitId, @PathVariable long citizenId, @RequestParam(value = "isActualPlanningScreen", required = false) boolean isActualPlanningScreen,
                                                                  @RequestParam(value = "startDate", required = false) String startDate) throws ParseException {

        return ResponseHandler.generateResponse(HttpStatus.OK, true, plannerService.getCitizenPlanning(unitId, citizenId, isActualPlanningScreen, startDate));

    }

    @ApiOperation("Create Tasks from TaskDemands timeslot")
    @RequestMapping(value = "/citizen/{citizenId}/create_tasks", method = RequestMethod.POST)
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> createTaskFromDemandTimeSlot(@PathVariable long unitId, @PathVariable long citizenId, @RequestBody Map<String, Object> requestPayload) throws ParseException, CloneNotSupportedException {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, plannerService.createTaskFromDemandTimeSlot(unitId, citizenId, requestPayload));

    }

    @ApiOperation("Create single task")
    @RequestMapping(value = "/citizen/{citizenId}/create_task", method = RequestMethod.POST)
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> generateIndividualTask(@PathVariable long unitId, @PathVariable long citizenId, @RequestBody List<List<Map<String, Object>>> taskData) throws ParseException, CloneNotSupportedException {

        return ResponseHandler.generateResponse(HttpStatus.OK, true, plannerService.generateIndividualTask(unitId, citizenId, taskData));

    }

    @ApiOperation("PrePlanning Task(s) Update")
    @RequestMapping(value = "/citizen/pre_planning_task_update", method = RequestMethod.PUT)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> prePlanningTaskUpdate(@PathVariable long unitId, @RequestBody TaskUpdateDTO taskData) throws ParseException, CloneNotSupportedException, JsonProcessingException {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, plannerService.prePlanningTaskUpdate(unitId, taskData));
    }

    @ApiOperation("ActualPlanning Task(s) Update")
    @RequestMapping(value = "/citizen/actual_planning_task_update", method = RequestMethod.PUT)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> actualPlanningTaskUpdate(@PathVariable long unitId, @RequestBody List<TaskUpdateDTO> taskData) throws ParseException, CloneNotSupportedException, JsonProcessingException {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, plannerService.actualPlanningTaskUpdate(unitId, taskData));
    }

    @ApiOperation("Synchronize Task in Visitour")
    @RequestMapping(value = "/citizen/{citizenId}/synchronize_task", method = RequestMethod.POST)
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> synchronizeTaskInVisitour(@PathVariable long unitId, @RequestBody Map<String, Object> payload, @PathVariable long citizenId,
                                                                         @RequestParam(value = "isActualPlanningScreen", required = false) boolean isActualPlanningScreen,
                                                                         @RequestParam(value = "startDate", required = false) String startDate) throws ParseException {

        return ResponseHandler.generateResponse(HttpStatus.OK, true, taskExceptionService.synchronizeTaskInVisitour(unitId, payload, citizenId, isActualPlanningScreen, startDate));

    }


     /*
    This was a test Api created specifically for testing task creation by citizen id
    It was required for FLS Visitour sync test of 1,10,1000 tasks.
     */
   /* @ApiOperation("Fix Schedule to confirm call")
    @RequestMapping(value = "/fixSchedule",method = RequestMethod.POST)
    ResponseEntity<Map<String, Object>> fixSchedule(@RequestBody Map<String, Object> fixSchedulePayload ) {

        return ResponseHandler.generateResponse(HttpStatus.OK, true, plannerService.fixSchedule(fixSchedulePayload ));

    }

    @ApiOperation("Optimize Tasks/Calls in Visitour")
    @RequestMapping(value = "/Optimize",method = RequestMethod.POST)
    ResponseEntity<Map<String, Object>> Optimize(@RequestBody Map<String, Object> optimizePayload  ) {

        return ResponseHandler.generateResponse(HttpStatus.OK, true, plannerService.optimize(optimizePayload ));

    }*/

    /*
    This was a test Api created specifically for testing task creation by citizen id
    It was required for FLS Visitour sync test of 1,10,1000 tasks.
     */
    /*@ApiOperation("Generate Task From Demand")
    @RequestMapping(value = "/generateTaskFromDemand",method = RequestMethod.POST)
    ResponseEntity<Map<String, Object>> generateTaskFromDemand(@PathVariable long unitId, @RequestBody Map<String, Object> payload  ) {

        return ResponseHandler.generateResponse(HttpStatus.OK, true, plannerService.generateTaskFromDemand(unitId, payload ));

    }*/

    @ApiOperation("Merge multiple tasks to single Task. Creating Main task by joining sub-tasks")
    @RequestMapping(value = "/citizen/{citizenId}/merge_multiple_tasks", method = RequestMethod.POST)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> mergeMultipleTasks(@PathVariable long unitId, @PathVariable long citizenId, @RequestBody Map<String, Object> tasksData,
                                                                  @RequestParam(value = "isActualPlanningScreen", required = false) boolean isActualPlanningScreen) throws CloneNotSupportedException {

        return ResponseHandler.generateResponse(HttpStatus.OK, true, tasksMergingService.mergeMultipleTasks(unitId, citizenId, tasksData, isActualPlanningScreen));

    }

    @ApiOperation("Unmerge task(s) from Main task.")
    @RequestMapping(value = "/citizen/{citizenId}/unmerge_multiple_tasks", method = RequestMethod.POST)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> unMergeMultipleTasks( @PathVariable long unitId, @PathVariable long citizenId, @RequestBody Map<String, Object> tasksData,
                                                                     @RequestParam(value = "isActualPlanningScreen", required = false) boolean isActualPlanningScreen) throws CloneNotSupportedException, ParseException {

        return ResponseHandler.generateResponse(HttpStatus.OK, true, tasksMergingService.unMergeMultipleTasks( unitId, citizenId, tasksData, isActualPlanningScreen));

    }

    @ApiOperation("Revert  actual planner tasks")
    @RequestMapping(value = "/citizen/{citizenId}/revert/actual_planning_task", method = RequestMethod.PUT)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> revertActualPlanningTask(@PathVariable long unitId, @RequestBody Map<String, Object> tasksToRevert) {

        List<String> taskIds = (List<String>) tasksToRevert.get("taskIds");

        return ResponseHandler.generateResponse(HttpStatus.OK, true, taskExceptionService.revertActualPlanningTask(taskIds,unitId));

    }


    @ApiOperation("Update Citizen TimeScale")
    @RequestMapping(value = "/citizen/{citizenId}/update_time_scale", method = RequestMethod.PUT)
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateCitizenTimeScale( @PathVariable long unitId, @PathVariable long citizenId, @RequestBody Map<String, Object> payload) {

        return ResponseHandler.generateResponse(HttpStatus.OK, true, customTimeScaleService.updateCitizenTimeScale( citizenId, unitId, payload));

    }

    @RequestMapping(value = "/citizen/{citizenId}/generate_tasks", method = RequestMethod.POST)
    @ApiOperation("to auto generate task for all citizens")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> autoGenerateTasks(@PathVariable long citizenId, @PathVariable long unitId) throws ParseException, CloneNotSupportedException {

        return ResponseHandler.generateResponse(HttpStatus.OK, true, plannerService.autoGenerateTasks(unitId, citizenId));
    }

    @RequestMapping(value = "/task_demands/preferred_time", method = RequestMethod.PUT)
    @ApiOperation("update task demands")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updatePreferredTimeOfDemands(@PathVariable long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.CREATED, true, visitatorService.updatePreferredTimeOfDemands(unitId));
    }

    @RequestMapping(value = "/one_time/sync", method = RequestMethod.POST)
    @ApiOperation("sync tasks")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> oneTimeSync(@PathVariable long unitId){

        boolean isOneTimeSyncPerformed = plannerService.oneTimeSync(unitId);
        if (isOneTimeSyncPerformed) {
            return ResponseHandler.generateResponse(HttpStatus.CREATED, true, true);
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, false);
    }

    @RequestMapping(value = "/citizen/{citizenId}/unhandled/actual_task/info", method = RequestMethod.PUT)
    @ApiOperation("update unhandled actual planner tasks")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateUnhandledActualPlanningTasks(@PathVariable long citizenId, @PathVariable long unitId, @RequestBody TaskDTO taskDTO){

        return ResponseHandler.generateResponse(HttpStatus.OK, true, taskExceptionService.updateUnhandledActualPlanningTasks(unitId,taskDTO));
    }

    @RequestMapping(value = "/actual_planning_task/{taskId}", method = RequestMethod.POST)
    @ApiOperation("generate a copy of single task")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> copySingleTask(@PathVariable BigInteger taskId,@RequestBody List<Map<String, Object>> taskData) throws ParseException {

        return ResponseHandler.generateResponse(HttpStatus.OK, true, taskExceptionService.copySingleTask(taskId,taskData));
    }

    @RequestMapping(value = "/actual_planning_settings", method = RequestMethod.POST)
    @ApiOperation("save settings of planner for actual planner")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> saveSettingsOfPlanner(@PathVariable long unitId,@RequestBody Map<String,Object> actualPlanningSettings) throws ParseException {
        taskExceptionService.saveSettingsOfPlanner(unitId,actualPlanningSettings);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, null);
    }

    @RequestMapping(value = "/actual_planning_tasks", method = RequestMethod.PUT)
    @ApiOperation("bulk update actual planner task")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateActualPlanningTask(@PathVariable long unitId, @RequestBody BulkUpdateTaskDTO bulkUpdateTaskDTO) throws ParseException {

        return ResponseHandler.generateResponse(HttpStatus.OK, true, taskExceptionService.updateBulkTask(unitId,bulkUpdateTaskDTO));
    }


    @RequestMapping(value = "/all_citizens/restrictions", method = RequestMethod.DELETE)
    @ApiOperation("remove all restrictions from tasks of all citizens")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> removeRestrictionsOfAllCitizens(@PathVariable long unitId,@RequestParam("restrictionValue") BigInteger id) throws ParseException {

        return ResponseHandler.generateResponse(HttpStatus.OK, true, taskExceptionService.removeRestrictionsOfAllCitizens(unitId,id));
    }

    @RequestMapping(value = "/citizen/{citizenId}/citizens/restrictions", method = RequestMethod.POST)
    @ApiOperation("remove all restrictions from tasks of selected citizens")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> removeRestrictionsOfCitizens(@PathVariable long unitId, @PathVariable long citizenId,@RequestBody List<TaskRestrictionDto> taskRestrictionDtos) throws ParseException {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, taskExceptionService.removeRestrictionsOfCitizens(unitId,citizenId,taskRestrictionDtos));
    }

    @RequestMapping(value = "/task/{taskId}/askAppointmentSuggestions", method = RequestMethod.GET)
    @ApiOperation("Ask Appointment Suggestions From Visitour")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> askAppointmentSuggestionsFromVisitour(@PathVariable long unitId, @PathVariable long taskId){
        return ResponseHandler.generateResponse(HttpStatus.OK, true, taskExceptionService.askAppointmentSuggestionsFromVisitour(unitId, taskId));
    }

    @RequestMapping(value = "/task/{taskId}/confirmAppointmentSuggestion", method = RequestMethod.POST)
    @ApiOperation("Confirm AppointmentSuggestion in Visitour")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> confirmAppointmentSuggestion(@PathVariable long unitId, @PathVariable long taskId, @RequestBody Map<String, Object> payload) throws ParseException {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, taskExceptionService.confirmAppointmentSuggestion(unitId, taskId, payload));
    }

    /**
     * @auther anil maurya
     *
     * @param citizenId
     * @return
     */
    @RequestMapping(value = "/citizen/{citizenId}/tasks", method = RequestMethod.DELETE)
    @ApiOperation("delete Task For Citizen ")
    ResponseEntity<Map<String, Object>> deleteTaskForCitizen(@PathVariable Long citizenId,
                                                             @RequestBody CitizenHealthStatus citizenHealthStatus,
                                                             @RequestParam("date") String deathDate) throws ParseException {

        logger.info("called from user micro service : delete task for citizen {}",citizenId);
        plannerService.deleteTasksForCitizen(citizenId,citizenHealthStatus,deathDate);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, true);
    }

    /**
     * @auther anil maurya
     * @param unitId
     * @param timeSlotIds
     * @return
     * @throws CloneNotSupportedException
     */
    @ApiOperation("Revert  actual planner tasks")
    @RequestMapping(value = "/citizen/filter_time_slot", method = RequestMethod.PUT)
    public ResponseEntity<Map<String, Object>> getCitizenByTimeSlotId(@PathVariable Long unitId, @RequestBody List<Long> timeSlotIds) throws CloneNotSupportedException {

        return ResponseHandler.generateResponse(HttpStatus.OK, true, plannerService.getCitizenListByTimeSlotIds(timeSlotIds, unitId));

    }

    @PostMapping(value = "/{solverConfigId}")
    @ApiOperation("submit solver config to planner")
    public ResponseEntity<Map<String, Object>> submitToPlanner(@PathVariable Long unitId,@PathVariable BigInteger solverConfigId,@RequestBody SolverConfigDTO solverConfigDTO) {
        return ResponseHandler.generateResponse(HttpStatus.CREATED, true,vrpPlanningService.submitToPlanner(unitId,solverConfigId,solverConfigDTO));
    }

    @PostMapping(value = "/resubmit")
    @ApiOperation("resubmit solver config to planner")
    public ResponseEntity<Map<String, Object>> resubmitToPlanner(@PathVariable Long unitId,@RequestBody SolverConfigDTO solverConfigDTO) {
        return ResponseHandler.generateResponse(HttpStatus.CREATED, true,vrpPlanningService.resubmitToPlanner(unitId,solverConfigDTO));
    }

    @DeleteMapping(value = "/{solverConfigId}")
    @ApiOperation("stop solver config")
    public ResponseEntity<Map<String, Object>> stopToPlannerBySolverConfig(@PathVariable Long unitId,@PathVariable BigInteger solverConfigId) {
        return ResponseHandler.generateResponse(HttpStatus.CREATED, true,vrpPlanningService.stopToPlannerBySolverConfig(unitId,solverConfigId));
    }

    @GetMapping(value = "/{solverConfigId}")
    @ApiOperation("get solution of solver config by date")
    public ResponseEntity<Map<String, Object>> getSolutionBySolverConfigByDate(@PathVariable Long unitId,@PathVariable BigInteger solverConfigId,@RequestParam(value = "date") @DateTimeFormat(pattern="yyyy-MM-dd") LocalDate date) {
        return ResponseHandler.generateResponse(HttpStatus.CREATED, true,vrpPlanningService.getSolutionBySolverConfigByDate(unitId,solverConfigId,date));
    }

    @GetMapping(value = "/solution_by_week/{solverConfigId}")
    @ApiOperation("get solution of solver config")
    public ResponseEntity<Map<String, Object>> getSolutionBySolverConfig(@PathVariable Long unitId,@PathVariable BigInteger solverConfigId) {
        return ResponseHandler.generateResponse(HttpStatus.CREATED, true,vrpPlanningService.getSolutionBySolverConfig(unitId,solverConfigId));
    }

    @PostMapping(value = "/vrp_completed/{solverConfigId}")
    @ApiOperation("update solver config")
    public ResponseEntity<Map<String, Object>> planningCompleted(@PathVariable Long unitId,@PathVariable BigInteger solverConfigId) {
        return ResponseHandler.generateResponse(HttpStatus.CREATED, true,vrpPlanningService.planningCompleted(unitId,solverConfigId));
    }



}
