package com.kairos.controller.widget;

import com.kairos.service.widget.WidgetService;
import com.kairos.utils.response.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.Map;

import static com.kairos.constants.ApiConstants.API_UNIT_URL;

/**
 * pradeep
 * 28/4/19
 */
@RestController
@RequestMapping(API_UNIT_URL)
@Api(API_UNIT_URL)
public class WidgetController {

    @Inject private WidgetService widgetService;

    @ApiOperation("get data for Dashboard widget")
    @PostMapping("/get_dashboard_widget")
    public ResponseEntity<Map<String, Object>> getDashboardWidget(@PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, widgetService.getWidgetData());
    }
}
