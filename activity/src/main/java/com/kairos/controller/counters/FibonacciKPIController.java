package com.kairos.controller.counters;

import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.kairos.constants.ApiConstants.API_V1;

/**
 * pradeep
 * 12/4/19
 */
@RestController
@RequestMapping(API_V1)
@Api(API_V1)
public class FibonacciKPIController {

 /*   @Inject private FibonacciKPIService fibonacciKPIService;

    @PostMapping(value = COUNTRY_URL+FIBONACCI)
    public ResponseEntity<ResponseDTO<Object>> createFibonacciKPI(@PathVariable Long countryId, @RequestBody FibonacciKPIDTO fibonacciKPIDTO){
        fibonacciKPIService.createFibonacciKPI(countryId,fibonacciKPIDTO, ConfLevel.COUNTRY);
        return ResponseHandler.generateResponseDTO(HttpStatus.OK, true, null);
    }

    @PostMapping(value = COUNTRY_URL+FIBONACCI)
    public ResponseEntity<ResponseDTO<Object>> updateFibonacciKPI(@PathVariable Long countryId, @RequestBody FibonacciKPIDTO fibonacciKPIDTO){
        fibonacciKPIService.updateFibonacciKPI(countryId,fibonacciKPIDTO,ConfLevel.COUNTRY);
        return ResponseHandler.generateResponseDTO(HttpStatus.OK, true, null);
    }

    @PostMapping(value = COUNTRY_URL+FIBONACCI)
    public ResponseEntity<ResponseDTO<Object>> getAllFibonacciKPI(@PathVariable Long countryId){
        return ResponseHandler.generateResponseDTO(HttpStatus.OK, true, fibonacciKPIService.getAllFibonacciKPI(countryId,ConfLevel.COUNTRY));
    }

    @PostMapping(value = COUNTRY_URL+FIBONACCI+"/{fibonacciKPIId}")
    public ResponseEntity<ResponseDTO<Object>> v(@PathVariable Long countryId,@PathVariable BigInteger fibonacciKPIId){
        return ResponseHandler.generateResponseDTO(HttpStatus.OK, true, fibonacciKPIService.deleteFibonacciKPI(fibonacciKPIId));
    }

    @PostMapping(value = UNIT_URL+FIBONACCI)
    public ResponseEntity<ResponseDTO<Object>> createFibonacciKPIAtUnit(@PathVariable Long unitId, @RequestBody FibonacciKPIDTO fibonacciKPIDTO){
        fibonacciKPIService.createFibonacciKPI(unitId,fibonacciKPIDTO, ConfLevel.UNIT);
        return ResponseHandler.generateResponseDTO(HttpStatus.OK, true, null);
    }

    @PostMapping(value = UNIT_URL+FIBONACCI)
    public ResponseEntity<ResponseDTO<Object>> updateFibonacciKPIAtUnit(@PathVariable Long unitId, @RequestBody FibonacciKPIDTO fibonacciKPIDTO){
        fibonacciKPIService.updateFibonacciKPI(unitId,fibonacciKPIDTO,ConfLevel.UNIT);
        return ResponseHandler.generateResponseDTO(HttpStatus.OK, true, null);
    }

    @PostMapping(value = UNIT_URL+FIBONACCI)
    public ResponseEntity<ResponseDTO<Object>> getAllFibonacciKPIAtUnit(@PathVariable Long unitId){
        return ResponseHandler.generateResponseDTO(HttpStatus.OK, true, fibonacciKPIService.getAllFibonacciKPI(unitId,ConfLevel.COUNTRY));
    }

    @PostMapping(value = UNIT_URL+FIBONACCI+"/{fibonacciKPIId}")
    public ResponseEntity<ResponseDTO<Object>> deleteFibonacciKPIAtUnit(@PathVariable BigInteger fibonacciKPIId){
        return ResponseHandler.generateResponseDTO(HttpStatus.OK, true, fibonacciKPIService.deleteFibonacciKPI(fibonacciKPIId));
    }*/
}
