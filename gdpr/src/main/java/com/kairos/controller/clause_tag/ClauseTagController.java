package com.kairos.controller.clause_tag;


import com.kairos.service.clause_tag.ClauseTagService;
import com.kairos.utils.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import static  com.kairos.constants.ApiConstant.API_ORGANIZATION_COUNTRY_URL;

import javax.inject.Inject;
import java.math.BigInteger;
/*
 *
 *  created by bobby 06/5/2018
 * */


@RestController
@RequestMapping(API_ORGANIZATION_COUNTRY_URL)
@Api(API_ORGANIZATION_COUNTRY_URL)
public class ClauseTagController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClauseTagController.class);


    @Inject
    private ClauseTagService clauseTagService;

    @ApiOperation("add clauseTag")
    @PostMapping("/clause_tag/add")
    public ResponseEntity<Object> createClauseTag(@PathVariable Long countryId,@RequestParam  String clauseTag) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, clauseTagService.createClauseTag(countryId,clauseTag));

    }


    @ApiOperation("get clauseTag by id")
    @GetMapping("/clause_tag/{id}")
    public ResponseEntity<Object> getClauseTagById(@PathVariable Long countryId,@PathVariable BigInteger id) {

        return ResponseHandler.generateResponse(HttpStatus.OK, true, clauseTagService.getClauseTagById(countryId,id));

    }


    @ApiOperation("get all clauseTag ")
    @GetMapping("/clause_tag/all")
    public ResponseEntity<Object> getAllClauseTag(@PathVariable Long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, clauseTagService.getAllClauseTag(countryId));

    }


    @ApiOperation("delete clauseTag  by id")
    @DeleteMapping("/clause_tag/delete/{id}")
    public ResponseEntity<Object> deleteClauseTagById(@PathVariable Long countryId,@PathVariable BigInteger id) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, clauseTagService.deleteClauseTagById(countryId,id));

    }

    @ApiOperation("update clauseTag by id")
    @PutMapping("/clause_tag/update/{id}")
    public ResponseEntity<Object> updateClauseTag(@PathVariable Long countryId,@PathVariable BigInteger id, @RequestParam  String clauseTag) {

        return ResponseHandler.generateResponse(HttpStatus.OK, true, clauseTagService.updateClauseTag(countryId,id, clauseTag));

    }




}
