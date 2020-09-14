package com.kairos.controller.tag;

import com.kairos.dto.TranslationInfo;
import com.kairos.dto.user.country.tag.TagDTO;
import com.kairos.enums.MasterDataTypeEnum;
import com.kairos.service.tag.TagService;
import com.kairos.utils.response.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.Map;

import static com.kairos.constants.ApiConstants.*;

/**
 * Created by prerna on 20/11/17.
 */
@RestController
@RequestMapping(API_V1)
@Api(API_V1)
public class TagController {

    private static final Logger LOGGER= LoggerFactory.getLogger(TagController.class);

    @Autowired
    private TagService tagService;

    @ApiOperation(value = "Create a New Tag in Country")
    @RequestMapping(value = COUNTRY_URL + "/tag", method = RequestMethod.POST)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> addCountryTag(@Validated @RequestBody TagDTO tagDTO, @PathVariable long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.CREATED, true,tagService.addCountryTag(countryId,tagDTO));
    }

    @ApiOperation(value = "Update a Country Tag")
    @RequestMapping(value = COUNTRY_URL + "/tag/{tagId}", method = RequestMethod.PUT)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateCountryTag(@Validated @RequestBody TagDTO tagDTO, @PathVariable long countryId, @PathVariable BigInteger tagId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,tagService.updateCountryTag(countryId, tagId, tagDTO));
    }

    @ApiOperation(value = "Get list of Country Tag")
    @RequestMapping(value = COUNTRY_URL + "/tag", method = RequestMethod.GET)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getCountryTag(@PathVariable long countryId,
                                                             @RequestParam(value = "filterText",required = false) String filterText,
                                                             @RequestParam(value = "masterDataType",required = false) MasterDataTypeEnum masterDataType,
                                                             @RequestParam(value = "includeStaffTags",required = false) boolean includeStaffTags) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,tagService.getListOfCountryTags(countryId, filterText, masterDataType, includeStaffTags));
    }

    @ApiOperation(value = "Delete Country Tag")
    @RequestMapping(value = COUNTRY_URL + "/tag/{tagId}", method = RequestMethod.DELETE)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> deleteCountryTag(@PathVariable long countryId, @PathVariable BigInteger tagId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,tagService.deleteCountryTag(countryId, tagId));
    }

    @ApiOperation(value = "Create a New Tag in Organization")
    @RequestMapping(value = UNIT_URL + "/tag", method = RequestMethod.POST)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> addOrganizationTag(@Validated @RequestBody TagDTO tagDTO, @PathVariable long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.CREATED, true,tagService.addOrganizationTag(unitId,tagDTO));
    }

    @ApiOperation(value = "Update a Organization Tag")
    @RequestMapping(value = UNIT_URL + "/tag/{tagId}", method = RequestMethod.PUT)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateOrganizationTag(@Validated @RequestBody TagDTO tagDTO, @PathVariable long unitId, @PathVariable BigInteger tagId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,tagService.updateOrganizationTag(unitId, tagId, tagDTO));
    }

    @ApiOperation(value = "Get list of Organization Tag")
    @RequestMapping(value = UNIT_URL + "/tag", method = RequestMethod.GET)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getOrganizationTag(@PathVariable long unitId,
                                                                  @RequestParam(value = "filterText",required = false) String filterText,
                                                                  @RequestParam(value = "masterDataType",required = false) MasterDataTypeEnum masterDataType,
                                                                  @RequestParam(value = "includeStaffTags",required = false) boolean includeStaffTags) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,tagService.getListOfOrganizationTags(unitId, filterText, masterDataType, includeStaffTags));
    }

    @ApiOperation(value = "Delete Organization Tag")
    @RequestMapping(value = UNIT_URL + "/tag/{tagId}", method = RequestMethod.DELETE)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> deleteOrganizationTag(@PathVariable long unitId, @PathVariable BigInteger tagId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,tagService.deleteOrganizationTag(unitId, tagId));
    }

    /*@ApiOperation(value = "Update a Country Tag Setting ")
    @RequestMapping(value = UNIT_URL + "/tag_setting", method = RequestMethod.PUT)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateCountryTag(@Validated @RequestBody ShowCountryTagSetting showCountryTagSetting, @PathVariable long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,tagService.updateShowCountryTagSettingOfOrganization(unitId, showCountryTagSetting.isShowCountryTags()));
    }*/

    @ApiOperation(value = "Remove Tag from activity")
    @GetMapping(value = UNIT_URL + "/tag/{tagId}/unlink")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> unlinkTagFromActivity(@PathVariable long unitId, @PathVariable BigInteger tagId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,tagService.unlinkTagFromActivity(tagId));
    }

    @ApiOperation(value = "Update translated data")
    @PutMapping(value = COUNTRY_URL + "/tag/{tagId}/language_settings")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateTranslationOfTags(@PathVariable BigInteger tagId, @RequestBody Map<String, TranslationInfo> translations) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,tagService.updateTranslation(tagId,translations));
    }

    @ApiOperation(value = "Update translated data")
    @PutMapping(value = UNIT_URL + "/tag/{tagId}/language_settings")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateTranslationOfTagsOfOrganization(@PathVariable BigInteger tagId, @RequestBody Map<String, TranslationInfo> translations) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,tagService.updateTranslation(tagId,translations));
    }
}
