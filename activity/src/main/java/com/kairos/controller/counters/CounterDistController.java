package com.kairos.controller.counters;

import com.kairos.activity.counter.*;
import com.kairos.enums.CounterType;
import com.kairos.persistence.model.counter.Counter;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.kairos.constants.ApiConstants.COUNTER_COUNTRY_DIST_URL;

/*
 * @author: mohit.shakya@oodlestechnologies.com
 * @dated: Jun/26/2018
 */

//TODO: TO be modified according to latest proposed distribution functionality
@RestController
@RequestMapping(COUNTER_COUNTRY_DIST_URL)
@Api(COUNTER_COUNTRY_DIST_URL)
public class CounterDistController {

    @Inject
    CounterManagementService counterManagementService;

    private final static Logger logger = LoggerFactory.getLogger(CounterDistController.class);

    public ResponseEntity<Map<String, Object>> getModuleCounterDistributionForCountry(@RequestParam BigInteger countryId){
        Map<String, Object> data = new HashMap();
        List<Counter> counters = counterManagementService.getAllCounters();
        CounterDistDTO responseData = new CounterDistDTO(
                CounterType.getCounterTypes(),
                counterManagementService.getCounterTypeAndIdMapping(counters),
                counterManagementService.getModuleCountersForCountry(countryId),
                null
        );
        return ResponseHandler.generateResponse(HttpStatus.OK, true, responseData);
    }

    public ResponseEntity<Map<String, Object>> saveModuleCounterDistributionForCountry(@RequestBody List<ModuleCounterGroupingDTO> moduleCounters, @RequestParam BigInteger countryId){
        counterManagementService.storeModuleCounters(moduleCounters, countryId);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, true);
    }

    public ResponseEntity<Map<String, Object>> getRoleCounterDistributionForUnit(@RequestParam BigInteger unitId, @RequestParam BigInteger countryId){
        Map<String, Object> data = new HashMap<>();
        List<Counter> counters = counterManagementService.getAllCounters();
        CounterDistDTO responseData = new CounterDistDTO(
                CounterType.getCounterTypes(),
                counterManagementService.getCounterIdAndTypeMapping(counters),
                counterManagementService.getModuleCountersForCountry(countryId),
                counterManagementService.getRoleCounterMapping(unitId)
        );
        //assuming UI have a unitLevelRoles list.
        return ResponseHandler.generateResponse(HttpStatus.OK, true, responseData);
    }

    public ResponseEntity<Map<String, Object>> storeRoleCounterDistributionForUnit(@RequestBody List<RoleCounterDTO> roleCounterDTOS, @RequestParam BigInteger unitId){
        counterManagementService.storeRoleCountersForUnit(roleCounterDTOS, unitId);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, true);
    }

    public ResponseEntity<Map<String,Object>> addDefaultCounterAtModuleLevel(){
        return ResponseHandler.generateResponse(HttpStatus.OK, true, true);
    }

    public ResponseEntity<Map<String, Object>> getInitialCounterSettingsForCountry(){
        return ResponseHandler.generateResponse(HttpStatus.OK, true, true);
    }

    public ResponseEntity<Map<String, Object>> getInitialCounterSettingsForUnit(){
        return ResponseHandler.generateResponse(HttpStatus.OK, true, true);
    }

    public ResponseEntity<Map<String, Object>> getInitialCounterSettingsForIndividual(){
        return ResponseHandler.generateResponse(HttpStatus.OK, true, true);
    }

    @GetMapping("/counters")
    public ResponseEntity<Map<String, Object>> getAvailableCountersList(){
        return ResponseHandler.generateResponse(HttpStatus.OK, true, counterManagementService.getKPIsList());
    }

    @GetMapping("/category")
    public ResponseEntity<Map<String, Object>> getInitialCategoryKPIDistributionData(){
        return ResponseHandler.generateResponse(HttpStatus.OK, true, counterManagementService.getInitialCategoryKPIDistData());
    }

    @PostMapping("/category")
    public ResponseEntity<Map<String, Object>> saveCategoryKPIDistribution(@RequestBody CategoryKPIsDTO categorieKPIsDetails){
        counterManagementService.updateCategoryKPIsDistribution(categorieKPIsDetails);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, null);
    }

    @GetMapping("/module/{moduleId}/initials")
    public ResponseEntity<Map<String, Object>> getInitialDataForTabKPIConfiguration(@PathVariable Long countryId, @PathVariable String moduleId){
        return ResponseHandler.generateResponse(HttpStatus.OK, true, counterManagementService.getInitialTabKPIDataConf(moduleId, countryId));
    }

    @PostMapping("/module/{moduleId}/create_dist_entry")
    public ResponseEntity<Map<String, Object>> addTabKPIsEntry(@RequestBody TabKPIEntryConfDTO tabKPIEntry){
        counterManagementService.addTabKPIEntries(tabKPIEntry);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, null);
    }

    @PutMapping("/module/{moduleId}/remove_dist_entry")
    public ResponseEntity<Map<String, Object>> removeTabKPIEntry(@RequestBody TabKPIEntryConfDTO tabKPIEntryConfDTO){
        counterManagementService.removeTabKPIEntries(tabKPIEntryConfDTO);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, null);
    }

    @PostMapping("/access_group/initials")
    public ResponseEntity<Map<String, Object>> getInitialDataForAccessGroupKPIConf(@RequestBody List<Long> accessGroupIds){
        return ResponseHandler.generateResponse(HttpStatus.OK, true, counterManagementService.getInitialAccessGroupKPIDataConf(accessGroupIds));
    }

    @PostMapping("/access_group/create_dist_entry")
    public ResponseEntity<Map<String, Object>> addAccessGroupKPIEntry(@RequestBody AccessGroupKPIConfDTO accessGroupKPIConf){
        counterManagementService.addAccessGroupKPIEntries(accessGroupKPIConf);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, null);
    }

    @PutMapping("/access_group/remove_dist_entry")
    public ResponseEntity<Map<String, Object>> removeAccessGroupKPIEntry(@RequestBody AccessGroupKPIConfDTO accessGroupKPIConf){
        counterManagementService.removeOrgTypeKPIEntries(accessGroupKPIConf);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, null);
    }

    @GetMapping("/org_type/{orgTypeId}/initials")
    public ResponseEntity<Map<String, Object>> getInitialDataForOrgTypeKPIConf(@PathVariable Long orgTypeId){
        return ResponseHandler.generateResponse(HttpStatus.OK, true, counterManagementService.getInitialOrgTypeKPIDataConf(orgTypeId));
    }

    @PostMapping("/org_type/create_dist_entry")
    public ResponseEntity<Map<String, Object>> addOrgTypeKPIEntry(@RequestBody OrgTypeKPIConfDTO orgTypeKPIConf){
        counterManagementService.addOrgTypeKPIEntries(orgTypeKPIConf);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, null);
    }

    @PutMapping("/org_type/remove_dist_entry")
    public ResponseEntity<Map<String, Object>> removeOrgTypeKPIEntry(@RequestBody OrgTypeKPIConfDTO orgTypeKPIConf){
        counterManagementService.removeOrgTypeKPIEntries(orgTypeKPIConf);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, null);
    }
}
