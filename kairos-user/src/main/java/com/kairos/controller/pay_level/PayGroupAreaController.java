package com.kairos.controller.pay_level;

import com.kairos.persistence.model.user.pay_level.PayGroupArea;
import com.kairos.response.dto.web.pay_level.PayGroupAreaDTO;
import com.kairos.service.pay_level.PayGroupAreaService;
import com.kairos.util.response.ResponseHandler;
import io.swagger.annotations.Api;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.Map;

import static com.kairos.constants.ApiConstants.API_ORGANIZATION_COUNTRY_URL;
import static com.kairos.constants.ApiConstants.API_ORGANIZATION_URL;
import static com.kairos.constants.ApiConstants.COUNTRY_URL;

/**
 * Created by prabjot on 20/12/17.
 */
@Api(API_ORGANIZATION_URL)
@RestController(value = API_ORGANIZATION_URL)
public class PayGroupAreaController {

    @Inject
    private PayGroupAreaService payGroupAreaService;

    @PostMapping(API_ORGANIZATION_COUNTRY_URL+"/pay_group_area")
    public ResponseEntity<Map<String,Object>> savePayGroupArea(@PathVariable Long countryId,
                                                               @Validated @RequestBody PayGroupAreaDTO payGroupAreaDTO){
        return ResponseHandler.generateResponse(HttpStatus.CREATED,true,payGroupAreaService.savePayGroupArea(countryId,payGroupAreaDTO));
    }

    @PutMapping(API_ORGANIZATION_COUNTRY_URL+"/pay_group_area/{payGroupAreaId}")
    public ResponseEntity<Map<String,Object>> updatePayGroupArea(@PathVariable Long payGroupAreaId, @RequestBody PayGroupAreaDTO payGroupAreaDTO){
        return ResponseHandler.generateResponse(HttpStatus.OK,true,payGroupAreaService.updatePayGroupArea(payGroupAreaId,payGroupAreaDTO));
    }

    @DeleteMapping(API_ORGANIZATION_COUNTRY_URL+"/pay_group_area/{payGroupAreaId}")
    public ResponseEntity<Map<String,Object>> deletePayGroup(@PathVariable Long payGroupAreaId){
        return ResponseHandler.generateResponse(HttpStatus.OK,true,payGroupAreaService.deletePayGroupArea(payGroupAreaId));
    }

    @GetMapping(API_ORGANIZATION_COUNTRY_URL+"/pay_group_area")
    public ResponseEntity<Map<String,Object>> getPayGroup(@PathVariable Long countryId){
        return ResponseHandler.generateResponse(HttpStatus.OK,true,payGroupAreaService.getPayGroupArea(countryId));
    }



}
