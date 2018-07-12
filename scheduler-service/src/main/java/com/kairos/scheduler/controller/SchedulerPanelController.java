package com.kairos.scheduler.controller;

import com.kairos.scheduler.service.scheduler_panel.SchedulerPanelService;
import com.kairos.scheduler.service.scheduler_panel.SchedulerPanelServiceTemp;
import com.kairos.scheduler.utils.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;

import java.io.IOException;
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
    public ResponseEntity<Map<String, Object>> getSchedulerPanel(@PathVariable long controlPanelId) throws IOException {

        return ResponseHandler.generateResponse(HttpStatus.OK, true, schedulerPanelService.getSchedulerPanelById(controlPanelId));

    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    @ApiOperation("Get Control Panel List ")
    public ResponseEntity<Map<String, Object>> getSchedulerPanelList(@PathVariable  long unitId) throws IOException {

        return ResponseHandler.generateResponse(HttpStatus.OK, true, schedulerPanelService.getSchedulerPanelByUnitId(unitId));

    }


    @RequestMapping(value = "", method = RequestMethod.POST)
    @ApiOperation("Create Control Panel ")
    public ResponseEntity<Map<String, Object>> addControlPanel(@RequestParam(value = "integrationConfigurationId", required = true) Long integrationConfigurationId, @PathVariable  long unitId, @RequestBody ControlPanel controlPanel) throws IOException {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, schedulerPanelService.createControlPanel(unitId, controlPanel, integrationConfigurationId));
    }

    @RequestMapping(value = "", method = RequestMethod.PUT)
    @ApiOperation("Update Control Panel ")
    public ResponseEntity<Map<String, Object>> updateControlPanel(@RequestBody ControlPanel controlPanel) throws IOException {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, schedulerPanelService.updateControlPanel(controlPanel));
    }

    @RequestMapping(value = "/jobDetails/{controlPanelId}", method = RequestMethod.GET)
    @ApiOperation("Update Control Panel ")
    public ResponseEntity<Map<String, Object>> getJobDetails(@PathVariable long controlPanelId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, schedulerPanelService.getJobDetails(controlPanelId));
    }

    @RequestMapping(value = "/delete/{controlPanelId}", method = RequestMethod.DELETE)
    @ApiOperation("Update Control Panel ")
    public ResponseEntity<Map<String, Object>> deleteJob(@PathVariable long controlPanelId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, schedulerPanelService.deleteJob(controlPanelId));
    }

    /**
     * this end point will be called from
     * activity micro service
     * @param controlPanelId
     * @return
     */
    @RequestMapping(value = "/{controlPanelId}/control_panel_details", method = RequestMethod.GET)
    @ApiOperation("Update Control Panel ")
    public ResponseEntity<Map<String, Object>> getRequiredControlPanelDataForTask(@PathVariable long controlPanelId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, schedulerPanelService.getSchedulerPanelData(controlPanelId));
    }





    @RequestMapping(value = "/push_to_queue", method = RequestMethod.PUT)
    @ApiOperation("Push to queue ")
    public ResponseEntity<Map<String, Object>> pushToQueue() {
        schedulerPanelService.pushToQueue();
        return ResponseHandler.generateResponse(HttpStatus.OK, true, null);
    }

}
