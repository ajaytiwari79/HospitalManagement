package com.kairos.controller.user;

import com.kairos.dto.user.user.user_personalized_settings.UserPersonalizedSettingsDto;
import com.kairos.service.UserPersonalizedSettingsService;
import com.kairos.utils.response.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.text.ParseException;
import java.util.Map;

import static com.kairos.constants.ApiConstants.API_V1;

/**
 * Created by yatharth on 1/5/18.
 */
@RestController
@RequestMapping(API_V1 + "/user/user_personalized_settings")
@Api(value = API_V1 + "/user/user_personalized_settings")
public class UserPersonalizedSettingsController {

    @Inject
    private UserPersonalizedSettingsService userPersonalizedSettingsService;

    @ApiOperation(value = "Get all view settings")
    @RequestMapping(value = "/{userId}", method = RequestMethod.GET)
    ResponseEntity<Map<String, Object>>  getAllViewSettings(@PathVariable Long userId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,userPersonalizedSettingsService.getAllSettingsByUser(userId));
    }

    @RequestMapping(value = "/{userId}", method = RequestMethod.POST)
    @ApiOperation("update User Personalized Settings")
    public ResponseEntity<Map<String, Object>> saveUserPersonalizedSettings(@PathVariable Long userId, @RequestBody UserPersonalizedSettingsDto userPersonalizedSettingsDto) throws ParseException {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,userPersonalizedSettingsService.updateUserPersonalizedSettings(userId,userPersonalizedSettingsDto));
    }
}
