package com.kairos.controller.counters;

import com.kairos.dto.activity.counter.DefaultKPISettingDTO;
import com.kairos.dto.activity.counter.distribution.access_group.AccessGroupKPIConfDTO;
import com.kairos.dto.activity.counter.distribution.access_group.AccessGroupMappingDTO;
import com.kairos.dto.activity.counter.distribution.category.CategoryKPIsDTO;
import com.kairos.dto.activity.counter.distribution.dashboard.DashboardKPIMappingDTO;
import com.kairos.dto.activity.counter.distribution.dashboard.DashboardKPIsDTO;
import com.kairos.dto.activity.counter.distribution.org_type.OrgTypeKPIConfDTO;
import com.kairos.dto.activity.counter.distribution.org_type.OrgTypeMappingDTO;
import com.kairos.dto.activity.counter.distribution.tab.TabKPIEntryConfDTO;
import com.kairos.dto.activity.counter.distribution.tab.TabKPIMappingDTO;
import com.kairos.dto.activity.counter.enums.ConfLevel;
import com.kairos.service.counter.CounterDistService;
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

/*
 * @author: mohit.shakya@oodlestechnologies.com
 * @dated: Jun/26/2018
 */

//TODO: TO be modified according to latest proposed distribution functionality
@RestController
@RequestMapping(API_ORGANIZATION_URL)
@Api(API_ORGANIZATION_URL)
public class CounterDistController {

    @Inject
    private CounterDistService counterManagementService;

    private final static Logger logger = LoggerFactory.getLogger(CounterDistController.class);

    @GetMapping(COUNTRY_URL+"/modules")
    public ResponseEntity<Map<String,Object>> getKPIEnabledTabsForModuleOfCountry(@PathVariable Long countryId){
        return ResponseHandler.generateResponse(HttpStatus.OK, true, counterManagementService.getKPIAccessPageListForCountry(countryId,ConfLevel.COUNTRY));
    }
    @GetMapping(UNIT_URL+"/modules")
    public ResponseEntity<Map<String,Object>> getKPIEnabledTabsForModuleOfUnit(@PathVariable Long unitId){
        return ResponseHandler.generateResponse(HttpStatus.OK, true,counterManagementService.getKPIAccessPageListForUnit(unitId,ConfLevel.UNIT));
    }
    @GetMapping(COUNTRY_URL+COUNTER_DIST_URL+"/counters")
    public ResponseEntity<Map<String, Object>> getAvailableKPIsListForCountry(@PathVariable Long countryId){
        return ResponseHandler.generateResponse(HttpStatus.OK, true, counterManagementService.getKPIsList(countryId, ConfLevel.COUNTRY));
    }

    @GetMapping(UNIT_URL+COUNTER_DIST_URL+"/counters")
    public ResponseEntity<Map<String, Object>> getAvailableKPIsListForUnit(@PathVariable Long unitId){
        return ResponseHandler.generateResponse(HttpStatus.OK, true, counterManagementService.getKPIsList(unitId,ConfLevel.UNIT));
    }

    @GetMapping(UNIT_URL+STAFF_URL+COUNTER_DIST_URL+"/counters")
    public ResponseEntity<Map<String, Object>> getAvailableKPIsListForStaff(@PathVariable Long unitId){
        return ResponseHandler.generateResponse(HttpStatus.OK, true, counterManagementService.getKPIsList(unitId, ConfLevel.STAFF));
    }

    //category KPI
    @GetMapping(COUNTRY_URL+COUNTER_DIST_URL+"/category")
    public ResponseEntity<Map<String, Object>> getInitialCategoryKPIDistributionDataForCountry(@PathVariable Long countryId){
        return ResponseHandler.generateResponse(HttpStatus.OK, true, counterManagementService.getInitialCategoryKPIDistData(countryId, ConfLevel.COUNTRY));
    }

    @PostMapping(COUNTRY_URL+COUNTER_DIST_URL+"/category")
    public ResponseEntity<Map<String, Object>> saveCategoryKPIDistributionForCountry(@RequestBody CategoryKPIsDTO categorieKPIsDetails, @PathVariable Long countryId){
        counterManagementService.addCategoryKPIsDistribution(categorieKPIsDetails, ConfLevel.COUNTRY, countryId);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, null);
    }

    @GetMapping(UNIT_URL+COUNTER_DIST_URL+"/category")
    public ResponseEntity<Map<String, Object>> getInitialCategoryKPIDistributionDataForUnit(@PathVariable Long unitId){
        return ResponseHandler.generateResponse(HttpStatus.OK, true, counterManagementService.getInitialCategoryKPIDistData(unitId, ConfLevel.UNIT));
    }

    @PostMapping(UNIT_URL+COUNTER_DIST_URL+"/category")
    public ResponseEntity<Map<String, Object>> saveCategoryKPIDistributionUnit(@RequestBody CategoryKPIsDTO categorieKPIsDetails, @PathVariable Long unitId){
        counterManagementService.addCategoryKPIsDistribution(categorieKPIsDetails, ConfLevel.UNIT, unitId);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, null);
    }

    @GetMapping(UNIT_URL+COUNTER_DIST_URL+"/staff/category")
    public ResponseEntity<Map<String, Object>> getInitialCategoryKPIDistributionDataForStaff(@PathVariable Long unitId){
        return ResponseHandler.generateResponse(HttpStatus.OK, true, counterManagementService.getInitialCategoryKPIDistDataForStaff(unitId));
    }

    //Tab kpi Apis

    @GetMapping(COUNTRY_URL+COUNTER_DIST_URL+"/tab/{tabId}")
    public ResponseEntity<Map<String, Object>> getInitialTabKPIDistConfForCountry(@PathVariable Long countryId, @PathVariable String tabId){
        return ResponseHandler.generateResponse(HttpStatus.OK, true, counterManagementService.getInitialTabKPIDataConf(tabId, countryId, ConfLevel.COUNTRY));
    }

    @GetMapping(UNIT_URL+COUNTER_DIST_URL+"/tab/{tabId}")
    public ResponseEntity<Map<String, Object>> getInitialTabKPIDistConfForUnit(@PathVariable Long unitId, @PathVariable String tabId){
        return ResponseHandler.generateResponse(HttpStatus.OK, true, counterManagementService.getInitialTabKPIDataConf(tabId, unitId, ConfLevel.UNIT));
    }

    @GetMapping(UNIT_URL+STAFF_URL+COUNTER_DIST_URL+"/tab/{tabId}")
    public ResponseEntity<Map<String, Object>> getInitialTabKPIDistConfForStaff(@PathVariable Long unitId, @PathVariable String tabId){
        return ResponseHandler.generateResponse(HttpStatus.OK, true, counterManagementService.getInitialTabKPIDataConfForStaff(tabId,unitId, ConfLevel.STAFF));
    }

    @PostMapping(COUNTRY_URL+COUNTER_DIST_URL+"/tab/create_dist_entry")
    public ResponseEntity<Map<String, Object>> addTabKPIsEntryForCounty(@RequestBody TabKPIEntryConfDTO tabKPIEntry,@PathVariable Long countryId){
        counterManagementService.addTabKPIEntries(tabKPIEntry,countryId,null,null,ConfLevel.COUNTRY);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, null);
    }

    @PostMapping(UNIT_URL+COUNTER_DIST_URL+"/tab/create_dist_entry")
    public ResponseEntity<Map<String, Object>> addTabKPIsEntryForUnit(@RequestBody TabKPIEntryConfDTO tabKPIEntry,@PathVariable Long unitId){
        counterManagementService.addTabKPIEntries(tabKPIEntry,null,unitId,null,ConfLevel.UNIT);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, null);
    }

    //staff will send list of KPITabMappings
    @PostMapping(UNIT_URL+STAFF_URL+COUNTER_DIST_URL+"/tab/create_dist_entry")
    public ResponseEntity<Map<String, Object>> addTabKPIsEntryForStaff(@PathVariable Long unitId,@RequestBody List<TabKPIMappingDTO> tabKPIMappingDTOS){
        return ResponseHandler.generateResponse(HttpStatus.OK, true,counterManagementService.addTabKPIEntriesOfStaff(tabKPIMappingDTOS,unitId,ConfLevel.STAFF));
    }

    @PutMapping(UNIT_URL+STAFF_URL+COUNTER_DIST_URL+"/tab/{tabId}/update_dist_entry")
    public ResponseEntity<Map<String, Object>> updateTabKPIsEntryForStaff(@PathVariable String tabId,@PathVariable Long unitId,@RequestBody List<TabKPIMappingDTO> tabKPIMappingDTOS){
        counterManagementService.updateTabKPIEntries(tabKPIMappingDTOS,tabId,unitId,ConfLevel.STAFF);
        return ResponseHandler.generateResponse(HttpStatus.OK, true,null);
    }

    @PutMapping(COUNTRY_URL+COUNTER_DIST_URL+"/tab/remove_dist_entry")
    public ResponseEntity<Map<String, Object>> removeTabKPIEntryForCountry(@PathVariable Long countryId, @RequestBody TabKPIMappingDTO tabKPIMappingDTO){
        counterManagementService.removeTabKPIEntries(tabKPIMappingDTO,countryId,ConfLevel.COUNTRY);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, null);
    }

   @PutMapping(UNIT_URL+COUNTER_DIST_URL+"/tab/remove_dist_entry")
    public ResponseEntity<Map<String, Object>> removeTabKPIEntryForUnit(@PathVariable Long unitId, @RequestBody TabKPIMappingDTO tabKPIMappingDTO){
        counterManagementService.removeTabKPIEntries(tabKPIMappingDTO,unitId,ConfLevel.UNIT);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, null);
    }

    @PutMapping(UNIT_URL+STAFF_URL+COUNTER_DIST_URL+"/tab/remove_dist_entry")
    public ResponseEntity<Map<String, Object>> removeTabKPIEntryForStaff(@PathVariable Long unitId,@RequestBody TabKPIMappingDTO tabKPIMappingDTO){
        counterManagementService.removeTabKPIEntries(tabKPIMappingDTO,unitId,ConfLevel.STAFF);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, null);
    }

    // accessGroup Apis

    @PostMapping(COUNTRY_URL+COUNTER_DIST_URL+"/access_group/create_dist_entry")
    public ResponseEntity<Map<String, Object>> addAccessGroupKPIEntryForCountry(@PathVariable Long countryId ,@RequestBody AccessGroupKPIConfDTO accessGroupKPIConf){
        counterManagementService.addAccessGroupKPIEntries(accessGroupKPIConf,countryId,ConfLevel.COUNTRY);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, null);
    }

    @PostMapping(UNIT_URL+COUNTER_DIST_URL+"/access_group/create_dist_entry")
    public ResponseEntity<Map<String, Object>> addAccessGroupKPIEntryForUnit(@PathVariable Long unitId,@RequestBody AccessGroupKPIConfDTO accessGroupKPIConf){
        counterManagementService.addAccessGroupKPIEntries(accessGroupKPIConf,unitId,ConfLevel.UNIT);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, null);
    }

    @PutMapping(COUNTRY_URL+COUNTER_DIST_URL+"/access_group/remove_dist_entry")
    public ResponseEntity<Map<String, Object>> removeAccessGroupKPIEntryByCounty(@PathVariable Long countryId,@RequestBody AccessGroupMappingDTO accessGroupMappingDTO){
        counterManagementService.removeAccessGroupKPIEntries(accessGroupMappingDTO,countryId,ConfLevel.COUNTRY);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, null);
    }

    @PutMapping(UNIT_URL+COUNTER_DIST_URL+"/access_group/remove_dist_entry")
    public ResponseEntity<Map<String, Object>> removeAccessGroupKPIEntryByUnit(@PathVariable Long unitId,@RequestBody AccessGroupMappingDTO accessGroupMappingDTO){
        counterManagementService.removeAccessGroupKPIEntries(accessGroupMappingDTO,unitId,ConfLevel.UNIT);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, null);
    }

    @GetMapping(COUNTRY_URL+COUNTER_DIST_URL+"/access_group/{accessGroupId}")
    public ResponseEntity<Map<String, Object>> getInitialDataForAccessGroupKPIConfOfCountry(@PathVariable Long countryId,@PathVariable Long accessGroupId){
        return ResponseHandler.generateResponse(HttpStatus.OK, true,  counterManagementService.getInitialAccessGroupKPIDataConf(accessGroupId,countryId,ConfLevel.COUNTRY));
    }

    @GetMapping(UNIT_URL+COUNTER_DIST_URL+"/access_group/{accessGroupId}")
    public ResponseEntity<Map<String, Object>> getInitialDataForAccessgroupKPIConfOfUnit(@PathVariable Long unitId,@PathVariable Long accessGroupId){
        return ResponseHandler.generateResponse(HttpStatus.OK, true, counterManagementService.getInitialAccessGroupKPIDataConf(accessGroupId,unitId,ConfLevel.UNIT));
    }

    // orgType Apis

    @GetMapping(COUNTRY_URL+COUNTER_DIST_URL+"/org_type/{orgTypeId}")
    public ResponseEntity<Map<String, Object>> getInitialDataForOrgTypeKPIConf(@PathVariable Long countryId,@PathVariable Long orgTypeId){
        return ResponseHandler.generateResponse(HttpStatus.OK, true, counterManagementService.getInitialOrgTypeKPIDataConf(orgTypeId,countryId));
    }

    @PostMapping(COUNTRY_URL+COUNTER_DIST_URL+"/org_type/create_dist_entry")
    public ResponseEntity<Map<String, Object>> addOrgTypeKPIEntry(@PathVariable Long countryId,@RequestBody OrgTypeKPIConfDTO orgTypeKPIConf){
        counterManagementService.addOrgTypeKPIEntries(orgTypeKPIConf,countryId);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, true);
    }

    @PutMapping(COUNTRY_URL+COUNTER_DIST_URL+"/org_type/remove_dist_entry")
    public ResponseEntity<Map<String, Object>> removeOrgTypeKPIEntry(@PathVariable Long countryId, @RequestBody OrgTypeMappingDTO orgTypeMappingDTO){
        counterManagementService.removeOrgTypeKPIEntries(orgTypeMappingDTO,countryId);
        return ResponseHandler.generateResponse(HttpStatus.OK, true,null);
    }

    //dashboard setting kpi conf


    @GetMapping(COUNTRY_URL+COUNTER_DIST_URL+"/dashboard")
    public ResponseEntity<Map<String, Object>> getInitialDashboardKPIDistributionDataForCountry(@PathVariable Long countryId){
        return ResponseHandler.generateResponse(HttpStatus.OK, true, counterManagementService.getInitialDashboardKPIDistData(countryId, ConfLevel.COUNTRY));
    }

    @PostMapping(COUNTRY_URL+COUNTER_DIST_URL+"/dashboard")
    public ResponseEntity<Map<String, Object>> saveDashboardKPIDistributionForCountry(@RequestBody DashboardKPIsDTO dashboardKPIsDTO, @PathVariable Long countryId){
        counterManagementService.addDashboradKPIsDistribution(dashboardKPIsDTO, ConfLevel.COUNTRY, countryId);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, null);
    }

    @GetMapping(UNIT_URL+COUNTER_DIST_URL+"/dashboard")
    public ResponseEntity<Map<String, Object>> getInitialDashboardKPIDistributionDataForUnit(@PathVariable Long unitId){
        return ResponseHandler.generateResponse(HttpStatus.OK, true, counterManagementService.getInitialDashboardKPIDistData(unitId, ConfLevel.UNIT));
    }

    @PostMapping(UNIT_URL+COUNTER_DIST_URL+"/dashboard")
    public ResponseEntity<Map<String, Object>> saveDashboardKPIDistributionUnit(@RequestBody DashboardKPIsDTO dashboardKPIsDTO, @PathVariable Long unitId){
        counterManagementService.addDashboradKPIsDistribution(dashboardKPIsDTO, ConfLevel.UNIT, unitId);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, null);
    }


    @PutMapping(COUNTRY_URL+COUNTER_DIST_URL+"/dashboard/remove_dist_entry")
    public ResponseEntity<Map<String, Object>> removeDashboardKPIEntryForCountry(@PathVariable Long countryId, @RequestBody DashboardKPIMappingDTO dashboardKPIMappingDTO){
        counterManagementService.removeDashboradKPIEntries(dashboardKPIMappingDTO,countryId,ConfLevel.COUNTRY);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, null);
    }

    @PutMapping(UNIT_URL+COUNTER_DIST_URL+"/dashboard/remove_dist_entry")
    public ResponseEntity<Map<String, Object>> removeDashboardKPIEntryForUnit(@PathVariable Long unitId, @RequestBody DashboardKPIMappingDTO dashboardKPIMappingDTO){
        counterManagementService.removeDashboradKPIEntries(dashboardKPIMappingDTO,unitId,ConfLevel.UNIT);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, null);
    }

    //
//    @PutMapping(UNIT_URL+STAFF_URL+COUNTER_DIST_URL+"/tab/{tabId}/update_dist_entry")
//    public ResponseEntity<Map<String, Object>> updateTabKPIsEntryForStaff(@PathVariable String tabId,@PathVariable Long unitId,@RequestBody List<TabKPIMappingDTO> tabKPIMappingDTOS){
//        counterManagementService.updateTabKPIEntries(tabKPIMappingDTOS,tabId,unitId,ConfLevel.STAFF);
//        return ResponseHandler.generateResponse(HttpStatus.OK, true,null);
//    }


//    @GetMapping(UNIT_URL+COUNTER_DIST_URL+"/staff/dashboard")
//    public ResponseEntity<Map<String, Object>> getInitialDashboardKPIDistributionDataForStaff(@PathVariable Long unitId){
//        return ResponseHandler.generateResponse(HttpStatus.OK, true, counterManagementService.getInitialDashboardKPIDataConfForStaff(unitId));
//    }




    //defalut setting for unit and staff

    @PostMapping(UNIT_URL+COUNTER_DIST_URL+"/default_kpi_setting")
    public ResponseEntity<Map<String, Object>> createDefaluSettingForUnit(@PathVariable Long unitId, @RequestBody DefaultKPISettingDTO defaultKPISettingDTO){
            counterManagementService.createDefaultKpiSetting(unitId, defaultKPISettingDTO);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, null);
    }

    @PostMapping(UNIT_URL+COUNTER_DIST_URL+"/staff_default_kpi_setting")
    public ResponseEntity<Map<String, Object>> createDefaluSettingForStaff(@PathVariable Long unitId, @RequestBody DefaultKPISettingDTO defaultKPISettingDTO){
        counterManagementService.createDefaultStaffKPISetting(unitId, defaultKPISettingDTO);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, null);
    }
}
