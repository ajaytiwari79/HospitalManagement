package com.kairos.controller.counters;

import com.kairos.activity.counter.FilterCriteria;
import com.kairos.activity.counter.distribution.category.KPICategoryUpdationDTO;
import com.kairos.activity.counter.enums.ConfLevel;
import com.kairos.persistence.model.counter.Counter;
import com.kairos.persistence.model.counter.KPICategory;
import com.kairos.service.counter.CounterConfService;
import com.kairos.util.response.ResponseHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import static com.kairos.constants.ApiConstants.*;

/*
 * @author: mohit.shakya@oodlestechnologies.com
 * @dated: Jun/26/2018
 */

@RestController
@RequestMapping(COUNTER_CONF_URL)
public class CounterConfController {

    @Inject
    private CounterConfService counterConfService;

    @PostMapping(value = "/counter"+COUNTRY_URL)
    public ResponseEntity<Map<String, Object>> addCounterEntries(@PathVariable Long countryId){
        counterConfService.addEntries(countryId);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, null);
    }

    /*
     * TODO: constraint: this should be applicable if creator updates the counter criteria after a prior notice period this will be effective to throughout hierarchy.
     * description: UPDATE IN COUNTER DEFINITION BY OWNER [version is best.]
     * Initially, any update in counter will point to a new document with updated details for that level.
     * counter can be updated by owning level.
     * child in hierarchy will be updated (or will point to that new document) after a time period.
     * there should be a prior notification of time period with changed details whenever any counter get updated. after that time this will be effective to hierarchy
     * updated counter copy will be different than normal counter copies with a boolean field updatedDefinition.
     * In this scenario, there should be two states for this 'draft' and 'publish'
    */
    @PutMapping(value="/counter/{counterId}")
    public ResponseEntity<Map<String, Object>> updateCounterCriteria(@PathVariable BigInteger counterId, @RequestBody List<FilterCriteria> criteriaList){
        counterConfService.updateCounterCriteria(counterId, criteriaList);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, criteriaList);
    }

    @PostMapping(value="/counter")
    public ResponseEntity<Map<String, Object>> addCounter(@RequestBody Counter counter){
        counterConfService.addCounter(counter);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, null);
    }

    @PostMapping(value=COUNTRY_URL+"/category")
    public ResponseEntity<Map<String, Object>> addCategoriesAtCountryLevel(@PathVariable Long countryId, @RequestBody List<KPICategory> categories){
        return ResponseHandler.generateResponse(HttpStatus.OK, true, counterConfService.addCategories(categories, ConfLevel.COUNTRY, countryId));
    }

    @PostMapping(value=UNIT_URL+"/category")
    public ResponseEntity<Map<String, Object>> addCategoriesAtUnitLevel(@PathVariable Long unitId, @RequestBody List<KPICategory> categories){
        return ResponseHandler.generateResponse(HttpStatus.OK, true, counterConfService.addCategories(categories, ConfLevel.UNIT, unitId));
    }

    @PutMapping(value = COUNTRY_URL+"/category")
    public ResponseEntity<Map<String, Object>> updateCategoriesForCountry(@RequestBody @Valid KPICategoryUpdationDTO categories, @PathVariable Long countryId){
        counterConfService.updateCategories(categories, ConfLevel.COUNTRY, countryId);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, null);
    }

    @PutMapping(value =UNIT_URL+"/category")
    public ResponseEntity<Map<String, Object>> updateCategoriesForUnit(@RequestBody @Valid KPICategoryUpdationDTO categories, @PathVariable Long unitId){
        counterConfService.updateCategories(categories, ConfLevel.UNIT, unitId);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, null);
    }
}
