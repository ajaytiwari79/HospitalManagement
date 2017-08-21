package com.kairos.controller.access_group;
import com.kairos.persistence.model.user.access_permission.AccessPage;
import com.kairos.persistence.model.user.access_permission.Tab;
import com.kairos.service.access_profile.AccessPageService;
import com.kairos.utils.response.ResponseHandler;
import io.swagger.annotations.Api;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.Map;

import static com.kairos.constants.ApiConstants.API_ORGANIZATION_UNIT_URL;

/**
 * Created by prabjot on 3/1/17.
 */
@RequestMapping(API_ORGANIZATION_UNIT_URL + "/page")
@Api(value = API_ORGANIZATION_UNIT_URL)
@RestController
public class AccessPageController {

    @Inject
    private AccessPageService accessPageService;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Map<String,Object>> createAccessPage(@RequestBody AccessPage accessPage){

        return ResponseHandler.generateResponse(HttpStatus.CREATED,true,accessPageService.createAccessPage(accessPage));
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<Map<String,Object>> getAllAccessPage(@RequestBody AccessPage accessPage){

        return ResponseHandler.generateResponse(HttpStatus.CREATED,true,accessPageService.getAllAccessPage());
    }

    @RequestMapping(value = "/xml" ,method = RequestMethod.POST,consumes = {MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Map<String,Object>> parseXml(@RequestBody Tab tab){
        accessPageService.createAccessPageByXml(tab);
        return ResponseHandler.generateResponse(HttpStatus.OK,true,true);
    }


}
