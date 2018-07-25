package com.kairos.controller.access_group;

import com.kairos.persistence.model.access_permission.AccessPageDTO;
import com.kairos.persistence.model.access_permission.Tab;
import com.kairos.service.access_permisson.AccessPageService;
import com.kairos.persistence.model.access_permission.AccessPageLanguageDTO;
import com.kairos.user.access_page.OrgCategoryTabAccessDTO;
import com.kairos.user.access_permission.AccessPageStatusDTO;
import com.kairos.util.response.ResponseHandler;
import io.swagger.annotations.Api;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.util.Map;

import static com.kairos.constants.ApiConstants.API_ORGANIZATION_URL;
import static com.kairos.constants.ApiConstants.API_V1;

/**
 * Created by prabjot on 3/1/17.
 */
@RequestMapping(API_ORGANIZATION_URL)
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

    @RequestMapping(value="/country/{countryId}/tab",method = RequestMethod.GET)
    public ResponseEntity<Map<String,Object>> getMainTabs(@PathVariable Long countryId){
        return ResponseHandler.generateResponse(HttpStatus.OK,true,accessPageService.getMainTabs(countryId));
    }

    @RequestMapping(value = "/country/{countryId}/tab/{tabId}/tabs",method = RequestMethod.GET)
    public ResponseEntity<Map<String,Object>> getChildTabs(@PathVariable Long tabId, @PathVariable Long countryId){
        return ResponseHandler.generateResponse(HttpStatus.OK,true,accessPageService.getChildTabs(tabId, countryId));
    }

    @RequestMapping(value = "/tab/{tabId}/status",method = RequestMethod.PUT)
    public ResponseEntity<Map<String,Object>> updateStatusOfTab(@PathVariable Long tabId,
                                                                @Valid @RequestBody AccessPageStatusDTO accessPageStatusDTO){
        return ResponseHandler.generateResponse(HttpStatus.OK,true,accessPageService.updateStatus(accessPageStatusDTO.getActive(),tabId));
    }

    @RequestMapping(value = "/country/{countryId}/tab/{tabId}/access_status",method = RequestMethod.PUT)
    public ResponseEntity<Map<String,Object>> updateAccessStatusOfTab(@PathVariable Long tabId,@PathVariable Long countryId,
                                                                @Valid @RequestBody OrgCategoryTabAccessDTO orgCategoryTabAccessDTO){
        return ResponseHandler.generateResponse(HttpStatus.OK,true,accessPageService.updateAccessForOrganizationCategory(countryId, tabId, orgCategoryTabAccessDTO));
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

    @GetMapping(value = "/country/{countryId}/module/{moduleId}/kpi_details")
    public ResponseEntity<Map<String, Object>> getKPITabsDataForCountry(@PathVariable String moduleId){
        return ResponseHandler.generateResponse(HttpStatus.OK, true, accessPageService.getKPIAccessPageList(moduleId));
    }

    @PutMapping(value = "/country/{countryId}/module/{moduleId}/language")
    public ResponseEntity<Map<String, Object>> addLanguageSpecificData(@PathVariable String moduleId, @RequestBody AccessPageLanguageDTO accessPageLanguageDTO){
        return ResponseHandler.generateResponse(HttpStatus.OK, true, accessPageService.assignLanguageToAccessPage(moduleId, accessPageLanguageDTO));
    }

    @GetMapping(value = "/country/{countryId}/module/{moduleId}/language/{languageId}")
    public ResponseEntity<Map<String, Object>> getLanguageSpecificData(@PathVariable String moduleId, @PathVariable Long languageId){
        return ResponseHandler.generateResponse(HttpStatus.OK, true, accessPageService.getLanguageDataByModuleId(moduleId,languageId));
    }
}
