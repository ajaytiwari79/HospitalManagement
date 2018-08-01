package com.kairos.controller.counters;

import com.kairos.activity.counter.DefalutKPISettingDTO;
import com.kairos.activity.counter.distribution.access_group.AccessGroupKPIConfDTO;
import com.kairos.activity.counter.distribution.category.CategoryKPIsDTO;
import com.kairos.activity.counter.distribution.org_type.OrgTypeKPIConfDTO;
import com.kairos.activity.counter.distribution.tab.TabKPIEntryConfDTO;
import com.kairos.activity.counter.enums.ConfLevel;
import com.kairos.persistence.model.counter.OrgTypeKPIEntry;
import com.kairos.service.counter.CounterManagementService;
import com.kairos.util.response.ResponseHandler;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
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
@RequestMapping(API_ORGANIZATION_URL)
@Api(API_ORGANIZATION_URL)
public class CounterDistController {

    @Inject
    private CounterManagementService counterManagementService;

    private final static Logger logger = LoggerFactory.getLogger(CounterDistController.class);

    @GetMapping(COUNTRY_URL+"/counter/dist/counters")
    public ResponseEntity<Map<String, Object>> getAvailableKPIsListForCountry(@PathVariable Long countryId){
        return ResponseHandler.generateResponse(HttpStatus.OK, true, counterManagementService.getKPIsList(countryId, ConfLevel.COUNTRY));
    }

    @GetMapping(UNIT_URL+"/counter/dist/counters")
    public ResponseEntity<Map<String, Object>> getAvailableKPIsListForUnit(@PathVariable Long unitId){
        return ResponseHandler.generateResponse(HttpStatus.OK, true, counterManagementService.getKPIsList(unitId, ConfLevel.UNIT));
    }

    @GetMapping(STAFF_URL+"/counter/dist/counters")
    public ResponseEntity<Map<String, Object>> getAvailableKPIsListForStaff(@PathVariable Long staffId){
        return ResponseHandler.generateResponse(HttpStatus.OK, true, counterManagementService.getKPIsList(staffId, ConfLevel.STAFF));
    }

    @GetMapping(COUNTRY_URL+"/counter/dist/category")
    public ResponseEntity<Map<String, Object>> getInitialCategoryKPIDistributionDataForCountry(@PathVariable Long countryId){
        return ResponseHandler.generateResponse(HttpStatus.OK, true, counterManagementService.getInitialCategoryKPIDistData(countryId, ConfLevel.COUNTRY));
    }

    @PostMapping(COUNTRY_URL+"/counter/dist/category")
    public ResponseEntity<Map<String, Object>> saveCategoryKPIDistributionForCountry(@RequestBody CategoryKPIsDTO categorieKPIsDetails, @PathVariable Long countryId){
        counterManagementService.updateCategoryKPIsDistribution(categorieKPIsDetails, ConfLevel.COUNTRY, countryId);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, null);
    }

    @GetMapping(UNIT_URL+"/counter/dist/category")
    public ResponseEntity<Map<String, Object>> getInitialCategoryKPIDistributionDataForUnit(@PathVariable Long unitId){
        return ResponseHandler.generateResponse(HttpStatus.OK, true, counterManagementService.getInitialCategoryKPIDistData(unitId, ConfLevel.UNIT));
    }

    @PostMapping(UNIT_URL+"/counter/dist/category")
    public ResponseEntity<Map<String, Object>> saveCategoryKPIDistributionUnit(@RequestBody CategoryKPIsDTO categorieKPIsDetails, @PathVariable Long unitId){
        counterManagementService.updateCategoryKPIsDistribution(categorieKPIsDetails, ConfLevel.UNIT, unitId);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, null);
    }

    @GetMapping(COUNTRY_URL+"/module/{moduleId}")
    public ResponseEntity<Map<String, Object>> getInitialTabKPIDistConfForCountry(@PathVariable Long countryId, @PathVariable String moduleId){
        return ResponseHandler.generateResponse(HttpStatus.OK, true, counterManagementService.getInitialTabKPIDataConf(moduleId, countryId, ConfLevel.COUNTRY));
    }

    @GetMapping(UNIT_URL+"/module/{moduleId}")
    public ResponseEntity<Map<String, Object>> getInitialTabKPIDistConfForUnit(@PathVariable Long unitId, @PathVariable String moduleId){
        return ResponseHandler.generateResponse(HttpStatus.OK, true, counterManagementService.getInitialTabKPIDataConf(moduleId, unitId, ConfLevel.UNIT));
    }

    @GetMapping(UNIT_URL+"/counter/dist/staff/{staffId}/module/{moduleId}")
    public ResponseEntity<Map<String, Object>> getInitialTabKPIDistConfForStaff(@PathVariable Long staffId, @PathVariable String moduleId){
        return ResponseHandler.generateResponse(HttpStatus.OK, true, counterManagementService.getInitialTabKPIDataConf(moduleId, staffId, ConfLevel.STAFF));
    }

    @PostMapping(COUNTRY_URL+"/counter/dist/module/create_dist_entry")
    public ResponseEntity<Map<String, Object>> addTabKPIsEntryForCounty(@RequestBody TabKPIEntryConfDTO tabKPIEntry,@PathVariable Long countryId){
        counterManagementService.addTabKPIEntries(tabKPIEntry,ConfLevel.COUNTRY,countryId);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, null);
    }

    @PostMapping(UNIT_URL+"/counter/dist/module/create_dist_entry")
    public ResponseEntity<Map<String, Object>> addTabKPIsEntryForUnit(@RequestBody TabKPIEntryConfDTO tabKPIEntry,@PathVariable Long unitId){
        counterManagementService.addTabKPIEntries(tabKPIEntry,ConfLevel.UNIT,unitId);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, null);
    }

    @PostMapping(UNIT_URL+"/counter/dist/staff/{staffId}/module/create_dist_entry")
    public ResponseEntity<Map<String, Object>> addTabKPIsEntryForStaff(@RequestBody TabKPIEntryConfDTO tabKPIEntry,@PathVariable Long staffId){
        counterManagementService.addTabKPIEntries(tabKPIEntry,ConfLevel.STAFF,staffId);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, null);
    }

    @PutMapping(COUNTRY_URL+"/counter/dist/module/remove_dist_entry/{moduleKpiId}")
    public ResponseEntity<Map<String, Object>> removeTabKPIEntryForCountry(@PathVariable Long moduleKpiId){
        counterManagementService.removeTabKPIEntries(moduleKpiId);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, null);
    }

    @PutMapping(UNIT_URL+"/counter/dist/module/remove_dist_entry/{moduleKpiId}")
    public ResponseEntity<Map<String, Object>> removeTabKPIEntryForUnit(@PathVariable Long moduleKpiId){
        counterManagementService.removeTabKPIEntries(moduleKpiId);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, null);
    }

    @PutMapping(UNIT_URL+"/counter/dist/staff/{staffId}/module/remove_dist_entry/{moduleKpiId}")
    public ResponseEntity<Map<String, Object>> removeTabKPIEntryForStaff(@PathVariable Long moduleKpiId){
        counterManagementService.removeTabKPIEntries(moduleKpiId);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, null);
    }

    @PostMapping(COUNTRY_URL+"/counter/dist/access_group/create_dist_entry")
    public ResponseEntity<Map<String, Object>> addAccessGroupKPIEntryForCountry(@PathVariable Long countryId ,@RequestBody AccessGroupKPIConfDTO accessGroupKPIConf){
        counterManagementService.addAccessGroupKPIEntries(accessGroupKPIConf,countryId,ConfLevel.COUNTRY);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, null);
    }

    @PostMapping(UNIT_URL+"/counter/dist/access_group/create_dist_entry")
    public ResponseEntity<Map<String, Object>> addAccessGroupKPIEntryForUnit(@PathVariable Long unitId,@RequestBody AccessGroupKPIConfDTO accessGroupKPIConf){
        counterManagementService.addAccessGroupKPIEntries(accessGroupKPIConf,unitId,ConfLevel.UNIT);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, null);
    }

    @PutMapping(COUNTRY_URL+"/counter/dist/access_group/remove_dist_entry/{accessGroupKpiId}")
    public ResponseEntity<Map<String, Object>> removeAccessGroupKPIEntryByCounty(@PathVariable Long accessGroupKpiId){
        return ResponseHandler.generateResponse(HttpStatus.OK, true,  counterManagementService.removeAccessGroupKPIEntries(accessGroupKpiId));
    }
    @PutMapping(UNIT_URL+"/counter/dist/access_group/remove_dist_entry/{accessGroupKpiId}")
    public ResponseEntity<Map<String, Object>> removeAccessGroupKPIEntryByUnit(@PathVariable Long accessGroupKpiId){
        return ResponseHandler.generateResponse(HttpStatus.OK, true,  counterManagementService.removeAccessGroupKPIEntries(accessGroupKpiId));
    }

    @GetMapping(COUNTRY_URL+"/counter/dist/access_group/{accessGroupId}")
    public ResponseEntity<Map<String, Object>> getInitialDataForAccessGroupKPIConfOfCountry(@PathVariable Long countryId,@PathVariable Long accessGroupId){
        return ResponseHandler.generateResponse(HttpStatus.OK, true, counterManagementService.getInitialAccessGroupKPIDataConf(accessGroupId,countryId,ConfLevel.COUNTRY));
    }

    @GetMapping(UNIT_URL+"/counter/dist/access_group/{accessGroupId}")
    public ResponseEntity<Map<String, Object>> getInitialDataForAccessgroupKPIConfOfUnit(@PathVariable Long unitId,@PathVariable Long accessGroupId){
        return ResponseHandler.generateResponse(HttpStatus.OK, true, counterManagementService.getInitialAccessGroupKPIDataConf(accessGroupId,unitId,ConfLevel.UNIT));
    }

    @GetMapping(COUNTRY_URL+"/counter/dist/org_type/{orgTypeId}")
    public ResponseEntity<Map<String, Object>> getInitialDataForOrgTypeKPIConf(@PathVariable Long countryId,@PathVariable Long orgTypeId){
        return ResponseHandler.generateResponse(HttpStatus.OK, true, counterManagementService.getInitialOrgTypeKPIDataConf(orgTypeId,countryId));
    }

    @PostMapping(COUNTRY_URL+"/counter/dist/org_type/create_dist_entry")
    public ResponseEntity<Map<String, Object>> addOrgTypeKPIEntry(@PathVariable Long countryId,@RequestBody OrgTypeKPIConfDTO orgTypeKPIConf){
        counterManagementService.addOrgTypeKPIEntries(orgTypeKPIConf,countryId);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, null);
    }

    @PutMapping(COUNTRY_URL+"/counter/dist/org_type/{orgTypeId}/remove_dist_entry")
    public ResponseEntity<Map<String, Object>> removeOrgTypeKPIEntry(@PathVariable Long orgTypeId, @PathVariable Long countryId){
        return ResponseHandler.generateResponse(HttpStatus.OK, true,   counterManagementService.removeOrgTypeKPIEntries(orgTypeId,countryId));
    }

//    @PostMapping(UNIT_URL+"/counter/dist/default_kpi_setting")
//    public ResponseEntity<Map<String, Object>> createDefaluSettingForUnit(@PathVariable Long unitId, @RequestBody DefalutKPISettingDTO defalutKPISettingDTO){
//            counterManagementService.createDefaultKpiSetting(unitId,defalutKPISettingDTO);
//        return ResponseHandler.generateResponse(HttpStatus.OK, true, null);
//    }
}
