package com.kairos.controller.organization;

import com.kairos.service.organization.OrganizationService;
import com.kairos.util.response.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.Map;

import static com.kairos.constants.ApiConstants.API_V1;

/**
 * Created by vipul on 27/9/17.
 */
@RestController
@RequestMapping(API_V1)
@Api(API_V1)
public class OrganizationDataController {
    @Inject
    OrganizationService organizationService;
    @RequestMapping(value = "/allOrganizationIds",method = RequestMethod.GET)
    @ApiOperation("init controller to get all organization Ids when application Loads")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String,Object>> allOrganizationIds(){
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationService.allOrganizationIds());
    }


}
