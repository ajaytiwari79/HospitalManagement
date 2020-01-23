package com.kairos.controller.filter;

import com.kairos.service.filter.StaffFilterService;
import com.kairos.utils.response.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.Map;

import static com.kairos.constants.ApiConstants.API_V1;
import static com.kairos.constants.ApiConstants.UNIT_URL;

/**
 * Created By G.P.Ranjan on 15/1/20
 **/
@RestController
@RequestMapping(API_V1)
@Api(API_V1)
public class StaffFilterController {
    @Inject
    private StaffFilterService staffFilterService;

    @ApiOperation("Get staff filter data")
    @GetMapping(UNIT_URL+ "/get_staff_filter_data")
    public ResponseEntity<Map<String, Object>> getStaffFilterData(@PathVariable long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, staffFilterService.getAllFilterData(unitId));
    }
}
