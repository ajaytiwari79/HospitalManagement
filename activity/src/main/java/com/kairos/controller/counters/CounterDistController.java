package com.kairos.controller.counters;

import com.kairos.activity.counter.AccessGroupKPIConfDTO;
import com.kairos.activity.counter.CategoryKPIsDTO;
import com.kairos.activity.counter.OrgTypeKPIConfDTO;
import com.kairos.activity.counter.TabKPIEntryConfDTO;
import com.kairos.activity.counter.enums.ConfLevel;
import com.kairos.service.counter.CounterManagementService;
import com.kairos.util.response.ResponseHandler;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.ws.rs.Path;
import java.util.List;
import java.util.Map;

import static com.kairos.constants.ApiConstants.COUNTER_COUNTRY_DIST_URL;
import static com.kairos.constants.ApiConstants.COUNTRY_URL;
import static com.kairos.constants.ApiConstants.UNIT_URL;

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

    @GetMapping("/counters/country/{countryId}")
    public ResponseEntity<Map<String, Object>> getAvailableKPIsListForCountry(@PathVariable Long countryId){
        return ResponseHandler.generateResponse(HttpStatus.OK, true, counterManagementService.getKPIsList(countryId, ConfLevel.COUNTRY));
    }

    @GetMapping("/counters/unit/{unitId}")
    public ResponseEntity<Map<String, Object>> getAvailableKPIsListForUnit(@PathVariable Long unitId){
        return ResponseHandler.generateResponse(HttpStatus.OK, true, counterManagementService.getKPIsList(unitId, ConfLevel.UNIT));
    }

    @GetMapping("/category/country/{countryId}")
    public ResponseEntity<Map<String, Object>> getInitialCategoryKPIDistributionDataForCountry(@PathVariable Long countryId){
        return ResponseHandler.generateResponse(HttpStatus.OK, true, counterManagementService.getInitialCategoryKPIDistData(countryId, ConfLevel.COUNTRY));
    }

    @PostMapping("/category/country/{countryId}")
    public ResponseEntity<Map<String, Object>> saveCategoryKPIDistributionForCountry(@RequestBody CategoryKPIsDTO categorieKPIsDetails, @PathVariable Long countryId){
        counterManagementService.updateCategoryKPIsDistribution(categorieKPIsDetails, ConfLevel.COUNTRY, countryId);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, null);
    }

    @GetMapping("/category/unit/{unitId}")
    public ResponseEntity<Map<String, Object>> getInitialCategoryKPIDistributionDataForUnit(@PathVariable Long unitId){
        return ResponseHandler.generateResponse(HttpStatus.OK, true, counterManagementService.getInitialCategoryKPIDistData(unitId, ConfLevel.UNIT));
    }

    @PostMapping("/category/unit/{unitId}")
    public ResponseEntity<Map<String, Object>> saveCategoryKPIDistributionUnit(@RequestBody CategoryKPIsDTO categorieKPIsDetails, @PathVariable Long unitId){
        counterManagementService.updateCategoryKPIsDistribution(categorieKPIsDetails, ConfLevel.UNIT, unitId);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, null);
    }

    @GetMapping("/module/{moduleId}"+COUNTRY_URL)
    public ResponseEntity<Map<String, Object>> getInitialTabKPIDistConfForCountry(@PathVariable Long countryId, @PathVariable String moduleId){
        return ResponseHandler.generateResponse(HttpStatus.OK, true, counterManagementService.getInitialTabKPIDataConf(moduleId, countryId, ConfLevel.COUNTRY));
    }

    @GetMapping("/module/{moduleId}"+UNIT_URL)
    public ResponseEntity<Map<String, Object>> getInitialTabKPIDistConfForUnit(@PathVariable Long unitId, @PathVariable String moduleId){
        return ResponseHandler.generateResponse(HttpStatus.OK, true, counterManagementService.getInitialTabKPIDataConf(moduleId, unitId, ConfLevel.UNIT));
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
