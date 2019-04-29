package com.kairos.controller.counters;
/*
 *Created By Pavan on 29/4/19
 *
 */

import com.kairos.dto.activity.counter.enums.ConfLevel;
import com.kairos.dto.activity.counter.kpi_set.KPISetDTO;
import com.kairos.service.counter.KPISetService;
import com.kairos.utils.response.ResponseHandler;
import io.swagger.annotations.Api;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.math.BigInteger;
import java.util.Map;

import static com.kairos.constants.ApiConstants.API_V1;
import static com.kairos.constants.ApiConstants.COUNTRY_URL;
import static com.kairos.constants.ApiConstants.UNIT_URL;

@RestController
@RequestMapping(API_V1)
@Api(API_V1)
public class KPISetController {
    @Inject
    private KPISetService kpiSetService;

    @PostMapping(COUNTRY_URL+"/kpi_set")
    public ResponseEntity<Map<String, Object>> createKPISet(@PathVariable Long countryId,@RequestBody @Valid  KPISetDTO kpiSetDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, kpiSetService.createKPISet(countryId,kpiSetDTO));
    }

    @PutMapping(COUNTRY_URL+"/kpi_set")
    public ResponseEntity<Map<String, Object>> updateKPISet(@RequestBody @Valid  KPISetDTO kpiSetDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, kpiSetService.updateKPISet(kpiSetDTO));
    }

    @DeleteMapping(COUNTRY_URL+"/kpi_set")
    public ResponseEntity<Map<String, Object>> deleteKPISet(@PathVariable BigInteger kpiSetId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, kpiSetService.deleteKPISet(kpiSetId));
    }

    @DeleteMapping(COUNTRY_URL+"/kpi_set")
    public ResponseEntity<Map<String, Object>> getAllKPISetByCountryId(@PathVariable Long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, kpiSetService.getAllKPISetByCountryId(countryId));
    }





}
