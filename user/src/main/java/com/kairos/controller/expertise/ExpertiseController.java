package com.kairos.controller.expertise;

import com.kairos.commons.service.locale.LocaleService;
import com.kairos.persistence.model.user.expertise.Response.FunctionalPaymentDTO;
import com.kairos.dto.user.country.experties.AgeRangeDTO;
import com.kairos.dto.user.country.experties.CopyExpertiseDTO;
import com.kairos.dto.user.country.experties.ExpertiseEmploymentTypeDTO;
import com.kairos.dto.user.country.experties.FunctionalSeniorityLevelDTO;
import com.kairos.service.expertise.ExpertiseService;
import com.kairos.service.expertise.FunctionalPaymentService;
import com.kairos.service.unit_position.UnitPositionService;
import com.kairos.utils.response.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private LocaleService localeService;
    @Autowired
    private FunctionalPaymentService functionalPaymentService;

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

    @RequestMapping(value = PARENT_ORGANIZATION_URL+"/unit/{unitId}/cta/expertise", method = RequestMethod.GET)
    @ApiOperation("get expertise for cta_response rule template")
    public ResponseEntity<Map<String, Object>> getExpertiseForCTA(@PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, expertiseService.getExpertiseForOrgCTA(unitId));
    }

    @ApiOperation(value = "Get all expertise by orgSubType")
    @RequestMapping(value = PARENT_ORGANIZATION_URL + COUNTRY_URL + "/organization_sub_type/{organizationSubTypeId}/expertise", method = RequestMethod.GET)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getExpertiseByOrganizationSubType(@PathVariable long countryId, @PathVariable long organizationSubTypeId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, expertiseService.getExpertiseByOrganizationSubType(countryId, organizationSubTypeId));
    }

    @ApiOperation(value = "Update Age range in Expertise")
    @PutMapping(value = PARENT_ORGANIZATION_URL + COUNTRY_URL + "/expertise/{expertiseId}/set_age_range")
    public ResponseEntity<Map<String, Object>> updateUnitPosition(@PathVariable Long expertiseId, @RequestBody @Valid List<AgeRangeDTO> ageRangeDTO, @RequestParam("wtaType") String wtaType) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, expertiseService.updateAgeRangeInExpertise(expertiseId, ageRangeDTO, wtaType));
    }

    @ApiOperation(value = "save a functional payment settings for expertise")
    @RequestMapping(value = PARENT_ORGANIZATION_URL + COUNTRY_URL + "/expertise/{expertiseId}/functional_payment", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> saveFunctionalPayment(@PathVariable Long expertiseId, @RequestBody @Valid FunctionalPaymentDTO functionalPaymentDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, functionalPaymentService.saveFunctionalPayment(expertiseId, functionalPaymentDTO));
    }

    @ApiOperation(value = "save a functional payment settings for expertise")
    @RequestMapping(value = PARENT_ORGANIZATION_URL + COUNTRY_URL + "/expertise/{expertiseId}/functional_payment", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> getFunctionalPayment(@PathVariable Long expertiseId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, functionalPaymentService.getFunctionalPayment(expertiseId));
    }

    @ApiOperation(value = "update a functional payment settings for expertise")
    @RequestMapping(value = PARENT_ORGANIZATION_URL + COUNTRY_URL + "/expertise/{expertiseId}/functional_payment", method = RequestMethod.PUT)
    public ResponseEntity<Map<String, Object>> updateFunctionalPayment(@PathVariable Long expertiseId, @RequestBody @Valid FunctionalPaymentDTO functionalPaymentDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, functionalPaymentService.updateFunctionalPayment(expertiseId, functionalPaymentDTO));
    }

    @ApiOperation(value = "Add a functional payment settings for functional_payment")
    @RequestMapping(value = PARENT_ORGANIZATION_URL + COUNTRY_URL + "/functional_payment/{functionalPaymentId}", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> addMatrixInFunctionalPayment(@PathVariable Long functionalPaymentId, @RequestBody @Valid FunctionalSeniorityLevelDTO functionalPaymentDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, functionalPaymentService.addMatrixInFunctionalPayment(functionalPaymentDTO));
    }

    @ApiOperation(value = "GET a functional payment settings for functional_payment")
    @RequestMapping(value = PARENT_ORGANIZATION_URL + COUNTRY_URL + "/functional_payment/{functionalPaymentId}", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> getMatrixOfFunctionalPayment(@PathVariable Long functionalPaymentId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, functionalPaymentService.getMatrixOfFunctionalPayment(functionalPaymentId));
    }

    @ApiOperation(value = "Add a functional payment settings for functional_payment")
    @RequestMapping(value = PARENT_ORGANIZATION_URL + COUNTRY_URL + "/functional_payment/{functionalPaymentId}", method = RequestMethod.PUT)
    public ResponseEntity<Map<String, Object>> updateMatrixInFunctionalPayment(@PathVariable Long functionalPaymentId, @RequestBody @Valid FunctionalSeniorityLevelDTO functionalPaymentDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, functionalPaymentService.updateMatrixInFunctionalPayment(functionalPaymentDTO));
    }

    @ApiOperation(value = "publish a functional payment settings for expertise")
    @RequestMapping(value = PARENT_ORGANIZATION_URL + COUNTRY_URL + "/functional_payment/{functionalPaymentId}/publish", method = RequestMethod.PUT)
    public ResponseEntity<Map<String, Object>> publishFunctionalPayment(@PathVariable Long functionalPaymentId, @RequestBody FunctionalPaymentDTO functionalPaymentDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, functionalPaymentService.publishFunctionalPayment(functionalPaymentId, functionalPaymentDTO));
    }

    @ApiOperation(value = "block planned time for employment type and expertise ")
    @RequestMapping(value = PARENT_ORGANIZATION_URL + COUNTRY_URL + "/expertise/{expertiseId}/planned_time", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> addPlannedTimeInExpertise(@PathVariable Long countryId, @PathVariable Long expertiseId, @RequestBody ExpertiseEmploymentTypeDTO expertiseEmploymentTypeDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, expertiseService.addPlannedTimeInExpertise(expertiseId, expertiseEmploymentTypeDTO));
    }

    @ApiOperation(value = "get planned time for employment type and expertise ")
    @RequestMapping(value = PARENT_ORGANIZATION_URL + COUNTRY_URL + "/expertise/{expertiseId}/planned_time", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> getPlannedTimeInExpertise(@PathVariable Long expertiseId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, expertiseService.getPlannedTimeInExpertise(expertiseId));
    }

    @ApiOperation(value = "All planned time and employment type")
    @RequestMapping(value = PARENT_ORGANIZATION_URL + COUNTRY_URL + "/expertise/planned_time/default_data", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> getPlannedTimeAndEmploymentType(@PathVariable Long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, expertiseService.getPlannedTimeAndEmploymentType(countryId));
    }

    @ApiOperation(value = "block planned time for employment type and expertise ")
    @RequestMapping(value = PARENT_ORGANIZATION_URL + COUNTRY_URL + "/expertise/{expertiseId}/planned_time", method = RequestMethod.PUT)
    public ResponseEntity<Map<String, Object>> updatePlannedTimeInExpertise(@PathVariable Long expertiseId, @RequestBody ExpertiseEmploymentTypeDTO expertiseEmploymentTypeDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, expertiseService.updatePlannedTimeInExpertise(expertiseId, expertiseEmploymentTypeDTO));
    }


    @ApiOperation(value = "copy Expertise")
    @PutMapping(value = PARENT_ORGANIZATION_URL + COUNTRY_URL + "/expertise/{expertiseId}/copy")
    public ResponseEntity<Map<String, Object>> copyExpertise(@PathVariable Long expertiseId, @RequestBody @Valid CopyExpertiseDTO copyExpertiseDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, expertiseService.copyExpertise(expertiseId, copyExpertiseDTO));
    }

}
