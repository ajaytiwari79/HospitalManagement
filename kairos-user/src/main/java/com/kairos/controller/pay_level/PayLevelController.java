package com.kairos.controller.pay_level;

import com.kairos.persistence.model.user.pay_level.PayLevelDTO;
import com.kairos.service.pay_level.PayLevelService;
import com.kairos.util.response.ResponseHandler;
import io.swagger.annotations.Api;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.Map;

import static com.kairos.constants.ApiConstants.API_ORGANIZATION_COUNTRY_URL;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * Created by prabjot on 26/12/17.
 */
@Api(value = API_ORGANIZATION_COUNTRY_URL)
@RestController(API_ORGANIZATION_COUNTRY_URL)
public class PayLevelController {

    @Inject
    private PayLevelService payLevelService;

    @RequestMapping(value = API_ORGANIZATION_COUNTRY_URL+ "/pay_level")
    public ResponseEntity<Map<String,Object>> getPayLevels(@PathVariable Long countryId){
        return ResponseHandler.generateResponse(HttpStatus.OK,true,payLevelService.getPayLevels(countryId));
    }

    @RequestMapping(value = "/pay_level",method = POST)
    public ResponseEntity<Map<String,Object>> createPayLevel(@PathVariable Long countryId, @RequestBody PayLevelDTO payLevelDTO){
        return ResponseHandler.generateResponse(HttpStatus.CREATED,true,payLevelService.createPayLevel(countryId,payLevelDTO));
    }
}
