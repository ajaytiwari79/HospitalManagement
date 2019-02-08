package com.kairos.controller.time_care;

import com.kairos.persistence.model.staffing_level.TimeCareStaffingLevelDTO;
import com.kairos.service.activity.ActivityService;
import com.kairos.service.task_type.TaskService;
import com.kairos.service.task_type.TaskTypeService;
import com.kairos.utils.response.ResponseHandler;
import com.kairos.utils.external_plateform_shift.GetAllActivitiesResponse;
import com.kairos.utils.external_plateform_shift.GetWorkShiftsFromWorkPlaceByIdResponse;
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

import static com.kairos.constants.ApiConstants.*;

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
            logger.info("Executing Shifts getting from Time care count is----> " + shifts.getGetWorkShiftsFromWorkPlaceByIdResult().size());
            return ResponseHandler.generateResponse(HttpStatus.OK, false, taskService.importShiftsFromTimeCare(shifts));
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
