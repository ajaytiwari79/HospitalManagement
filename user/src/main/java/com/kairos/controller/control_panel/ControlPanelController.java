package com.kairos.controller.control_panel;
import com.kairos.persistence.model.user.control_panel.ControlPanel;
import com.kairos.service.control_panel.ControlPanelService;
import com.kairos.util.response.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Map;

import static com.kairos.constants.ApiConstants.API_CONTROL_PANEL_URL;
import static com.kairos.constants.ApiConstants.API_ORGANIZATION_UNIT_URL;

/**
 * Created by oodles on 29/12/16.
 */

@RestController
@RequestMapping(API_CONTROL_PANEL_URL)
@Api(API_ORGANIZATION_UNIT_URL)
public class ControlPanelController {

    @Inject
    private ControlPanelService controlPanelService;


    @RequestMapping(value = "/{controlPanelId}", method = RequestMethod.GET)
    @ApiOperation("Get Control Panel ")
    public ResponseEntity<Map<String, Object>> getControlPanel(@PathVariable  long controlPanelId) throws IOException {

            return ResponseHandler.generateResponse(HttpStatus.OK, true, controlPanelService.getControlPanelById(controlPanelId));

    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    @ApiOperation("Get Control Panel List ")
    public ResponseEntity<Map<String, Object>> getControlPanelList(@PathVariable  long unitId) throws IOException {

            return ResponseHandler.generateResponse(HttpStatus.OK, true, controlPanelService.getControlPanelByUnitId(unitId));

    }


    @RequestMapping(value = "", method = RequestMethod.POST)
    @ApiOperation("Create Control Panel ")
    public ResponseEntity<Map<String, Object>> addControlPanel(@RequestParam(value = "integrationConfigurationId", required = true) Long integrationConfigurationId, @PathVariable  long unitId, @RequestBody ControlPanel controlPanel) throws IOException {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, controlPanelService.createControlPanel(unitId, controlPanel, integrationConfigurationId));
    }

    @RequestMapping(value = "", method = RequestMethod.PUT)
    @ApiOperation("Update Control Panel ")
    public ResponseEntity<Map<String, Object>> updateControlPanel(@RequestBody ControlPanel controlPanel) throws IOException {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, controlPanelService.updateControlPanel(controlPanel));
    }

    @RequestMapping(value = "/jobDetails/{controlPanelId}", method = RequestMethod.GET)
    @ApiOperation("Update Control Panel ")
    public ResponseEntity<Map<String, Object>> getJobDetails(@PathVariable long controlPanelId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, controlPanelService.getJobDetails(controlPanelId));
    }

    @RequestMapping(value = "/delete/{controlPanelId}", method = RequestMethod.DELETE)
    @ApiOperation("Update Control Panel ")
    public ResponseEntity<Map<String, Object>> deleteJob(@PathVariable long controlPanelId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, controlPanelService.deleteJob(controlPanelId));
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
        return ResponseHandler.generateResponse(HttpStatus.OK, true, controlPanelService.getControlPanelData(controlPanelId));
    }


}
