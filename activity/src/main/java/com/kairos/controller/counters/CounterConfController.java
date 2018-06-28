package com.kairos.controller.counters;

import com.kairos.activity.counter.FilterCriteria;
import com.kairos.persistence.model.counter.Counter;
import com.kairos.service.counter.CounterConfService;
import com.kairos.util.response.ResponseHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import static com.kairos.constants.ApiConstants.COUNTER_CONF_URL;

/*
 * @author: mohit.shakya@oodlestechnologies.com
 * @dated: Jun/26/2018
 */

@RestController
@RequestMapping(COUNTER_CONF_URL)
public class CounterConfController {

    @Inject
    private CounterConfService counterConfService;

    @RequestMapping(value="/{counterId}", method = RequestMethod.PUT)
    public ResponseEntity<Map<String, Object>> updateCounterCriteria(@PathVariable BigInteger counterId, @RequestBody List<FilterCriteria> criteriaList){
        counterConfService.updateCounterCriteria(counterId, criteriaList);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, criteriaList);
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> addCounter(@RequestBody Counter counter){
        counterConfService.addCounter(counter);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, null);
    }
}
