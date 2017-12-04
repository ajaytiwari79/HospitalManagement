package com.kairos.controller.country.feature;

import com.kairos.util.response.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static com.kairos.constants.ApiConstants.API_ORGANIZATION_URL;
import static com.kairos.constants.ApiConstants.COUNTRY_URL;

/**
 * Created by prerna on 4/12/17.
 */
@RestController

@RequestMapping(API_ORGANIZATION_URL)
@Api(API_ORGANIZATION_URL)
public class FeatureController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /*@ApiOperation(value = "Create a New Tag in Country")
    @RequestMapping(value = COUNTRY_URL + "/tag", method = RequestMethod.POST)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> addCountryTag(@Validated @RequestBody TagDTO tagDTO, @PathVariable long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.CREATED, true,tagService.addCountryTag(countryId,tagDTO));
    }
}*/
