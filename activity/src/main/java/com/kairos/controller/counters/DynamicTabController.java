package com.kairos.controller.counters;

import com.kairos.dto.activity.counter.distribution.dashboard.KPIDashboardDTO;
import com.kairos.dto.activity.counter.distribution.category.KPIDashboardUpdationDTO;
import com.kairos.dto.activity.counter.enums.ConfLevel;
import com.kairos.service.counter.DynamicTabService;
import com.kairos.utils.response.ResponseHandler;
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

    @GetMapping(UNIT_URL+COUNTER_CONF_URL+DASHBOARD_URL)
    public ResponseEntity<Map<String, Object>> getCategoriesAtUnitLevel(@PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dynamicTabService.getDashboardTabOfRef(unitId, ConfLevel.UNIT));
    }

    @GetMapping(COUNTRY_URL +COUNTER_CONF_URL+ DASHBOARD_URL)
    public ResponseEntity<Map<String, Object>> getCategoriesAtCountryLevel(@PathVariable Long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dynamicTabService.getDashboardTabOfRef(countryId,ConfLevel.COUNTRY));
    }

    @GetMapping(UNIT_URL+STAFF_URL +COUNTER_CONF_URL+ DASHBOARD_URL)
    public ResponseEntity<Map<String, Object>> getCategoriesAtStaffLevel(@PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,  dynamicTabService.getDashboardTabOfRef(unitId,ConfLevel.STAFF));
    }

    @PostMapping(UNIT_URL +COUNTER_CONF_URL+ DASHBOARD_URL)
    public ResponseEntity<Map<String, Object>> addCategoriesAtUnitLevel(@PathVariable Long unitId, @RequestBody List<KPIDashboardDTO> kpiDashboardList) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dynamicTabService.addDashboardTabToRef(unitId,null,kpiDashboardList, ConfLevel.UNIT));
    }

    @PostMapping(COUNTRY_URL +COUNTER_CONF_URL+ DASHBOARD_URL)
    public ResponseEntity<Map<String, Object>> addCategoriesAtCountryLevel(@PathVariable Long countryId, @RequestBody List<KPIDashboardDTO> kpiDashboardList) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dynamicTabService.addDashboardTabToRef(null,countryId,kpiDashboardList,ConfLevel.COUNTRY));
    }

    @PostMapping(UNIT_URL+STAFF_URL+COUNTER_CONF_URL+DASHBOARD_URL)
    public ResponseEntity<Map<String, Object>> addCategoriesAtStaffLevel(@PathVariable Long unitId, @RequestBody List<KPIDashboardDTO> kpiDashboardList) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,  dynamicTabService.addDashboardTabToRef(unitId,null,kpiDashboardList,ConfLevel.STAFF));
    }

    @PutMapping(UNIT_URL +COUNTER_CONF_URL+ DASHBOARD_URL)
    public ResponseEntity<Map<String, Object>> updateCategoriesAtUnitLevel(@PathVariable Long unitId, @RequestBody KPIDashboardUpdationDTO kpiDashboardUpdationDTOS) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dynamicTabService.updateDashboardTabs(unitId,kpiDashboardUpdationDTOS, ConfLevel.UNIT));
    }

    @PutMapping(COUNTRY_URL +COUNTER_CONF_URL+ DASHBOARD_URL)
    public ResponseEntity<Map<String, Object>> updateCategoriesAtCountryLevel(@PathVariable Long countryId, @RequestBody KPIDashboardUpdationDTO kpiDashboardUpdationDTOS) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dynamicTabService.updateDashboardTabs(countryId,kpiDashboardUpdationDTOS,ConfLevel.COUNTRY));
    }

    @PutMapping(UNIT_URL+STAFF_URL +COUNTER_CONF_URL+ DASHBOARD_URL)
    public ResponseEntity<Map<String, Object>> updateCategoriesAtStaffLevel(@PathVariable Long unitId, @RequestBody KPIDashboardUpdationDTO kpiDashboardUpdationDTOS) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,  dynamicTabService.updateDashboardTabs(unitId,kpiDashboardUpdationDTOS,ConfLevel.STAFF));
    }
}
