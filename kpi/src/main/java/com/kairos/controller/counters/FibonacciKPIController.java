package com.kairos.controller.counters;

import com.kairos.commons.response.ResponseHandler;
import com.kairos.constants.ApiConstants;
import com.kairos.dto.TranslationInfo;
import com.kairos.dto.activity.counter.enums.ConfLevel;
import com.kairos.dto.activity.counter.fibonacci_kpi.FibonacciKPIDTO;
import com.kairos.dto.response.ResponseDTO;
import com.kairos.service.FibonacciKPIService;
import io.swagger.annotations.Api;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.math.BigInteger;
import java.util.Map;

/**
 * pradeep
 * 12/4/19
 */
@RestController
@RequestMapping(ApiConstants.API_V1)
@Api(ApiConstants.API_V1)
public class FibonacciKPIController {

    @Inject private FibonacciKPIService fibonacciKPIService;

    @PostMapping(value = ApiConstants.COUNTRY_URL+ ApiConstants.FIBONACCI)
    public ResponseEntity<ResponseDTO<Object>> createFibonacciKPIAtCountry(@PathVariable Long countryId, @RequestBody @Valid FibonacciKPIDTO fibonacciKPIDTO){
        return ResponseHandler.generateResponseDTO(HttpStatus.OK, true, fibonacciKPIService.createFibonacciKPI(countryId,fibonacciKPIDTO, ConfLevel.COUNTRY));
    }

    @PutMapping(value = ApiConstants.COUNTRY_URL+ ApiConstants.FIBONACCI+"/{fibonacciKPIId}")
    public ResponseEntity<ResponseDTO<Object>> updateFibonacciKPIAtCountry(@PathVariable Long countryId, @RequestBody @Valid FibonacciKPIDTO fibonacciKPIDTO){
        return ResponseHandler.generateResponseDTO(HttpStatus.OK, true,  fibonacciKPIService.updateFibonacciKPI(countryId,fibonacciKPIDTO,ConfLevel.COUNTRY));
    }

    @GetMapping(value = ApiConstants.COUNTRY_URL+ ApiConstants.FIBONACCI)
    public ResponseEntity<ResponseDTO<Object>> getAllFibonacciKPIAtCountry(@PathVariable Long countryId){
        return ResponseHandler.generateResponseDTO(HttpStatus.OK, true, fibonacciKPIService.getAllFibonacciKPI(countryId,ConfLevel.COUNTRY));
    }

    @DeleteMapping(value = ApiConstants.COUNTRY_URL+ ApiConstants.FIBONACCI+"/{fibonacciKPIId}")
    public ResponseEntity<ResponseDTO<Object>> deleteFibonacciKPIAtCountry(@PathVariable Long countryId,@PathVariable BigInteger fibonacciKPIId){
        return ResponseHandler.generateResponseDTO(HttpStatus.OK, true, fibonacciKPIService.deleteFibonacciKPI(fibonacciKPIId));
    }

    @GetMapping(value = ApiConstants.COUNTRY_URL+ ApiConstants.FIBONACCI+"/default_data")
    public ResponseEntity<ResponseDTO<Object>> getdefaultDataAtCountry(@PathVariable Long unitId){
        return ResponseHandler.generateResponseDTO(HttpStatus.OK, true, fibonacciKPIService.getAllFibonacciKPI(unitId,ConfLevel.UNIT));
    }

    @GetMapping(value = ApiConstants.COUNTRY_URL+ ApiConstants.FIBONACCI+"/{fibonacciKPIId}")
    public ResponseEntity<ResponseDTO<Object>> getOneFibonacciKPIAtCountry(@PathVariable BigInteger fibonacciKPIId,@PathVariable Long countryId){
        return ResponseHandler.generateResponseDTO(HttpStatus.OK, true, fibonacciKPIService.getOneFibonacciKPI(fibonacciKPIId,countryId,ConfLevel.COUNTRY));
    }

    @PostMapping(value = ApiConstants.UNIT_URL+ ApiConstants.FIBONACCI)
    public ResponseEntity<ResponseDTO<Object>> createFibonacciKPIAtUnit(@PathVariable Long unitId, @RequestBody @Valid FibonacciKPIDTO fibonacciKPIDTO){
        return ResponseHandler.generateResponseDTO(HttpStatus.OK, true, fibonacciKPIService.createFibonacciKPI(unitId,fibonacciKPIDTO, ConfLevel.UNIT));
    }

    @PutMapping(value = ApiConstants.UNIT_URL+ ApiConstants.FIBONACCI+"/{fibonacciKPIId}")
    public ResponseEntity<ResponseDTO<Object>> updateFibonacciKPIAtUnit(@PathVariable Long unitId, @RequestBody @Valid FibonacciKPIDTO fibonacciKPIDTO){
        return ResponseHandler.generateResponseDTO(HttpStatus.OK, true, fibonacciKPIService.updateFibonacciKPI(unitId,fibonacciKPIDTO,ConfLevel.UNIT));
    }

    @GetMapping(value = ApiConstants.UNIT_URL+ ApiConstants.FIBONACCI)
    public ResponseEntity<ResponseDTO<Object>> getAllFibonacciKPIAtUnit(@PathVariable Long unitId){
        return ResponseHandler.generateResponseDTO(HttpStatus.OK, true, fibonacciKPIService.getAllFibonacciKPI(unitId,ConfLevel.UNIT));
    }

    @GetMapping(value = ApiConstants.UNIT_URL+ ApiConstants.FIBONACCI+"/default_data")
    public ResponseEntity<ResponseDTO<Object>> getdefaultDataAtUnit(@PathVariable Long unitId){
        return ResponseHandler.generateResponseDTO(HttpStatus.OK, true, fibonacciKPIService.getAllFibonacciKPI(unitId,ConfLevel.UNIT));
    }

    @DeleteMapping(value = ApiConstants.UNIT_URL+ ApiConstants.FIBONACCI+"/{fibonacciKPIId}")
    public ResponseEntity<ResponseDTO<Object>> deleteFibonacciKPIAtUnit(@PathVariable BigInteger fibonacciKPIId){
        return ResponseHandler.generateResponseDTO(HttpStatus.OK, true, fibonacciKPIService.deleteFibonacciKPI(fibonacciKPIId));
    }

    @GetMapping(value = ApiConstants.UNIT_URL+ ApiConstants.FIBONACCI+"/{fibonacciKPIId}")
    public ResponseEntity<ResponseDTO<Object>> getOneFibonacciKPIAtUnit(@PathVariable BigInteger fibonacciKPIId,@PathVariable Long unitId){
        return ResponseHandler.generateResponseDTO(HttpStatus.OK, true, fibonacciKPIService.getOneFibonacciKPI(fibonacciKPIId,unitId,ConfLevel.UNIT));
    }

    @PutMapping(value = ApiConstants.COUNTRY_URL+ ApiConstants.FIBONACCI+"/{fibonacciKPIId}/language_settings")
    public ResponseEntity<ResponseDTO<Object>> updateTranslationDataFibonacciKPIAtCountry(@PathVariable BigInteger fibonacciKPIId, @RequestBody Map<String, TranslationInfo> translations){
        return ResponseHandler.generateResponseDTO(HttpStatus.OK, true, fibonacciKPIService.updateTranslationData(fibonacciKPIId,translations));
    }

    @PutMapping(value = ApiConstants.UNIT_URL+ ApiConstants.FIBONACCI+"/{fibonacciKPIId}/language_settings")
    public ResponseEntity<ResponseDTO<Object>> updateTranslationDataFibonacciKPIAtUnit
            (@PathVariable BigInteger fibonacciKPIId, @RequestBody Map<String, TranslationInfo> translations){
        return ResponseHandler.generateResponseDTO(HttpStatus.OK, true, fibonacciKPIService.updateTranslationData(fibonacciKPIId,translations));
    }
}
