package com.kairos.controller.clause;



import com.kairos.dto.gdpr.master_data.ClauseDTO;
import com.kairos.service.clause.ClauseService;
import com.kairos.utils.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.inject.Inject;
import java.math.BigInteger;
import static com.kairos.constants.ApiConstant.API_ORGANIZATION_COUNTRY_URL;


/*
 *
 *  created by bobby 04/5/2018
 * */


@RestController
@RequestMapping(API_ORGANIZATION_COUNTRY_URL)
@Api(API_ORGANIZATION_COUNTRY_URL)
public class ClauseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClauseController.class);

    @Inject
    private ClauseService clauseService;

    @Inject
    ApplicationEventPublisher eventPublisher;


    @ApiOperation("add new clause")
    @PostMapping( "/clause/add")
    public ResponseEntity<Object> createClause(@PathVariable Long countryId, @Validated @RequestBody ClauseDTO clauseDto)  {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, clauseService.createClause(countryId, clauseDto));
    }

    @ApiOperation("get clause by id")
    @GetMapping("/clause/{id}")
    public ResponseEntity<Object> getClause(@PathVariable Long countryId, @PathVariable BigInteger id) {

        return ResponseHandler.generateResponse(HttpStatus.OK, true, clauseService.getClauseById(countryId, id));


    }


    @ApiOperation("delete clause by id")
    @DeleteMapping("/clause/delete/{id}")
    public ResponseEntity<Object> deleteClause(@PathVariable Long countryId, @PathVariable BigInteger id) {

        return ResponseHandler.generateResponse(HttpStatus.OK, true, clauseService.deleteClause(countryId,id));

    }

    @ApiOperation("update clause description")
    @PutMapping("/clause/update/{clauseId}")
    public ResponseEntity<Object> updateClause(@PathVariable Long countryId,@PathVariable BigInteger clauseId, @Validated @RequestBody ClauseDTO clauseDto)  {

        return ResponseHandler.generateResponse(HttpStatus.OK, true, clauseService.updateClause(countryId,  clauseId, clauseDto));

    }



    @ApiOperation("get All clauses")
    @GetMapping("/clause/all")
    public ResponseEntity<Object> getAllClauses(@PathVariable Long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, clauseService.getAllClauses(countryId));
    }



}