package com.kairos.controller.client;

import com.kairos.service.client.VRPClientService;
import com.kairos.utils.response.ResponseHandler;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.inject.Inject;
import java.util.Map;

import static com.kairos.constants.ApiConstants.API_ORGANIZATION_UNIT_URL;

/**
 * @author pradeep
 * @date - 11/6/18
 */
@RestController
@RequestMapping(API_ORGANIZATION_UNIT_URL)
public class VRPClientController {

    @Inject
    private VRPClientService vrpClientService;

    @ApiOperation(value = "import Unit Client Excel File")
    @PostMapping(value = "/importClients")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> importVrpClient(@PathVariable Long unitId, @RequestParam("file") MultipartFile multipartFile) {
        return ResponseHandler.generateResponse(HttpStatus.CREATED, true,
                vrpClientService.importClients(unitId,multipartFile));
    }

    @ApiOperation(value = "get All VRPClient by Organization")
    @GetMapping(value = "/vrpClient")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getClients(@PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                vrpClientService.getAllClient(unitId));
    }

    @ApiOperation(value = "get VRPClient by Id")
    @GetMapping(value = "/vrpClient/{clientId}")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getClient(@PathVariable Long unitId,@PathVariable Long clientId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                vrpClientService.getClient(clientId));
    }

    @ApiOperation(value = "delete VRPClient by Organization")
    @DeleteMapping(value = "/vrpClient/{clientId}")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> deleteClient(@PathVariable Long unitId,@PathVariable Long clientId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                vrpClientService.deleteClient(clientId));
    }


    @ApiOperation(value = "update VRPClient by Organization")
    @PutMapping(value = "/vrpClient/{clientId}/client_prefered_time_window/{preferedTimeWindowId}")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateClient(@PathVariable Long unitId, @PathVariable Long preferedTimeWindowId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                vrpClientService.updateClientPreferedTimeWindow(unitId,preferedTimeWindowId));
    }


}
