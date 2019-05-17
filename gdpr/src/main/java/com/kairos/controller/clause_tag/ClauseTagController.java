package com.kairos.controller.clause_tag;


import com.kairos.service.clause_tag.ClauseTagService;
import com.kairos.utils.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;

import static com.kairos.constants.ApiConstant.*;
/*
 *
 *  created by bobby 06/5/2018
 * */


@RestController
@RequestMapping(API_ORGANIZATION_URL)
@Api(API_ORGANIZATION_URL)
class ClauseTagController {


    @Inject
    private ClauseTagService clauseTagService;


    @ApiOperation("get all master clauseTag ")
    @GetMapping(COUNTRY_URL + "/clause_tag")
    public ResponseEntity<Object> getAllMasterClauseTag(@PathVariable Long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, clauseTagService.getAllClauseTagByCountryId(countryId));

    }


    @ApiOperation("get all clauseTag of unit ")
    @GetMapping(UNIT_URL + "/clause_tag")
    public ResponseEntity<Object> getAllClauseTag(@PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, clauseTagService.getAllClauseTagByUnitId(unitId));

    }

}
