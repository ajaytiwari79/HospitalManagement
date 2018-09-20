package com.kairos.controller.restrcition_freuency;

import com.kairos.persistence.model.restrcition_freuency.RestrictionFrequency;
import com.kairos.service.restrcition_freuency.RestrictionFrequencyService;
import com.kairos.utils.response.ResponseHandler;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.Map;

import static com.kairos.constants.ApiConstants.API_ORGANIZATION_UNIT_URL;

/**
 * Created by prabjot on 15/9/17.
 */
@RequestMapping(API_ORGANIZATION_UNIT_URL + "/restriction_frequency")
@RestController
public class RestrictionFrequencyController {

    @Inject
    RestrictionFrequencyService restrictionFrequencyService;

    @ApiOperation("Save restrcition frequency")
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> saveRestrictionFrequency(@RequestBody RestrictionFrequency restrictionFrequency) {

        return ResponseHandler.generateResponse(HttpStatus.OK, true, restrictionFrequencyService.saveRestrictionFrequency(restrictionFrequency));

    }

    @ApiOperation("Save restriction frequency")
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> getRestrictionFrequency() {

        return ResponseHandler.generateResponse(HttpStatus.OK, true, restrictionFrequencyService.getRestrictionFrequency());

    }



}
