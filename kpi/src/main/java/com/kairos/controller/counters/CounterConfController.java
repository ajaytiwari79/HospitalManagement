package com.kairos.controller.counters;

import com.kairos.commons.response.ResponseHandler;
import com.kairos.constants.ApiConstants;
import com.kairos.dto.activity.counter.data.FilterCriteria;
import com.kairos.dto.activity.counter.distribution.category.KPICategoryDTO;
import com.kairos.dto.activity.counter.distribution.category.KPICategoryUpdationDTO;
import com.kairos.dto.activity.counter.enums.ConfLevel;
import com.kairos.dto.response.ResponseDTO;
import com.kairos.persistence.model.counter.Counter;
import com.kairos.service.counter.CounterConfService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
@RequestMapping(ApiConstants.API_V1)
public class CounterConfController {

    @Inject
    private CounterConfService counterConfService;

    @PostMapping(value = ApiConstants.COUNTRY_URL+ ApiConstants.COUNTER_CONF_URL+"/counter")
    public ResponseEntity<ResponseDTO<Object>> addCounterEntries(@PathVariable Long countryId){
        counterConfService.addEntries(countryId);
        return ResponseHandler.generateResponseDTO(HttpStatus.OK, true, null);
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
    @PutMapping(value= ApiConstants.COUNTER_CONF_URL+"/counter/{counterId}")
    public ResponseEntity<Map<String, Object>> updateCounterCriteria(@PathVariable BigInteger counterId, @RequestBody List<FilterCriteria> criteriaList){
        counterConfService.updateCounterCriteria(counterId, criteriaList);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, criteriaList);
    }

    @PostMapping(value= ApiConstants.COUNTER_CONF_URL+"/counter")
    public ResponseEntity<Map<String, Object>> addCounter(@RequestBody Counter counter){
        counterConfService.addCounter(counter);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, null);
    }

    @PostMapping(value= ApiConstants.COUNTRY_URL+ ApiConstants.COUNTER_CONF_URL+"/category")
    public ResponseEntity<Map<String, Object>> addCategoriesAtCountryLevel(@PathVariable Long countryId, @RequestBody List<KPICategoryDTO> categories){
        return ResponseHandler.generateResponse(HttpStatus.OK, true, counterConfService.addCategories(categories, ConfLevel.COUNTRY, countryId));
    }

    @PostMapping(value= ApiConstants.UNIT_URL+ ApiConstants.COUNTER_CONF_URL+"/category")
    public ResponseEntity<Map<String, Object>> addCategoriesAtUnitLevel(@PathVariable Long unitId, @RequestBody List<KPICategoryDTO> categories){
        return ResponseHandler.generateResponse(HttpStatus.OK, true, counterConfService.addCategories(categories, ConfLevel.UNIT, unitId));
    }

    @PutMapping(value = ApiConstants.COUNTRY_URL+ ApiConstants.COUNTER_CONF_URL+"/category")
    public ResponseEntity<Map<String, Object>> updateCategoriesForCountry(@RequestBody @Valid KPICategoryUpdationDTO categories, @PathVariable Long countryId){
        return ResponseHandler.generateResponse(HttpStatus.OK, true, counterConfService.updateCategories(categories, ConfLevel.COUNTRY, countryId));
    }

    @PutMapping(value = ApiConstants.UNIT_URL+ ApiConstants.COUNTER_CONF_URL+"/category")
    public ResponseEntity<Map<String, Object>> updateCategoriesForUnit(@RequestBody @Valid KPICategoryUpdationDTO categories, @PathVariable Long unitId){
        return ResponseHandler.generateResponse(HttpStatus.OK, true, counterConfService.updateCategories(categories, ConfLevel.UNIT, unitId));
    }
}
