package com.kairos.controller.region;
import com.kairos.service.region.MunicipalityService;
import com.kairos.utils.response.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.Map;

import static com.kairos.constants.ApiConstants.API_ORGANIZATION_UNIT_URL;
import static com.kairos.constants.ApiConstants.API_V1;


/**
 * Created by oodles on 22/12/16.
 */
@RestController
@RequestMapping(API_ORGANIZATION_UNIT_URL)
@Api(API_V1+"/municipality")
public class MunicipalityController {


    @Inject
    private MunicipalityService municipalityService;


//
//    @ApiOperation(value = "Get All Municipality")
//    @RequestMapping(value = "", method = RequestMethod.GET)
//    public ResponseEntity<Map<String, Object>> getAllMunicipality() {
//        return ResponseHandler.generateResponse(HttpStatus.OK, true, municipalityService.findAllMunicipality());
//    }
//
//    @ApiOperation(value = "Get municipality by Id")
//    @RequestMapping(value = "/{municipalityId}", method = RequestMethod.GET)
//    public ResponseEntity<Map<String, Object>> getMunicipalityById(@PathVariable Long municipalityId) {
//        if (municipalityId != null) {
//            return ResponseHandler.generateResponse(HttpStatus.OK, true, municipalityService.findMunicipalityById(municipalityId));
//        }
//        return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false, null);
//    }
//
//
//    @ApiOperation(value = "Create municipality")
//    @RequestMapping(value = "", method = RequestMethod.POST)
//    public ResponseEntity<Map<String, Object>> createMunicipality(@RequestBody Municipality municipality) {
//        if (municipality != null) {
//            return ResponseHandler.generateResponse(HttpStatus.OK, true, municipalityService.createMunicipality(municipality));
//        }
//        return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false, null);
//    }
//
//
//
//
//
//    @ApiOperation(value = "Add City to Municipality")
//    @RequestMapping(value = "/{municipalityId}/city", method = RequestMethod.POST)
//    public ResponseEntity<Map<String, Object>> addCityToMunicipality(@PathVariable Long municipalityId , @RequestBody City city) {
//        if (city != null) {
//            return ResponseHandler.generateResponse(HttpStatus.OK, true, municipalityService.addCityToMunicipality(municipalityId, city));
//        }
//        return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false, null);
//    }


    @ApiOperation(value = "get muncipalities by zip code value")
    @RequestMapping(value = "/zipcode/{zipCode}/municipality", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> addCityToMunicipality(@PathVariable int zipCode) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, municipalityService.getMunicipalitiesByZipCode(zipCode));
    }
}
