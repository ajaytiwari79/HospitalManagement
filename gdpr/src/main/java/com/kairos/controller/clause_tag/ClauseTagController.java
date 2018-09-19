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
    public ResponseEntity<Object> createClauseTag(@PathVariable Long countryId,@PathVariable Long organizationId,@RequestParam  String clauseTag) {
        if (StringUtils.isBlank(clauseTag)) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "name can't be empty ");
        }
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");

        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, clauseTagService.createClauseTag(countryId,organizationId,clauseTag));

    }


    @ApiOperation("get clauseTag by id")
    @GetMapping("/clause_tag/{id}")
    public ResponseEntity<Object> getClauseTagById(@PathVariable Long countryId,@PathVariable Long organizationId,@PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        }
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");

        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, clauseTagService.getClauseTagById(countryId,organizationId,id));

    }


    @ApiOperation("get all clauseTag ")
    @GetMapping("/clause_tag/all")
    public ResponseEntity<Object> getAllClauseTag(@PathVariable Long countryId,@PathVariable Long organizationId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, clauseTagService.getAllClauseTag(countryId,organizationId));

    }


    @ApiOperation("delete clauseTag  by id")
    @DeleteMapping("/clause_tag/delete/{id}")
    public ResponseEntity<Object> deleteClauseTagById(@PathVariable Long countryId,@PathVariable Long organizationId,@PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        }
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");

        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, clauseTagService.deleteClauseTagById(countryId,organizationId,id));

    }

    @ApiOperation("update clauseTag by id")
    @PutMapping("/clause_tag/update/{id}")
    public ResponseEntity<Object> updateClauseTag(@PathVariable Long countryId,@PathVariable Long organizationId,@PathVariable BigInteger id, @RequestParam  String clauseTag) {

        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        }
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, clauseTagService.updateClauseTag(countryId,organizationId,id, clauseTag));

    }




}
