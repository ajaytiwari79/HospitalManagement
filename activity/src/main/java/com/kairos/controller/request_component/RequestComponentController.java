package com.kairos.controller.request_component;

import com.kairos.activity.response.RequestComponent;
import com.kairos.enums.RequestType;
import com.kairos.service.request_component.RequestComponentService;
import com.kairos.util.response.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.Map;

import static com.kairos.constants.ApiConstants.API_REQUEST_COMPONENT_URL;


/**
 * Created by oodles on 22/8/17.
 */
@RestController
@RequestMapping(API_REQUEST_COMPONENT_URL)
@Api(API_REQUEST_COMPONENT_URL)
public class RequestComponentController {

    @Inject
    private RequestComponentService requestComponentService;

    /**
     * Create a new request
     *
     */
    @ApiOperation(value = "create a new request")
    @RequestMapping(method = RequestMethod.POST)
    ResponseEntity<Map<String, Object>> createRequest(@PathVariable Long organizationId, @RequestBody RequestComponent requestComponent) {

        RequestComponent requestComponent1 = requestComponentService.createRequest(organizationId, requestComponent);
        if (requestComponent1 != null) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true, "Success");
        }
        return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false, null);

    }

    /**
     * Create a new request
     *
     */
    @ApiOperation(value = "fetch request utilities")
    @RequestMapping(value = "/types", method = RequestMethod.GET)
    ResponseEntity<Map<String, Object>> fetchRequestTypes() {

        RequestType[] requestTypes = requestComponentService.fetchRequestTypes();
        if (requestTypes != null) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true, requestTypes);
        }
        return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false, null);

    }

    }
