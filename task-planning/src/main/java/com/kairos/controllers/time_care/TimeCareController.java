package com.kairos.controllers.time_care;

import com.kairos.commons.response.ResponseHandler;
import com.kairos.services.task_type.TaskService;
import com.kairos.services.task_type.TaskTypeService;
import com.kairos.utils.external_plateform_shift.GetWorkShiftsFromWorkPlaceByIdResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

import static com.kairos.constants.ApiConstants.API_V1;

/**
 * Created by oodles on 13/12/16.
 */

@RestController
@RequestMapping(API_V1)
public class TimeCareController {

    @Inject
    private TaskTypeService taskTypeService;
    @Inject
    private TaskService taskService;


    private static final Logger logger = LoggerFactory.getLogger(TimeCareController.class);

    /**
     * Get Shifts data from TimeCare
     *
     * @return
     * @params
     */

    @RequestMapping(value = "/unit/{unitId}/time_care/getShifts/{controlPanelId}", method = RequestMethod.POST, consumes = {MediaType.APPLICATION_XML_VALUE})
    ResponseEntity<Map<String, Object>> getShiftsFromTimeCare(@RequestBody GetWorkShiftsFromWorkPlaceByIdResponse shifts) {
            logger.info("Executing Shifts getting from Time care count is----> " + shifts.getGetWorkShiftsFromWorkPlaceByIdResult().size());
            return ResponseHandler.generateResponse(HttpStatus.OK, false, taskService.importShiftsFromTimeCare(shifts));
    }

}
