package com.kairos.controller.counters;

import com.kairos.activity.counter.KPIDashboardDTO;
import com.kairos.activity.counter.enums.ConfLevel;
import com.kairos.persistence.model.counter.chart.KPIDashboard;
import com.kairos.service.counter.DynamicTabService;
import com.kairos.util.response.ResponseHandler;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

import static com.kairos.constants.ApiConstants.*;

@RestController
@RequestMapping(API_ORGANIZATION_URL)
@Api(API_ORGANIZATION_URL)
public class DynamicTabController {

    @Inject
    private DynamicTabService dynamicTabService;

    private final static Logger logger = LoggerFactory.getLogger(DynamicTabController.class);

    @PostMapping(UNIT_URL + "/dashboard_tab")
    public ResponseEntity<Map<String, Object>> addCategoriesAtUnitLevel(@PathVariable Long unitId, @RequestBody List<KPIDashboardDTO> kpiDashboardList) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dynamicTabService.addDashboardTabToRef(unitId,null,null,kpiDashboardList, ConfLevel.UNIT));
    }

    @PostMapping(COUNTRY_URL + "/dashboard_tab")
    public ResponseEntity<Map<String, Object>> addCategoriesAtCountryLevel(@PathVariable Long countryId, @RequestBody List<KPIDashboardDTO> kpiDashboardList) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dynamicTabService.addDashboardTabToRef(null,countryId,null,kpiDashboardList,ConfLevel.COUNTRY);
    }

    @PostMapping(UNIT_URL+STAFF_URL + "/dashboard_tab")
    public ResponseEntity<Map<String, Object>> addCategoriesAtStaffLevel(@PathVariable Long unitId, @RequestBody List<KPIDashboardDTO> kpiDashboardList) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,  dynamicTabService.addDashboardTabToRef(unitId,null,kpiDashboardList,ConfLevel.STAFF);
    }
}
