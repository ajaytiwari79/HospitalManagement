package com.kairos.controller.system_setting;

import com.kairos.dto.user.country.system_setting.GeneralSettingDTO;
import com.kairos.service.system_setting.GeneralSettingService;
import com.kairos.utils.response.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;

import java.util.Map;

import static com.kairos.constants.ApiConstants.API_V1;

/**
 * Created By G.P.Ranjan on 25/11/19
 **/
@Controller
@RequestMapping(API_V1 )
@Api(value = API_V1 )
public class GeneralSettingController {
    @Inject
    private GeneralSettingService generalSettingService;

    @PutMapping("/general_setting/")
    @ApiOperation("To update System Language")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateGeneralSetting(@Valid @RequestBody GeneralSettingDTO generalSettingDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, generalSettingService.updateGeneralSetting(generalSettingDTO));
    }

    @GetMapping(value = "/general_setting/{generalSettingId}")
    @ApiOperation("To fetch System Language")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getGeneralSetting(@PathVariable Long generalSettingId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, generalSettingService.getGeneralSetting(generalSettingId));
    }
}
