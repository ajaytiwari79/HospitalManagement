package com.kairos.controller.visitator;

import com.kairos.service.visitator.VisitatorService;
import com.kairos.utils.response.ResponseHandler;
import com.kairos.wrapper.task.TaskDemandDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

import static com.kairos.constants.ApiConstants.API_ORGANIZATION_UNIT_URL;

/**
 * Created by oodles on 15/11/16.
 */
@RestController
@RequestMapping(API_ORGANIZATION_UNIT_URL+"/visitator")
@Api(API_ORGANIZATION_UNIT_URL+"/visitator")
public class VisitatorController {

    @Inject
    VisitatorService visitatorService;


    @ApiOperation("Get Services provided by a Organization Unit")
    @RequestMapping(value = "/services/{unitId}",method = RequestMethod.GET)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getServicesProvidedByUnit(@PathVariable long unitId){
        return ResponseHandler.generateResponse(HttpStatus.OK,true,
                visitatorService.getUnitProvidedServices(unitId));
    }

    @ApiOperation("Get Client Availing a Service ")
    @RequestMapping(value = "/services/clients/{serviceId}",method = RequestMethod.GET)
   // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getClientsAvailingService(@PathVariable long serviceId){
        return ResponseHandler.generateResponse(HttpStatus.OK,true,
                visitatorService.getClientAvailingOrganizationService(serviceId));
    }


//    @ApiOperation("Get schedued Task for Client")
//    @RequestMapping(value = "/services/task/client/{clientId}",method = RequestMethod.GET)
//    ResponseEntity<Map<String, Object>> getClientTasks(@PathVariable long clientId){
//        return ResponseHandler.generateResponse(HttpStatus.OK,true,
//                visitatorService.getClientAssignedTask(clientId));
//
//    }

    @ApiOperation("Get Unit Visitation Info")
    @RequestMapping(value = "/unit_visitation",method = RequestMethod.GET)
   // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getUnitVisitationInfo(@PathVariable long unitId){
        return ResponseHandler.generateResponse(HttpStatus.OK,true,
                visitatorService.getUnitVisitationInfo( unitId));

    }

    @ApiOperation("Create Task Demand")
    @RequestMapping(value = "/citizen/{citizenId}/task_demand",method = RequestMethod.POST)
   // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> createTaskDemand(@PathVariable long unitId,@PathVariable long citizenId,  @RequestBody @Validated TaskDemandDTO taskDemandDTO){

        return ResponseHandler.generateResponse(HttpStatus.CREATED,true,visitatorService.createTaskDemand(unitId,citizenId,taskDemandDTO));

    }

    @ApiOperation("Update Task Demand")
    @RequestMapping(value = "/task_demand/{taskDemandId}",method = RequestMethod.PUT)
   // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateTaskDemand(@PathVariable long unitId,@PathVariable String taskDemandId , @RequestBody @Validated TaskDemandDTO taskDemandDTO) throws CloneNotSupportedException {
        return ResponseHandler.generateResponse(HttpStatus.OK,true,
                visitatorService.updateTaskDemand(unitId,taskDemandId,taskDemandDTO));

    }

    @ApiOperation("Delete Task Demand")
    @RequestMapping(value = "/task_demand/{taskDemandId}",method = RequestMethod.DELETE)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> deleteTaskDemand(@PathVariable String taskDemandId,@PathVariable long unitId) throws ParseException, CloneNotSupportedException {

        visitatorService.deleteTaskDemand(taskDemandId, unitId);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, null);
    }

    @ApiOperation("Fetch Task Demands citizen between date range")
    @RequestMapping(value = "/task_demand/citizen/{citizenId}/filter",method = RequestMethod.GET)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> fetchTaskDemands(@PathVariable long unitId, @PathVariable long citizenId, @RequestParam Map<String,String> requestParams ){

        List<Map<String, Object>> taskDemandList = visitatorService.fetchTaskDemand(unitId,citizenId,requestParams);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, visitatorService.fetchTaskDemand(unitId,citizenId,requestParams));

    }

    @ApiOperation("Get Citizen visitation data")
    @RequestMapping(value = "/task_demand/citizen/{citizenId}/visitation_info",method = RequestMethod.GET)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getCitizenVisitation(@PathVariable long unitId, @PathVariable long citizenId  ){

        return ResponseHandler.generateResponse(HttpStatus.OK, true, visitatorService.getCitizenVisitation(unitId,citizenId));

    }

    @ApiOperation("Get Task Demands by Task Package Id")
    @RequestMapping(value = "/task_demand/task_package/{taskPackageId}",method = RequestMethod.GET)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getTaskDemandsByTaskPackageId(@PathVariable String taskPackageId){

        return ResponseHandler.generateResponse(HttpStatus.OK, true, visitatorService.getTaskDemandsByTaskPackageId(taskPackageId));

    }


}
