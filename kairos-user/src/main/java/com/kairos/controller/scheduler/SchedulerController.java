package com.kairos.controller.scheduler;

import com.kairos.service.organization.OrganizationService;
import com.kairos.util.response.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.Map;

import static com.kairos.constants.ApiConstants.API_V1;

/**
 * Created by vipul on 20/9/17.
 */
@RestController
@RequestMapping(API_V1)
@Api(API_V1)
public class SchedulerController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Inject
    OrganizationService organizationService;
    @RequestMapping(value = "/organizationWithPhases", method = RequestMethod.GET)
    @ApiOperation("Scheduler to get all organization with phases")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String,Object>> organizationWithPhases(){
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationService.organizationWithPhases());
    }


}
