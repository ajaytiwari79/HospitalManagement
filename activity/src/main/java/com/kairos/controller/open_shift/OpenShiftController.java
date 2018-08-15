package com.kairos.controller.open_shift;

import com.kairos.service.open_shift.AutomaticOpenShiftGenerationService;
import com.kairos.service.open_shift.OpenShiftService;
import com.kairos.user.access_permission.AccessGroupRole;
import com.kairos.util.response.ResponseHandler;
import com.kairos.activity.open_shift.OpenShiftResponseDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.inject.Inject;
import java.math.BigInteger;
import java.util.Map;
import java.util.List;

import static com.kairos.constants.ApiConstants.API_ORGANIZATION_UNIT_URL;
import static com.kairos.constants.ApiConstants.OPEN_SHIFT_URL;


@RestController
@Api(API_ORGANIZATION_UNIT_URL)
@RequestMapping(API_ORGANIZATION_UNIT_URL)
public class OpenShiftController {

    @Inject
    OpenShiftService openShiftService;
    @Inject
    AutomaticOpenShiftGenerationService automaticOpenShiftGenerationService;


    @RequestMapping(value = "/order/{orderId}/openshifts", method = RequestMethod.POST)
    @ApiOperation("create openshifts")
    public ResponseEntity<Map<String, Object>> createOpenShift(@RequestBody OpenShiftResponseDTO openShiftResponseDTO)  {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,openShiftService.createOpenShift(openShiftResponseDTO));
    }

   /* @RequestMapping(value = "/{openShiftId}", method = RequestMethod.PUT)
    @ApiOperation("update openShift")
    public ResponseEntity<Map<String, Object>> updateOpenShift(@PathVariable BigInteger openShiftId, @RequestBody OpenShiftResponseDTO openShiftResponseDTO)  {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,openShiftService.updateOpenShift(openShiftResponseDTO,openShiftId));
    }*/
    @RequestMapping(value = "/order/{orderId}/openshifts", method = RequestMethod.PUT)
    @ApiOperation("update openShift")
    public ResponseEntity<Map<String, Object>> updateOpenShift(@PathVariable BigInteger openShiftId, @PathVariable BigInteger orderId, @RequestBody List<OpenShiftResponseDTO> openShiftResponseDTOs)  {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,openShiftService.updateOpenShift(openShiftResponseDTOs,orderId));
    }

    @ApiOperation("delete an openshift")
    @DeleteMapping(value = "/order/{orderId}/openshifts/{openshiftId}")
    public ResponseEntity<Map<String, Object>> deleteOpenShift(@PathVariable BigInteger openShiftId) {
        openShiftService.deleteOpenShift(openShiftId);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, true);
    }

    @ApiOperation(value = "Get All openshifts by order and unitId")
    @RequestMapping(value = "/order/{orderId}/openshifts", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> getAllOpenShiftsByUnitId(@PathVariable Long unitId,@PathVariable BigInteger orderId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, openShiftService.getOpenshiftsByUnitIdAndOrderId(unitId,orderId));
    }

    @ApiOperation(value = "Pick open Shift by staff")
    @RequestMapping(value = "open_shift/{openShiftId}/assign", method = RequestMethod.POST)
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")

    public ResponseEntity<Map<String, Object>> pickOpenShiftByStaff(@PathVariable Long unitId, @PathVariable BigInteger openShiftId, @RequestParam("staffId") Long staffId,@RequestParam("action") String action) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, openShiftService.pickOpenShiftByStaff(unitId,openShiftId,staffId,action));
        }

    @ApiOperation(value = "fetch details for open shift")
    @RequestMapping(value = "open_shift/{openShiftId}/staff/{staffId}", method = RequestMethod.GET)
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")

    public ResponseEntity<Map<String, Object>> fetchOpenShiftDataByStaff(@PathVariable Long unitId, @PathVariable BigInteger openShiftId, @PathVariable Long staffId, @RequestParam("role") AccessGroupRole role) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, openShiftService.fetchOpenShiftDataByStaff(unitId,openShiftId,staffId,role));
    }

    @ApiOperation(value = "notify staff for open shift")
    @RequestMapping(value = "open_shift/{openShiftId}/notify", method = RequestMethod.POST)
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> notifyStaff(@PathVariable Long unitId, @PathVariable BigInteger openShiftId, @RequestBody List<Long> staffIds,@RequestParam("action") String action) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, openShiftService.notifyStaff(unitId,openShiftId,staffIds,action));
    }

    @ApiOperation(value = "Automatic openshift genration")
    @RequestMapping(value = "open_shift/automatic_openshift_genration", method = RequestMethod.POST)
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> automaticOpenShiftGenration(@PathVariable Long unitId) {
        automaticOpenShiftGenerationService.findUnderStaffingByActivityId(unitId);
        return ResponseHandler.generateResponse(HttpStatus.OK, true,null);
    }
}
