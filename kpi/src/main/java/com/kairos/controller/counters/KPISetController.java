package com.kairos.controller.counters;
/*
 *Created By Pavan on 29/4/19
 *
 */

import com.kairos.commons.response.ResponseHandler;
import com.kairos.constants.ApiConstants;
import com.kairos.dto.TranslationInfo;
import com.kairos.dto.activity.counter.enums.ConfLevel;
import com.kairos.dto.activity.counter.kpi_set.KPISetDTO;
import com.kairos.dto.response.ResponseDTO;
import com.kairos.service.counter.KPISetService;
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
@RequestMapping(ApiConstants.API_V1)
@Api(ApiConstants.API_V1)
public class KPISetController {
    @Inject
    private KPISetService kpiSetService;

    @PostMapping(ApiConstants.COUNTRY_URL+ ApiConstants.KPI_SET)
    public ResponseEntity<Map<String, Object>> createKPISet(@PathVariable Long countryId,@RequestBody @Valid  KPISetDTO kpiSetDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, kpiSetService.createKPISet(countryId,kpiSetDTO, ConfLevel.COUNTRY));
    }

    @PutMapping(ApiConstants.COUNTRY_URL+ ApiConstants.KPI_SET+"/{kpiSetId}")
    public ResponseEntity<Map<String, Object>> updateKPISet(@PathVariable Long countryId,@PathVariable BigInteger kpiSetId ,@RequestBody @Valid  KPISetDTO kpiSetDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, kpiSetService.updateKPISet(countryId,kpiSetDTO, ConfLevel.COUNTRY));
    }

    @GetMapping(ApiConstants.COUNTRY_URL+ ApiConstants.KPI_SET)
    public ResponseEntity<Map<String, Object>> getAllKPISetByCountryId(@PathVariable Long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, kpiSetService.getAllKPISetByReferenceId(countryId));
    }

    @PostMapping(ApiConstants.UNIT_URL+ ApiConstants.KPI_SET)
    public ResponseEntity<Map<String, Object>> createKPISetAtUnit(@PathVariable Long unitId,@RequestBody @Valid  KPISetDTO kpiSetDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, kpiSetService.createKPISet(unitId,kpiSetDTO, ConfLevel.UNIT));
    }

    @PutMapping(ApiConstants.UNIT_URL+ ApiConstants.KPI_SET+"/{kpiSetId}")
    public ResponseEntity<Map<String, Object>> updateKPISetAtUnit(@PathVariable Long unitId,@PathVariable BigInteger kpiSetId,@RequestBody @Valid  KPISetDTO kpiSetDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, kpiSetService.updateKPISet(unitId,kpiSetDTO, ConfLevel.UNIT));
    }


    @GetMapping(ApiConstants.UNIT_URL+ ApiConstants.KPI_SET)
    public ResponseEntity<Map<String, Object>> getAllKPISetByUnitId(@PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, kpiSetService.getAllKPISetByReferenceId(unitId));
    }

    @DeleteMapping(ApiConstants.KPI_SET+"/{kpiSetId}")
    public ResponseEntity<Map<String, Object>> deleteKPISet(@PathVariable BigInteger kpiSetId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, kpiSetService.deleteKPISet(kpiSetId));
    }

    @GetMapping(ApiConstants.KPI_SET+"/{kpiSetId}")
    public ResponseEntity<Map<String, Object>> getKPISetById(@PathVariable BigInteger kpiSetId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, kpiSetService.findById(kpiSetId));
    }


    @GetMapping(ApiConstants.UNIT_URL+"/kpi_set_calculation")
    public ResponseEntity<Map<String, Object>> createKPISetCalculation(@PathVariable Long unitId, @RequestParam  @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate , @RequestParam(required = false)  @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, kpiSetService.getKPISetCalculationData(unitId,startDate,endDate));
    }

    @PutMapping(ApiConstants.UNIT_URL+ ApiConstants.KPI_SET+"/{kpiSetId}/language_settings")
    public ResponseEntity<Map<String, Object>> updateTranslationDataOfKPISetAtCountry(@PathVariable BigInteger kpiSetId,@RequestBody Map<String,TranslationInfo> translations) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, kpiSetService.updateTranslationData(kpiSetId,translations));
    }

    @PutMapping(value = ApiConstants.COUNTRY_URL+ ApiConstants.KPI_SET+"/{kpiSetId}/language_settings")
    public ResponseEntity<ResponseDTO<Object>> updateTranslationDataFibonacciKPIAtCountry(@PathVariable BigInteger kpiSetId, @RequestBody Map<String, TranslationInfo> translations){
        return ResponseHandler.generateResponseDTO(HttpStatus.OK, true, kpiSetService.updateTranslationData(kpiSetId,translations));
    }

}
