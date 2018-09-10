package com.kairos.controller.position_code;

import com.kairos.persistence.model.user.position_code.PositionCode;
import com.kairos.dto.user.organization.position_code.PositionCodeDTO;
import com.kairos.service.position_code.PositionCodeService;
import com.kairos.utils.response.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.Map;

import static com.kairos.constants.ApiConstants.API_ORGANIZATION_UNIT_URL;

/**
 * Created by pawanmandhan on 27/7/17.
 */

@RestController
@RequestMapping(API_ORGANIZATION_UNIT_URL)
@Api(API_ORGANIZATION_UNIT_URL)
public class PositionCodeController {

    @Inject
    private PositionCodeService positionCodeService;

    @ApiOperation("Create PositionCode")
    @PostMapping(value = "/position_code")
    ResponseEntity<Map<String, Object>> createPositionCode(@RequestParam("type") String type, @PathVariable Long unitId, @RequestBody PositionCodeDTO positionCodeDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, positionCodeService.createPositionCode(unitId, positionCodeDTO, type));
    }

    @ApiOperation("Update PositionCode")
    @PutMapping(value = "/position_code/{positionCodeId}")
    ResponseEntity<Map<String, Object>> updatePositionCode(@RequestParam("type") String type, @PathVariable Long unitId, @PathVariable long positionCodeId, @RequestBody PositionCode positionCode) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, positionCodeService.updatePositionCode(unitId, positionCodeId, positionCode, type));

    }

    @ApiOperation("Delete PositionCode")
    @DeleteMapping(value = "/position_code/{positionCodeId}")
    ResponseEntity<Map<String, Object>> deletePositionCode(@RequestParam("type") String type, @PathVariable Long unitId, @PathVariable Long positionCodeId) {

        return ResponseHandler.generateResponse(HttpStatus.OK, true, positionCodeService.deletePositionCode(unitId, positionCodeId, type));

    }

    @ApiOperation("Get PositionCode")
    @GetMapping(value = "/position_code/{positionCodeId}")
    ResponseEntity<Map<String, Object>> getPositionCode(@RequestParam("type") String type, @PathVariable Long positionCodeId) {

        return ResponseHandler.generateResponse(HttpStatus.OK, true, positionCodeService.getPositionCode(positionCodeId));

    }

    //TODO  fixture org rest call

    @ApiOperation("Get All PositionCode")
    @GetMapping(value = "/position_code")
    ResponseEntity<Map<String, Object>> getAllPositionCode(@RequestParam("type") String type, @PathVariable Long unitId) {

        return ResponseHandler.generateResponse(HttpStatus.OK, true, positionCodeService.getAllPositionCodes(unitId, type));

    }

    @RequestMapping(value = "/unions_with_position_code", method = RequestMethod.GET)
    @ApiOperation("Get All Unions and position code for unit employment  by organization ")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getUnionsAndPositionCodes(@PathVariable Long unitId, @RequestParam("type") String type, @RequestParam("staffId") Long staffId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                positionCodeService.getUnionsAndPositionCodes(unitId, type, staffId));
    }


}
