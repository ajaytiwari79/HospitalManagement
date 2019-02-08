package com.kairos.controller.clause_tag;


import com.kairos.service.clause_tag.ClauseTagService;
import com.kairos.utils.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;

import static com.kairos.constants.ApiConstant.*;
/*
 *
 *  created by bobby 06/5/2018
 * */


@RestController
@RequestMapping(API_ORGANIZATION_URL)
@Api(API_ORGANIZATION_URL)
public class ClauseTagController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClauseTagController.class);


    @Inject
    private ClauseTagService clauseTagService;

    /*@ApiOperation("add clauseTag")
    @PostMapping(COUNTRY_URL + "/clause_tag")
    public ResponseEntity<Object> createClauseTag(@PathVariable Long countryId, @RequestParam String clauseTag) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, clauseTagService.createClauseTag(countryId, clauseTag));

    }


    @ApiOperation("get clauseTag by id")
    @GetMapping(COUNTRY_URL + "/clause_tag/{id}")
    public ResponseEntity<Object> getClauseTagById(@PathVariable Long countryId, @PathVariable BigInteger id) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, clauseTagService.getClauseTagById(countryId, id));

    }*/


    @ApiOperation("get all master clauseTag ")
    @GetMapping(COUNTRY_URL + "/clause_tag")
    public ResponseEntity<Object> getAllMasterClauseTag(@PathVariable Long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, clauseTagService.getAllClauseTagByCountryId(countryId));

    }


    /*@ApiOperation("delete clauseTag  by id")
    @DeleteMapping(COUNTRY_URL + "/clause_tag/{id}")
    public ResponseEntity<Object> deleteClauseTagById(@PathVariable Long countryId, @PathVariable BigInteger id) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, clauseTagService.deleteClauseTagById(countryId, id));

    }*/

   /* @ApiOperation("update clauseTag by id")
    @PutMapping(COUNTRY_URL + "/clause_tag/{id}")
    public ResponseEntity<Object> updateClauseTag(@PathVariable Long countryId, @PathVariable BigInteger id, @RequestParam String clauseTag) {

        return ResponseHandler.generateResponse(HttpStatus.OK, true, clauseTagService.updateClauseTag(countryId, id, clauseTag));

    }*/


    @ApiOperation("get all clauseTag of unit ")
    @GetMapping(UNIT_URL + "/clause_tag")
    public ResponseEntity<Object> getAllClauseTag(@PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, clauseTagService.getAllClauseTagByUnitId(unitId));

    }

}
