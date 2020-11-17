package com.kairos.controller.system_setting;

import com.kairos.dto.TranslationInfo;
import com.kairos.dto.user.country.system_setting.SystemLanguageDTO;
import com.kairos.service.system_setting.SystemLanguageService;
import com.kairos.utils.response.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.Map;

import static com.kairos.constants.ApiConstants.API_V1;
import static com.kairos.constants.ApiConstants.COUNTRY_URL;

@Controller
@RequestMapping(API_V1 )
@Api(value = API_V1 )
public class SystemLanguageController {

    @Inject
    private SystemLanguageService systemLanguageService;

    @PostMapping(value = "/system_language")
    @ApiOperation("To add System Language")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> addSystemLanguage(@RequestBody SystemLanguageDTO systemLanguageDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, systemLanguageService.addSystemLanguage(systemLanguageDTO));
    }

    @PutMapping(value = "/system_language/{systemLanguageId}")
    @ApiOperation("To update System Language")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateSystemLanguage(@RequestBody SystemLanguageDTO systemLanguageDTO, @PathVariable Long systemLanguageId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, systemLanguageService.updateSystemLanguage(systemLanguageId,systemLanguageDTO));
    }

    @GetMapping(value = "/system_language")
    @ApiOperation("To fetch System Language")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getSystemLanguage() {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, systemLanguageService.getListOfSystemLanguage());
    }

    @DeleteMapping(value = "/system_language/{systemLanguageId}")
    @ApiOperation("To delete System Language")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> deleteSystemLanguage(@PathVariable Long systemLanguageId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, systemLanguageService.deleteSystemLanguage(systemLanguageId));
    }

    @PutMapping(value = COUNTRY_URL + "/system_language/{systemLanguageId}")
    @ApiOperation("To update System Language of Country")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    // it use for delete and assign system language and set default language of country
    public ResponseEntity<Map<String, Object>> updateSystemLanguageOfCountry(@PathVariable Long countryId, @PathVariable Long systemLanguageId,@RequestParam(value = "defaultLanguage",required=false) Boolean defaultLanguage,@RequestParam(value = "selected",required=false) Boolean selected) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, systemLanguageService.updateSystemLanguageOfCountry(countryId, systemLanguageId,defaultLanguage,selected));
    }

    @GetMapping(value = COUNTRY_URL + "/system_language" )
    @ApiOperation("To get System Language of Country")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getSystemLanguageOfCountry(@PathVariable Long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, systemLanguageService.getSystemLanguageOfCountry(countryId));
    }

    @GetMapping(value =COUNTRY_URL+"/system_language_mapping")
    @ApiOperation("To get System Language of Country")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getSystemLanguageAndCounryMapping(@PathVariable Long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, systemLanguageService.getSystemLanguageAndCountryMapping(countryId));
    }


    @PutMapping(value = "system_language/{id}/language_settings")
    @ApiOperation("update translation data")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateTranslationOfSystemLanguage(@PathVariable Long id, @RequestBody Map<String, TranslationInfo> translations) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, systemLanguageService.updateTranslation(id,translations));
    }

    @PutMapping(value = COUNTRY_URL+ "/system_language/{id}/language_settings")
    @ApiOperation("update translation data")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateTranslationOfSystemLanguageOnCountryLevel(@PathVariable Long id, @RequestBody Map<String, TranslationInfo> translations) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, systemLanguageService.updateTranslation(id,translations));
    }


}
