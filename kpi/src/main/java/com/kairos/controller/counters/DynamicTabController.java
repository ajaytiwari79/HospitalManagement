package com.kairos.controller.counters;

import com.kairos.commons.response.ResponseHandler;
import com.kairos.constants.ApiConstants;
import com.kairos.dto.activity.counter.distribution.category.KPIDashboardUpdationDTO;
import com.kairos.dto.activity.counter.distribution.dashboard.KPIDashboardDTO;
import com.kairos.dto.activity.counter.enums.ConfLevel;
import com.kairos.service.counter.DynamicTabService;
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
@RequestMapping(ApiConstants.API_V1)
@Api(ApiConstants.API_V1)
public class DynamicTabController {

    @Inject
    private DynamicTabService dynamicTabService;

    private final static Logger logger = LoggerFactory.getLogger(DynamicTabController.class);

    @GetMapping(ApiConstants.UNIT_URL+ ApiConstants.COUNTER_CONF_URL+ ApiConstants.DASHBOARD_URL)
    public ResponseEntity<Map<String, Object>> getDashboardTabAtUnitLevel(@PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dynamicTabService.getDashboardTabOfRef(unitId, ConfLevel.UNIT));
    }

    @GetMapping(ApiConstants.COUNTRY_URL + ApiConstants.COUNTER_CONF_URL+ ApiConstants.DASHBOARD_URL)
    public ResponseEntity<Map<String, Object>> getDashboardTabAtCountryLevel(@PathVariable Long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dynamicTabService.getDashboardTabOfRef(countryId,ConfLevel.COUNTRY));
    }

    @GetMapping(ApiConstants.UNIT_URL+ ApiConstants.STAFF_URL + ApiConstants.COUNTER_CONF_URL+ ApiConstants.DASHBOARD_URL)
    public ResponseEntity<Map<String, Object>> getDashboardTabAtStaffLevel(@PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,  dynamicTabService.getDashboardTabOfRef(unitId,ConfLevel.STAFF));
    }

   @PostMapping(ApiConstants.UNIT_URL+ ApiConstants.STAFF_URL + ApiConstants.COUNTER_CONF_URL+ ApiConstants.DASHBOARD_SAVE_URL)
    public ResponseEntity<Map<String, Object>> saveDashboardTabAtStaffLevel(@PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,  dynamicTabService.addDefaultTab(unitId,ConfLevel.STAFF));
    }

    @PostMapping(ApiConstants.UNIT_URL + ApiConstants.COUNTER_CONF_URL+ ApiConstants.DASHBOARD_URL)
    public ResponseEntity<Map<String, Object>> addDashboardTabAtUnitLevel(@PathVariable Long unitId, @RequestBody List<KPIDashboardDTO> kpiDashboardList) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dynamicTabService.addDashboardTabToRef(unitId,null,kpiDashboardList, ConfLevel.UNIT));
    }

    @PostMapping(ApiConstants.COUNTRY_URL + ApiConstants.COUNTER_CONF_URL+ ApiConstants.DASHBOARD_URL)
    public ResponseEntity<Map<String, Object>> addDashboardTabAtCountryLevel(@PathVariable Long countryId, @RequestBody List<KPIDashboardDTO> kpiDashboardList) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dynamicTabService.addDashboardTabToRef(null,countryId,kpiDashboardList,ConfLevel.COUNTRY));
    }

    @PostMapping(ApiConstants.UNIT_URL+ ApiConstants.STAFF_URL+ ApiConstants.COUNTER_CONF_URL+ ApiConstants.DASHBOARD_URL)
    public ResponseEntity<Map<String, Object>> addDashboardTabAtStaffLevel(@PathVariable Long unitId, @RequestBody List<KPIDashboardDTO> kpiDashboardList) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,  dynamicTabService.addDashboardTabToRef(unitId,null,kpiDashboardList,ConfLevel.STAFF));
    }

    @PostMapping(ApiConstants.UNIT_URL+ ApiConstants.STAFF_URL+ ApiConstants.COUNTER_CONF_URL+ ApiConstants.DASHBOARD_URL+"/default")
    public ResponseEntity<Map<String, Object>> addDefaultDashboardTabAtStaffLevel(@RequestBody KPIDashboardDTO kpiDashboardList) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,  dynamicTabService.addDashboardDefaultTabToRef(kpiDashboardList,ConfLevel.STAFF));
    }

    @PutMapping(ApiConstants.UNIT_URL + ApiConstants.COUNTER_CONF_URL+ ApiConstants.DASHBOARD_URL)
    public ResponseEntity<Map<String, Object>> updateDashboardTabAtUnitLevel(@PathVariable Long unitId, @RequestBody KPIDashboardUpdationDTO kpiDashboardUpdationDTOS) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dynamicTabService.updateDashboardTabs(unitId,kpiDashboardUpdationDTOS, ConfLevel.UNIT));
    }

    @PutMapping(ApiConstants.COUNTRY_URL + ApiConstants.COUNTER_CONF_URL+ ApiConstants.DASHBOARD_URL)
    public ResponseEntity<Map<String, Object>> updateDashboardTabAtCountryLevel(@PathVariable Long countryId, @RequestBody KPIDashboardUpdationDTO kpiDashboardUpdationDTOS) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dynamicTabService.updateDashboardTabs(countryId,kpiDashboardUpdationDTOS,ConfLevel.COUNTRY));
    }

    @PutMapping(ApiConstants.UNIT_URL+ ApiConstants.STAFF_URL + ApiConstants.COUNTER_CONF_URL+ ApiConstants.DASHBOARD_URL)
    public ResponseEntity<Map<String, Object>> updateDashboardTabAtStaffLevel(@PathVariable Long unitId, @RequestBody KPIDashboardUpdationDTO kpiDashboardUpdationDTOS) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,  dynamicTabService.updateDashboardTabs(unitId,kpiDashboardUpdationDTOS,ConfLevel.STAFF));
    }
}
