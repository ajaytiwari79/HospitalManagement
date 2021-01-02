package com.kairos.controller.access_group;

import com.kairos.dto.TranslationInfo;
import com.kairos.dto.user.access_page.OrgCategoryTabAccessDTO;
import com.kairos.dto.user.access_permission.AccessPageStatusDTO;
import com.kairos.persistence.model.access_permission.AccessPageDTO;
import com.kairos.persistence.model.access_permission.AccessPageLanguageDTO;
import com.kairos.persistence.model.access_permission.Tab;
import com.kairos.service.access_permisson.AccessPageService;
import com.kairos.utils.response.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Map;

import static com.kairos.constants.ApiConstants.*;

/**
 * Created by prabjot on 3/1/17.
 */
@RequestMapping(API_V1)
@Api(value = API_V1)
@RestController
public class AccessPageController {

    @Inject
    private AccessPageService accessPageService;

    @RequestMapping(value="/tab", method = RequestMethod.POST)
    public ResponseEntity<Map<String,Object>> createAccessPage(@Valid @RequestBody AccessPageDTO accessPageDTO){
        return ResponseHandler.generateResponse(HttpStatus.CREATED,true,accessPageService.createAccessPage(accessPageDTO));
    }

    @RequestMapping(value = "/tab/{tabId}",method = RequestMethod.PUT)
    public ResponseEntity<Map<String,Object>> createAccessPage(@PathVariable Long tabId,
                                                               @Valid @RequestBody AccessPageDTO accessPageDTO){
        return ResponseHandler.generateResponse(HttpStatus.OK,true,accessPageService.updateAccessPage(tabId,accessPageDTO));
    }

    @RequestMapping(value="/tab",method = RequestMethod.GET)
    public ResponseEntity<Map<String,Object>> getMainTabs(){
        return ResponseHandler.generateResponse(HttpStatus.OK,true,accessPageService.getMainTabs());
    }

    @RequestMapping(value=UNIT_URL+"/tab",method = RequestMethod.GET)
    public ResponseEntity<Map<String,Object>> getMainTabsForUnit(@PathVariable Long unitId){
        return ResponseHandler.generateResponse(HttpStatus.OK,true,accessPageService.getMainTabsForUnit(unitId));
    }
    @RequestMapping(value = "/tab/{tabId}/tabs",method = RequestMethod.GET)
    public ResponseEntity<Map<String,Object>> getChildTabs(@PathVariable Long tabId){
        return ResponseHandler.generateResponse(HttpStatus.OK,true,accessPageService.getChildTabs(tabId));
    }

    @RequestMapping(value = "/tab/{tabId}/status",method = RequestMethod.PUT)
    public ResponseEntity<Map<String,Object>> updateStatusOfTab(@PathVariable Long tabId,
                                                                @Valid @RequestBody AccessPageStatusDTO accessPageStatusDTO){
        return ResponseHandler.generateResponse(HttpStatus.OK,true,accessPageService.updateStatus(accessPageStatusDTO.getActive(),tabId));
    }

    @RequestMapping(value = "/tab/{tabId}/access_status",method = RequestMethod.PUT)
    public ResponseEntity<Map<String,Object>> updateAccessStatusOfTab(@PathVariable Long tabId,
                                                                @Valid @RequestBody OrgCategoryTabAccessDTO orgCategoryTabAccessDTO){
        return ResponseHandler.generateResponse(HttpStatus.OK,true,accessPageService.updateAccessForOrganizationCategory(tabId, orgCategoryTabAccessDTO));
    }


    @RequestMapping(value = "/tab/xml" ,method = RequestMethod.POST,consumes = {MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Map<String,Object>> parseXml(@RequestBody Tab tab){
        accessPageService.createAccessPageByXml(tab);
        return ResponseHandler.generateResponse(HttpStatus.OK,true,true);
    }

    @RequestMapping(value = "/tab/page/permissions",method = RequestMethod.POST)
    public ResponseEntity<Map<String,Object>> setPermissionsToPage(){
        accessPageService.setPermissionToAccessPage();
        return ResponseHandler.generateResponse(HttpStatus.OK,true,true);
    }

    @GetMapping(value = COUNTRY_URL+"/kpi_details")
    public ResponseEntity<Map<String, Object>> getKPITabsDataForModuleForCountry(@PathVariable Long countryId){
        return ResponseHandler.generateResponse(HttpStatus.OK, true, accessPageService.getKPIAccessPageListForCountry(countryId));
    }

    @GetMapping(value = UNIT_URL+"/kpi_details")
    public ResponseEntity<Map<String, Object>> getKPITabsDataForModuleForUnit(@PathVariable Long unitId){
        return ResponseHandler.generateResponse(HttpStatus.OK, true, accessPageService.getKPIAccessPageListForUnit(unitId));
    }

    @GetMapping(value = "/module/{moduleId}/kpi_details")
    public ResponseEntity<Map<String, Object>> getKPITabsDataForModule(@PathVariable String moduleId){
        return ResponseHandler.generateResponse(HttpStatus.OK, true, accessPageService.getKPIAccessPageList(moduleId));
    }

    @PutMapping(value = "/module/{moduleId}/language")
    public ResponseEntity<Map<String, Object>> addLanguageSpecificData(@PathVariable String moduleId, @RequestBody AccessPageLanguageDTO accessPageLanguageDTO){
        return ResponseHandler.generateResponse(HttpStatus.OK, true, accessPageService.assignLanguageToAccessPage(moduleId, accessPageLanguageDTO));
    }

    @GetMapping(value = "/module/{moduleId}/language/{languageId}")
    public ResponseEntity<Map<String, Object>> getLanguageSpecificData(@PathVariable String moduleId, @PathVariable Long languageId){
        return ResponseHandler.generateResponse(HttpStatus.OK, true, accessPageService.getLanguageDataByModuleId(moduleId,languageId));
    }

    @ApiOperation(value = "add translated data")
    @PostMapping(COUNTRY_URL + "/access_page/{accessPageId}/update_translation")
    public ResponseEntity<Map<String, Object>> updateTranslationOfaccesspage(@PathVariable String moduleId, @RequestBody Map<String, TranslationInfo> translations) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, accessPageService.updateTranslation(moduleId, translations));
    }

    @ApiOperation(value = "get translated data")
    @GetMapping(COUNTRY_URL + "/access_page/{accessPageId}/translation")
    public ResponseEntity<Map<String, Object>> getTranslatedData(@PathVariable Long accessPageId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, accessPageService.getTranslatedData(accessPageId));
    }

    @ApiOperation(value = "get tab hierarchy")
    @GetMapping("/tab_hierarchy")
    public ResponseEntity<Map<String, Object>> getTabHierarchy(@RequestParam("languageId") Long languageId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, accessPageService.getTabHierarchy(languageId));
    }

    @ApiOperation(value = "set ulr in  tab hierarchy")
    @PutMapping("/set_url_in_pages")
    public ResponseEntity<Map<String, Object>> setUrlInTabHierarchy() throws IOException {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, accessPageService.setUrlInAccessPages());
    }

    @ApiOperation(value = "add translated data")
    @PutMapping(COUNTRY_URL + "/access_page//module/{moduleId}/language_settings")
    public ResponseEntity<Map<String, Object>> updateTranslationOfKpiAccessPages(@PathVariable String moduleId, @RequestBody Map<String, TranslationInfo> translations) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, accessPageService.updateTranslation(moduleId, translations));
    }

    @ApiOperation(value = "add translated data")
    @PutMapping(UNIT_URL + "/access_page//module/{moduleId}/language_settings")
    public ResponseEntity<Map<String, Object>> updateTranslationOfKpiAccessPagesAtUnit(@PathVariable String moduleId, @RequestBody Map<String, TranslationInfo> translations) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, accessPageService.updateTranslation(moduleId, translations));
    }
}
