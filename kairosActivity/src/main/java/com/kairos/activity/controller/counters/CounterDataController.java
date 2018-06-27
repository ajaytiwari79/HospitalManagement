package com.kairos.activity.controller.counters;


import com.kairos.activity.enums.CounterType;
import com.kairos.activity.enums.counter.ChartType;
import com.kairos.activity.enums.counter.RepresentationUnit;
import com.kairos.activity.persistence.model.counter.KPI;
import com.kairos.activity.persistence.model.counter.chart.BaseChart;
import com.kairos.activity.persistence.model.counter.chart.GaugeChart;
import com.kairos.activity.persistence.model.counter.chart.SingleNumberChart;
import com.kairos.activity.util.response.ResponseHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.kairos.activity.constants.ApiConstants.COUNTER_DATA_URL;

/*
 * @author: mohit.shakya@oodlestechnologies.com
 * @dated: Jun/27/2018
 */

@RestController
@RequestMapping(COUNTER_DATA_URL)
public class CounterDataController {

    @GetMapping
    public ResponseEntity<Map<String, Object>> getCounterInitialData(@RequestParam String moduleId) {
        /*
    return map keys( List<BigInteger> order, metaData )
    { block1, block2, block3 }
    { order }
    { size:same }
    { filter }
         */
        KPI kpi;
        BaseChart baseChart;
        ArrayList<KPI> kpiList = new ArrayList<>();

        //CounterType.TOTAL_KM_DRIVEN_PER_DAY
        baseChart = new SingleNumberChart(12, RepresentationUnit.DECIMAL, "Km");
        kpi = new KPI();
        kpi.setId(BigInteger.valueOf(1));
        kpi.setChart(baseChart);
        kpi.setType(CounterType.TOTAL_KM_DRIVEN_PER_DAY);
        kpi.setTitle(kpi.getType().getName());
        kpi.setChartType(ChartType.NUMBER_ONLY);
        kpiList.add(kpi);

        //CounterType.TASK_UNPLANNED
        baseChart = new GaugeChart(0, 100, 32, null, null, RepresentationUnit.NUMBER, "Task");
        kpi = new KPI();
        kpi.setId(BigInteger.valueOf(2));
        kpi.setType(CounterType.TASK_UNPLANNED);
        kpi.setTitle(kpi.getType().getName());
        kpi.setChart(baseChart);
        kpi.setChartType(ChartType.GAUGE);
        kpiList.add(kpi);

        //CounterType.TASK_UNPLANNED_HOURS
        baseChart = new GaugeChart(0, 240, 30.3, null, null, RepresentationUnit.DECIMAL, "Hour");
        kpi = new KPI();
        kpi.setId(BigInteger.valueOf(3));
        kpi.setType(CounterType.TASK_UNPLANNED_HOURS);
        kpi.setTitle(kpi.getType().getName());
        kpi.setChart(baseChart);
        kpi.setChartType(ChartType.GAUGE);
        kpiList.add(kpi);

        //CounterType.TASKS_PER_STAFF
        baseChart = new SingleNumberChart(12, RepresentationUnit.NUMBER, "Task");
        kpi = new KPI();
        kpi.setId(BigInteger.valueOf(4));
        kpi.setType(CounterType.TASKS_PER_STAFF);
        kpi.setTitle(kpi.getType().getName());
        kpi.setChart(baseChart);
        kpi.setChartType(ChartType.NUMBER_ONLY);
        kpiList.add(kpi);


        return ResponseHandler.generateResponse(HttpStatus.OK, true, kpiList);
    }

    @GetMapping("/getMetaData")
    public ResponseEntity<Map<String, Object>> getMetaData(@RequestParam String moduleId){
        Map<String, Object> respData = new HashMap<>();
        Map<String, BigInteger> tabData = new HashMap<>();
        tabData.put("tab1", BigInteger.valueOf(1));
        tabData.put("tab1", BigInteger.valueOf(2));
        tabData.put("tab1", BigInteger.valueOf(3));
        tabData.put("tab1", BigInteger.valueOf(4));
        tabData.put("tab1", BigInteger.valueOf(5));
        tabData.put("tab1", BigInteger.valueOf(6));
        tabData.put("tab1", BigInteger.valueOf(7));
        tabData.put("tab1", BigInteger.valueOf(8));
        tabData.put("tab2", BigInteger.valueOf(9));
        tabData.put("tab2", BigInteger.valueOf(10));
        tabData.put("tab2", BigInteger.valueOf(11));
        tabData.put("tab2", BigInteger.valueOf(12));
        respData.put("tabDistribution", tabData);
        return ResponseHandler.generateResponse(HttpStatus.ACCEPTED, true, respData);
    }
}
