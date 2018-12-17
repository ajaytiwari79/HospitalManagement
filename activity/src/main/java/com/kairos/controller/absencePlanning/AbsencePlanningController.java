package com.kairos.controller.absencePlanning;

import com.kairos.commons.service.mail.MailService;
import com.kairos.dto.activity.task.TaskDTO;
import com.kairos.service.absencePlanning.AbsencePlanningService;
import com.kairos.utils.response.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

import static com.kairos.constants.ApiConstants.API_ABSENCE_PLANNING_URL;


/**
 * Created by oodles on 27/1/17.
 */
@RestController
@RequestMapping(API_ABSENCE_PLANNING_URL)
@Api(value = API_ABSENCE_PLANNING_URL)
public class AbsencePlanningController {

    @Inject
    private AbsencePlanningService absencePlanningService;


    @Inject
    MailService mailService;

    private static final Logger logger = LoggerFactory.getLogger(AbsencePlanningController.class);

    @RequestMapping(value = "/tab/{tab}", method = RequestMethod.GET)
    @ApiOperation("fetch absence_planning data")
    public ResponseEntity<Map<String, Object>> getAbsencePlanningData(@PathVariable long unitId, @PathVariable String tab) {
        //absencePlanningService.checkDailyAbsencePlanningData();
        Map<String, Object> response = absencePlanningService.getAbsencePlanningData(unitId, tab);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, response);
    }


    @ApiOperation("Update Task(s)")
    @RequestMapping(value = "/update_task",method = RequestMethod.PUT)
    ResponseEntity<Map<String, Object>> updateTask(@PathVariable long unitId, @RequestBody List<TaskDTO> taskData  )  {

        return ResponseHandler.generateResponse(HttpStatus.OK, true, absencePlanningService.updateTask(unitId,taskData));

    }

    @ApiOperation("Sync Partial Absences With FlS")
    @RequestMapping(value = "/syncPartialAbsencesWithFLS",method = RequestMethod.POST)
    ResponseEntity<Map<String, Object>> syncPartialAbsencesWithFLS(@PathVariable long unitId, @RequestBody List<TaskDTO> taskData  )  {

        return ResponseHandler.generateResponse(HttpStatus.OK, true, absencePlanningService.syncPartialAbsencesWithFLS(unitId,taskData));

    }

    @ApiOperation("Sync Present and Full day Absences With FlS")
    @RequestMapping(value = "/syncPresentFullDayAbsencesWithFLS",method = RequestMethod.POST)
    ResponseEntity<Map<String, Object>> syncPresentFullDayAbsencesWithFLS(@PathVariable long unitId, @RequestBody List<TaskDTO> taskData  )  {

        return ResponseHandler.generateResponse(HttpStatus.OK, true, absencePlanningService.syncPresentFullDayAbsencesWithFLS(unitId,taskData));

    }

    @ApiOperation("Get Common data required for Staff generation and Absence Planning view")
    @RequestMapping(value = "/getCommonData",method = RequestMethod.GET)
    ResponseEntity<Map<String, Object>> getCommonAbsencePlanningData(@PathVariable long unitId)  {
        // absencePlanningService.sendDataToFLSJob();
        return ResponseHandler.generateResponse(HttpStatus.OK, true, absencePlanningService.getCommonDataOfOrganization(unitId));

    }


    @ApiOperation("Get Common data required for Staff generation and Absence Planning view")
    @RequestMapping(value = "/getCommonData1",method = RequestMethod.GET)
    ResponseEntity<Map<String, Object>> getCommonAbsencePlanningData1(@PathVariable long unitId)  {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, absencePlanningService.getCommonDataOfOrganization(unitId));

    }


    @ApiOperation("Sync All Absences With FlS")
    @RequestMapping(value = "/syncAllAbsencesWithFLS",method = RequestMethod.POST)
    ResponseEntity<Map<String, Object>> syncAllAbsencesWithFLS(@PathVariable long unitId, @RequestBody List<TaskDTO> taskData  )  {

        return ResponseHandler.generateResponse(HttpStatus.OK, true, absencePlanningService.syncAllAbsencesWithFLS(unitId,taskData));

    }


}

