package com.kairos.scheduler.controller;

import com.kairos.dto.SchedulerPanelDTO;
import com.kairos.scheduler.persistence.model.scheduler_panel.SchedulerPanel;
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
import java.util.Map;

import static com.kairos.scheduler.constants.ApiConstants.API_SCHEDULER_URL;

@RestController
@RequestMapping(API_SCHEDULER_URL)
@Api(API_SCHEDULER_URL)
public class SchedulerPanelController {

    @Inject
    private SchedulerPanelService schedulerPanelService;

    @RequestMapping(value = "/{schedulerPanelId}", method = RequestMethod.GET)
    @ApiOperation("Get Scheduler Panel ")
    public ResponseEntity<Map<String, Object>> getSchedulerPanel(@PathVariable BigInteger controlPanelId) throws IOException {

        return ResponseHandler.generateResponse(HttpStatus.OK, true, schedulerPanelService.findSchedulerPanelById(controlPanelId));

    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    @ApiOperation("Get Control Panel List ")
    public ResponseEntity<Map<String, Object>> getSchedulerPanelList(@PathVariable  long unitId) throws IOException {

        return ResponseHandler.generateResponse(HttpStatus.OK, true, schedulerPanelService.getSchedulerPanelByUnitId(unitId));

    }


    @RequestMapping(value = "", method = RequestMethod.POST)
    @ApiOperation("Create Scheduler Panel ")
    public ResponseEntity<Map<String, Object>> addSchedulerPanel(@RequestParam(value = "integrationConfigurationId", required = false) BigInteger integrationConfigurationId, @PathVariable  long unitId, @RequestBody SchedulerPanelDTO schedulerPanelDTO) throws IOException {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, schedulerPanelService.createSchedulerPanel(unitId, schedulerPanelDTO, integrationConfigurationId));
    }

    @RequestMapping(value = "/{schedulerPanelId}", method = RequestMethod.PUT)
    @ApiOperation("Update Scheduler Panel ")
    public ResponseEntity<Map<String, Object>> updateSchedulerPanel(@RequestBody SchedulerPanelDTO schedulerPanelDTO,@PathVariable BigInteger schedulerPanelId) throws IOException {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, schedulerPanelService.updateSchedulerPanel(schedulerPanelDTO,schedulerPanelId));
    }

    @RequestMapping(value = "/jobDetails/{schedulerPanelId}", method = RequestMethod.GET)
    @ApiOperation("Get job details ")
    public ResponseEntity<Map<String, Object>> getJobDetails(@PathVariable BigInteger schedulerPanelId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, schedulerPanelService.getJobDetails(schedulerPanelId));
    }

    @RequestMapping(value = "/delete/{schedulerPanelId}", method = RequestMethod.DELETE)
    @ApiOperation("Delete Scheduler Panel ")
    public ResponseEntity<Map<String, Object>> deleteJob(@PathVariable BigInteger schedulerPanelId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, schedulerPanelService.deleteJob(schedulerPanelId));
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
