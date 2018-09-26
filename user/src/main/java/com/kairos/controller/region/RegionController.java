package com.kairos.controller.region;

import com.kairos.persistence.model.user.region.Municipality;
import com.kairos.persistence.model.user.region.Province;
import com.kairos.persistence.model.user.region.Region;
import com.kairos.persistence.model.user.region.ZipCode;
import com.kairos.service.region.MunicipalityService;
import com.kairos.service.region.ProvinceService;
import com.kairos.service.region.RegionService;
import com.kairos.utils.response.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.inject.Inject;
import java.util.Map;

import static com.kairos.constants.ApiConstants.API_ORGANIZATION_URL;
import static com.kairos.constants.ApiConstants.COUNTRY_URL;


/**
 * Created by oodles on 22/12/16.
 */
@RestController
@RequestMapping(API_ORGANIZATION_URL+COUNTRY_URL+"/region")
@Api(API_ORGANIZATION_URL+COUNTRY_URL+"/region")
public class RegionController {

    @Inject
    private RegionService regionService;

    @Inject
    private ProvinceService provinceService;

    @Inject
    private MunicipalityService municipalityService;



    @ApiOperation(value = "Get All Region")
    @RequestMapping(value = "", method = RequestMethod.GET)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getAllRegion(@PathVariable Long countryId) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true, regionService.getRegionByCountryId(countryId));
    }

    @ApiOperation(value = "Add Region ")
    @RequestMapping(value = "", method = RequestMethod.POST)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> addRegionById(@PathVariable Long countryId,@RequestBody @Validated Region region) {
        if (region != null) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true, regionService.createRegionOfCountry(countryId,region));
        }
        return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false, null);
    }


    @ApiOperation(value = "Update Region by Id")
    @RequestMapping(value = "", method = RequestMethod.PUT)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateRegionById(@RequestBody @Validated Region region) {
        if (region != null) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true, regionService.updateRegionById(region));
        }
        return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false, null);
    }

    @ApiOperation(value = "Delete Region by Id")
    @RequestMapping(value = "/{regionId}", method = RequestMethod.DELETE)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> deleteRegionById(@PathVariable long regionId) {
           return ResponseHandler.generateResponse(HttpStatus.OK, true, regionService.deleteRegion(regionId));

    }




    // Province
    @ApiOperation(value = "Add Province to Region")
    @RequestMapping(value = "/{regionId}/province", method = RequestMethod.POST)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> addProvinceToRegion(@PathVariable Long regionId, @Validated @RequestBody Province province) {
        if (province != null) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true, provinceService.addProvinceToRegion(province,regionId));
        }
        return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false, null);
    }

    @ApiOperation(value = "GET Province to Region")
    @RequestMapping(value = "/{regionId}/province", method = RequestMethod.GET)
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getProvinceToRegion(@PathVariable Long regionId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, provinceService.getProvinceToRegion(regionId));
    }

    @ApiOperation(value = "Update Province by Id")
    @RequestMapping(value = "/province", method = RequestMethod.PUT)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateProvinceById(@RequestBody @Validated Province province) {
        if (province != null) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true, provinceService.updateProvinceById(province));
        }
        return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false, null);
    }

    @ApiOperation(value = "Delete Province by Id")
    @RequestMapping(value = "/province/{provinceId}", method = RequestMethod.DELETE)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> deleteProvinceById(@PathVariable long provinceId) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true, provinceService.deleteProvinceById(provinceId));
    }





    // Municipality
    @ApiOperation(value = "Add Municipality to Province")
    @RequestMapping(value = "/province/{provinceId}/municipality", method = RequestMethod.POST)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> addMunicipalityToProvince(@PathVariable long provinceId, @Validated @RequestBody Municipality municipality) {
        if (municipality != null) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true, municipalityService.addMunicipalityToProvince(municipality,provinceId));
        }
        return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false, null);
    }

    @ApiOperation(value = "GET Municipality to Province")
    @RequestMapping(value = "/province/{provinceId}/municipality", method = RequestMethod.GET)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getMunicipalityToProvince(@PathVariable long provinceId) {
           return ResponseHandler.generateResponse(HttpStatus.OK, true, municipalityService.getMunicipalityToProvince(provinceId));
    }

    @ApiOperation(value = "Update municipality by Id")
    @RequestMapping(value = "/province/municipality", method = RequestMethod.PUT)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateMunicipalityById(@RequestBody @Validated Municipality municipality) {
        if (municipality != null) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true, municipalityService.updateMunicipalityById(municipality));
        }
        return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false, null);
    }
    @ApiOperation(value = "Delete municipality by Id")
    @RequestMapping(value = "/province/municipality/{municipalityId}", method = RequestMethod.DELETE)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> deleteMunicipalityById(@PathVariable long municipalityId) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true, municipalityService.deleteMunicipalityById(municipalityId));
    }



    // ZipCode
    @ApiOperation(value = "GET all Zip to Municipality")
    @RequestMapping(value = "/province/municipality/{municipalityId}/zipcode", method = RequestMethod.GET)
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getAllZipCodeToMunicipality(@PathVariable Long municipalityId) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true,
                    municipalityService.getAllZipCodeToMunicipality(municipalityId));
    }
    @ApiOperation(value = "Add Zip to Municipality")
    @RequestMapping(value = "/province/municipality/{municipalityId}/zipcode", method = RequestMethod.POST)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> addZipCodeToMunicipality(@PathVariable Long municipalityId , @Validated @RequestBody ZipCode zipCode) {
        if (zipCode != null) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true, municipalityService.addZipCodeToMunicipality(municipalityId,zipCode));
        }
        return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false, null);
    }

    @ApiOperation(value = "Update Zip to Municipality")
    @RequestMapping(value = "/province/municipality/zipcode", method = RequestMethod.PUT)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateZipCodeToMunicipality(@RequestBody @Validated ZipCode zipCode) {
        if (zipCode != null) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true, municipalityService.updateZipCodeToMunicipality(zipCode));
        }
        return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false, null);
    }


    @ApiOperation(value = "Delete Zip to Municipality")
    @RequestMapping(value = "/province/municipality/zipcode/{zipCodeId}", method = RequestMethod.DELETE)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> deleteZipCodeToMunicipality(@PathVariable long zipCodeId) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true, municipalityService.deleteZipCodeToMunicipality(zipCodeId));
    }




    // All Region by MunicipalityID
    @ApiOperation(value = "GET all Region Data")
    @RequestMapping(value = "/municipality/{municipalityId}", method = RequestMethod.GET)
  //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getAllRegionMunicipalityId(@PathVariable long municipalityId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, regionService.getRegionByMunicipalityId(municipalityId));
    }

    // All Municipality by zipCode
    @ApiOperation(value = "GET all Municipality Data")
    @RequestMapping(value = "/municipality/zipCode/{zipCodeId}", method = RequestMethod.GET)
  //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getMunicipalityByZipCode(@PathVariable long zipCodeId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, regionService.getMunicipalityByZipCode(zipCodeId));
    }


    // All  ZipCode
    @ApiOperation(value = "GET all ZipCode Data")
    @RequestMapping(value = "/municipality/zipCode/", method = RequestMethod.GET)
  //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getAllZipCode(@PathVariable long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, regionService.getAllZipCodes());
    }



    // All Region Data
    @ApiOperation(value = "GET all Region Data")
    @RequestMapping(value = "/all", method = RequestMethod.GET)
   // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getAllRegionData() {
            return ResponseHandler.generateResponse(HttpStatus.OK, true, regionService.getAllRegionData());
    }



    // Xls Region Data upload
    @ApiOperation(value = "Upload all Region Data via XLSX")
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
   // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> uploadRegionXLSX(@PathVariable Long countryId, @RequestParam("file") MultipartFile multipartFile) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, regionService.batchProcessGeographyExcelSheet(multipartFile,countryId));
    }


    // Get Address data using zipcodeId
    @ApiOperation(value = "GET all ZipCode Data")
    @RequestMapping(value = "/municipality/zipCode/{zipCodeId}/data", method = RequestMethod.GET)
   // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getAllZipCodeData(@PathVariable long zipCodeId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, regionService.getAllZipCodesData(zipCodeId));
    }

    @ApiOperation(value = "GET all ZipCode Data")
    @RequestMapping(value = "/contact_address/municipality", method = RequestMethod.POST)
   // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> setMunicipalityInContactAddress() {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, regionService.setMunicipalityInContactAddress());
    }





}
