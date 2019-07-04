package com.kairos.controller.unit_settings;

import com.kairos.dto.activity.unit_settings.ProtectedDaysOffDTO;
import com.kairos.dto.response.ResponseDTO;
import com.kairos.enums.ProtectedDaysOffUnitSettings;
import com.kairos.service.unit_settings.ProtectedDaysOffService;
import com.kairos.utils.response.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import static com.kairos.constants.ApiConstants.*;

/**
 * Created By G.P.Ranjan on 1/7/19
 **/
@RestController
@RequestMapping(API_V1)
@Api(API_V1)
public class ProtectedDaysOffController {
    @Inject
    private ProtectedDaysOffService protectedDaysOffService;

    @ApiOperation("get protected days off by unit id ")
    @GetMapping(UNIT_URL +"/protected_days_off")
    public ResponseEntity<ResponseDTO<ProtectedDaysOffDTO>> getProtectedDaysOffByUnitId(@PathVariable Long unitId) {
        return ResponseHandler.generateResponseDTO(HttpStatus.OK, true, protectedDaysOffService.getProtectedDaysOffByUnitId(unitId));
    }

    @ApiOperation("update protected days off by unit id ")
    @PutMapping(UNIT_URL + "/protected_days_off")
    public ResponseEntity<ResponseDTO<ProtectedDaysOffDTO>> updateProtectedDaysOffByUnitId(@PathVariable Long unitId, @RequestBody ProtectedDaysOffDTO protectedDaysOffDTO) {
        return ResponseHandler.generateResponseDTO(HttpStatus.OK, true, protectedDaysOffService.updateProtectedDaysOffByUnitId(unitId, protectedDaysOffDTO));
    }


}
