package com.kairos.controller.dashboard;

import com.kairos.service.dashboard.SickService;
import com.kairos.utils.response.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.Map;

import static com.kairos.constants.ApiConstants.API_V1;

/**
 * CreatedBy vipulpandey on 30/8/18
 **/
@RestController
@Api(API_V1)
@RequestMapping(API_V1)
public class SickController {

    @Inject
    private SickService sickService;

    @ApiOperation("API is used to get the default data when user is sick")
    @GetMapping("/sick")
    public ResponseEntity<Map<String, Object>> getDefaultDataOnUserSick(@RequestParam(value = "unitId", required = false) Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, sickService.getDefaultDataOnUserSick(unitId));
    }

    @ApiOperation("API is used to call the user as fine")
    @GetMapping("/fine")
    public ResponseEntity<Map<String, Object>> markUserAsFine(@RequestParam(value = "unitId", required = false) Long unitId, @RequestParam(value = "staffId") Long staffId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, sickService.markUserAsFine(staffId, unitId));
    }

    @ApiOperation("API is used to call the user as fine")
    @PutMapping("/check_user_health")
    public ResponseEntity<Map<String, Object>> checkStatusOfUserAndUpdateStatus(@RequestParam(value = "unitId", required = false) Long unitId) {
        sickService.checkStatusOfUserAndUpdateStatus(unitId);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, null);
    }


}
