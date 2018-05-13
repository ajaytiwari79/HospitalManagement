package com.kairos.activity.controller.open_shift;

import com.kairos.activity.service.open_shift.OpenShiftService;
import com.kairos.activity.service.open_shift.OrderService;
import com.kairos.activity.util.response.ResponseHandler;
import com.kairos.response.dto.web.open_shift.OpenShiftResponseDTO;
import com.kairos.response.dto.web.open_shift.OrderOpenshiftResponseDTO;
import com.kairos.response.dto.web.open_shift.OrderResponseDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.Map;

import static com.kairos.activity.constants.ApiConstants.OPENSHIFT_URL;

@RestController
@Api(OPENSHIFT_URL)
@RequestMapping(OPENSHIFT_URL)
public class OpenShiftController {

    @Inject
    OpenShiftService openShiftService;

    @RequestMapping(value = "/", method = RequestMethod.POST)
    @ApiOperation("create openshifts")
    public ResponseEntity<Map<String, Object>> createOpenShift(@RequestBody OpenShiftResponseDTO openShiftResponseDTO)  {
        openShiftService.createOpenShift(openShiftResponseDTO);
        return ResponseHandler.generateResponse(HttpStatus.OK, true,true);
    }

    @RequestMapping(value = "/{openShiftId}", method = RequestMethod.PUT)
    @ApiOperation("update openShift")
    public ResponseEntity<Map<String, Object>> updateOpenShift(@PathVariable BigInteger openShiftId, @RequestBody OpenShiftResponseDTO openShiftResponseDTO)  {
        openShiftService.updateOpenShift(openShiftResponseDTO,openShiftId);
        return ResponseHandler.generateResponse(HttpStatus.OK, true,true);
    }

    @ApiOperation("delete an openshift")
    @DeleteMapping(value = "/{openshiftId}")
    public ResponseEntity<Map<String, Object>> deleteOpenShift(@PathVariable BigInteger openShiftId) {
        openShiftService.deleteOpenShift(openShiftId);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, true);
    }

    @ApiOperation(value = "Get All openshifts by order and unitId")
    @RequestMapping(value = "/{orderId}", method = RequestMethod.GET)
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getAllOrdersByUnitId(@PathVariable Long unitId,@PathVariable BigInteger orderId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, openShiftService.getOpenshiftsByUnitIdAndOrderId(unitId,orderId));
    }


}
