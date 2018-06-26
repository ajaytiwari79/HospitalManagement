package com.kairos.controller.system_setting;

import com.kairos.response.dto.web.system_setting.SystemLanguageDTO;
import com.kairos.service.system_setting.SystemLanguageService;
import com.kairos.util.response.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.inject.Inject;
import java.util.Map;

import static com.kairos.constants.ApiConstants.API_ORGANIZATION_URL;
import static com.kairos.constants.ApiConstants.COUNTRY_URL;

@Controller
@RequestMapping(API_ORGANIZATION_URL )
@Api(value = API_ORGANIZATION_URL )
public class SystemLanguageController {

    @Inject
    private SystemLanguageService systemLanguageService;

    @RequestMapping(value = "/system_language", method = RequestMethod.POST)
    @ApiOperation("To add System Language")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> addSystemLanguage(@RequestBody SystemLanguageDTO systemLanguageDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, systemLanguageService.addSystemLanguage(systemLanguageDTO));
    }

    @RequestMapping(value = "/system_language/{systemLanguageId}", method = RequestMethod.PUT)
    @ApiOperation("To update System Language")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateSystemLanguage(@RequestBody SystemLanguageDTO systemLanguageDTO, @PathVariable Long systemLanguageId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, systemLanguageService.updateSystemLanguage(systemLanguageId,systemLanguageDTO));
    }

    @RequestMapping(value = "/system_language", method = RequestMethod.GET)
    @ApiOperation("To fetch System Language")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getSystemLanguage() {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, systemLanguageService.getListOfSystemLanguage());
    }

    @RequestMapping(value = "/system_language/{systemLanguageId}", method = RequestMethod.DELETE)
    @ApiOperation("To delete System Language")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> deleteSystemLanguage(@PathVariable Long systemLanguageId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, systemLanguageService.deleteSystemLanguage(systemLanguageId));
    }

    @RequestMapping(value = COUNTRY_URL + "/system_language/{systemLanguageId}", method = RequestMethod.PUT)
    @ApiOperation("To update System Language of Country")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateSystemLanguageOfCountry(@PathVariable Long countryId, @PathVariable Long systemLanguageId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, systemLanguageService.updateSystemLanguageOfCountry(countryId, systemLanguageId));
    }


}
