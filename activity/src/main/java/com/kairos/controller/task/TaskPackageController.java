package com.kairos.controller.task;
import com.kairos.persistence.model.task.TaskPackage;
import com.kairos.persistence.model.task_demand.TaskDemand;
import com.kairos.service.task_type.TaskPackageService;
import com.kairos.constants.ApiConstants;
import com.kairos.utils.response.ResponseHandler;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Created by prabjot on 16/11/16.
 */
@RestController
@RequestMapping(ApiConstants.API_ORGANIZATION_UNIT_URL+"/task_package" )
@Api(value = ApiConstants.API_ORGANIZATION_UNIT_URL+"/task_package")
public class TaskPackageController {

    @Autowired
    TaskPackageService taskPackageService;

    @RequestMapping(method = RequestMethod.POST)
    ResponseEntity<Map<String, Object>> createTaskPackage(@PathVariable long unitId,@RequestBody TaskPackage taskPackage){
        taskPackage.setUnitId(unitId);
        taskPackage = taskPackageService.createTaskPackage(taskPackage);
        return ResponseHandler.generateResponse(HttpStatus.CREATED,true,taskPackage);
    }

    @RequestMapping(method = RequestMethod.GET)
    ResponseEntity<Map<String, Object>> getTaskPackages(@PathVariable long unitId){
        return ResponseHandler.generateResponse(HttpStatus.OK
                ,true,taskPackageService.getTaskPackages(unitId));
    }

    @RequestMapping(value = "/task_demand/task_package/{packageId}",method = RequestMethod.POST)
    ResponseEntity<Map<String, Object>> createTaskDemandByPackage(@PathVariable String packageId,@RequestBody Map<String,Object> reqData){
        long staffId = Long.parseLong((String) reqData.get("staffId"));
        long clientId = Long.parseLong((String) reqData.get("clientId"));
        List<TaskDemand> taskDemands = taskPackageService.createTaskDemandByPackage(packageId,clientId,staffId);
        if(taskDemands == null){
            return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST
                    ,false,taskDemands);
        }
        return ResponseHandler.generateResponse(HttpStatus.OK
                ,true,taskDemands);
    }

    @RequestMapping(value = "/{packageId}",method = RequestMethod.DELETE)
    ResponseEntity<Map<String,Object>> deleteTaskPackage(@PathVariable String packageId){
        return ResponseHandler.generateResponse(HttpStatus.OK
                ,true,taskPackageService.deleteTaskPackage(packageId));
    }


}
