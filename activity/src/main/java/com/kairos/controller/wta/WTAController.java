package com.kairos.controller.wta;


import com.kairos.dto.activity.wta.basic_details.WTADTO;
import com.kairos.dto.user.employment.UnitPositionIdDTO;
import com.kairos.service.wta.WTAOrganizationService;
import com.kairos.service.wta.WTAService;
import com.kairos.utils.response.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static com.kairos.constants.ApiConstants.*;


/**
 * Created by pawanmandhan on 2/8/17.
 */

@RestController
@RequestMapping(API_V1)
@Api(API_V1)
public class WTAController {


    @Inject
    private WTAService wtaService;
    @Inject
    private WTAOrganizationService wtaOrganizationService;

    @ApiOperation(value = "Create a New WTA")
    @PostMapping(value =   COUNTRY_URL + "/wta")
    public ResponseEntity<Map<String, Object>> createWta(@PathVariable long countryId, @Validated @RequestBody WTADTO wta) {
        return ResponseHandler.generateResponse(HttpStatus.CREATED, true, wtaService.createWta(countryId, wta, true,false));
    }

    @ApiOperation(value = "Update WTA")
    @PutMapping(value =  COUNTRY_URL + "/wta/{wtaId}")
    public ResponseEntity<Map<String, Object>> updateWta(@PathVariable long countryId, @PathVariable BigInteger wtaId, @RequestBody @Valid WTADTO wta) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, wtaService.updateWtaOfCountry(countryId, wtaId, wta));
    }


    @ApiOperation(value = "Get WTA")
    @GetMapping(value =  COUNTRY_URL + "/wta/{wtaId}")
    public ResponseEntity<Map<String, Object>> getWta(@PathVariable BigInteger wtaId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, wtaService.getWta(wtaId));
    }

    @ApiOperation(value = "Remove WTA")
    @DeleteMapping(value =  COUNTRY_URL + "/wta/{wtaId}")
    public ResponseEntity<Map<String, Object>> deleteWta(@PathVariable BigInteger wtaId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, wtaService.removeWta(wtaId));
    }


    /*
     *get all WTA by countryId
     * Created by vipul on 10 august 2017
     */

    @ApiOperation(value = "Get WTA by CountryId ID")
    @RequestMapping(value =  COUNTRY_URL + "/wta/ByCountry", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> getAllWTAByCountryId(@PathVariable long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, wtaService.getAllWTAByCountryId(countryId));
    }

    /*
     * get all available expertise by org Sub type Id
     * Created by vipul on 12 sept 2017
     */

/*    @ApiOperation(value = "Get all expertise which are not unlinked with this subOrg type of country")
    @GetMapping(value = COUNTRY_URL + "/{organizationSubTypeId}/expertises")
    public ResponseEntity<Map<String, Object>> getAllAvailableExpertise(@PathVariable long organizationSubTypeId, @PathVariable long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, wtaService.getAllAvailableExpertise(organizationSubTypeId, countryId));
    }*/

    //TODO need to refactor URL pattern

    /**
     * get all WTA by organizationSubType by using Id
     *
     * @Author vipul
     * @Date 11 august 2017
     */

    @ApiOperation(value = "Get WTA by Organization sub type  by using sub type Id")
    @RequestMapping(value =  COUNTRY_URL + "/organization_type/{organizationSubTypeId}/wta/OrganizationSubType", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> getAllWTAByOrganizationSubType(@PathVariable long countryId,@PathVariable long organizationSubTypeId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, wtaService.getAllWTAByOrganizationSubType(organizationSubTypeId,countryId));
    }

    /**
     * @Author vipul
     * api to get all org and sub-org with wta's
     */
    @ApiOperation(value = "Get All organization and sub-organization based on CountryId")
    @RequestMapping(value =  COUNTRY_URL + "/OrganizationType", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> getAllWTAWithOrganization(@PathVariable long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, wtaService.getAllWTAWithOrganization(countryId));
    }

    /**
     * @Author vipul
     * api to get organization type  and sub organization type with wta Id
     * This was used for used to get data for uses of this WTA to other organization
     */
    @ApiOperation(value = "Get All organization and sub organization based on wtaId")
    @RequestMapping(value =  COUNTRY_URL + "/organization_type/{organizationTypeId}/wta/{wtaId}", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> getAllWTAWithWTAId(@PathVariable BigInteger wtaId, @PathVariable long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, wtaService.getAllWTAWithWTAId(countryId, wtaId));
    }

    @ApiOperation(value = "link unlink wta with org Type")
    @PutMapping(value =  COUNTRY_URL + "/organization_type/{organizationSubTypeId}/wta/{wtaId}")
    public ResponseEntity<Map<String, Object>> setWtaWithOrganizationType(@PathVariable long countryId, @PathVariable BigInteger wtaId, @PathVariable long organizationSubTypeId, @RequestParam(value = "checked") boolean checked) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, wtaService.setWtaWithOrganizationType(countryId, wtaId, organizationSubTypeId, checked));
    }


    /**
     * get all WTA by organization Id
     *
     * @Author Vipul on 20 DEC
     */

    @ApiOperation(value = "Get WTA by Org subType")
    @RequestMapping(value =  UNIT_URL + "/wta/ByOrganization", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> getWTAOfOrganization(@PathVariable long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, wtaOrganizationService.getAllWTAByOrganization(unitId));
    }


    @ApiOperation(value = "Remove WTA from organization ")
    @DeleteMapping(value =  UNIT_URL + "/wta/{wtaId}")
    public ResponseEntity<Map<String, Object>> deleteWtaFromOrganization(@PathVariable BigInteger wtaId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, wtaService.removeWta(wtaId));
    }

    @ApiOperation(value = "Update WTA")
    @PutMapping(value =  UNIT_URL + "/wta/{wtaId}")
    public ResponseEntity<Map<String, Object>> updateWtaOfOrganization(@PathVariable long unitId, @PathVariable BigInteger wtaId, @RequestBody  @Valid WTADTO wta) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, wtaOrganizationService.updateWtaOfOrganization(unitId, wtaId, wta));
    }


    @ApiOperation(value = "get WTA of Organization by Expertise")
    @GetMapping(value =  UNIT_URL + "/expertise/{expertiseId}/cta_wta")
    public ResponseEntity<Map<String, Object>> getAllWtaOfOrganizationByExpertise(@PathVariable long unitId, @PathVariable Long expertiseId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, wtaOrganizationService.getAllWtaOfOrganizationByExpertise(unitId, expertiseId));
    }

    @ApiOperation(value = "Update WTA of UnitPosition")
    @PutMapping(value =  UNIT_URL + "/wta")
    public ResponseEntity<Map<String, Object>> updateWtaOfUnitPosition(@PathVariable long unitId, @RequestBody @Valid WTADTO wtadto, @RequestParam Boolean unitPositionPublished) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, wtaService.updateWtaOfUnitPosition(unitId, wtadto, unitPositionPublished));
    }


    @ApiOperation(value = "get default WTA info of country")
    @GetMapping(value =  COUNTRY_URL + "/getDefaultWtaInfo")
    public ResponseEntity<Map<String, Object>> getDefaultWtaInfo(@PathVariable Long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, wtaService.getDefaultWtaInfo(countryId));
    }

    @ApiOperation(value = "get default WTA info of Unit")
    @GetMapping(value =  UNIT_URL + "/getDefaultWtaInfoForUnit")
    public ResponseEntity<Map<String, Object>> getDefaultWtaInfoForUnit(@PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, wtaService.getDefaultWtaInfoForUnit(unitId));
    }

    @ApiOperation(value = "get Wta By Ids")
    @GetMapping(value =  UNIT_URL + "/unitposition-cta-wta")
    public ResponseEntity<Map<String, Object>> getWTAByIds(@RequestParam List<Long> upIds) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, wtaService.getWTACTAByUpIds(upIds));
    }


    @ApiOperation(value = "assign wta and cta to unitPosition while a position is created")
    @PostMapping(value =  UNIT_URL + "/unitPosition/{unitPositionId}/wta/{wtaId}/cta/{ctaId}")
    public ResponseEntity<Map<String, Object>> assignCTAWTAToUnitPosition(@PathVariable Long unitPositionId,@PathVariable BigInteger wtaId,@PathVariable BigInteger ctaId,
     @RequestParam(value = "startDate") @DateTimeFormat(pattern="yyyy-MM-dd") LocalDate startDate) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, wtaService.assignCTAWTAToUnitPosition(unitPositionId,wtaId,ctaId,startDate));
    }

    @ApiOperation(value = "get wta of unitPosition")
    @GetMapping(value =  UNIT_URL + "/wta/unitPosition/{unitPositionId}")
    public ResponseEntity<Map<String, Object>> getWTAOfUnitPosition(@PathVariable Long unitPositionId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, wtaService.getWTAOfUnitPosition(unitPositionId));
    }




    @ApiOperation(value = "get Wta By Id")
    @GetMapping(value =  UNIT_URL + "/wta/{wtaId}")
    public ResponseEntity<Map<String, Object>> getWTAById(@PathVariable BigInteger wtaId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, wtaService.getWta(wtaId));
    }

    @ApiOperation(value = "get Wta By Ids")
    @PostMapping(value = COUNTRY_URL + "/wta/organization/{unitId}")
    public ResponseEntity<Map<String, Object>> assignWTAToOrganization(@RequestBody List<Long> subTypeIds, @PathVariable Long unitId, @PathVariable Long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, wtaService.assignWTAToNewOrganization(subTypeIds, unitId, countryId));
    }

    @ApiOperation(value = "get Wta with versions By Ids")
    @GetMapping(value =  UNIT_URL + "/wta/versions")
    public ResponseEntity<Map<String, Object>> getWTAWithVersionIds(@PathVariable Long unitId, @RequestParam List<Long> upIds) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, wtaService.getWTAWithVersionIds(unitId, upIds));
    }

    @ApiOperation(value = "copy cta wta to new uniposition")
    @PostMapping(value = "/copy_wta_cta")
    public ResponseEntity<Map<String, Object>> copyCTAWTAForUnitPositionId(@RequestBody List<UnitPositionIdDTO> unitPositionIDs) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, wtaService.copyWtaCTA(unitPositionIDs));
    }
    @ApiOperation(value = "get Wta rule template By wta Id")
    @GetMapping(value =  UNIT_URL + "/wta/{wtaId}/rule_templates")
    public ResponseEntity<Map<String, Object>> getwtaRuletemplates(@PathVariable BigInteger wtaId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, wtaService.getwtaRuletemplates(wtaId));
    }

    @ApiOperation(value = "get current cta and wta applicable on unit position ")
    @GetMapping(value =  UNIT_URL + "/applicable-cta-wta")
    public ResponseEntity<Map<String, Object>> getWTACTAByOfUnitPosition(@RequestParam(value = "unitPositionId") Long unitPositionId, @RequestParam(value = "startDate") @DateTimeFormat(pattern="yyyy-MM-dd") LocalDate startDate) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, wtaService.getWTACTAByOfUnitPosition(unitPositionId,startDate));
    }


    @ApiOperation(value = "assign wta and cta to unitPosition")
    @PostMapping(value =  UNIT_URL + "/unitPosition/{unitPositionId}/apply_cta_wta")
    public ResponseEntity<Map<String, Object>> assignCTAWTAToUnitPosition(@PathVariable Long unitPositionId,
                                                                          @RequestParam(value = "wtaId",required = false)  BigInteger wtaId,
                                                                          @RequestParam(value = "oldwtaId",required = false)  BigInteger oldwtaId,
                                                                          @RequestParam(value = "ctaId",required = false)   BigInteger ctaId,
                                                                          @RequestParam(value = "oldctaId",required = false)   BigInteger oldctaId,
                                                                          @RequestParam(value = "startDate") @DateTimeFormat(pattern="yyyy-MM-dd") LocalDate startDate) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, wtaService.assignCTAWTAToUnitPosition(unitPositionId,wtaId,oldwtaId,ctaId,oldctaId,startDate));
    }


    @ApiOperation(value = "set end date to  wta and cta of unitPosition")
    @PutMapping(value =  UNIT_URL + "/unitPosition/{unitPositionId}/apply_end_date")
    public ResponseEntity<Map<String, Object>> setEndCTAWTAOfUnitPosition(@PathVariable Long unitPositionId,
                                                                          @RequestParam(value = "endDate") @DateTimeFormat(pattern="yyyy-MM-dd") LocalDate endDate) {

        return ResponseHandler.generateResponse(HttpStatus.OK, true, wtaService.setEndCTAWTAOfUnitPosition(unitPositionId,endDate));
    }

   /* @ApiOperation(value = "check scheduler load balncing")
    @GetMapping(value = UNIT_URL + "/check_load_balancing_scheduler")
    public ResponseEntity<Map<String, Object>> checkSchedulerLoadBalancing(@PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, genericIntegrationService.checkSchedulerLoadBalanceWorking());
    }
*/

    @ApiOperation(value = "Create a New WTA at Organization")
    @PostMapping(value =  UNIT_URL+ "/wta")
    public ResponseEntity<Map<String, Object>> createWtaAtOrganization(@PathVariable long unitId, @Validated @RequestBody @Valid WTADTO wta) {
        return ResponseHandler.generateResponse(HttpStatus.CREATED, true, wtaService.createWta(unitId, wta, false,false));
    }
}
