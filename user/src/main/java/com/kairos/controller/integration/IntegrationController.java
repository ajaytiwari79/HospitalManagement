package com.kairos.controller.integration;

import com.kairos.persistence.model.user.integration.TimeCare;
import com.kairos.persistence.model.user.integration.Twillio;
import com.kairos.persistence.model.user.integration.Visitour;
import com.kairos.service.integration.IntegrationService;
import com.kairos.utils.response.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

import static com.kairos.constants.ApiConstants.API_INTEGRATION_URL;

/**
 * Created by oodles on 21/2/17.
 */
@RestController
@RequestMapping(API_INTEGRATION_URL)
@Api(value = API_INTEGRATION_URL)
public class IntegrationController {
    @Inject
    private IntegrationService integrationService;


    @ApiOperation("Save/Update time care integration")
    @RequestMapping(value = "/timeCare",method = RequestMethod.POST)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> saveTimeCareIntegrationData(@PathVariable long unitId, @RequestBody TimeCare timeCare  )  {

        return ResponseHandler.generateResponse(HttpStatus.OK, true, integrationService.saveTimeCareIntegrationData(unitId,timeCare));

    }

    @ApiOperation("fetch time care integration")
    @RequestMapping(value = "/timeCare",method = RequestMethod.GET)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> fetchTimeCareIntegrationData(@PathVariable long unitId  )  {

        return ResponseHandler.generateResponse(HttpStatus.OK, true, integrationService.fetchTimeCareIntegrationData(unitId));

    }

    @ApiOperation("Save/Update twillio integration")
    @RequestMapping(value = "/twillio",method = RequestMethod.POST)
   // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> saveTwillioIntegrationData(@PathVariable long unitId, @RequestBody Twillio twillio  )  {

        return ResponseHandler.generateResponse(HttpStatus.OK, true, integrationService.saveTwillioIntegrationData(unitId,twillio));

    }

    @ApiOperation("fetch twillio integration")
    @RequestMapping(value = "/twillio",method = RequestMethod.GET)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> fetchTwillioIntegrationData(@PathVariable long unitId  )  {

        return ResponseHandler.generateResponse(HttpStatus.OK, true, integrationService.fetchTwillioIntegrationData(unitId));

    }

    @ApiOperation("Save/Update time care integration")
    @RequestMapping(value = "/visitour",method = RequestMethod.POST)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> saveVisitourIntegrationData(@PathVariable long unitId, @RequestBody Visitour visitour  )  {

        return ResponseHandler.generateResponse(HttpStatus.OK, true, integrationService.saveVisitourIntegrationData(unitId,visitour));

    }

    @ApiOperation("fetch time care integration")
    @RequestMapping(value = "/visitour",method = RequestMethod.GET)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> fetchVisitourIntegrationData(@PathVariable long unitId  )  {

        return ResponseHandler.generateResponse(HttpStatus.OK, true, integrationService.fetchVisitourIntegrationData(unitId));

    }

    /**
     * @auther anil maurya
     * call this endpoints via task micro service
     * @param citizenUnitId
     * @return
     */

    @ApiOperation("fetch FLS_Credentials ")
    @RequestMapping(value = "/unit/{citizenUnitId}/flsCred",method = RequestMethod.GET)
    ResponseEntity<Map<String, Object>> getFLSCredentials(@PathVariable Long citizenUnitId)  {

        return ResponseHandler.generateResponse(HttpStatus.OK, true, integrationService.getFLS_Credentials(citizenUnitId));

    }

    @ApiOperation("fetch FLS_Credentials ")
    @RequestMapping(value = "/units/flsCred",method = RequestMethod.POST)
    ResponseEntity<Map<String, Object>> getFLSCredentials(@RequestBody List<Long> unitIds)  {

        return ResponseHandler.generateResponse(HttpStatus.OK, true, integrationService.getFLSCredentials(unitIds));

    }
}
