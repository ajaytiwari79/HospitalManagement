package com.kairos.scheduler.controller;


import com.kairos.dto.response.ResponseDTO;
import com.kairos.dto.scheduler.scheduler_panel.LocalDateTimeScheduledPanelIdDTO;
import com.kairos.dto.scheduler.scheduler_panel.SchedulerPanelDTO;
import com.kairos.scheduler.service.UserIntegrationService;
import com.kairos.scheduler.service.scheduler_panel.SchedulerPanelService;
import com.kairos.scheduler.utils.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.kairos.scheduler.constants.ApiConstants.API_SCHEDULER_URL;
import static com.kairos.scheduler.constants.ApiConstants.API_UNIT_SCHEDULER_URL;

@RestController
@RequestMapping
@Api
public class SchedulerPanelController {

    @Inject
    private SchedulerPanelService schedulerPanelService;
    @Inject
    private UserIntegrationService userIntegrationService;

    @GetMapping(API_UNIT_SCHEDULER_URL+"/{schedulerPanelId}")
    @ApiOperation("Get Scheduler Panel ")
    public ResponseEntity<Map<String, Object>> getSchedulerPanel(@PathVariable BigInteger schedulerPanelId) throws IOException {

        return ResponseHandler.generateResponse(HttpStatus.OK, true, schedulerPanelService.findSchedulerPanelById(schedulerPanelId));

    }

    @GetMapping(API_UNIT_SCHEDULER_URL)
    @ApiOperation("Get Control Panel List ")
    public ResponseEntity<Map<String, Object>> getSchedulerPanelList(@PathVariable Long unitId) throws IOException {

        return ResponseHandler.generateResponse(HttpStatus.OK, true, schedulerPanelService.getSchedulerPanelByUnitId(unitId));

    }


    @PostMapping(API_UNIT_SCHEDULER_URL)
    @ApiOperation("Create Scheduler Panel ")
    public ResponseEntity<Map<String, Object>> addSchedulerPanel(@PathVariable(required = false) Long unitId,
                                                                 @RequestBody List<SchedulerPanelDTO> schedulerPanelDTOs) throws IOException {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, schedulerPanelService.createSchedulerPanel(unitId, schedulerPanelDTOs));
    }

    @PutMapping(API_UNIT_SCHEDULER_URL+"/{schedulerPanelId}")
    @ApiOperation("Update Scheduler Panel ")
    public ResponseEntity<Map<String, Object>> updateSchedulerPanel(@RequestBody SchedulerPanelDTO schedulerPanelDTO, @PathVariable BigInteger schedulerPanelId) throws IOException {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, schedulerPanelService.updateSchedulerPanel(schedulerPanelDTO, schedulerPanelId));
    }

    @PutMapping(API_UNIT_SCHEDULER_URL+"/update_date_only")
    @ApiOperation("Update Scheduler Panel ")
    public ResponseEntity<Map<String, Object>> updateSchedulerPanelOneTimeTriggerDate(@RequestBody List<LocalDateTimeScheduledPanelIdDTO> localDateTimeScheduledPanelIdDTOS, @PathVariable Long unitId) throws IOException {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, schedulerPanelService.updateSchedulerPanelsOneTimeTriggerDate(localDateTimeScheduledPanelIdDTOS, unitId));
    }

    @GetMapping(API_UNIT_SCHEDULER_URL+"/job_details/{schedulerPanelId}")
    @ApiOperation("Get job details ")
    public ResponseEntity<Map<String, Object>> getJobDetails(@PathVariable BigInteger schedulerPanelId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, schedulerPanelService.getJobDetails(schedulerPanelId));
    }

    @GetMapping(API_UNIT_SCHEDULER_URL+"/job_details")
    @ApiOperation("Get job details by unitId and offset")
    public ResponseEntity<Map<String, Object>> getAllJobDetails(@PathVariable Long unitId, @RequestParam("offset") int offset) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, schedulerPanelService.getAllJobDetailsByUnitId(unitId, offset));
    }

    /*@GetMapping("/authToken")
    @ApiOperation("Get job details ")
    public ResponseEntity<Map<String, Object>> getAuthToken() {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, userIntegrationService.getAuthToken());
    }*/
    @DeleteMapping(API_UNIT_SCHEDULER_URL)
    @ApiOperation("Delete Scheduler Panel ")
    public ResponseEntity<Map<String, Object>> deleteJobs(@RequestBody Set<BigInteger> schedulerPanelIds) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, schedulerPanelService.deleteJobs(schedulerPanelIds));
    }

    @DeleteMapping(API_UNIT_SCHEDULER_URL+"/{schedulerPanelId}")
    @ApiOperation("Delete Scheduler Panel ")
    public ResponseEntity<Map<String, Object>> deleteJob(@PathVariable BigInteger schedulerPanelId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, schedulerPanelService.deleteJob(schedulerPanelId));
    }

    @GetMapping(API_UNIT_SCHEDULER_URL+"/default_data")
    @ApiOperation("Get default data")
    public ResponseEntity<Map<String, Object>> getDefaultData() {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, schedulerPanelService.getDefaultData());
    }


   /* @PostMapping("/default_data")
    @ApiOperation("Get default data")
    public ResponseEntity<Map<String, Object>> createSchedulerPanel(@PathVariable Long unitId, @RequestBody List<SchedulerPanelDTO> schedulerPanelDTOs) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, schedulerPanelService.createSchedulerPanel(unitId, schedulerPanelDTOs));
    }*/

    @PutMapping(API_UNIT_SCHEDULER_URL+"/update_scheduler_panel_by_job_sub_type")
    @ApiOperation("update scheduler panel")
    public ResponseEntity<Map<String, Object>> updateSchedulerPanel(@RequestBody SchedulerPanelDTO schedulerPanelDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, schedulerPanelService.updateSchedulerPanelByJobSubTypeAndEntityId(schedulerPanelDTO));
    }

    @DeleteMapping(API_UNIT_SCHEDULER_URL+"/delete_job_by_sub_type_and_entity_id")
    @ApiOperation("delete job by subType")
    public ResponseEntity<Map<String, Object>> deleteSchedulerPanel(@RequestBody SchedulerPanelDTO schedulerPanelDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, schedulerPanelService.deleteJobBySubTypeAndEntityId(schedulerPanelDTO));
    }


    @PostMapping(API_UNIT_SCHEDULER_URL+"time_zone")
    @ApiOperation("update time zones by unit id and time zone for Scheduler Panels ")
    public ResponseEntity<ResponseDTO<Boolean>> updateSchedularPanelsDateByUnitIdAndTimeZone(@PathVariable Long unitId, @RequestBody String timeZone)  {
        return ResponseHandler.generateResponseDTO(HttpStatus.OK, true, schedulerPanelService.updateSchedulerPanelsByUnitIdAndTimeZone(unitId, timeZone.substring(1,timeZone.length()-1)));
        }

    @PostMapping(API_SCHEDULER_URL)
    @ApiOperation("Create System Scheduler Panel ")
    public ResponseEntity<Map<String, Object>> addSystemSchedulerPanel(@RequestBody List<SchedulerPanelDTO> schedulerPanelDTOs) throws IOException {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, schedulerPanelService.createSchedulerPanel(null, schedulerPanelDTOs));
    }


    /**
     * this end point will be called from
     * activity micro service
     * @param schedulerPanelId
     * @return
     */
    //Dont Remove
    /*@RequestMapping(value = "/{schedulerPanelId}/control_panel_details", method = RequestMethod.GET)
    @ApiOperation("Get scheduler panel details ")
    public ResponseEntity<Map<String, Object>> getRequiredControlPanelDataForTask(@PathVariable BigInteger schedulerPanelId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, schedulerPanelService.getControlPanelData(schedulerPanelId));
    }
*/

}
