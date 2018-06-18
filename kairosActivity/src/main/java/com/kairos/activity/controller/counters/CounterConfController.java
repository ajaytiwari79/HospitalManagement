package com.kairos.activity.controller.counters;

import com.kairos.activity.client.dto.counter.CounterCriteriaDTO;
import com.kairos.activity.persistence.model.counter.Counter;
import com.kairos.activity.persistence.model.counter.FilterCriteria;
import com.kairos.activity.service.counter.CounterConfService;
import com.kairos.activity.util.response.ResponseHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import static com.kairos.activity.constants.ApiConstants.COUNTER_CONF_URL;

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
