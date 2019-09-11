package com.kairos.controller.language;

import com.kairos.persistence.model.user.language.Language;
import com.kairos.service.language.LanguageService;
import com.kairos.utils.response.ResponseHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.Map;

import static com.kairos.constants.ApiConstants.API_V1;


/**
 * Created by prabjot on 28/11/16.
 */
@RestController
@RequestMapping(API_V1 + "/language")
public class LanguageController {

    @Inject
    private LanguageService languageService;

    @RequestMapping(method = RequestMethod.POST)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> addNewLanguage(@RequestBody Language language){

        return ResponseHandler.generateResponse(HttpStatus.CREATED,true,languageService.save(language));
    }

    @RequestMapping(method = RequestMethod.GET)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getLanguage(){

        return ResponseHandler.generateResponse(HttpStatus.CREATED,true,languageService.getAllLanguage());
    }
}
