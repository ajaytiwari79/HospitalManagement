package com.kairos.controller.table_settings;

import com.kairos.service.table_settings.TableSettingService;
import com.kairos.utils.response.ResponseHandler;
import com.kairos.utils.user_context.UserContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.Map;

import static com.kairos.constants.ApiConstants.API_ORGANIZATION_UNIT_URL;

/**
 * Created by prabjot on 1/5/17.
 */
@RequestMapping(API_ORGANIZATION_UNIT_URL)
@RestController
public class TableSettingController {


    @Inject
    private TableSettingService tableSettingService;

    @RequestMapping(value = "/table/{tableId}/settings", method = RequestMethod.POST)
    ResponseEntity<Map<String, Object>> saveTableSettings(@PathVariable long unitId, @PathVariable String tableId, @RequestBody Map<String, Object> tableSettings) {

        //User loggedInUser = UserAuthentication.getCurrentUser();
        Long loggedInUserId = UserContext.getUserDetails().getId();

        return ResponseHandler.generateResponse(HttpStatus.OK, true, tableSettingService.saveTableSettings(loggedInUserId, unitId, tableId, tableSettings));
    }

    /**
     * @param staffId
     * @return
     * @auther anil maurya
     */

    @RequestMapping(value = "/table/{staffId}", method = RequestMethod.GET)
    ResponseEntity<Map<String, Object>> getTableConfiguration(@PathVariable long unitId, @PathVariable long staffId) {


        return ResponseHandler.generateResponse(HttpStatus.OK, true, tableSettingService.getTableConfiguration(staffId, unitId));
    }

    @GetMapping("/table_settings/{tableId}")
    ResponseEntity<Map<String, Object>> getTableConfigurationByTableId(@PathVariable long unitId, @PathVariable BigInteger tableId) {


        return ResponseHandler.generateResponse(HttpStatus.OK, true, tableSettingService.getTableConfigurationByTableId(unitId, tableId));
    }
}
