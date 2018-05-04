package com.kairos.controller.expertise;

import com.kairos.response.dto.web.experties.AgeRangeDTO;
import com.kairos.service.expertise.ExpertiseService;
import com.kairos.service.unit_position.UnitPositionService;
import com.kairos.util.response.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.util.List;
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
    private UnitPositionService unitPositionService;


    @ApiOperation(value = "Assign Staff expertise")
    @RequestMapping(value = "/expertise/staff/{staffId}", method = RequestMethod.PUT)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> setExpertiseToStaff(@PathVariable Long staffId, @RequestBody List<Long> expertiseIds) {

        Map<String, Object> expertiseObj = expertiseService.setExpertiseToStaff(staffId, expertiseIds);
        if (expertiseObj == null) {
            return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false, expertiseObj);
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, expertiseIds);
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
    @RequestMapping(value = PARENT_ORGANIZATION_URL + UNIT_URL + "/expertise/{expertiseId}/cta_wta")
    ResponseEntity<Map<String, Object>> getCtaAndWtaByExpertiseId(@PathVariable Long unitId, @PathVariable Long expertiseId, @RequestParam("staffId") Long staffId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, unitPositionService.getCtaAndWtaWithExpertiseDetailByExpertiseId(unitId, expertiseId, staffId));
    }

    @ApiOperation(value = "Get Available expertise")
    @RequestMapping(value = PARENT_ORGANIZATION_URL + COUNTRY_URL + "/all_expertise", method = RequestMethod.GET)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getUnpublishedExpertise(@PathVariable long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, expertiseService.getUnpublishedExpertise(countryId));
    }


    @ApiOperation(value = "Get all expertise by orgSubType")
    @RequestMapping(value = PARENT_ORGANIZATION_URL + COUNTRY_URL + "/organization_sub_type/{organizationSubTypeId}/expertise", method = RequestMethod.GET)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getExpertiseByOrganizationSubType(@PathVariable long countryId,@PathVariable long organizationSubTypeId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, expertiseService.getExpertiseByOrganizationSubType(countryId, organizationSubTypeId));
    }

    @ApiOperation(value = "Update Age range in Expertise")
    @PutMapping(value = PARENT_ORGANIZATION_URL + COUNTRY_URL + "/expertise/{expertiseId}/set_age_range")
    public ResponseEntity<Map<String, Object>> updateUnitPosition(@PathVariable Long expertiseId, @RequestBody @Valid List<AgeRangeDTO> ageRangeDTO, @RequestParam("type") String wtaType) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, expertiseService.updateAgeRangeInExpertise(expertiseId, ageRangeDTO,wtaType));

    }

}
