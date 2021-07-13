package com.kairos.controller.counters;


import com.kairos.commons.response.ResponseHandler;
import com.kairos.constants.ApiConstants;
import com.kairos.dto.activity.counter.chart.BaseChart;
import com.kairos.dto.activity.counter.data.FilterCriteriaDTO;
import com.kairos.dto.response.ResponseDTO;
import com.kairos.persistence.model.counter.KPI;
import com.kairos.service.counter.CounterDataService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Map;

import static com.kairos.constants.ApiConstants.COUNTER_DATA_URL;

/*
 * @author: mohit.shakya@oodlestechnologies.com
 * @dated: Jun/27/2018
 */

@RestController
@RequestMapping(ApiConstants.COUNTER_DATA_URL)
public class CounterDataController {

    @Inject
    CounterDataService counterDataService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getCounterInitialData(@RequestParam BigInteger solverConfigId, @PathVariable Long unitId) {
        KPI kpi;
        BaseChart baseChart;
        ArrayList<KPI> kpiList = new ArrayList<>();
        //TODO: TO COMPLETE
        kpiList.addAll(new ArrayList<>());

        return ResponseHandler.generateResponse(HttpStatus.OK, true, kpiList);
    }

    @PostMapping
    public ResponseEntity<ResponseDTO<Object>> getFilteredCounterData(@RequestBody FilterCriteriaDTO criteria){
        //get filters and get counter ids.
        return ResponseHandler.generateResponseDTO(HttpStatus.OK, true, new Object());
    }
}
