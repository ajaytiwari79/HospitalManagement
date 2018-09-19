package com.kairos.controller.risk_management;


import com.kairos.enums.RiskSeverity;
import com.kairos.utils.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.kairos.constants.ApiConstant.API_ORGANIZATION_COUNTRY_URL;

@RestController
@RequestMapping(API_ORGANIZATION_COUNTRY_URL)
@Api(API_ORGANIZATION_COUNTRY_URL)
public class RiskController {


    @ApiOperation(value = "get All risk Severity Level")
    @GetMapping("/risk/level")
    public ResponseEntity<Object> getRisKlevels(@PathVariable Long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, RiskSeverity.values());

    }


}
