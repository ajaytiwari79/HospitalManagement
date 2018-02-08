package com.kairos.controller.expertise;

import com.kairos.service.expertise.ExpertiseService;
import com.kairos.service.unitEmploymentPosition.UnitEmploymentPositionService;
import com.kairos.util.response.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.Map;

import static com.kairos.constants.ApiConstants.*;

/**
 * Created by prabjot on 28/10/16.
 */
@RestController
@RequestMapping(API_V1)
@Api(value = API_V1)
public class ExpertiseController {

    @Inject
    private ExpertiseService expertiseService;
    @Inject
    private UnitEmploymentPositionService unitEmploymentPositionService;


    @ApiOperation(value = "Assign Staff expertise")
    @RequestMapping(value = "/expertise/staff/{staffId}", method = RequestMethod.PUT)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> setExpertiseToStaff(@PathVariable Long staffId, @RequestBody Map<String, Object> expertise) {
        Long expertiseId = Long.valueOf(expertise.get("id").toString());
        Map<String, Object> expertiseObj = expertiseService.setExpertiseToStaff(staffId, expertiseId);
        if (expertiseObj == null) {
            return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false, expertiseObj);
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, expertise);
    }

    @ApiOperation(value = "Get Staff expertise")
    @RequestMapping(value = "/expertise/staff/{staffId}", method = RequestMethod.GET)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getExpertiseToStaff(@PathVariable Long staffId) {
        Map<String, Object> expertise = expertiseService.getExpertiseToStaff(staffId);
        if (expertise == null) {
            return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false, expertise);
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, expertise);
    }

    @ApiOperation(value = "Get cta and wta by expertise")
    @RequestMapping(value = PARENT_ORGANIZATION_URL+UNIT_URL + "/expertise/{expertiseId}/cta_wta")
    ResponseEntity<Map<String, Object>> getCtaAndWtaByExpertiseId(@PathVariable Long unitId, @PathVariable Long expertiseId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, unitEmploymentPositionService.getCtaAndWtaByExpertiseId(unitId, expertiseId));
    }


}
