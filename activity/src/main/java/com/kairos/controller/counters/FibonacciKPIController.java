package com.kairos.controller.counters;

import com.kairos.dto.activity.counter.enums.ConfLevel;
import com.kairos.dto.activity.counter.fibonacci_kpi.FibonacciKPIDTO;
import com.kairos.dto.response.ResponseDTO;
import com.kairos.service.counter.FibonacciKPIService;
import com.kairos.utils.response.ResponseHandler;
import io.swagger.annotations.Api;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.math.BigInteger;

import static com.kairos.constants.ApiConstants.*;


/**
 * pradeep
 * 12/4/19
 */
@RestController
@RequestMapping(API_V1)
@Api(API_V1)
public class FibonacciKPIController {

    @Inject private FibonacciKPIService fibonacciKPIService;

    @PostMapping(value = COUNTRY_URL+FIBONACCI)
    public ResponseEntity<ResponseDTO<Object>> createFibonacciKPIAtCountry(@PathVariable Long countryId, @RequestBody @Valid FibonacciKPIDTO fibonacciKPIDTO){
        return ResponseHandler.generateResponseDTO(HttpStatus.OK, true, fibonacciKPIService.createFibonacciKPI(countryId,fibonacciKPIDTO, ConfLevel.COUNTRY));
    }

    @PutMapping(value = COUNTRY_URL+FIBONACCI+"/{fibonacciKPIId}")
    public ResponseEntity<ResponseDTO<Object>> updateFibonacciKPIAtCountry(@PathVariable Long countryId, @RequestBody @Valid FibonacciKPIDTO fibonacciKPIDTO){
        return ResponseHandler.generateResponseDTO(HttpStatus.OK, true,  fibonacciKPIService.updateFibonacciKPI(countryId,fibonacciKPIDTO,ConfLevel.COUNTRY));
    }

    @GetMapping(value = COUNTRY_URL+FIBONACCI)
    public ResponseEntity<ResponseDTO<Object>> getAllFibonacciKPIAtCountry(@PathVariable Long countryId){
        return ResponseHandler.generateResponseDTO(HttpStatus.OK, true, fibonacciKPIService.getAllFibonacciKPI(countryId,ConfLevel.COUNTRY));
    }

    @DeleteMapping(value = COUNTRY_URL+FIBONACCI+"/{fibonacciKPIId}")
    public ResponseEntity<ResponseDTO<Object>> deleteFibonacciKPIAtCountry(@PathVariable Long countryId,@PathVariable BigInteger fibonacciKPIId){
        return ResponseHandler.generateResponseDTO(HttpStatus.OK, true, fibonacciKPIService.deleteFibonacciKPI(fibonacciKPIId));
    }

    @GetMapping(value = COUNTRY_URL+FIBONACCI+"/default_data")
    public ResponseEntity<ResponseDTO<Object>> getdefaultDataAtCountry(@PathVariable Long unitId){
        return ResponseHandler.generateResponseDTO(HttpStatus.OK, true, fibonacciKPIService.getAllFibonacciKPI(unitId,ConfLevel.UNIT));
    }

    @GetMapping(value = COUNTRY_URL+FIBONACCI+"/{fibonacciKPIId}")
    public ResponseEntity<ResponseDTO<Object>> getOneFibonacciKPIAtCountry(@PathVariable BigInteger fibonacciKPIId){
        return ResponseHandler.generateResponseDTO(HttpStatus.OK, true, fibonacciKPIService.getOneFibonacciKPI(fibonacciKPIId));
    }

    @PostMapping(value = UNIT_URL+FIBONACCI)
    public ResponseEntity<ResponseDTO<Object>> createFibonacciKPIAtUnit(@PathVariable Long unitId, @RequestBody @Valid FibonacciKPIDTO fibonacciKPIDTO){
        return ResponseHandler.generateResponseDTO(HttpStatus.OK, true, fibonacciKPIService.createFibonacciKPI(unitId,fibonacciKPIDTO, ConfLevel.UNIT));
    }

    @PutMapping(value = UNIT_URL+FIBONACCI+"/{fibonacciKPIId}")
    public ResponseEntity<ResponseDTO<Object>> updateFibonacciKPIAtUnit(@PathVariable Long unitId, @RequestBody @Valid FibonacciKPIDTO fibonacciKPIDTO){
        return ResponseHandler.generateResponseDTO(HttpStatus.OK, true, fibonacciKPIService.updateFibonacciKPI(unitId,fibonacciKPIDTO,ConfLevel.UNIT));
    }

    @GetMapping(value = UNIT_URL+FIBONACCI)
    public ResponseEntity<ResponseDTO<Object>> getAllFibonacciKPIAtUnit(@PathVariable Long unitId){
        return ResponseHandler.generateResponseDTO(HttpStatus.OK, true, fibonacciKPIService.getAllFibonacciKPI(unitId,ConfLevel.UNIT));
    }

    @GetMapping(value = UNIT_URL+FIBONACCI+"/default_data")
    public ResponseEntity<ResponseDTO<Object>> getdefaultDataAtUnit(@PathVariable Long unitId){
        return ResponseHandler.generateResponseDTO(HttpStatus.OK, true, fibonacciKPIService.getAllFibonacciKPI(unitId,ConfLevel.UNIT));
    }

    @DeleteMapping(value = UNIT_URL+FIBONACCI+"/{fibonacciKPIId}")
    public ResponseEntity<ResponseDTO<Object>> deleteFibonacciKPIAtUnit(@PathVariable BigInteger fibonacciKPIId){
        return ResponseHandler.generateResponseDTO(HttpStatus.OK, true, fibonacciKPIService.deleteFibonacciKPI(fibonacciKPIId));
    }

    @GetMapping(value = UNIT_URL+FIBONACCI+"/{fibonacciKPIId}")
    public ResponseEntity<ResponseDTO<Object>> getOneFibonacciKPIAtUnit(@PathVariable BigInteger fibonacciKPIId){
        return ResponseHandler.generateResponseDTO(HttpStatus.OK, true, fibonacciKPIService.getOneFibonacciKPI(fibonacciKPIId));
    }
}
