package com.kairos.controller.function;

import com.kairos.service.country.FunctionService;
import com.kairos.utils.response.ResponseHandler;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.Map;
import java.util.Set;

import static com.kairos.constants.ApiConstants.PARENT_ORGANIZATION_URL;
import static com.kairos.constants.ApiConstants.UNIT_URL;

@RestController
@RequestMapping(PARENT_ORGANIZATION_URL+UNIT_URL)
public class FunctionController {
    @Inject
    private FunctionService functionService;

    //===============================================================
    @ApiOperation(value = "")
    @GetMapping("/appliedFunctionsByUnitPositionIds")
    public ResponseEntity<Map<String, Object>> getUnitPositionIdWithFunctionIdShiftDateMap(@RequestBody Set<Long> unitPositionIds) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                functionService.getUnitPositionIdWithFunctionIdShiftDateMap(unitPositionIds));
    }

    @ApiOperation(value = "")
    @GetMapping("/updateFunctionOnPhaseRestoration")
    public ResponseEntity<Map<String, Object>> updateUnitPositionFunctionRelationShipDates(@RequestBody Map<Long, Map<Long, Set<LocalDate>>> unitPositionWithFunctionIdAndLocalDateMap) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                functionService.updateUnitPositionFunctionRelationShipDates(unitPositionWithFunctionIdAndLocalDateMap));
    }
}
