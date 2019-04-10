package com.kairos.controller.expertise;

import com.kairos.commons.service.locale.LocaleService;
import com.kairos.dto.user.country.experties.AgeRangeDTO;
import com.kairos.dto.user.country.experties.CopyExpertiseDTO;
import com.kairos.dto.user.country.experties.ExpertiseEmploymentTypeDTO;
import com.kairos.dto.user.country.experties.FunctionalSeniorityLevelDTO;
import com.kairos.persistence.model.user.expertise.Response.FunctionalPaymentDTO;
import com.kairos.service.expertise.ExpertiseService;
import com.kairos.service.expertise.ExpertiseUnitService;
import com.kairos.service.expertise.FunctionalPaymentService;
import com.kairos.service.unit_position.EmploymentCTAWTAService;
import com.kairos.service.unit_position.EmploymentService;
import com.kairos.utils.response.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.time.LocalDate;
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
    private EmploymentService employmentService;
    @Inject
    private LocaleService localeService;
    @Inject
    private FunctionalPaymentService functionalPaymentService;
    @Inject
    ExpertiseUnitService expertiseUnitService;
    @Inject private EmploymentCTAWTAService employmentCTAWTAService;

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

    @ApiOperation(value = "find an expertise by id")
    @GetMapping(value = "country/{countryId}/expertise/{expertiseId}")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getExpertiseById(@PathVariable Long expertiseId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, expertiseService.getExpertiseById(expertiseId));
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
    @RequestMapping(value =  UNIT_URL + "/expertise/{expertiseId}/cta_wta")
    ResponseEntity<Map<String, Object>> getCtaAndWtaByExpertiseId(@PathVariable Long unitId, @PathVariable Long expertiseId, @RequestParam("staffId") Long staffId,
                                                                  @RequestParam(value = "selectedDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate selectedDate) throws Exception {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, employmentCTAWTAService.getCtaAndWtaWithExpertiseDetailByExpertiseId(unitId, expertiseId, staffId, selectedDate));
    }

    @ApiOperation(value = "Get Available expertise")
    @RequestMapping(value =  COUNTRY_URL + "/all_expertise", method = RequestMethod.GET)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getUnpublishedExpertise(@PathVariable long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, expertiseService.getUnpublishedExpertise(countryId));
    }

    @RequestMapping(value =  "/unit/{unitId}/cta/expertise", method = RequestMethod.GET)
    @ApiOperation("get expertise for cta_response rule template")
    public ResponseEntity<Map<String, Object>> getExpertiseForCTA(@PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, expertiseService.getExpertiseForOrgCTA(unitId));
    }

    @ApiOperation(value = "Get all expertise by orgSubType")
    @RequestMapping(value =  COUNTRY_URL + "/organization_sub_type/{organizationSubTypeId}/expertise", method = RequestMethod.GET)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getExpertiseByOrganizationSubType(@PathVariable long countryId, @PathVariable long organizationSubTypeId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, expertiseService.getExpertiseByOrganizationSubType(countryId, organizationSubTypeId));
    }

    @ApiOperation(value = "Update Age range in Expertise")
    @PutMapping(value =  COUNTRY_URL + "/expertise/{expertiseId}/set_age_range")
    public ResponseEntity<Map<String, Object>> updateUnitPosition(@PathVariable Long expertiseId, @RequestBody @Valid List<AgeRangeDTO> ageRangeDTO, @RequestParam("wtaType") String wtaType) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, expertiseService.updateAgeRangeInExpertise(expertiseId, ageRangeDTO, wtaType));
    }

    @ApiOperation(value = "save a functional payment settings for expertise")
    @RequestMapping(value =  COUNTRY_URL + "/expertise/{expertiseId}/functional_payment", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> saveFunctionalPayment(@PathVariable Long expertiseId, @RequestBody @Valid FunctionalPaymentDTO functionalPaymentDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, functionalPaymentService.saveFunctionalPayment(expertiseId, functionalPaymentDTO));
    }

    @ApiOperation(value = "get  functional payment settings of expertise")
    @RequestMapping(value =  COUNTRY_URL + "/expertise/{expertiseId}/functional_payment", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> getFunctionalPayment(@PathVariable Long expertiseId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, functionalPaymentService.getFunctionalPayment(expertiseId));
    }

    @ApiOperation(value = "update a functional payment settings for expertise")
    @RequestMapping(value =  COUNTRY_URL + "/expertise/{expertiseId}/functional_payment", method = RequestMethod.PUT)
    public ResponseEntity<Map<String, Object>> updateFunctionalPayment(@PathVariable Long expertiseId, @RequestBody @Valid FunctionalPaymentDTO functionalPaymentDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, functionalPaymentService.updateFunctionalPayment(expertiseId, functionalPaymentDTO));
    }

    @ApiOperation(value = "Add a functional payment settings for functional_payment")
    @RequestMapping(value =  COUNTRY_URL + "/functional_payment/{functionalPaymentId}", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> addMatrixInFunctionalPayment(@PathVariable Long functionalPaymentId, @RequestBody @Valid FunctionalSeniorityLevelDTO functionalPaymentDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, functionalPaymentService.addMatrixInFunctionalPayment(functionalPaymentDTO));
    }

    @ApiOperation(value = "GET a functional payment settings for functional_payment")
    @RequestMapping(value =  COUNTRY_URL + "/functional_payment/{functionalPaymentId}", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> getMatrixOfFunctionalPayment(@PathVariable Long functionalPaymentId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, functionalPaymentService.getMatrixOfFunctionalPayment(functionalPaymentId));
    }

    @ApiOperation(value = "Add a functional payment settings for functional_payment")
    @RequestMapping(value =  COUNTRY_URL + "/functional_payment/{functionalPaymentId}", method = RequestMethod.PUT)
    public ResponseEntity<Map<String, Object>> updateMatrixInFunctionalPayment(@PathVariable Long functionalPaymentId, @RequestBody @Valid FunctionalSeniorityLevelDTO functionalPaymentDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, functionalPaymentService.updateMatrixInFunctionalPayment(functionalPaymentDTO));
    }

    @ApiOperation(value = "publish a functional payment settings for expertise")
    @RequestMapping(value =  COUNTRY_URL + "/functional_payment/{functionalPaymentId}/publish", method = RequestMethod.PUT)
    public ResponseEntity<Map<String, Object>> publishFunctionalPayment(@PathVariable Long functionalPaymentId, @RequestBody FunctionalPaymentDTO functionalPaymentDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, functionalPaymentService.publishFunctionalPayment(functionalPaymentId, functionalPaymentDTO));


    }

    @ApiOperation(value = "block planned time for employment type and expertise ")
    @RequestMapping(value =  COUNTRY_URL + "/expertise/{expertiseId}/planned_time", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> addPlannedTimeInExpertise(@PathVariable Long countryId, @PathVariable Long expertiseId, @RequestBody ExpertiseEmploymentTypeDTO expertiseEmploymentTypeDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, expertiseService.addPlannedTimeInExpertise(expertiseId, expertiseEmploymentTypeDTO));
    }

    @ApiOperation(value = "get planned time for employment type and expertise ")
    @RequestMapping(value =  COUNTRY_URL + "/expertise/{expertiseId}/planned_time", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> getPlannedTimeInExpertise(@PathVariable Long expertiseId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, expertiseService.getPlannedTimeInExpertise(expertiseId));
    }

    @ApiOperation(value = "All planned time and employment type")
    @RequestMapping(value =  COUNTRY_URL + "/expertise/planned_time/default_data", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> getPlannedTimeAndEmploymentType(@PathVariable Long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, expertiseService.getPlannedTimeAndEmploymentType(countryId));
    }

    @ApiOperation(value = "block planned time for employment type and expertise ")
    @RequestMapping(value =  COUNTRY_URL + "/expertise/{expertiseId}/planned_time", method = RequestMethod.PUT)
    public ResponseEntity<Map<String, Object>> updatePlannedTimeInExpertise(@PathVariable Long expertiseId, @RequestBody ExpertiseEmploymentTypeDTO expertiseEmploymentTypeDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, expertiseService.updatePlannedTimeInExpertise(expertiseId, expertiseEmploymentTypeDTO));
    }


    @ApiOperation(value = "copy Expertise")
    @PutMapping(value =  COUNTRY_URL + "/expertise/{expertiseId}/copy")
    public ResponseEntity<Map<String, Object>> copyExpertise(@PathVariable Long expertiseId, @RequestBody @Valid CopyExpertiseDTO copyExpertiseDTO, @PathVariable long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, expertiseService.copyExpertise(expertiseId, copyExpertiseDTO, countryId));
    }

    @ApiOperation(value = "Get senior Days and child Care days at country")
    @GetMapping(value = "/expertise/{expertiseId}/senior_and_child_care_days")
    public ResponseEntity<Map<String, Object>> getSeniorAndChildCareDaysAtCountry(@PathVariable Long expertiseId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, expertiseService.getSeniorAndChildCareDays(expertiseId));
    }
    //-------------------UNIT LEVEL ----------------------\\


    @ApiOperation(value = "get all expertise at unit level to show")
    @GetMapping(value = UNIT_URL + "/expertise")
    public ResponseEntity<Map<String, Object>> findAllExpertise(@PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, expertiseUnitService.findAllExpertise(unitId));
    }

    @ApiOperation(value = "get functional payment  for expertise")
    @GetMapping(value = UNIT_URL + "/expertise/{expertiseId}/functional_payment")
    public ResponseEntity<Map<String, Object>> getFunctionalPaymentForUnit(@PathVariable Long expertiseId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, functionalPaymentService.getFunctionalPayment(expertiseId));
    }


    @ApiOperation(value = "get functional payment matrix for functional payment at unit level")
    @GetMapping(value = UNIT_URL + "/functional_payment/{functionalPaymentId}")
    public ResponseEntity<Map<String, Object>> getMatrixOfFunctionalPaymentForUnit(@PathVariable Long functionalPaymentId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, functionalPaymentService.getMatrixOfFunctionalPayment(functionalPaymentId));
    }

    @ApiOperation(value = "get planned time for employment type and expertise ")
    @GetMapping(value = UNIT_URL + "/expertise/{expertiseId}/planned_time")
    public ResponseEntity<Map<String, Object>> getPlannedTimeOfExpertise(@PathVariable Long expertiseId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, expertiseService.getPlannedTimeInExpertise(expertiseId));
    }

    @ApiOperation(value = "get all staff who has this expertise assigned ")
    @GetMapping(value = UNIT_URL + "/expertise/{expertiseId}/staff_location")
    public ResponseEntity<Map<String, Object>> getStaffListOfExpertise(@PathVariable Long expertiseId, @PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, expertiseUnitService.getStaffListOfExpertise(expertiseId, unitId));
    }

    @ApiOperation(value = "All planned time and employment type for unit level")
    @GetMapping(value = UNIT_URL + "/expertise/planned_time/default_data")
    public ResponseEntity<Map<String, Object>> getPlannedTimeAndEmploymentTypeForUnit(@PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, expertiseService.getPlannedTimeAndEmploymentTypeForUnit(unitId));
    }

    @ApiOperation(value = "update location and staff representative in expertise")
    @PutMapping(value = UNIT_URL + "/expertise/{expertiseId}")
    public ResponseEntity<Map<String, Object>> updateExpertiseAtUnit(@PathVariable Long unitId, @PathVariable Long expertiseId, @RequestParam("unionLocationId") Long locationId, @RequestParam("unionRepresentativeId") Long staffId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, expertiseUnitService.updateExpertiseAtUnit(unitId, staffId, expertiseId, locationId));
    }

    @ApiOperation(value = "get all expertise for multiple units")
    @GetMapping(value = UNIT_URL + "/units_expertise")
    public ResponseEntity<Map<String, Object>> findAllExpertiseWithUnits() {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, expertiseUnitService.findAllExpertiseWithUnits());
    }


}
