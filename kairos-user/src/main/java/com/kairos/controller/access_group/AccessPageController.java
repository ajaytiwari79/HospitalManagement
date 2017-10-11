package com.kairos.controller.access_group;

import com.kairos.persistence.model.user.access_permission.AccessPage;
import com.kairos.persistence.model.user.access_permission.AccessPageDTO;
import com.kairos.persistence.model.user.access_permission.AccessPageStatusDTO;
import com.kairos.persistence.model.user.access_permission.Tab;
import com.kairos.service.access_permisson.AccessPageService;
import com.kairos.util.response.ResponseHandler;
import io.swagger.annotations.Api;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.util.Map;

import static com.kairos.constants.ApiConstants.API_ORGANIZATION_UNIT_URL;
import static com.kairos.constants.ApiConstants.API_ORGANIZATION_URL;
import static com.kairos.constants.ApiConstants.API_V1;

/**
 * Created by prabjot on 3/1/17.
 */
@RequestMapping(API_ORGANIZATION_URL+ "/tab")
@Api(value = API_V1)
@RestController
public class AccessPageController {

    @Inject
    private AccessPageService accessPageService;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Map<String,Object>> createAccessPage(@Valid @RequestBody AccessPageDTO accessPageDTO){
        return ResponseHandler.generateResponse(HttpStatus.CREATED,true,accessPageService.createAccessPage(accessPageDTO));
    }

    @RequestMapping(value = "/{tabId}",method = RequestMethod.PUT)
    public ResponseEntity<Map<String,Object>> createAccessPage(@PathVariable Long tabId,
                                                               @Valid @RequestBody AccessPageDTO accessPageDTO){
        return ResponseHandler.generateResponse(HttpStatus.OK,true,accessPageService.updateAccessPage(tabId,accessPageDTO));
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<Map<String,Object>> getMainTabs(){
        return ResponseHandler.generateResponse(HttpStatus.OK,true,accessPageService.getMainTabs());
    }

    @RequestMapping(value = "/{tabId}/tabs",method = RequestMethod.GET)
    public ResponseEntity<Map<String,Object>> getChildTabs(@PathVariable Long tabId){
        return ResponseHandler.generateResponse(HttpStatus.OK,true,accessPageService.getChildTabs(tabId));
    }

    @RequestMapping(value = "/{tabId}/status",method = RequestMethod.PUT)
    public ResponseEntity<Map<String,Object>> updateStatusOfTab(@PathVariable Long tabId,
                                                                @Valid @RequestBody AccessPageStatusDTO accessPageStatusDTO){
        return ResponseHandler.generateResponse(HttpStatus.OK,true,accessPageService.updateStatus(accessPageStatusDTO.getActive(),tabId));
    }


    @RequestMapping(value = "/xml" ,method = RequestMethod.POST,consumes = {MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Map<String,Object>> parseXml(@RequestBody Tab tab){
        accessPageService.createAccessPageByXml(tab);
        return ResponseHandler.generateResponse(HttpStatus.OK,true,true);
    }

    @RequestMapping(value = "/page/permissions",method = RequestMethod.POST)
    public ResponseEntity<Map<String,Object>> setPermissionsToPage(){
        accessPageService.setPermissionToAccessPage();
        return ResponseHandler.generateResponse(HttpStatus.OK,true,true);
    }
}
