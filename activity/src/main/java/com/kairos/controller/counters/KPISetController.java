package com.kairos.controller.counters;
/*
 *Created By Pavan on 29/4/19
 *
 */

import com.kairos.dto.activity.counter.kpi_set.KPISetDTO;
import com.kairos.service.counter.KPISetService;
import com.kairos.utils.response.ResponseHandler;
import io.swagger.annotations.Api;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Map;

import static com.kairos.constants.ApiConstants.*;
import static com.kairos.dto.activity.counter.enums.ConfLevel.COUNTRY;
import static com.kairos.dto.activity.counter.enums.ConfLevel.UNIT;

@RestController
@RequestMapping(API_V1)
@Api(API_V1)
public class KPISetController {
    @Inject
    private KPISetService kpiSetService;

    @PostMapping(COUNTRY_URL+KPI_SET)
    public ResponseEntity<Map<String, Object>> createKPISet(@PathVariable Long countryId,@RequestBody @Valid  KPISetDTO kpiSetDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, kpiSetService.createKPISet(countryId,kpiSetDTO,COUNTRY));
    }

    @PutMapping(COUNTRY_URL+KPI_SET+"/{kpiSetId}")
    public ResponseEntity<Map<String, Object>> updateKPISet(@PathVariable Long countryId,@PathVariable BigInteger kpiSetId ,@RequestBody @Valid  KPISetDTO kpiSetDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, kpiSetService.updateKPISet(countryId,kpiSetDTO,COUNTRY));
    }

    @GetMapping(COUNTRY_URL+KPI_SET)
    public ResponseEntity<Map<String, Object>> getAllKPISetByCountryId(@PathVariable Long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, kpiSetService.getAllKPISetByReferenceId(countryId));
    }

    @PostMapping(UNIT_URL+KPI_SET)
    public ResponseEntity<Map<String, Object>> createKPISetAtUnit(@PathVariable Long unitId,@RequestBody @Valid  KPISetDTO kpiSetDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, kpiSetService.createKPISet(unitId,kpiSetDTO,UNIT));
    }

    @PutMapping(UNIT_URL+KPI_SET+"/{kpiSetId}")
    public ResponseEntity<Map<String, Object>> updateKPISetAtUnit(@PathVariable Long unitId,@PathVariable BigInteger kpiSetId,@RequestBody @Valid  KPISetDTO kpiSetDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, kpiSetService.updateKPISet(unitId,kpiSetDTO,UNIT));
    }


    @GetMapping(UNIT_URL+KPI_SET)
    public ResponseEntity<Map<String, Object>> getAllKPISetByUnitId(@PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, kpiSetService.getAllKPISetByReferenceId(unitId));
    }

    @DeleteMapping(KPI_SET+"/{kpiSetId}")
    public ResponseEntity<Map<String, Object>> deleteKPISet(@PathVariable BigInteger kpiSetId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, kpiSetService.deleteKPISet(kpiSetId));
    }

    @GetMapping(KPI_SET+"/{kpiSetId}")
    public ResponseEntity<Map<String, Object>> getKPISetById(@PathVariable BigInteger kpiSetId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, kpiSetService.findById(kpiSetId));
    }


    @GetMapping(UNIT_URL+"/kpi_set_calculation")
    public ResponseEntity<Map<String, Object>> createKPISetCalculation(@PathVariable Long unitId, @RequestParam  @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate , @RequestParam(required = false)  @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, kpiSetService.getKPISetCalculationData(unitId,startDate,endDate));
    }


}
