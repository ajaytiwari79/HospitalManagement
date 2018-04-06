package com.kairos.activity.controller.counters;

import com.kairos.activity.persistence.model.counter.CustomCounterSettings;
import com.kairos.activity.service.counter.CounterManagementService;
import com.kairos.activity.util.response.ResponseHandler;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.Map;

import static com.kairos.activity.constants.ApiConstants.COUNTER_URL;

@RestController
@RequestMapping(COUNTER_URL)
@Api(COUNTER_URL)
public class CounterController {

    @Inject
    CounterManagementService counterManagementService;

    private final static Logger logger = LoggerFactory.getLogger(CounterController.class);

    public ResponseEntity<Map<String, Object>> saveCustomCounterConfiguration(@RequestBody CustomCounterSettings counterSettings, @PathVariable Long unitId){
        return ResponseHandler.generateResponse(HttpStatus.OK, true, null);
    }



}
