package com.kairos.activity.controller.counters;

import com.kairos.activity.persistence.enums.counter.CounterType;
import com.kairos.activity.persistence.model.counter.Counter;
import com.kairos.activity.response.dto.counter.ModulewiseCounterGroupingDTO;
import com.kairos.activity.response.dto.counter.RolewiseCounterDTO;
import com.kairos.activity.service.counter.CounterManagementService;
import com.kairos.activity.util.response.ResponseHandler;
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

import static com.kairos.activity.constants.ApiConstants.COUNTER_URL;

@RestController
@RequestMapping(COUNTER_URL)
@Api(COUNTER_URL)
public class CounterController {

    @Inject
    CounterManagementService counterManagementService;

    private final static Logger logger = LoggerFactory.getLogger(CounterController.class);

    public ResponseEntity<Map<String, Object>> getModulewiseCounterDistributionForCountry(@RequestParam BigInteger countryId){
        Map<String, Object> data = new HashMap();
        List<Counter> counters = counterManagementService.getAllCounters();
        data.put("counterTypeDefs", CounterType.getCounterTypes());
        data.put("countersIdMap", counterManagementService.getCounterTypeAndIdMapping(counters));
        data.put("modulewiseCounters", counterManagementService.getModulewiseCountersForCountry(countryId));
        return ResponseHandler.generateResponse(HttpStatus.OK, true, data);
    }

    public ResponseEntity<Map<String, Object>> saveModulewiseCounterDistributionForCountry(@RequestBody List<ModulewiseCounterGroupingDTO> modulewiseCounters, @RequestParam BigInteger countryId){
        counterManagementService.storeModuleWiseCounters(modulewiseCounters, countryId);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, true);
    }

    public ResponseEntity<Map<String, Object>> getRolewiseCounterDistributionForUnit(@RequestParam BigInteger unitId, @RequestParam BigInteger countryId){
        Map<String, Object> data = new HashMap<>();
        List<Counter> counters = counterManagementService.getAllCounters();
        data.put("counterTypeDefs", CounterType.getCounterTypes());
        data.put("counterIdTypeMap", counterManagementService.getCounterIdAndTypeMapping(counters));
        data.put("modulewiseCounters", counterManagementService.getModulewiseCountersForCountry(countryId));
        data.put("rolewiseCounters", counterManagementService.getRolewiseCounterMapping(unitId));
        //assuming UI have a unitLevelRoles list.
        return ResponseHandler.generateResponse(HttpStatus.OK, true, data);
    }

    public ResponseEntity<Map<String, Object>> storeRolewiseCounterDistributionForUnit(@RequestBody List<RolewiseCounterDTO> rolewiseCounterDTOS, @RequestParam BigInteger unitId){
        counterManagementService.storeRolewiseCountersForUnit(rolewiseCounterDTOS, unitId);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, true);
    }

    public ResponseEntity<Map<String,Object>> addDefaultCounterAtModuleLevel(){
        return ResponseHandler.generateResponse(HttpStatus.OK, true, true);
    }

}
