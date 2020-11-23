package com.kairos.controller.country;


import com.kairos.dto.TranslationInfo;
import com.kairos.dto.user.organization.company_category.CompanyCategoryDTO;
import com.kairos.service.country.CompanyCategoryService;
import com.kairos.service.translation.TranslationService;
import com.kairos.utils.response.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.Map;

import static com.kairos.constants.ApiConstants.API_ORGANIZATION_COUNTRY_URL;
import static com.kairos.constants.ApiConstants.COUNTRY_URL;

/**
 * Created by pavan on 6/4/18.
 */
@RequestMapping(API_ORGANIZATION_COUNTRY_URL)
@Api(API_ORGANIZATION_COUNTRY_URL)
@RestController
public class CompanyCategoryController {
    @Inject
    CompanyCategoryService companyCategoryService;

    @Inject private TranslationService translationService;

    //CompanyCategory

    @ApiOperation(value = "Add CompanyCategory by countryId")
    @RequestMapping(value = "/company_category", method = RequestMethod.POST)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> addCompanyCategory(@PathVariable long countryId,  @RequestBody CompanyCategoryDTO companyCategoryDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, companyCategoryService.createCompanyCategory(countryId, companyCategoryDTO));
    }

    @ApiOperation(value = "Get CompanyCategories by countryId")
    @RequestMapping(value = "/company_categories", method = RequestMethod.GET)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getCompanyCategories(@PathVariable long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, companyCategoryService.getCompanyCategories(countryId));

    }

    @ApiOperation(value = "Update CompanyCategory")
    @RequestMapping(value = "/company_category/{companyCategoryId}", method = RequestMethod.PUT)
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateCompanyCategory(@PathVariable long countryId, @RequestBody CompanyCategoryDTO companyCategoryDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, companyCategoryService.updateCompanyCategory(countryId,companyCategoryDTO));
    }

    @ApiOperation(value = "Delete CompanyCategory by companyCategoryId")
    @RequestMapping(value = "/company_category/{companyCategoryId}", method = RequestMethod.DELETE)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> deleteCompanyCategory(@PathVariable long countryId, @PathVariable long companyCategoryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, companyCategoryService.deleteCompanyCategory(countryId,companyCategoryId));
    }

    @RequestMapping(value="/company_category/{id}/language_settings", method = RequestMethod.PUT)
    @ApiOperation("update translation data")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateTranslationOfEmploymentTypes(@PathVariable Long id, @RequestBody Map<String, TranslationInfo> translations) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, translationService.updateTranslation(id,translations));
    }
}
