package com.kairos.controller.country.tag;

import com.kairos.dto.user.country.tag.ShowCountryTagSetting;
import com.kairos.dto.user.country.tag.TagDTO;
import com.kairos.enums.MasterDataTypeEnum;
import com.kairos.service.country.tag.TagService;
import com.kairos.utils.response.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.Map;

import static com.kairos.constants.ApiConstants.*;

/**
 * Created by prerna on 10/11/17.
 */
@RestController

@RequestMapping(API_V1)
@Api(API_V1)
public class TagController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Inject
    private TagService tagService;

    @ApiOperation(value = "Create a New Tag in Country")
    @PostMapping(value = COUNTRY_URL + "/tag")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> addCountryTag(@Validated @RequestBody TagDTO tagDTO, @PathVariable long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.CREATED, true,tagService.addCountryTag(countryId,tagDTO));
    }

    @ApiOperation(value = "Update a Country Tag")
    @PutMapping(value = COUNTRY_URL + "/tag/{tagId}")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateCountryTag(@Validated @RequestBody TagDTO tagDTO, @PathVariable long countryId, @PathVariable long tagId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,tagService.updateCountryTag(countryId, tagId, tagDTO));
    }

    @ApiOperation(value = "Get list of Country Tag")
    @GetMapping(value = COUNTRY_URL + "/tag")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getCountryTag(@PathVariable long countryId,
                                                             @RequestParam(value = "filterText",required = false) String filterText,
                                                             @RequestParam(value = "masterDataType",required = false) MasterDataTypeEnum masterDataType) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,tagService.getListOfCountryTags(countryId, filterText, masterDataType));
    }

    @ApiOperation(value = "Delete Country Tag")
    @DeleteMapping(value = COUNTRY_URL + "/tag/{tagId}")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> deleteCountryTag(@PathVariable long countryId, @PathVariable long tagId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,tagService.deleteCountryTag(countryId, tagId));
    }

    @ApiOperation(value = "Create a New Tag in Organization")
    @PostMapping(value = UNIT_URL + "/tag")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> addOrganizationTag(@Validated @RequestBody TagDTO tagDTO, @PathVariable long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.CREATED, true,tagService.addOrganizationTag(unitId,tagDTO));
    }

    @ApiOperation(value = "Update a Organization Tag")
    @PutMapping(value = UNIT_URL + "/tag/{tagId}")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateOrganizationTag(@Validated @RequestBody TagDTO tagDTO, @PathVariable long unitId, @PathVariable long tagId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,tagService.updateOrganizationTag(unitId, tagId, tagDTO));
    }


    @ApiOperation(value = "Get list of Organization Tag")
    @GetMapping(value = UNIT_URL + "/tag")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getOrganizationTag(@PathVariable long unitId,
                                                                  @RequestParam(value = "filterText",required = false) String filterText,
                                                                  @RequestParam(value = "masterDataType",required = false) MasterDataTypeEnum masterDataType) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,tagService.getListOfOrganizationTags(unitId, filterText, masterDataType));
    }

    @ApiOperation(value = "Delete Organization Tag")
    @DeleteMapping(value = UNIT_URL + "/tag/{tagId}")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> deleteOrganizationTag(@PathVariable long unitId, @PathVariable long tagId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,tagService.deleteOrganizationTag(unitId, tagId));
    }

    @ApiOperation(value = "Update a Country Tag Setting ")
    @PutMapping(value = UNIT_URL + "/tag_setting")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateCountryTag(@Validated @RequestBody ShowCountryTagSetting showCountryTagSetting, @PathVariable long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,tagService.updateShowCountryTagSettingOfOrganization(unitId, showCountryTagSetting.isShowCountryTags()));
    }

    // TO get tags of skill
    @ApiOperation(value = "Get list of Tags of Skill")
    @GetMapping(value = COUNTRY_URL + "/skill/{skillId}/tag")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getTagsOfSkill(@PathVariable long countryId, @PathVariable long skillId,
                                                             @RequestParam(value = "filterText",required = false) String filterText) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,tagService.getCountryTagsOfSkill(countryId, skillId, filterText));
    }

    @ApiOperation(value = "Get list of Tags of Expertise")
    @GetMapping(value = COUNTRY_URL + "/expertise/{expertiseId}/tag")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getTagsOfExpertise(@PathVariable long countryId, @PathVariable long expertiseId,
                                                              @RequestParam(value = "filterText",required = false) String filterText) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,tagService.getCountryTagsOfExpertise(countryId, expertiseId, filterText));
    }


    @ApiOperation(value = "Get list of Tags of rule Template Category")
    @GetMapping(value = COUNTRY_URL + "/rule_template_category/{ruleTemplateCategoryId}/tag")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getTagsOfRuleTemplateCategory(@PathVariable long countryId, @PathVariable long ruleTemplateCategoryId,
                                                              @RequestParam(value = "filterText",required = false) String filterText) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,tagService.getCountryTagsOfRuleTemplateCategory(countryId, ruleTemplateCategoryId, filterText));
    }

    @ApiOperation(value = "Get list of Tags Category")
    @GetMapping(value = "/tag_category")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getTagsCategory() {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,tagService.getListOfMasterDataType());
    }

    @ApiOperation(value = "Get list of Tags Category for organization")
    @GetMapping(value = UNIT_URL + "/tag_category")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getTagsCategoryForOrganization(@PathVariable long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,tagService.getListOfMasterDataType(unitId));
    }


}
