package com.kairos.controller.counters;

import com.kairos.dto.activity.counter.DefaultKPISettingDTO;
import com.kairos.dto.activity.counter.configuration.CounterDTO;
import com.kairos.dto.activity.counter.data.FilterCriteriaDTO;
import com.kairos.dto.activity.counter.distribution.access_group.AccessGroupKPIConfDTO;
import com.kairos.dto.activity.counter.distribution.access_group.AccessGroupMappingDTO;
import com.kairos.dto.activity.counter.distribution.access_group.AccessGroupPermissionCounterDTO;
import com.kairos.dto.activity.counter.distribution.category.CategoryKPIsDTO;
import com.kairos.dto.activity.counter.distribution.org_type.OrgTypeKPIConfDTO;
import com.kairos.dto.activity.counter.distribution.org_type.OrgTypeMappingDTO;
import com.kairos.dto.activity.counter.distribution.tab.TabKPIDTO;
import com.kairos.dto.activity.counter.distribution.tab.TabKPIEntryConfDTO;
import com.kairos.dto.activity.counter.distribution.tab.TabKPIMappingDTO;
import com.kairos.dto.activity.counter.enums.ConfLevel;
import com.kairos.service.counter.CounterDataService;
import com.kairos.service.counter.CounterDistService;
import com.kairos.service.counter.DynamicTabService;
import com.kairos.service.counter.RestingHoursCalculationService;
import com.kairos.service.shift.ShiftService;
import com.kairos.utils.response.ResponseHandler;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.ws.rs.QueryParam;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import static com.kairos.constants.ApiConstants.*;

/*
 * @author: mohit.shakya@oodlestechnologies.com
 * @dated: Jun/26/2018
 */

//TODO: TO be modified according to latest proposed distribution functionality
@RestController
@RequestMapping(API_V1)
@Api(API_V1)
public class CounterDistController {

    @Inject
    private ShiftService shiftService;
    @Inject
    private CounterDistService counterManagementService;

    @Inject
    private DynamicTabService dynamicTabService;

    @Inject
    private CounterDataService counterDataService;

    @Inject private RestingHoursCalculationService restingHoursCalculationService;
    private final static Logger logger = LoggerFactory.getLogger(CounterDistController.class);

    @GetMapping(COUNTRY_URL+UNIT_URL+"/modules")
    public ResponseEntity<Map<String,Object>> getKPIEnabledTabsForModuleOfCountry(@PathVariable Long countryId,@PathVariable Long unitId){
        return ResponseHandler.generateResponse(HttpStatus.OK, true, counterManagementService.getKPIAccessPageListForCountry(countryId,unitId,ConfLevel.COUNTRY));
    }
    @GetMapping(UNIT_URL+"/modules")
    public ResponseEntity<Map<String,Object>> getKPIEnabledTabsForModuleOfUnit(@PathVariable Long unitId){
        return ResponseHandler.generateResponse(HttpStatus.OK, true,counterManagementService.getKPIAccessPageListForUnit(unitId,ConfLevel.UNIT));
    }
    @GetMapping(COUNTER_COUNTRY_DIST_URL+COUNTERS)
    public ResponseEntity<Map<String, Object>> getAvailableKPIsListForCountry(@PathVariable Long countryId){
        return ResponseHandler.generateResponse(HttpStatus.OK, true, counterManagementService.getKPIsList(countryId, ConfLevel.COUNTRY));
    }

    @GetMapping(COUNTER_UNIT_DIST_URL+COUNTERS)
    public ResponseEntity<Map<String, Object>> getAvailableKPIsListForUnit(@PathVariable Long unitId){
        return ResponseHandler.generateResponse(HttpStatus.OK, true, counterManagementService.getKPIsList(unitId,ConfLevel.UNIT));
    }

    @GetMapping(COUNTER_STAFF_UNIT_DIST_URL+COUNTERS)
    public ResponseEntity<Map<String, Object>> getAvailableKPIsListForStaff(@PathVariable Long unitId){
        return ResponseHandler.generateResponse(HttpStatus.OK, true, counterManagementService.getKPIsList(unitId, ConfLevel.STAFF));
    }

    //category KPI
    @GetMapping(COUNTER_COUNTRY_DIST_URL+CATEGORY)
    public ResponseEntity<Map<String, Object>> getInitialCategoryKPIDistributionDataForCountry(@PathVariable Long countryId){
        return ResponseHandler.generateResponse(HttpStatus.OK, true, counterManagementService.getInitialCategoryKPIDistData(countryId, ConfLevel.COUNTRY));
    }

    @PostMapping(COUNTER_COUNTRY_DIST_URL+CATEGORY)
    public ResponseEntity<Map<String, Object>> saveCategoryKPIDistributionForCountry(@RequestBody CategoryKPIsDTO categorieKPIsDetails, @PathVariable Long countryId){
        counterManagementService.addCategoryKPIsDistribution(categorieKPIsDetails, ConfLevel.COUNTRY, countryId);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, null);
    }

    @GetMapping(COUNTER_UNIT_DIST_URL+CATEGORY)
    public ResponseEntity<Map<String, Object>> getInitialCategoryKPIDistributionDataForUnit(@PathVariable Long unitId){
        return ResponseHandler.generateResponse(HttpStatus.OK, true, counterManagementService.getInitialCategoryKPIDistData(unitId, ConfLevel.UNIT));
    }

    @PostMapping(COUNTER_UNIT_DIST_URL+CATEGORY)
    public ResponseEntity<Map<String, Object>> saveCategoryKPIDistributionUnit(@RequestBody CategoryKPIsDTO categorieKPIsDetails, @PathVariable Long unitId){
        counterManagementService.addCategoryKPIsDistribution(categorieKPIsDetails, ConfLevel.UNIT, unitId);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, null);
    }

    @GetMapping(COUNTER_UNIT_DIST_URL+"/staff/category")
    public ResponseEntity<Map<String, Object>> getInitialCategoryKPIDistributionDataForStaff(@PathVariable Long unitId){
        return ResponseHandler.generateResponse(HttpStatus.OK, true, counterManagementService.getInitialCategoryKPIDistDataForStaff(unitId));
    }

    //Tab kpi Apis

    @GetMapping(COUNTER_COUNTRY_DIST_URL+TAB+"/{tabId}")
    public ResponseEntity<Map<String, Object>> getInitialTabKPIDistConfForCountry(@PathVariable Long countryId, @PathVariable String tabId){
        return ResponseHandler.generateResponse(HttpStatus.OK, true, counterManagementService.getInitialTabKPIDataConf(tabId, countryId, ConfLevel.COUNTRY));
    }

    @GetMapping(COUNTER_UNIT_DIST_URL+TAB+"/{tabId}")
    public ResponseEntity<Map<String, Object>> getInitialTabKPIDistConfForUnit(@PathVariable Long unitId, @PathVariable String tabId){
        return ResponseHandler.generateResponse(HttpStatus.OK, true, counterManagementService.getInitialTabKPIDataConf(tabId, unitId, ConfLevel.UNIT));
    }

    @PostMapping(COUNTER_STAFF_UNIT_DIST_URL+TAB+"/{tabId}")
    public ResponseEntity<Map<String, Object>> getInitialTabKPIDistConfForStaff(@PathVariable Long unitId, @PathVariable String tabId, @RequestBody FilterCriteriaDTO filtersDTO){
        return ResponseHandler.generateResponse(HttpStatus.OK, true, counterManagementService.getInitialTabKPIDataConfForStaff(tabId,unitId, ConfLevel.STAFF, filtersDTO));
    }

    @PutMapping(COUNTER_UNIT_DIST_URL+TAB+"/{tabId}")
    public ResponseEntity<Map<String, Object>> UpdateInitialTabKPIDistConfForUnit(@PathVariable Long unitId, @RequestBody TabKPIDTO tabKPIDTO){
        return ResponseHandler.generateResponse(HttpStatus.OK, true, counterManagementService.updateInitialTabKPIDataConf(tabKPIDTO, unitId, ConfLevel.UNIT));
    }

    @PutMapping(COUNTER_STAFF_UNIT_DIST_URL+TAB+"/{tabId}")
    public ResponseEntity<Map<String, Object>> UpdateInitialTabKPIDistConfForCountry(@PathVariable Long unitId,  @RequestBody TabKPIDTO tabKPIDTO){
        return ResponseHandler.generateResponse(HttpStatus.OK, true, counterManagementService.updateInitialTabKPIDataConf(tabKPIDTO,unitId, ConfLevel.COUNTRY));
    }

    @GetMapping(UNIT_URL+STAFF_URL+COUNTER_DIST_URL+"/priority/tab/{tabId}")
    public ResponseEntity<Map<String, Object>> getInitialTabKPIDistConfForStaffPriority(@PathVariable Long unitId, @PathVariable String tabId){
        return ResponseHandler.generateResponse(HttpStatus.OK, true, counterManagementService.getInitialTabKPIDataConfForStaffPriority(tabId,unitId, ConfLevel.STAFF));
    }


    @PostMapping(COUNTER_COUNTRY_DIST_URL+TAB+"/create_dist_entry")
    public ResponseEntity<Map<String, Object>> addTabKPIsEntryForCounty(@RequestBody TabKPIEntryConfDTO tabKPIEntry,@PathVariable Long countryId){
        counterManagementService.addTabKPIEntries(tabKPIEntry,countryId,null,null,ConfLevel.COUNTRY);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, null);
    }

    @PostMapping(COUNTER_UNIT_DIST_URL+TAB+"/create_dist_entry")
    public ResponseEntity<Map<String, Object>> addTabKPIsEntryForUnit(@RequestBody TabKPIEntryConfDTO tabKPIEntry,@PathVariable Long unitId){
        counterManagementService.addTabKPIEntries(tabKPIEntry,null,unitId,null,ConfLevel.UNIT);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, null);
    }

    //staff will send list of KPITabMappings
    @PostMapping(COUNTER_STAFF_UNIT_DIST_URL+TAB+"/create_dist_entry")
    public ResponseEntity<Map<String, Object>> addTabKPIsEntryForStaff(@PathVariable Long unitId,@RequestBody List<TabKPIMappingDTO> tabKPIMappingDTOS){
        return ResponseHandler.generateResponse(HttpStatus.OK, true,counterManagementService.addTabKPIEntriesOfStaff(tabKPIMappingDTOS,unitId,ConfLevel.STAFF));
    }

    @PutMapping(COUNTER_STAFF_UNIT_DIST_URL+TAB+"/{tabId}/update_dist_entry")
    public ResponseEntity<Map<String, Object>> updateTabKPIsEntryForStaff(@PathVariable String tabId,@PathVariable Long unitId,@RequestBody List<TabKPIMappingDTO> tabKPIMappingDTOS){
        counterManagementService.updateTabKPIEntries(tabKPIMappingDTOS,tabId,unitId,ConfLevel.STAFF);
        return ResponseHandler.generateResponse(HttpStatus.OK, true,null);
    }

    @PutMapping(COUNTER_COUNTRY_DIST_URL+TAB+"/remove_dist_entry")
    public ResponseEntity<Map<String, Object>> removeTabKPIEntryForCountry(@PathVariable Long countryId, @RequestBody TabKPIMappingDTO tabKPIMappingDTO){
        counterManagementService.removeTabKPIEntries(tabKPIMappingDTO,countryId,ConfLevel.COUNTRY);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, null);
    }

   @PutMapping(COUNTER_UNIT_DIST_URL+TAB+"/remove_dist_entry")
    public ResponseEntity<Map<String, Object>> removeTabKPIEntryForUnit(@PathVariable Long unitId, @RequestBody TabKPIMappingDTO tabKPIMappingDTO){
        counterManagementService.removeTabKPIEntries(tabKPIMappingDTO,unitId,ConfLevel.UNIT);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, null);
    }

    @PutMapping(COUNTER_STAFF_UNIT_DIST_URL+TAB+"/remove_dist_entry")
    public ResponseEntity<Map<String, Object>> removeTabKPIEntryForStaff(@PathVariable Long unitId,@RequestBody TabKPIMappingDTO tabKPIMappingDTO){
        counterManagementService.removeTabKPIEntries(tabKPIMappingDTO,unitId,ConfLevel.STAFF);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, null);
    }

    // accessGroup Apis

    @PostMapping(COUNTER_COUNTRY_DIST_URL+ACCESS_GROUP+"/create_dist_entry")
    public ResponseEntity<Map<String, Object>> addAccessGroupKPIEntryForCountry(@PathVariable Long countryId ,@RequestBody AccessGroupKPIConfDTO accessGroupKPIConf){
        counterManagementService.addAccessGroupKPIEntries(accessGroupKPIConf,countryId,ConfLevel.COUNTRY);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, null);
    }

    @PostMapping(COUNTER_UNIT_DIST_URL+ACCESS_GROUP+"/create_dist_entry")
    public ResponseEntity<Map<String, Object>> addAccessGroupKPIEntryForUnit(@PathVariable Long unitId,@RequestBody AccessGroupKPIConfDTO accessGroupKPIConf){
        counterManagementService.addAccessGroupKPIEntries(accessGroupKPIConf,unitId,ConfLevel.UNIT);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, null);
    }

    @PutMapping(COUNTER_COUNTRY_DIST_URL+ACCESS_GROUP+"/remove_dist_entry")
    public ResponseEntity<Map<String, Object>> removeAccessGroupKPIEntryByCounty(@PathVariable Long countryId,@RequestBody AccessGroupMappingDTO accessGroupMappingDTO){
        counterManagementService.removeAccessGroupKPIEntries(accessGroupMappingDTO,countryId,ConfLevel.COUNTRY);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, null);
    }

    @PutMapping(COUNTER_UNIT_DIST_URL+ACCESS_GROUP+"/remove_dist_entry")
    public ResponseEntity<Map<String, Object>> removeAccessGroupKPIEntryByUnit(@PathVariable Long unitId,@RequestBody AccessGroupMappingDTO accessGroupMappingDTO){
        counterManagementService.removeAccessGroupKPIEntries(accessGroupMappingDTO,unitId,ConfLevel.UNIT);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, null);
    }

    @GetMapping(COUNTER_COUNTRY_DIST_URL+ACCESS_GROUP+"/{accessGroupId}")
    public ResponseEntity<Map<String, Object>> getInitialDataForAccessGroupKPIConfOfCountry(@PathVariable Long countryId,@PathVariable Long accessGroupId){
        return ResponseHandler.generateResponse(HttpStatus.OK, true,  counterManagementService.getInitialAccessGroupKPIDataConf(accessGroupId,countryId,ConfLevel.COUNTRY));
    }

    @GetMapping(COUNTER_UNIT_DIST_URL+ACCESS_GROUP+"/{accessGroupId}")
    public ResponseEntity<Map<String, Object>> getInitialDataForAccessgroupKPIConfOfUnit(@PathVariable Long unitId,@PathVariable Long accessGroupId){
        return ResponseHandler.generateResponse(HttpStatus.OK, true, counterManagementService.getInitialAccessGroupKPIDataConf(accessGroupId,unitId,ConfLevel.UNIT));
    }

    @PostMapping(UNIT_URL+COUNTER_DIST_URL+"/staff/access_group/{accessGroupId}/update_kpi")
    public ResponseEntity<Map<String,Object>> updateStaffAccessGroupKPISetting(@PathVariable Long unitId,@PathVariable Long accessGroupId,@RequestBody AccessGroupPermissionCounterDTO accessGroups,@QueryParam("created") Boolean created){
        counterManagementService.addAndRemoveStaffAccessGroupKPISetting(unitId,accessGroupId,accessGroups,created);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, null);
    }
    // orgType Apis

    @GetMapping(COUNTER_COUNTRY_DIST_URL+ORG_TYPE+"/{orgTypeId}")
    public ResponseEntity<Map<String, Object>> getInitialDataForOrgTypeKPIConf(@PathVariable Long countryId,@PathVariable Long orgTypeId){
        return ResponseHandler.generateResponse(HttpStatus.OK, true, counterManagementService.getInitialOrgTypeKPIDataConf(orgTypeId,countryId));
    }

    @PostMapping(COUNTER_COUNTRY_DIST_URL+ORG_TYPE+"/create_dist_entry")
    public ResponseEntity<Map<String, Object>> addOrgTypeKPIEntry(@PathVariable Long countryId,@RequestBody OrgTypeKPIConfDTO orgTypeKPIConf){
        counterManagementService.addOrgTypeKPIEntries(orgTypeKPIConf,countryId);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, true);
    }

    @PutMapping(COUNTER_COUNTRY_DIST_URL+ORG_TYPE+"/remove_dist_entry")
    public ResponseEntity<Map<String, Object>> removeOrgTypeKPIEntry(@PathVariable Long countryId, @RequestBody OrgTypeMappingDTO orgTypeMappingDTO){
        counterManagementService.removeOrgTypeKPIEntries(orgTypeMappingDTO,countryId);
        return ResponseHandler.generateResponse(HttpStatus.OK, true,null);
    }


    //defalut setting for unit and staff

    @PostMapping(COUNTER_UNIT_DIST_URL+"/default_kpi_setting")
    public ResponseEntity<Map<String, Object>> createDefaluSettingForUnit(@PathVariable Long unitId, @RequestBody DefaultKPISettingDTO defaultKPISettingDTO){
            counterManagementService.createDefaultKpiSetting(unitId, defaultKPISettingDTO);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, null);
    }

    @PostMapping(COUNTER_UNIT_DIST_URL+"/staff_default_kpi_setting")
    public ResponseEntity<Map<String, Object>> createDefaluSettingForStaff(@PathVariable Long unitId, @RequestBody DefaultKPISettingDTO defaultKPISettingDTO){
        counterManagementService.createDefaultStaffKPISetting(unitId, defaultKPISettingDTO);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, null);
    }


    //save and copy kpi and default data of kpi

    @GetMapping(COUNTRY_URL+KPI_URL+"/kpi_default_data")
    public ResponseEntity<Map<String,Object>> getDefaultKpiFilterDataOfCountry(@PathVariable Long countryId,@PathVariable BigInteger kpiId){
        return ResponseHandler.generateResponse(HttpStatus.OK,true,counterManagementService.getDefaultFilterDataOfKpi(kpiId,countryId,ConfLevel.COUNTRY));
    }

    @GetMapping(UNIT_URL+KPI_URL+"/kpi_default_data")
    public ResponseEntity<Map<String,Object>> getDefaultKpiFilterDataOfUnit(@PathVariable Long unitId,@PathVariable BigInteger kpiId){
        return ResponseHandler.generateResponse(HttpStatus.OK,true,counterManagementService.getDefaultFilterDataOfKpi(kpiId,unitId,ConfLevel.UNIT));
    }

    @PutMapping(COUNTRY_URL+KPI_URL+"/save_kpi")
    public ResponseEntity<Map<String,Object>> saveKpiDateOfCountry(@PathVariable Long countryId,@PathVariable BigInteger kpiId,@RequestBody CounterDTO counterDTO){
        return ResponseHandler.generateResponse(HttpStatus.OK,true,counterManagementService.saveKpiFilterData(countryId,kpiId,counterDTO,ConfLevel.COUNTRY));
    }

    @PutMapping(UNIT_URL+KPI_URL+"/save_kpi")
    public ResponseEntity<Map<String,Object>> saveKpiDateOfUnit(@PathVariable Long unitId, @PathVariable BigInteger kpiId, @RequestBody CounterDTO counterDTO){
        return ResponseHandler.generateResponse(HttpStatus.OK,true,counterManagementService.saveKpiFilterData(unitId,kpiId,counterDTO,ConfLevel.UNIT));
    }

    @PostMapping(COUNTRY_URL+KPI_URL+"/copy_kpi")
    public ResponseEntity<Map<String,Object>> copyKpiDateOfCountry(@RequestParam(value = "tabId",required=false) String tabId,@PathVariable Long countryId,@PathVariable BigInteger kpiId,@RequestBody CounterDTO counterDTO){
        return ResponseHandler.generateResponse(HttpStatus.OK,true,  counterManagementService.copyKpiFilterData(tabId,countryId,kpiId,counterDTO,ConfLevel.COUNTRY));
    }

    @PostMapping(UNIT_URL+KPI_URL+"/copy_kpi")
    public ResponseEntity<Map<String,Object>> copyKpiDateOfUnit(@RequestParam(value = "tabId",required=false) String tabId,@PathVariable Long unitId,@PathVariable BigInteger kpiId, @RequestBody CounterDTO counterDTO){
        return ResponseHandler.generateResponse(HttpStatus.OK,true,counterManagementService.copyKpiFilterData(tabId,unitId,kpiId,counterDTO,ConfLevel.UNIT));
    }

    @PostMapping(UNIT_URL+KPI_URL+"/preview_kpi")
    public ResponseEntity<Map<String,Object>> previewKpiDate(@PathVariable BigInteger kpiId,@PathVariable Long unitId, @RequestBody FilterCriteriaDTO filterCriteria){
        return ResponseHandler.generateResponse(HttpStatus.OK,true,counterManagementService.getKpiPreviewWithFilter(kpiId,unitId,filterCriteria));
    }
}
