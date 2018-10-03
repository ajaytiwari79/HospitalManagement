package com.kairos.scheduler.controller;

import com.kairos.dto.scheduler.scheduler_panel.LocalDateTimeIdDTO;
import com.kairos.dto.scheduler.scheduler_panel.SchedulerPanelDTO;
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
import java.util.List;
import java.util.Set;

import static com.kairos.scheduler.constants.ApiConstants.API_SCHEDULER_URL;

@RestController
@RequestMapping(API_SCHEDULER_URL)
@Api(API_SCHEDULER_URL)
public class SchedulerPanelController {

    @Inject
    private SchedulerPanelService schedulerPanelService;

    @GetMapping("/{schedulerPanelId}")
    @ApiOperation("Get Scheduler Panel ")
    public ResponseEntity<Map<String, Object>> getSchedulerPanel(@PathVariable BigInteger schedulerPanelId) throws IOException {

        return ResponseHandler.generateResponse(HttpStatus.OK, true, schedulerPanelService.findSchedulerPanelById(schedulerPanelId));

    }

    @GetMapping("")
    @ApiOperation("Get Control Panel List ")
    public ResponseEntity<Map<String, Object>> getSchedulerPanelList(@PathVariable  long unitId) throws IOException {

        return ResponseHandler.generateResponse(HttpStatus.OK, true, schedulerPanelService.getSchedulerPanelByUnitId(unitId));

    }


    @PostMapping("")
    @ApiOperation("Create Scheduler Panel ")
    public ResponseEntity<Map<String, Object>> addSchedulerPanel(@PathVariable  long unitId,
                                                                 @RequestBody List<SchedulerPanelDTO> schedulerPanelDTOs) throws IOException {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, schedulerPanelService.createSchedulerPanel(unitId, schedulerPanelDTOs));
    }

    @PutMapping("/{schedulerPanelId}")
    @ApiOperation("Update Scheduler Panel ")
    public ResponseEntity<Map<String, Object>> updateSchedulerPanel(@RequestBody SchedulerPanelDTO schedulerPanelDTO,@PathVariable BigInteger schedulerPanelId) throws IOException {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, schedulerPanelService.updateSchedulerPanel(schedulerPanelDTO,schedulerPanelId));
    }

    @PutMapping("/update_date_only")
    @ApiOperation("Update Scheduler Panel ")
    public ResponseEntity<Map<String, Object>> updateSchedulerPanelOneTimeTriggerDate(@RequestBody List<LocalDateTimeIdDTO> localDateTimeIdDTOs, @PathVariable Long unitId) throws IOException {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, schedulerPanelService.updateSchedulerPanelsOneTimeTriggerDate(localDateTimeIdDTOs,unitId));
    }

    @GetMapping("/job_details/{schedulerPanelId}")
    @ApiOperation("Get job details ")
    public ResponseEntity<Map<String, Object>> getJobDetails(@PathVariable BigInteger schedulerPanelId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, schedulerPanelService.getJobDetails(schedulerPanelId));
    }

    @GetMapping("/job_details")
    @ApiOperation("Get job details by unitId and offset")
    public ResponseEntity<Map<String, Object>> getAllJobDetails(@PathVariable Long unitId,@RequestParam("offset") int offset) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, schedulerPanelService.getAllJobDetailsByUnitId(unitId,offset));
    }
    @DeleteMapping("")
    @ApiOperation("Delete Scheduler Panel ")
    public ResponseEntity<Map<String, Object>> deleteJob(@RequestBody Set<BigInteger> schedulerPanelIds) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, schedulerPanelService.deleteJob(schedulerPanelIds));
    }

    @GetMapping("/default_data")
    @ApiOperation("Get default data")
    public ResponseEntity<Map<String, Object>> getDefaultData() {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, schedulerPanelService.getDefaultData());
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
