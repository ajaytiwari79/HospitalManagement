package com.kairos.controller.flexible_time;
/*
 *Created By Pavan on 20/10/18
 *
 */

import com.kairos.dto.activity.flexible_time.FlexibleTimeSettingsDTO;
import com.kairos.service.flexible_time.FlexibleTimeSettingsService;
import com.kairos.utils.response.ResponseHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.Map;

import static com.kairos.constants.ApiConstants.*;

@RestController
@RequestMapping(API_ORGANIZATION_URL)
public class FlexibleTimeSettingsController {

    @Inject
    private FlexibleTimeSettingsService flexibleTimeSettingsService;

    @PutMapping(value = COUNTRY_FLEXI_TIME_SETTINGS)
    public ResponseEntity<Map<String,Object>> saveFlexibleTimeSettings(@PathVariable Long countryId, @RequestBody FlexibleTimeSettingsDTO flexibleTimeSettings){
        return ResponseHandler.generateResponse(HttpStatus.OK,true,flexibleTimeSettingsService.saveFlexibleTimeSettings(countryId,flexibleTimeSettings));
    }

    @GetMapping(value = COUNTRY_FLEXI_TIME_SETTINGS)
    public ResponseEntity<Map<String,Object>> getFlexibleTimeSettings(@PathVariable Long countryId){
        return ResponseHandler.generateResponse(HttpStatus.OK,true,flexibleTimeSettingsService.getFlexibleTimeSettings(countryId));
    }
}
