package com.kairos.activity.controller.time_care;

import com.kairos.activity.persistence.model.staffing_level.TimeCareStaffingLevelDTO;
import com.kairos.activity.service.activity.ActivityService;
import com.kairos.activity.service.task_type.TaskService;
import com.kairos.activity.service.task_type.TaskTypeService;
import com.kairos.activity.util.response.ResponseHandler;
import com.kairos.activity.util.timeCareShift.GetAllActivitiesResponse;
import com.kairos.activity.util.timeCareShift.GetWorkShiftsFromWorkPlaceByIdResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import static com.kairos.activity.constants.ApiConstants.*;

/**
 * Created by oodles on 13/12/16.
 */

@RestController
@RequestMapping(API_ORGANIZATION_URL)
public class TimeCareController {

    @Inject
    private TaskTypeService taskTypeService;
    @Inject
    private TaskService taskService;
    @Inject
    private ActivityService activityService;


    private static final Logger logger = LoggerFactory.getLogger(TimeCareController.class);

    /**
     * Get Shifts data from TimeCare
     *
     * @return
     * @params
     */

    @RequestMapping(value = "/unit/{unitId}/time_care/getShifts/{controlPanelId}", method = RequestMethod.POST, consumes = {MediaType.APPLICATION_XML_VALUE})
    ResponseEntity<Map<String, Object>> getShiftsFromTimeCare(@RequestBody GetWorkShiftsFromWorkPlaceByIdResponse shifts) {

        try {
            logger.info("Executing Shifts getting from Time care count is----> " + shifts.getGetWorkShiftsFromWorkPlaceByIdResult().size());

            return ResponseHandler.generateResponse(HttpStatus.OK, false, taskService.importShiftsFromTimeCare(shifts));

        } catch (Exception exception) {
            logger.warn("Exception while hitting rest", exception);
        }
        return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false, "Received");
    }

    @RequestMapping(value = "/time_care/staffing_levels", method = RequestMethod.POST, consumes = {MediaType.APPLICATION_XML_VALUE})
    ResponseEntity<Map<String, Object>> getStaffingLevelsFromTimeCare(@RequestBody List<TimeCareStaffingLevelDTO> timeCareStaffingLevelDTOList) {
        return null;
    }

    @RequestMapping(value = ORGANIZATION_UNIT_URL + COUNTRY_URL + "/time_care/time_care_activites/presenceTimeType/{presenceTimeTypeId}/absenceTimeType/{absenceTimeTypeId}", method = RequestMethod.POST, consumes = {MediaType.APPLICATION_XML_VALUE})
    ResponseEntity<Map<String, Object>> getActivitesFromTimeCare(@RequestBody GetAllActivitiesResponse getAllActivitiesResponses,
                                                                 @PathVariable Long countryId, @PathVariable Long unitId,
                                                                 @PathVariable BigInteger presenceTimeTypeId,@PathVariable BigInteger absenceTimeTypeId) {
        logger.info("Get activities from time care " + getAllActivitiesResponses);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, activityService.createActivitiesFromTimeCare(getAllActivitiesResponses, unitId, countryId,presenceTimeTypeId,absenceTimeTypeId));
    }


}
