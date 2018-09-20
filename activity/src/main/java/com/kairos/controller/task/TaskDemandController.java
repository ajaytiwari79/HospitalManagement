package com.kairos.controller.task;

import com.kairos.constants.ApiConstants;
import com.kairos.persistence.model.task_demand.TaskDemand;
import com.kairos.service.task_type.TaskDemandService;
import com.kairos.dto.user.staff.client.ClientFilterDTO;
import com.kairos.utils.response.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

/**
 * Created by prabjot on 2/11/16.
 */
@RestController
@RequestMapping(ApiConstants.API_V1 + "/task_demand")
@Api(value = ApiConstants.API_V1 + "/task_demand")
public class TaskDemandController {

    @Inject
    TaskDemandService taskDemandService;


    @RequestMapping(method = RequestMethod.POST)
    @ApiOperation("create task demand")
    public ResponseEntity<Map<String, Object>> createTaskDemand(@RequestBody Map<String,Object> reqData) throws ParseException {
        // validate task
        TaskDemand response = taskDemandService.createTaskDemand(reqData);
        if (response == null) {
            return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false, response);
        }
        return ResponseHandler.generateResponse(HttpStatus.CREATED, true, response);
    }

    /**
     * @auther anil maurya
     *
     * @param
     * @return
     */
    @RequestMapping(value ="/unit/{unitId}",method = RequestMethod.GET)
    @ApiOperation("getTaskTypes of unit")
    public ResponseEntity<Map<String, Object>> getTaskTypesOfUnit(@PathVariable Long unitId){
        return ResponseHandler.generateResponse(HttpStatus.OK, true, taskDemandService.getTaskTypesOfUnit(unitId));
    }

    /**
     * @auther anil maurya
     *
     * @param
     * @return
     */
    @RequestMapping(value ="/citizen/task_types",method = RequestMethod.POST)
    @ApiOperation("getTaskTypes of citizens")
    public ResponseEntity<Map<String, Object>> getTaskTypesOfCitizens(@RequestBody List<Long> citizenIds){
        return ResponseHandler.generateResponse(HttpStatus.OK, true, taskDemandService.getTaskTypesOfCitizens(citizenIds));
    }

    /**
     * @auther anil maurya
     * This endpoint called from user micro service via rest template
     * @param organizationId
     * @param staffId
     * @param mapList
     * @return
     */
    @RequestMapping(value ="/organization/{organizationId}/{staffId}",method = RequestMethod.POST)
    @ApiOperation("getOrganizationClientsWithPlanning")
    public ResponseEntity<Map<String, Object>> getOrganizationClientsWithPlanning(@PathVariable  Long organizationId,@PathVariable Long staffId,@RequestBody  List<Map<String, Object>> mapList) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                taskDemandService.getOrganizationClientsWithPlanning(organizationId,staffId,mapList));
    }

    /**
     * @auther anil maurya
     * This endpoint called from user micro service via rest template
     * @param organizationId
     *
     * @param mapList
     * @return
     */
    @RequestMapping(value ="/organization/{organizationId}",method = RequestMethod.POST)
    @ApiOperation("getOrganizationClientsInfo")
    public ResponseEntity<Map<String, Object>> getOrganizationClientsInfo(@PathVariable  Long organizationId,@RequestBody  List<Map<String, Object>> mapList) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                taskDemandService.getOrganizationClientsInfo(organizationId,mapList));
    }

    /**
     * @auther anil maurya
     * This endpoint called from user micro service via rest template
     *
     * @return
     */
    @RequestMapping(value ="/organization/{organizationId}/service/{serviceId}",method = RequestMethod.POST)
    @ApiOperation("getOrganizationClientsInfo")
    public ResponseEntity<Map<String, Object>> createTaskDemandsOfKMD(@PathVariable  Long serviceId,@RequestBody Map<String, Object> grantObject) throws CloneNotSupportedException {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                taskDemandService.createGrants(grantObject,serviceId));
    }

    /**
     * @auther anil maurya
     * This endpoint called from user micro service via rest template
     * @param organizationId
     * @return
     */
    @RequestMapping(value ="/organization/{organizationId}/getCitizensByTaskTypeIds",method = RequestMethod.POST)
    @ApiOperation("getOrganizationClientsInfo")
    public ResponseEntity<Map<String, Object>> getCitizensByTaskTypeIds(@PathVariable  Long organizationId,@RequestBody ClientFilterDTO clientFilterDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                taskDemandService.getCitizensWithFilters(organizationId,clientFilterDTO));
    }

    /**
     * @auther anil maurya
     * This endpoint called from user micro service via rest template
     * @return
     */
    @RequestMapping(value ="/organization/{organizationId}/getCitizensExceptionTypes",method = RequestMethod.POST)
    @ApiOperation("getOrganizationClientsInfo")
    public ResponseEntity<Map<String, Object>> getCitizensExceptionTypes() {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                taskDemandService.getCitizensExceptionTypes());
    }
}
