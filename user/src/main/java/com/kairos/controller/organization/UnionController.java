package com.kairos.controller.organization;

import com.kairos.dto.user.organization.union.LocationDTO;
import com.kairos.dto.user.organization.union.SectorDTO;
import com.kairos.dto.user.organization.union.UnionDTO;
import com.kairos.service.organization.UnionService;
import com.kairos.utils.response.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.util.Map;

import static com.kairos.constants.ApiConstants.*;

/**
 * Created by vipul on 13/2/18.
 */
@RestController

@RequestMapping(API_V1)
@Api(API_V1)
public class UnionController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Inject
    private UnionService unionService;

    @RequestMapping(value =COUNTRY_URL+"/unions", method = RequestMethod.GET)
    @ApiOperation("Get All Unions")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getAllUnionOfCountry(@PathVariable long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                unionService.getUnionData(countryId));
    }
    @PutMapping(COUNTRY_URL+"/sectors/{sectorId}")
    @ApiOperation("Update sector")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateSector(@PathVariable long sectorId,@RequestBody SectorDTO sectorDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                unionService.updateSector(sectorDTO,sectorId));
    }
    @GetMapping(value = COUNTRY_URL+"/sectors")
    @ApiOperation("Get All sectors")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getAllSectors(@PathVariable long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                unionService.findAllSectorsByCountry(countryId));
    }
    @PostMapping(value = COUNTRY_URL+"/sectors")
    @ApiOperation("Create sector")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> createSector(@PathVariable long countryId,@RequestBody SectorDTO sectorDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                unionService.createSector(sectorDTO,countryId));
    }
    @DeleteMapping(value = COUNTRY_URL+"/sectors/{sectorId}")
    @ApiOperation("delete sector")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> deleteSector(@PathVariable long sectorId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                unionService.deleteSector(sectorId));
    }


    @PutMapping(COUNTRY_URL+"/unions/{unionId}/locations/{locationId}")
    @ApiOperation("Update location")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateLocation(@PathVariable long locationId,@PathVariable long unionId, @RequestBody LocationDTO locationDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                unionService.updateLocation(locationDTO,unionId,locationId));
    }
    @GetMapping(value =COUNTRY_URL+"/unions/{unionId}/locations")
    @ApiOperation("Get All locations")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getAllLocations(@PathVariable long unionId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                unionService.findAllLocationsByUnion(unionId));
    }
    @PostMapping(value = COUNTRY_URL+"/unions/{unionId}/locations")
    @ApiOperation("Create location")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> createLocation(@PathVariable long unionId,@RequestBody LocationDTO locationDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                unionService.createLocation(locationDTO,unionId));
    }
    @DeleteMapping(value = COUNTRY_URL+"/unions/{unionId}/locations/{locationId}")
    @ApiOperation("delete location")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> deleteLocation(@PathVariable long locationId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                unionService.deleteLocation(locationId));
    }

    @ApiOperation(value = "Create a Union")
    @RequestMapping(value = COUNTRY_URL + "/unions", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> createUnion(
            @PathVariable long countryId,
            @Valid @RequestBody UnionDTO unionDTO, @RequestParam(required = false) boolean publish) {
        return ResponseHandler.generateResponse(HttpStatus.CREATED, true, unionService.
                createUnion(unionDTO, countryId,publish));
    }

    @ApiOperation(value = "Update Union")
    @RequestMapping(value = COUNTRY_URL + "/unions/{unionId}", method = RequestMethod.PUT)
    public ResponseEntity<Map<String, Object>> updateUnion(@PathVariable long countryId, @PathVariable long unionId, @Valid @RequestBody UnionDTO unionDTO,@RequestParam(required = false) boolean publish) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, unionService.updateUnion(unionDTO,countryId,unionId,publish));
    }
  // TODO NEED TO IMPLEMENT IN FUTURE
//    @RequestMapping(value = UNIT_URL + "/unions", method = RequestMethod.GET)
//    @ApiOperation("Get All Unions by organization ")
//    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
//    public ResponseEntity<Map<String, Object>> getAllApplicableUnionsForOrganization(@PathVariable Long unitId) {
//        return ResponseHandler.generateResponse(HttpStatus.OK, true,
//                unionService.getAllApplicableUnionsForOrganization(unitId));
//    }
//    @RequestMapping(value = UNIT_URL + "/unions/{unionId}", method = RequestMethod.GET)
//    @ApiOperation("Get All Unions by organization ")
//    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
//    public ResponseEntity<Map<String, Object>> addUnionInOrganization(@PathVariable Long unitId,@PathVariable Long unionId,@RequestParam("joined") boolean joined) {
//        return ResponseHandler.generateResponse(HttpStatus.OK, true,
//                unionService.addUnionInOrganization(unitId,unionId,joined));
//    }
}
