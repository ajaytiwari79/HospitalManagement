package com.kairos.controller.risk_management;


import com.kairos.dto.response.ResponseDTO;
import com.kairos.enums.RiskSeverity;
import com.kairos.response.dto.common.RiskResponseDTO;
import com.kairos.service.risk_management.RiskService;
import com.kairos.utils.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;

import java.util.List;

import static com.kairos.constants.ApiConstant.*;

@RestController
@RequestMapping(API_ORGANIZATION_URL)
@Api(API_ORGANIZATION_URL)
public class RiskController {


    @Inject
    private RiskService riskService;


    @ApiOperation(value = "get All risk Severity Level")
    @GetMapping("/risk/level")
    public ResponseEntity<Object> getRiskLevels() {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, RiskSeverity.values());

    }


    @ApiOperation(value = "get All risk of Level")
    @GetMapping(UNIT_URL + "/risk")
    public ResponseEntity<ResponseDTO<List<RiskResponseDTO>>> getAllRiskOfUnit(@PathVariable Long unitId) {
        return ResponseHandler.generateResponseDTO(HttpStatus.OK, true, riskService.getAllRiskByUnitId(unitId));
    }


}
